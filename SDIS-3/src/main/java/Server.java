import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class Server implements DNSRemoteInterface {
    static HashMap<String, String> DNSTable = new HashMap<>();
    static final String IP_NOT_FOUND = "NOT_FOUND";

    public static void main(String[] args) {
        try {
            Server obj = new Server();
            DNSRemoteInterface stub = (DNSRemoteInterface) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("DNSRemoteInterface", stub);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public int registerIP(String DNS, String IP) {
        if (lookupIP(DNS).equals(IP_NOT_FOUND)) {
            DNSTable.put(DNS, IP);
            return DNSTable.size();
        }
        else return -1;
    }

    public String lookupIP(String DNS) {
        String IP = DNSTable.get(DNS);
        if (IP == null)
            return IP_NOT_FOUND;
        return IP;
    }
}
