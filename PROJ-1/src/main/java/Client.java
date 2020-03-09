import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Client {
    private static int CLIENT_PORT = 8888;

    public static void main(String[] args) throws IOException, InterruptedException {
        MulticastGroup MCCGroup = new MulticastGroup("224.0.0.0", 1000);
        MulticastGroup MDBGroup = new MulticastGroup("225.0.0.0", 1100);
        MulticastGroup MDRGroup = new MulticastGroup("226.0.0.0", 1200);
        while (true) {
            if (Math.random() < 0.8) {
                System.out.println("Sending message...");
                MCCGroup.sendToGroup("Hello");
            }
            else {
                String message = MCCGroup.receiveFromGroup();
                System.out.println("Received message: " + message);
            }
            Thread.sleep(1000);


            File epic;
            // String[] chunks;
            // faz mensagem
            // envia envia envia

            String chunkinteirodoficheiro;
            // guardar focv



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
