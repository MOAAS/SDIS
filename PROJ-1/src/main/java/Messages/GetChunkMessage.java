package Messages;

import Chunks.ChunkID;

public class GetChunkMessage extends Message{
    public final int chunkNo;

    public final String hostname;
    public final int port;

    private final String body;

    public GetChunkMessage(String version, int senderId, String fileId, int chunkNo) {
        super(version, senderId, fileId);
        this.chunkNo = chunkNo;
        this.hostname = "";
        this.port = 0;
        this.body = "";
    }

    public GetChunkMessage(String version, int senderId, String fileId, int chunkNo, String hostname, int port) {
        super(version, senderId, fileId);
        this.chunkNo = chunkNo;
        this.hostname = hostname;
        this.port = port;
        this.body = this.hostname + ":" + this.port;
    }

    @Override
    public String toString() {
        return this.version + " GETCHUNK " + this.senderId + " " + this.fileId + " " + this.chunkNo + " "  + Message.HEADER_ENDING + this.body;
    }

    @Override
    public void process(MessageProcessor processor) {
        processor.processGetChunkMessage(this);
    }

    public ChunkID chunkID() {
        return new ChunkID(fileId, chunkNo);
    }
}
