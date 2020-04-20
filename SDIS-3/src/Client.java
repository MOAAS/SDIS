import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    private static int CLIENT_PORT = 8888;

    public static void main(String[] args) throws IOException {
        String hostName = "localhost";
        int serverPort = 6969;

        try {
            Registry registry = LocateRegistry.getRegistry(hostName);
            DNSRemoteInterface stub = (DNSRemoteInterface) registry.lookup("DNSRemoteInterface");

            System.out.println(stub.registerIP("gamer.gamer", "192.168.1.2")); // 1

            System.out.println(stub.lookupIP("gamer.gamer")); // ....2
            System.out.println(stub.lookupIP("gamer.gamer.gamer")); // NOT_FOUND

            System.out.println(stub.registerIP("gamer.gamer.gamer", "192.168.1.3")); // 2

            System.out.println(stub.lookupIP("gamer.gamer.gamer")); // ....3

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
