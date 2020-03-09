import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Client {
    private static int CLIENT_PORT = 8888;

    public static void main(String[] args) throws IOException, InterruptedException {
        MulticastSocket MCCSocket = SuperUtils.joinMulticastGroup("224.0.0.0", 1000);
        MulticastSocket MDBSocket = SuperUtils.joinMulticastGroup("225.0.0.0", 1000);
        MulticastSocket MDRSocket = SuperUtils.joinMulticastGroup("226.0.0.0", 1000);


        while (true) {


            double num = Math.random();
            System.out.println(num);
            if (num < 0.5) {
                SuperUtils.sendToMulticast(MCCSocket, "Hello gamer");
            }
            else {
                DatagramPacket packet = SuperUtils.makeEmptyPacket();
                MCCSocket.receive(packet);

                System.out.println("LMAO " + SuperUtils.packetToString(packet));
            }
            Thread.sleep(1000);
        }

        /*

        // get ip and port
        DatagramPacket announcementPacket = SuperUtils.makeEmptyPacket();
        multicastSocket.receive(announcementPacket);
        String hostName = parseHostName(SuperUtils.packetToString(announcementPacket));
        int serverPort = parseServerPort(SuperUtils.packetToString(announcementPacket));
        System.out.println("Received announcement: " + SuperUtils.packetToString(announcementPacket));

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

         */
    }

    private static int parseServerPort(String message) {
        return Integer.parseInt(message.split(":")[1]);
    }

    private static String parseHostName(String message) {
        return message.split(":")[0];
    }
}
