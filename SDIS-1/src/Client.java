import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    private static int CLIENT_PORT = 8888;

    public static void main(String[] args) throws IOException {
        String hostName = "localhost";
        int serverPort = 6969;
        String message = "lookup gamer.gamer";


        byte[] buf = message.getBytes();

        DatagramSocket socket = new DatagramSocket(CLIENT_PORT);
        DatagramPacket messagePacket = new DatagramPacket(buf, buf.length, InetAddress.getByName(hostName), serverPort);
        DatagramPacket responsePacket = SuperUtils.makeEmptyPacket();


        socket.send(messagePacket);
        socket.receive(responsePacket);

        System.out.println("Sent message: " + message);
        System.out.println("Got reply: " + SuperUtils.packetToString(responsePacket));
    }
}
