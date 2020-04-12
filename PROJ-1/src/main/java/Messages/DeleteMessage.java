package Messages;

public class DeleteMessage extends Message{
    public DeleteMessage(String version, int senderId, String fileId) {
        super(version, senderId, fileId);
    }

    @Override
    public String toString() {
        return this.version + " DELETE " + this.senderId + " " + this.fileId + " "  + Message.HEADER_ENDING;
    }

    @Override
    public void process(MessageProcessor processor) {
        processor.processDeleteMessage(this);
    }
}
