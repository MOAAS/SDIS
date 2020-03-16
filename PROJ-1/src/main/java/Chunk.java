import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

    public void save(String backupFolderPath) {
        File fileFolder = new File(backupFolderPath + "/" + this.fileID);
        if (!fileFolder.exists()) {
            fileFolder.mkdir();
        }

        try {
            File chunkFile = new File(fileFolder.getPath() + "/" + this.chunkNo);

            chunkFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(chunkFile, false);
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
