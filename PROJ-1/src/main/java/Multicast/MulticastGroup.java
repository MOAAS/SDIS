package Multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class MulticastGroup {
    private int port;
    private InetAddress address;
    private MulticastSocket socket;

    public MulticastGroup(String address, int port) {
        try {
            this.address = InetAddress.getByName(address);
            this.port = port;
            this.socket = new MulticastSocket(port);
            this.socket.joinGroup(InetAddress.getByName(address));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToGroup(String content) {
        DatagramPacket packet = new DatagramPacket(content.getBytes(StandardCharsets.ISO_8859_1), content.getBytes().length, address, port);
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveFromGroup() {
        DatagramPacket packet = new DatagramPacket(new byte[65535], 65535);
        try {
            this.socket.receive(packet);
            return new String(packet.getData(), StandardCharsets.ISO_8859_1).substring(0, packet.getLength());
        } catch (IOException e) {
            return "";
        }
    }
}
