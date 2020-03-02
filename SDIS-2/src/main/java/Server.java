import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;

public class Server {
    static HashMap<String, String> DNSTable = new HashMap<>();
    static final String IP_NOT_FOUND = "NOT_FOUND";
    static final String INVALID_MSG = "INVALID_MSG";

    public static void main(String[] args) throws IOException {
        // ** Args ** //
        int serverPort = 6969;
        String multicastAddress = "224.0.0.0";
        int multicastPort = 1000;
        // **  ** //

        Thread announcementThread =  new Thread(() -> {
            try {
                InetAddress serverIP = InetAddress.getLocalHost();
                String announcement = serverIP.getHostAddress() + ":" + serverPort;
                while (true) {
                    MulticastSocket multicastSocket = new MulticastSocket(multicastPort);
                    DatagramPacket announcementPacket = new DatagramPacket(announcement.getBytes(), announcement.getBytes().length, InetAddress.getByName(multicastAddress), multicastPort);
                    multicastSocket.send(announcementPacket);

                    System.out.println("Sent announcement: " + announcement);
                    Thread.sleep(5000);
                }
            }
            catch (IOException | InterruptedException e) {
                System.err.println("Error: " + e.getMessage());
            }
        });
        announcementThread.start();


        DatagramPacket messagePacket = SuperUtils.makeEmptyPacket();
        DatagramSocket socket = new DatagramSocket(serverPort);
        while (true) {

            SuperUtils.clearPacket(messagePacket);
            socket.receive(messagePacket);
            String message = SuperUtils.packetToString(messagePacket);
            String response = processMessage(message);

            DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, messagePacket.getAddress(), messagePacket.getPort());
            socket.send(responsePacket);

            System.out.println("Got message: \"" + message + "\" --> " + response);
          //  System.out.println("MESSAGE: " + message);
          //  System.out.println("RESPONSE: " + response);
          //  System.out.println("Addr: " + messagePacket.getAddress());
          //  System.out.println("Port: " + messagePacket.getPort());

        }



    }

    private static String processMessage(String message) {
        String[] words = message.toUpperCase().split(" ");
        if (words.length == 0)
            return INVALID_MSG;
        switch (words[0]) {
            case "REGISTER": return processRegister(words);
            case "LOOKUP": return processLookup(words);
            default: return INVALID_MSG;
        }
    }

    private static String processRegister(String[] words) {
        if (words.length != 3)
            return INVALID_MSG;
        //ValidIpAddressRegex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
        //ValidHostnameRegex = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\-]*[A-Za-z0-9])$";
        return Integer.toString(registerIP(words[1], words[2]));
    }

    private static String processLookup(String[] words) {
        if (words.length != 2)
            return INVALID_MSG;
        return lookupIP(words[1]);
    }

    private static int registerIP(String DNS, String IP) {
        if (lookupIP(DNS).equals(IP_NOT_FOUND)) {
            DNSTable.put(DNS, IP);
            return DNSTable.size();
        }
        else return -1;
    }

    private static String lookupIP(String DNS) {
        String IP = DNSTable.get(DNS);
        if (IP == null)
            return IP_NOT_FOUND;
        return IP;
    }
}
