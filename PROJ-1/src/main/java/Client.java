import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private static int CLIENT_PORT = 8888;

    private static String BACKUP_FOLDER = "files/backup1";

    static void showChunkListStats(String name, List<Chunk> chunks) {
        System.out.println("--- Stats for " + name + " chunks ---");

        System.out.println("Num chunks: " + chunks.size());

        for (int i = 0; i < chunks.size(); i++)
            System.out.println("chunk " + i + ": " + chunks.get(i).toString());
        System.out.println("--- --- --- ---");

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        MulticastGroup MCCGroup = new MulticastGroup("224.0.0.0", 1000);
        MulticastGroup MDBGroup = new MulticastGroup("225.0.0.0", 1100);
        MulticastGroup MDRGroup = new MulticastGroup("226.0.0.0", 1200);


        List<Chunk> chunks1 = new SplitFile("files/file1.txt").split();
        List<Chunk> chunks2 = new SplitFile("files/file2.txt").split();
        List<Chunk> chunks3 = new SplitFile("files/shrek.mp3").split();

        showChunkListStats("file1", chunks1);
        showChunkListStats("file2", chunks2);
        showChunkListStats("shrek", chunks3);

        chunks1.get(0).save(BACKUP_FOLDER);
        chunks1.get(2).save(BACKUP_FOLDER);

        chunks2.get(1).save(BACKUP_FOLDER);

        chunks3.get(0).save(BACKUP_FOLDER);
        chunks3.get(1).save(BACKUP_FOLDER);
        chunks3.get(2).save(BACKUP_FOLDER);
        chunks3.get(3).save(BACKUP_FOLDER);
        chunks3.get(63).save(BACKUP_FOLDER);



        /*
        while (true) {
            double num = Math.random();
            if (num < 0.8) {
                System.out.println("Sending message... " + num);
                MCCGroup.sendToGroup(Double.toString(num));
            }
            else {
                String message = MCCGroup.receiveFromGroup();
                System.out.println("Received message: " + message);
            }
            Thread.sleep(1000);

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
