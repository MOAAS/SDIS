import java.net.DatagramPacket;
import java.util.Arrays;

public class SuperUtils {
    static String packetToString(DatagramPacket packet) {
        return  new String(packet.getData()).trim();
    }

    static DatagramPacket makeEmptyPacket() {
        return new DatagramPacket(new byte[65535], 65535);
    }

    public static void clearPacket(DatagramPacket messagePacket) {
        Arrays.fill(messagePacket.getData(), (byte) 0);
    }
}
