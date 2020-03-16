import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class SplitFile {
    private static final int CHUNK_SIZE = 64000;

    private final String fileID;
    private final File file;

    public SplitFile(String filePath) {
        this.file = new File(filePath);

        this.fileID = this.createID();
    }

    public List<Chunk> split() throws IOException {
        FileInputStream inputStream = new FileInputStream(file);

        List<Chunk> chunks = new ArrayList<>();

        int fileSizeBytes = (int)(file.length());
        int numFullChunks = fileSizeBytes / CHUNK_SIZE;

        for (int i = 0; i < numFullChunks; i++) {
            chunks.add(this.readChunk(inputStream, CHUNK_SIZE, i));
        }

        chunks.add(this.readChunk(inputStream, fileSizeBytes % CHUNK_SIZE, numFullChunks));

        inputStream.close();
        return chunks;
    }

    public String getFileID() {
        return this.fileID;
    }

    private Chunk readChunk(FileInputStream inputStream, int size, int chunkNo) throws IOException {
        byte[] data = new byte[size];
        inputStream.read(data, 0, size);
        return new Chunk(this.fileID, chunkNo, data);
    }

    private String createID() {
        try {
            String input = file.getAbsolutePath() + file.lastModified();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printHexBinary(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
