package Messages;

public class NullMessage extends Message {
    NullMessage(String error) {
        super(error, 0, "");
    }

    @Override
    public void process(MessageProcessor processor) {

    }
}
