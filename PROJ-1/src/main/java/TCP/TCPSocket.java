package TCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TCPSocket {
    public static String readAny(int port, int maxSize) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(1000);
        Socket clientSocket = serverSocket.accept();
        DataInputStream in = new DataInputStream(clientSocket.getInputStream());

        byte[] message = new byte[maxSize];
        int totalBytes = 0;
        while (totalBytes < maxSize) {
            int read = in.read(message, totalBytes, maxSize - totalBytes);
            if (read < 0)
                break;
            totalBytes += read;
        }

        serverSocket.close();
        clientSocket.close();
        return new String(Arrays.copyOfRange(message, 0, totalBytes), StandardCharsets.ISO_8859_1);
    }

    public static void send(String address, int port, String content) throws IOException {
        Socket socket = new Socket(address, port);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.write(content.getBytes(StandardCharsets.ISO_8859_1), 0, content.getBytes(StandardCharsets.ISO_8859_1).length);
        out.close();
    }
}
