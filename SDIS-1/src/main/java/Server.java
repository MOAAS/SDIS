import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class Server {
    static HashMap<String, String> DNSTable = new HashMap<>();
    static final String IP_NOT_FOUND = "NOT_FOUND";

    public static void main(String[] args) throws IOException {
        DatagramPacket packet = Server.makeEmptyPacket();
        DatagramSocket socket = new DatagramSocket(6969, InetAddress.getByName("localhost"));

        while (loopCondition()) {
            socket.receive(packet);
            SuperUtils.printPacket(packet);

        }



    }

    static DatagramPacket makeEmptyPacket() {
        return new DatagramPacket(new byte[65535], 65535);
    }

    static boolean loopCondition() {
        return true;
    }

    static int registerIP(String DNS, String IP) {
        if (lookupIP(DNS).equals(IP_NOT_FOUND)) {
            DNSTable.put(DNS, IP);
            return DNSTable.size();
        }
        else return -1;
    }

    static String lookupIP(String DNS) {
        String IP = DNSTable.get(DNS);
        if (IP == null)
            return IP_NOT_FOUND;
        return IP;
    }
}
