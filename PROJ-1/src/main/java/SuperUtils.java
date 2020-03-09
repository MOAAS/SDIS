import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Arrays;

public class SuperUtils {
    public static String packetToString(DatagramPacket packet) {
        return  new String(packet.getData()).trim();
    }
    public static DatagramPacket makeEmptyPacket() {
        return new DatagramPacket(new byte[65535], 65535);
    }
    public static void clearPacket(DatagramPacket messagePacket) {
        Arrays.fill(messagePacket.getData(), (byte) 0);
    }

    public static void makePacket(MulticastSocket socket, String content) {
        try {
            System.out.println(content.getBytes());
            System.out.println(content.getBytes().length);
            System.out.print(socket.joinGroup();
            DatagramPacket pakcet = new DatagramPacket(content.getBytes(), content.getBytes().length, socket.getInetAddress(), socket.getLocalPort());
            socket.send(pakcet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MulticastSocket joinMulticastGroup(String address, int localPort) {
        MulticastSocket multicastSocket = null;
        try {
            multicastSocket = new MulticastSocket(localPort);
            multicastSocket.joinGroup(InetAddress.getByName(address));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return multicastSocket;
    }
}
