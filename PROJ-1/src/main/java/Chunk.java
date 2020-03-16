import java.io.File;

public class Chunk {
    private final String fileID;
    private final int chunkNo;
    private final byte[] bytes;

    public Chunk(String fileID, int chunkNo, byte[] bytes) {
        this.fileID = fileID;
        this.chunkNo = chunkNo;
        this.bytes = bytes;
    }

    public String getDataString() {
        return new String(bytes);
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "fileID=" + fileID +
                ", chunkNo=" + chunkNo +
                ", numBytes=" + bytes.length +
                '}';
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public String getFileID() {
        return fileID;
    }

    public void save(String backupFolder) {
        //File fileFolder = backupFolder + "/" +


    }
}
