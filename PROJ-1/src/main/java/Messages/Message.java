package Messages;

import java.nio.charset.StandardCharsets;

public abstract class Message {
    public static final String VERSION_VANILLA = "1.0";
    public static final String VERSION_ENHANCED = "1.1";

    private final static char CR  = (char) 0x0D;
    private final static char LF  = (char) 0x0A;
    private final static String CRLF  = "" + CR + LF;
    public final static String HEADER_ENDING  = CRLF + CRLF;

    public final int senderId;
    public final String fileId;
    public final String version;

    Message(String version, int senderId, String fileId){
        this.version = version;
        this.senderId = senderId;
        this.fileId = fileId;
    }

    public static String toString(byte[] bytes) {
        return new String(bytes, StandardCharsets.ISO_8859_1);
    }

    public static byte[] toBytes(String string) {
        return string.getBytes(StandardCharsets.ISO_8859_1);
    }

    public abstract void process(MessageProcessor processor);
}
