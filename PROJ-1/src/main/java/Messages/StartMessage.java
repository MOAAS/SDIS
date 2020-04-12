package Messages;

public class StartMessage extends Message{
    public StartMessage(String version, int senderId) {
        super(version, senderId, null);
    }

    @Override
    public String toString() {
        return this.version + " START " + this.senderId + " " + "?" + Message.HEADER_ENDING;
    }

    @Override
    public void process(MessageProcessor processor) {
        processor.processStartMessage();
    }
}
