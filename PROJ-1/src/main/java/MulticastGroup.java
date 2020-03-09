import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastGroup {
    private int port;
    private InetAddress address;
    private MulticastSocket socket;

    MulticastGroup(String address, int port) {
        try {
            this.address = InetAddress.getByName(address);
            this.port = port;
            this.socket = new MulticastSocket(port);
            this.socket.joinGroup(InetAddress.getByName(address));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendToGroup(String content) {
        DatagramPacket packet = new DatagramPacket(content.getBytes(), content.getBytes().length, address, port);
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String receiveFromGroup() {
        DatagramPacket packet = new DatagramPacket(new byte[65535], 65535);
        try {
            this.socket.receive(packet);
            return new String(packet.getData()).trim();
        } catch (IOException e) {
            return "";
        }
    }
}
