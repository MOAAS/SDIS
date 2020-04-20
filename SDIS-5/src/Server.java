import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class Server {
    static HashMap<String, String> DNSTable = new HashMap<>();
    static final String IP_NOT_FOUND = "NOT_FOUND";
    static final String INVALID_MSG = "INVALID_MSG";

    private static SSLServerSocketFactory sf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();


    public static void main(String[] args) throws IOException {
        /* Args */
        int port = 2000;
        String[] cypherSuites = new String[]{"SSL_RSA_WITH_RC4_128_MD5", "SSL_RSA_WITH_RC4_128_SHA"};
        /* -- */

        SSLServerSocket serverSocket = (SSLServerSocket) sf.createServerSocket(port);
        serverSocket.setNeedClientAuth(true);
        serverSocket.setEnabledCipherSuites(sf.getSupportedCipherSuites());

        while (true) {
            SSLSocket socket = (SSLSocket) serverSocket.accept();

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String message = in.readLine();
            System.out.println("Got message: " + message);

            String reply = processMessage(message);
            out.println(reply);
            System.out.println("Sent reply: " + reply);


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
