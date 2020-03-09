import java.io.IOException;
import java.net.MulticastSocket;

public class MulticastGroup {
    private final String address;
    private final int port;
    private MulticastSocket socket = null;

    MulticastGroup(String address, int port, int localPort) {
        this.address = address;
        this.port = port;
        try {
            this.socket = new MulticastSocket(localPort);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    MulticastSocket join(int localPort) {

    }
}
