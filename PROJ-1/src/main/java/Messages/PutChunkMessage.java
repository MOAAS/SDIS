package Messages;

import Chunks.Chunk;
import Chunks.ChunkID;

public class PutChunkMessage extends Message {
    private final byte[] body;

    public final int repDeg;
    public final int chunkNo;

    public PutChunkMessage(String version, int senderId, int repDeg, Chunk chunk) {
        super(version, senderId, chunk.getFileID());
        this.chunkNo = chunk.getChunkNo();
        this.repDeg = repDeg;
        this.body = chunk.getBytes();
    }

    @Override
    public String toString() {
        return this.version + " PUTCHUNK " + this.senderId + " " + this.fileId + " " + this.chunkNo + " " + this.repDeg + " " + Message.HEADER_ENDING + Message.toString(body);
    }

    public Chunk getChunk() {
        return new Chunk(this.fileId, this.chunkNo, this.body);
    }

    @Override
    public void process(MessageProcessor processor) {
        processor.processPutChunkMessage(this);
    }

    public ChunkID chunkID() {
        return new ChunkID(fileId, chunkNo);
    }
}
