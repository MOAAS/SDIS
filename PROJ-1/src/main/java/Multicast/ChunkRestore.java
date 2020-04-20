package Multicast;

import Chunks.Chunk;
import Chunks.ChunkID;
import Messages.ChunkMessage;
import Messages.GetChunkMessage;
import Messages.Message;
import Messages.MessageParser;
import StateLogging.PeerState;
import TCP.TCPSocket;

import java.net.InetAddress;

public class ChunkRestore {
    private static final int TIMEOUT_MS = 1000;
    private static final int MAX_TRIES = 5;

    private final ChunkID chunkID;
    private final int peerID;
    private final PeerState peerState;

    public ChunkRestore(ChunkID chunkToBackup, int peerID, PeerState peerState) {
        this.chunkID = chunkToBackup;
        this.peerID = peerID;
        this.peerState = peerState;
    }

    public Chunk restoreChunk() throws Exception {
        ChunkMessage recoverMessage;
        int waitingInterval = TIMEOUT_MS;
        for (int i = 0; i < MAX_TRIES; i++) {
            switch (peerState.getVersion()) {
                case Message.VERSION_VANILLA:
                    peerState.getChunkWaitingRoom().put(chunkID, ChunkMessage.MakeNull());

                    peerState.getMCGroups().MCCGroup.sendToGroup(new GetChunkMessage(peerState.getVersion(), peerID, chunkID.fileID, chunkID.chunkNo).toString());
                    MulticastThread.sleep(waitingInterval);
                    recoverMessage = peerState.getChunkWaitingRoom().get(chunkID);

                    peerState.getChunkWaitingRoom().remove(chunkID);

                    if(!recoverMessage.isNull()) {
                        Log("Successfully retrieved chunk: " + recoverMessage.getChunk());
                        return recoverMessage.getChunk();
                    }
                    break;
                case Message.VERSION_ENHANCED:
                    int port = (this.peerID % 64000) + 1024;
                    peerState.getMCGroups().MCCGroup.sendToGroup(new GetChunkMessage(peerState.getVersion(), peerID, chunkID.fileID, chunkID.chunkNo, InetAddress.getLocalHost().getHostAddress(), port).toString());
                    MulticastThread.sleep(waitingInterval);
                    try {
                        recoverMessage = (ChunkMessage)MessageParser.parseMessage(TCPSocket.readAny(port, 65535));
                        if (recoverMessage.chunkID().equals(this.chunkID)) {
                            Log("Successfully retrieved chunk: " + recoverMessage.getChunk());
                            return recoverMessage.getChunk();
                        }
                    } catch (Exception ignored) { }
                    break;
            }
            waitingInterval *= 2;
            if (i != MAX_TRIES - 1)
                Log("Failed to retrieve chunk. Retrying in " + waitingInterval / 1000 + " seconds...");
        }
        throw new Exception("Failed to retrieve chunk. Aborting...");
    }

    private void Log(String message) {
        System.out.println("ChunkRestore (Peer " + peerID + "): " + message);
    }
}
