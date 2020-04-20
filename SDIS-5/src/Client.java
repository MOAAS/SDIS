import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    private static int CLIENT_PORT = 8888;

    private static SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();

    public static void main(String[] args) throws IOException {
        /* Args */

        String hostname = "localhost";
        int port = 2000;
        String message = "lookup gamer.gamer";
        String[] cypherSuites = new String[]{"SSL_RSA_WITH_RC4_128_MD5", "SSL_RSA_WITH_RC4_128_SHA"};
        /* -- */

        SSLSocket socket = (SSLSocket) sf.createSocket(InetAddress.getByName(hostname), port);
        socket.setEnabledCipherSuites(sf.getSupportedCipherSuites());

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out.println(message);
        System.out.println("Sent message: " + message);

        String response = in.readLine();
        System.out.println("Got reply: " + response);
    }
}
