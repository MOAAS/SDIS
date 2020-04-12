package Messages;

public class ConfirmDeletionMessage extends Message{
    public ConfirmDeletionMessage(String version, int senderId, String fileId) {
        super(version, senderId, fileId);
    }

    @Override
    public String toString() {
        return this.version +  " CONFIRMDELETION " + this.senderId + " " + this.fileId + " "  + Message.HEADER_ENDING;
    }

    @Override
    public void process(MessageProcessor processor) {
        processor.processConfirmDeletionMessage(this);
    }
}