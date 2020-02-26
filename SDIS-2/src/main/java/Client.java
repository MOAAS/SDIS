import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Client {
    private static int CLIENT_PORT = 8888;

    public static void main(String[] args) throws IOException {
        //String hostName = "localhost";
        //int serverPort = 6969;
        String message = "lookup gamer.gamer";

        String multicastAddress = "localhost";
        int multicastPort = 1000;

        // join group
        MulticastSocket multicastSocket = new MulticastSocket(multicastPort);
        multicastSocket.joinGroup(InetAddress.getByName(multicastAddress));

        // get ip and port
        DatagramPacket announcementPacket = SuperUtils.makeEmptyPacket();
        multicastSocket.receive(announcementPacket);
        String hostName = parseHostName(SuperUtils.packetToString(announcementPacket));
        int serverPort = parseServerPort(SuperUtils.packetToString(announcementPacket));
        System.out.println(SuperUtils.packetToString(announcementPacket));

        // Send
        byte[] buf = message.getBytes();
        DatagramSocket socket = new DatagramSocket(CLIENT_PORT);
        DatagramPacket messagePacket = new DatagramPacket(buf, buf.length, InetAddress.getByName(hostName), serverPort);
        DatagramPacket responsePacket = SuperUtils.makeEmptyPacket();
        socket.send(messagePacket);

        // get response
        socket.receive(responsePacket);

        System.out.println("Sent message: " + message);
        System.out.println("Got reply: " + SuperUtils.packetToString(responsePacket));
    }

    private static int parseServerPort(String message) {
        return Integer.parseInt(message.split(":")[1]);
    }

    private static String parseHostName(String message) {
        return message.split(":")[0];
    }
}
