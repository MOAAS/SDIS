package StateLogging;

import Chunks.Chunk;
import Chunks.ChunkID;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StoredChunkLog implements Serializable, StoredMessageLogger {
    private static final long serialVersionUID = 2L;
    private final ConcurrentHashMap<ChunkID, StoredChunkEntry> chunkStoredMap = new ConcurrentHashMap<>();

    public Collection<StoredChunkEntry> getEntries() {
        return this.chunkStoredMap.values();
    }

    public int getDesiredRepDeg(ChunkID chunkID) {
        return this.chunkStoredMap.get(chunkID).getDesiredRepDeg();
    }

    public boolean withinDesiredRepDeg(ChunkID chunkID) {
        StoredChunkEntry chunkEntry =  chunkStoredMap.get(chunkID);
        return chunkEntry.numStorers() >= chunkEntry.getDesiredRepDeg();
    }

    public void addChunk(ChunkID chunkID, int desiredRepDeg, int chunkSize) {
        chunkStoredMap.put(chunkID, new StoredChunkEntry(desiredRepDeg, chunkSize));
    }

    public void addStorer(ChunkID chunkID, int peerID) {
        chunkStoredMap.get(chunkID).addStorer(peerID);
    }

    public boolean hasChunk(ChunkID chunkID) {
        return chunkStoredMap.containsKey(chunkID);
    }

    public void removeChunk(ChunkID chunkID) {
        this.chunkStoredMap.remove(chunkID);
    }

    public void removeFileRecords(String fileID) {
        Set<ChunkID> foundKeys = new HashSet<>();
        for (ChunkID key : chunkStoredMap.keySet()) {
            if (key.fileID.equals(fileID))
                foundKeys.add(key);
        }
        chunkStoredMap.keySet().removeAll(foundKeys);
    }

    public boolean hasFile(String fileID) {
        for (ChunkID key : chunkStoredMap.keySet()) {
            if (key.fileID.equals(fileID))
                return true;
        }
        return false;
    }

    public void removeChunkStorer(ChunkID chunkID, int peerID) {
        chunkStoredMap.get(chunkID).removeStorer(peerID);
    }

    public Chunk getChunk(ChunkID chunkID, String backupFolderPath) {
        String chunkPath = backupFolderPath + '/' + chunkID.fileID + '/' + chunkID.chunkNo;
        try {
            return new Chunk(chunkID.fileID, chunkID.chunkNo, Files.readAllBytes(Paths.get(chunkPath)));
        } catch (IOException e) {
            return null;
        }
    }

    public void deleteStoredChunk(ChunkID chunkID, String backupFolderPath) {
        new File(backupFolderPath, chunkID.fileID + '/' + chunkID.chunkNo).delete();
        this.removeChunk(chunkID);
    }

    public List<ChunkID> freeEntireSpace(String backupFolderPath) {
        List<ChunkID> freedChunkIDs = new ArrayList<>();
        for (Map.Entry<ChunkID, StoredChunkEntry> entry : this.chunkStoredMap.entrySet()) {
            ChunkID chunkID = entry.getKey();
            freedChunkIDs.add(chunkID);
            this.deleteStoredChunk(chunkID, backupFolderPath);
        }
        return freedChunkIDs;
    }

    public List<ChunkID> freeChunkSpace(int toFree, String backupFolderPath) {
        if (toFree <= 0)
            return new ArrayList<>();
        Map<StoredChunkEntry, ChunkID> orderedStorage = new TreeMap<>();
        for (Map.Entry<ChunkID, StoredChunkEntry> entry : this.chunkStoredMap.entrySet()) {
            orderedStorage.put(entry.getValue(), entry.getKey());
        }
        int freedCapacity = 0;
        List<ChunkID> freedChunkIDs = new ArrayList<>();
        for (Map.Entry<StoredChunkEntry, ChunkID> entry : orderedStorage.entrySet()) {
            ChunkID chunkID = entry.getValue();
            freedCapacity += entry.getKey().getChunkSize();
            freedChunkIDs.add(chunkID);
            this.deleteStoredChunk(chunkID, backupFolderPath);

            if (freedCapacity >= toFree)
                break;
        }

        return freedChunkIDs;
    }
        
    public ConcurrentHashMap<ChunkID, StoredChunkEntry> getChunkStoredMap(){
        return chunkStoredMap;
    }
}
