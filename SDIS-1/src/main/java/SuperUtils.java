import java.net.DatagramPacket;

public class SuperUtils {
    static void printPacket(DatagramPacket packet) {
        System.out.println(new String(packet.getData()).trim());
    }
}
