import Chunks.Chunk;
import Chunks.ChunkID;
import Chunks.FileIDMaker;
import Chunks.FileSplitter;
import Multicast.*;
import StateLogging.*;
import Messages.*;

import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Peer implements TestAppRemoteInterface {
    private final int peerID;

    private final String rootFolderPath;
    private final String peerFolderPath;
    private final String backupFolderPath;

    private final PeerState peerState;

    public Peer(String protocolVersion, int peerID, String accessPoint, MCGroups multicastGroups) {
        this.peerID = peerID;
        this.rootFolderPath = "peers";
        this.peerFolderPath = this.rootFolderPath + "/peer" + peerID;
        this.backupFolderPath =  this.peerFolderPath + "/backup";

        this.peerState = PeerState.loadPeerState(this.backupFolderPath, protocolVersion, multicastGroups);

        this.setupFileSystem();



        try {
            TestAppRemoteInterface stub = (TestAppRemoteInterface) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(accessPoint, stub);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            return;
        }

        if(this.peerState.getVersion().equals(Message.VERSION_ENHANCED))
            multicastGroups.MCCGroup.sendToGroup(new StartMessage(this.peerState.getVersion(), peerID).toString());

        Runtime.getRuntime().addShutdownHook(new Thread(this.peerState::save));
        Log("Armed and ready! Running protocol version " + protocolVersion);
    }


    private void setupFileSystem() {
        File rootFolder = new File(this.rootFolderPath);
        File peerFolder = new File(this.peerFolderPath);
        File backupFolder = new File(this.backupFolderPath);
        File documentFolder = new File(this.peerFolderPath, "Documents");

        if (!rootFolder.exists())
            rootFolder.mkdir();
        if (!peerFolder.exists())
            peerFolder.mkdir();
        if (!backupFolder.exists())
            backupFolder.mkdir();
        if (!documentFolder.exists())
            documentFolder.mkdir();
    }
    
    public void run() {
        new MulticastThread("MCCThread", this.peerID, peerState.getMCGroups().MCCGroup, this.peerState).start();
        new MulticastThread("MDABThread", this.peerID, peerState.getMCGroups().MDBGroup, this.peerState).start();
        new MulticastThread("MDRThread", this.peerID, peerState.getMCGroups().MDRGroup, this.peerState).start();

        new Thread(() -> {
            while (!Thread.interrupted()) {
                int capacity = this.peerState.getCapacity();
                Log("Saving peer state. Used capacity: " + (capacity -this.peerState.availableCapacity()) + "/" + capacity);
                this.peerState.save();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ignored) { }
            }
        }).start();
    }

    @Override
    public void backupFile(String filePath, int repDeg) throws Exception {
        if (repDeg > 9) {
            ThrowError("Replication degree must be lower than 10 (was " + repDeg + ")");
        }

        File file = new File(this.peerFolderPath, filePath);
        String fileID = FileIDMaker.createID(file);
        if (fileID == null) {
            ThrowError("Could not create file ID for " + filePath);
        }

        String oldFileID = peerState.getBackupLog().getFileID(filePath);
        if (oldFileID != null && !oldFileID.equals(fileID)) {
            deleteFile(filePath);
        }

        FileSplitter fileSplitter = null;
        try {
            fileSplitter = new FileSplitter(file, fileID);
            peerState.getBackupLog().addFile(fileID, filePath, repDeg, fileSplitter.getNumChunks());

            while (!fileSplitter.isDone()) {
                new ChunkBackup(fileSplitter.getNextChunk(), this.peerID, this.peerState).backup(repDeg, peerState.getBackupLog());
            }

            Log("Successfully backed up file: " + filePath);
        } catch (Exception e) {
            if (fileSplitter != null)
                fileSplitter.close();
            if (peerState.getBackupLog().hasFile(fileID)) {
                deleteFile(filePath);
                peerState.getBackupLog().removeFile(fileID);
            }
            ThrowError("Error backing up file: " + filePath + ". Error message: " + e.getMessage());
        }
    }

    @Override
    public void restoreFile(String filePath) throws Exception {
        String fileId = peerState.getBackupLog().getFileID(filePath);
        if (fileId == null) {
            ThrowError("Cannot find backed up instance of " + filePath + ", aborting...");
        }
        File restoredFile = new File(this.peerFolderPath, filePath);
        FileOutputStream outputStream = new FileOutputStream(restoredFile, false);

        for (int chunkNo = 0; ; chunkNo++) {
            try {
                Chunk chunk = new ChunkRestore(new ChunkID(fileId, chunkNo), this.peerID, this.peerState).restoreChunk();
                outputStream.write(chunk.getBytes());

                if(chunk.getBytes().length < Chunk.MAX_CHUNK_SIZE) {
                    outputStream.close();
                    Log("Successfully restored file (" + (chunkNo + 1) + " chunks)");
                    return;
                }
            } catch (Exception e) {
                restoredFile.delete();
                outputStream.close();
                ThrowError("Error restoring file: " + filePath + ". Error message: " + e.getMessage());
            }
        }
    }

    @Override
    public void deleteFile(String fileName) throws Exception {
        String fileID = peerState.getBackupLog().getFileID(fileName);

        if (fileID == null) {
            ThrowError("Did not find " + fileName + " in backup log!");
        }

        peerState.getMCGroups().MCCGroup.sendToGroup(new DeleteMessage(peerState.getVersion(), this.peerID, fileID).toString());

        if(peerState.getVersion().equals(Message.VERSION_ENHANCED))
            peerState.getBackupLog().addToDeletion(fileID);
        else peerState.getBackupLog().removeFile(fileID);
    }

    @Override
    public void reclaimDiskSpace(int spaceKB) {
        List<ChunkID> freedChunks = this.peerState.setCapacity(spaceKB * 1000, this.backupFolderPath);

        for (ChunkID chunkID : freedChunks) {
            Log("Freed chunk with ID: " + chunkID);
            peerState.getMCGroups().MCCGroup.sendToGroup(new RemovedMessage(peerState.getVersion(), this.peerID, chunkID.fileID, chunkID.chunkNo).toString());
        }
    }

    @Override
    public PeerState getInternalState() {
        return peerState;
    }

    public void Log(String message) {
        System.out.println("Peer " + this.peerID + ": " + message);
    }

    public void ThrowError(String message) throws Exception {
        Log(message);
        throw new Exception(message);
    }
}