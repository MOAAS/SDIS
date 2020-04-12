package Messages;

import Chunks.Chunk;
import Chunks.ChunkID;

public class StoredMessage  extends Message{
    public final int chunkNo;


    public StoredMessage(String version, int senderId, String fileId, int chunkNo) {
        super(version, senderId, fileId);
        this.chunkNo = chunkNo;
    }

    public StoredMessage(String version, int senderId, Chunk chunk) {
        this(version, senderId, chunk.getFileID(), chunk.getChunkNo());
    }

    @Override
    public String toString() {
        return this.version + " STORED " + this.senderId + " " + this.fileId + " " + this.chunkNo + " "  + Message.HEADER_ENDING;
    }

    @Override
    public void process(MessageProcessor processor) {
        processor.processStoredMessage(this);
    }

    public ChunkID chunkID() {
        return new ChunkID(fileId, chunkNo);
    }
}
