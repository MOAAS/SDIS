package Messages;

public interface MessageProcessor {
    void processChunkMessage(ChunkMessage message);
    void processDeleteMessage(DeleteMessage message);
    void processConfirmDeletionMessage(ConfirmDeletionMessage message);
    void processGetChunkMessage(GetChunkMessage message);
    void processPutChunkMessage(PutChunkMessage message);
    void processRemovedMessage(RemovedMessage message);
    void processStoredMessage(StoredMessage message);
    void processStartMessage();
}
