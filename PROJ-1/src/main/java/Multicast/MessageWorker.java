package Multicast;

import Chunks.Chunk;
import Chunks.ChunkID;
import Messages.*;
import StateLogging.FileBackupLog;
import StateLogging.PeerState;
import StateLogging.StoredChunkLog;
import TCP.TCPSocket;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class MessageWorker implements Runnable, MessageProcessor {
    private final PeerState peerState;
    private final StoredChunkLog storedLog;
    private final FileBackupLog backupLog;
    private final String backupFolderPath;
    private final ConcurrentHashMap<ChunkID, Boolean> putChunkWaitingRoom;
    private final ConcurrentHashMap<ChunkID, ChunkMessage> chunkWaitingRoom;

    private final Message message;
    private final int peerID;

    public MessageWorker(Message message, int peerID, PeerState peerState) {
        this.message = message;
        this.peerID = peerID;
        this.peerState = peerState;
        this.storedLog = peerState.getStoredLog();
        this.backupLog = peerState.getBackupLog();
        this.backupFolderPath = peerState.getBackupFolderPath();
        this.putChunkWaitingRoom = peerState.getPutChunkWaitingRoom();
        this.chunkWaitingRoom = peerState.getChunkWaitingRoom();
    }

    @Override
    public void run() {
        Log("Processing message: " + message.toString().split(Message.HEADER_ENDING)[0]);
        this.message.process(this);
    }


    private void Log(String message) {
        System.out.println("MessageWorker (Peer " + peerID + "): " + message);
    }

    @Override
    public void processChunkMessage(ChunkMessage message) {
        if (chunkWaitingRoom.containsKey(message.chunkID()))
            chunkWaitingRoom.put(message.chunkID(), message);
    }

    @Override
    public void processDeleteMessage(DeleteMessage message) {
        if (!this.storedLog.hasFile(message.fileId))
            return;
        this.storedLog.removeFileRecords(message.fileId);

        File folder = new File(backupFolderPath, message.fileId);
        if (folder.exists()) {
            for (String chunkNo : folder.list()) {
                new File(folder.getPath(), chunkNo).delete();
            }
            folder.delete();
        }

        if (message.version.equals(Message.VERSION_ENHANCED)) {
            peerState.getMCGroups().MCCGroup.sendToGroup(new ConfirmDeletionMessage(message.version, peerID, message.fileId).toString());
        }
    }

    @Override
    public void processGetChunkMessage(GetChunkMessage message) {
        if (!this.storedLog.hasChunk(message.chunkID()))
            return;
        switch (message.version) {
            // No Enhancements
            case Message.VERSION_VANILLA:
                chunkWaitingRoom.put(message.chunkID(), ChunkMessage.MakeNull());
                MulticastThread.sleep(new Random().nextInt(400));
                if (chunkWaitingRoom.get(message.chunkID()).isNull()) {
                    Chunk storedChunk = this.storedLog.getChunk(message.chunkID(), this.backupFolderPath);
                    Log("Sending chunk to peer " + message.senderId + ": " + storedChunk);
                    peerState.getMCGroups().MDRGroup.sendToGroup(new ChunkMessage(message.version, peerID, storedChunk).toString());
                }
                chunkWaitingRoom.remove(message.chunkID());
                break;
            // With Enhancements
            case Message.VERSION_ENHANCED:
                try {
                    Chunk storedChunk = this.storedLog.getChunk(message.chunkID(), this.backupFolderPath);
                    Log("Sending chunk (VIA TCP) to peer " + message.senderId + ": " + storedChunk);
                    TCPSocket.send(message.hostname, message.port, new ChunkMessage(message.version, peerID, storedChunk).toString());
                } catch (Exception ignored) { }
                break;
        }
    }

    @Override
    public void processPutChunkMessage(PutChunkMessage message) {
        if (this.putChunkWaitingRoom.containsKey(message.chunkID()))
            this.putChunkWaitingRoom.put(message.chunkID(), true);

        if (this.backupLog.hasFile(message.fileId)) {
            Log("Cannot store chunk for a file of which I am the original owner. FileID: " + message.fileId);
            return;
        }

        Chunk chunk = message.getChunk();
        if (this.storedLog.hasChunk(chunk.getChunkID())) {
            peerState.getMCGroups().MCCGroup.sendToGroup(new StoredMessage(message.version, this.peerID, chunk).toString());
            Log("Chunk is already stored: " + chunk);
            return;
        }

        if (this.peerState.availableCapacity() < chunk.size() || this.peerState.getCapacity() == 0) {
            Log("Not enough storage space for chunk ( " + chunk.size() + " bytes). Available space: " + this.peerState.availableCapacity() + "/" + this.peerState.getCapacity());
            return;
        }

        this.storedLog.addChunk(chunk.getChunkID(), message.repDeg, chunk.size());

        switch (peerState.getVersion()) {
            case Message.VERSION_VANILLA:
                MulticastThread.sleep(new Random().nextInt(400));
                break;
            case Message.VERSION_ENHANCED:
                int lowBound = (int) (peerState.getStorageUsedPercent() * 399 / 100.0);
                int randomNum = (int) (Math.random() * (400 - lowBound)) + lowBound;
                MulticastThread.sleep(randomNum);
                break;
        }

        if (this.storedLog.withinDesiredRepDeg(chunk.getChunkID())) {
            this.storedLog.removeChunk(chunk.getChunkID());
            return;
        }
        Log("Saving chunk: " + chunk);

        try {
            chunk.save(this.backupFolderPath);
        } catch (IOException e) {
            Log("IO Exception: Could not save chunk: " + chunk);
            return;
        }
        peerState.getMCGroups().MCCGroup.sendToGroup(new StoredMessage(message.version, this.peerID, chunk).toString());
        this.storedLog.addStorer(chunk.getChunkID(), this.peerID);
    }

    @Override
    public void processRemovedMessage(RemovedMessage message) {
        if (this.backupLog.hasFile(message.fileId)) {
            this.backupLog.removeStorer(message.chunkID(), message.senderId);
            return;
        }
        if (!this.storedLog.hasChunk(message.chunkID()))
            return;
        this.storedLog.removeChunkStorer(message.chunkID(), message.senderId);
        if (!this.storedLog.withinDesiredRepDeg(message.chunkID())) {
            this.putChunkWaitingRoom.put(message.chunkID(), false);

            MulticastThread.sleep(new Random().nextInt(400));

            if (!this.putChunkWaitingRoom.get(message.chunkID())) {
                Log("Chunk fell below desired replication degree! Initiating backup of " + message.chunkID());
                try {
                    Chunk chunkToBackup = this.storedLog.getChunk(message.chunkID(), this.backupFolderPath);
                    new ChunkBackup(chunkToBackup, peerID, peerState).backup(this.storedLog.getDesiredRepDeg(message.chunkID()), this.storedLog);
                    peerState.getMCGroups().MCCGroup.sendToGroup(new StoredMessage(message.version, this.peerID, chunkToBackup).toString());
                } catch (Exception e) {
                    Log("Error backing up chunk. Error message: " + e.getMessage());
                }
            }
            this.putChunkWaitingRoom.remove(message.chunkID());
        }
    }

    @Override
    public void processStoredMessage(StoredMessage message) {
        if (storedLog.hasChunk(message.chunkID()))
            storedLog.addStorer(message.chunkID(), message.senderId);
        if (backupLog.hasFile(message.fileId))
            backupLog.addStorer(message.chunkID(), message.senderId);
    }

    @Override
    public void processStartMessage() {
        if (peerState.getVersion().equals(Message.VERSION_VANILLA))
            return;
        ArrayList<String> files = backupLog.filesWaitingForDeletion();
        for(String fileId : files) {
            peerState.getMCGroups().MCCGroup.sendToGroup(new DeleteMessage(peerState.getVersion(), this.peerID, fileId).toString());
        }
    }

    @Override
    public void processConfirmDeletionMessage(ConfirmDeletionMessage message) {
        if (peerState.getVersion().equals(Message.VERSION_VANILLA))
            return;

        if(backupLog.hasFile(message.fileId))
            backupLog.removeStorer(message.fileId, message.senderId);
    }
}
