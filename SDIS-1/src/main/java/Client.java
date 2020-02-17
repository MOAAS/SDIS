import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello gamer!");

        byte[] buf = "Hello gamer!".getBytes();

        DatagramSocket socket = new DatagramSocket(8888);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, InetAddress.getByName("localhost"), 6969);

        socket.send(packet);
    }
}
