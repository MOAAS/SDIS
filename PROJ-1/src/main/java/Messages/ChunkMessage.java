package Messages;

import Chunks.Chunk;
import Chunks.ChunkID;

public class ChunkMessage extends Message{
    public final int chunkNo;
    public final byte[] body;

    public ChunkMessage(String version, int senderId, Chunk chunk) {
        super(version, senderId, chunk.getFileID());
        this.chunkNo = chunk.getChunkNo();
        this.body = chunk.getBytes();

    }

    public static ChunkMessage MakeNull() {
        return new ChunkMessage(null, 0, new Chunk("", 0, new byte[0]));
    }

    public boolean isNull() {
        return this.version == null;
    }

    @Override
    public String toString() {
        return this.version + " CHUNK " + this.senderId + " " + this.fileId + " " + this.chunkNo  + " " + Message.HEADER_ENDING + Message.toString(body);
    }

    @Override
    public void process(MessageProcessor processor) {
        processor.processChunkMessage(this);
    }

    public ChunkID chunkID() {
        return new ChunkID(fileId, chunkNo);
    }

    public Chunk getChunk() {
        return new Chunk(this.fileId, this.chunkNo, this.body);
    }
}

