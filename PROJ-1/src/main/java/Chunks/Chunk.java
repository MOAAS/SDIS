package Chunks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Chunk {
    public static final int MAX_CHUNK_SIZE = 64000;

    private final ChunkID chunkID;
    private final byte[] bytes;

    public Chunk(String fileID, int chunkNo, byte[] bytes) {
        this.chunkID = new ChunkID(fileID, chunkNo);
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public int size() {
        return bytes.length;
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "fileID=" + this.getFileID() +
                ", chunkNo=" + this.getChunkNo() +
                ", numBytes=" + this.bytes.length +
                '}';
    }

    public int getChunkNo() {
        return this.getChunkID().chunkNo;
    }

    public String getFileID() {
        return this.getChunkID().fileID;
    }

    public ChunkID getChunkID() {
        return this.chunkID;
    }

    public void save(String backupFolderPath) throws IOException {
        File fileFolder = new File(backupFolderPath + "/" + this.getFileID());
        if (!fileFolder.exists()) {
            fileFolder.mkdir();
        }

        File chunkFile = new File(backupFolderPath, this.getFileID() + "/" + this.getChunkNo());
        FileOutputStream outputStream = new FileOutputStream(chunkFile, false);
        outputStream.write(this.bytes);
        outputStream.close();
    }
}
