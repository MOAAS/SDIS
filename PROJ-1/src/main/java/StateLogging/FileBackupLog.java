package StateLogging;

import Chunks.ChunkID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileBackupLog implements Serializable, StoredMessageLogger {
    private static final long serialVersionUID = 3L;
    private final ConcurrentHashMap<String, FileBackupEntry> fileBackupMap = new ConcurrentHashMap<>();

    public void addFile(String fileID, String pathName, int desiredDegree, int numChunks) {
        if(hasFile(fileID) && fileBackupMap.get(fileID).waitingForDeletion()) {
            fileBackupMap.get(fileID).removeFromDeletion();
            fileBackupMap.get(fileID).setDesiredDegree(desiredDegree);
        }
        else this.fileBackupMap.put(fileID, new FileBackupEntry(pathName, desiredDegree, numChunks));
    }

    public void removeFile(String fileID) {
        fileBackupMap.remove(fileID);
    }

    public boolean hasFile(String fileId) {
        return fileBackupMap.containsKey(fileId);
    }

    public void addStorer(ChunkID chunkID, int peerID) {
        fileBackupMap.get(chunkID.fileID).addStorer(chunkID.chunkNo, peerID);
    }

    public void removeStorer(ChunkID chunkID, int peerID) {
        fileBackupMap.get(chunkID.fileID).removeStorer(chunkID.chunkNo, peerID);
    }

    public void removeStorer(String fileId,int peerId){
        fileBackupMap.get(fileId).removeStorer(peerId);
        if(fileBackupMap.get(fileId).getMaxPerceivedRepDeg() == 0)
            removeFile(fileId);
    }

    public void addToDeletion(String fileId){
        fileBackupMap.get(fileId).addToDeletion();
    }

    public ArrayList<String> filesWaitingForDeletion(){
        ArrayList<String> files = new ArrayList<>();
        for (ConcurrentHashMap.Entry<String, FileBackupEntry> entry : fileBackupMap.entrySet()){
            if(entry.getValue().waitingForDeletion())
                files.add(entry.getKey());
        }
        return files;
    }

    public String getFileID(String fileName) {
        for (Map.Entry<String, FileBackupEntry> entry : fileBackupMap.entrySet()) {
            if (entry.getValue().getPathName().equalsIgnoreCase(fileName))
                return entry.getKey();
        }
        return null;
    }

    @Override
    public boolean withinDesiredRepDeg(ChunkID chunkID) {
        FileBackupEntry chunkEntry = this.fileBackupMap.get(chunkID.fileID);
        return chunkEntry.numStorers(chunkID.chunkNo) >= chunkEntry.getDesiredRepDeg();
    }

    public ConcurrentHashMap<String, FileBackupEntry> getFileBackupMap(){
        return fileBackupMap;
    }


}
