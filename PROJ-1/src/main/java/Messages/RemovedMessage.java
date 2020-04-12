package Messages;

import Chunks.ChunkID;

public class RemovedMessage extends Message{
    public final int chunkNo;

    public RemovedMessage(String version, int senderId, String fileId, int chunkNo) {
        super(version, senderId, fileId);
        this.chunkNo = chunkNo;
    }

    @Override
    public String toString() {
        return this.version + " REMOVED " + this.senderId + " " + this.fileId + " " + this.chunkNo + " "  + Message.HEADER_ENDING;
    }

    @Override
    public void process(MessageProcessor processor) {
        processor.processRemovedMessage(this);
    }

    public ChunkID chunkID() {
        return new ChunkID(fileId, chunkNo);
    }
}

