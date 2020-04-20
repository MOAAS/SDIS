import java.rmi.Remote;
import java.rmi.RemoteException;


public interface DNSRemoteInterface extends Remote {
    String lookupIP(String DNS) throws RemoteException;
    int registerIP(String DNS, String IP) throws RemoteException;

}