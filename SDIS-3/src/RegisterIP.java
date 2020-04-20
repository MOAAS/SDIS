import java.rmi.Remote;
import java.rmi.RemoteException;


public interface RegisterIP extends Remote {
    int registerIP(String DNS, String IP) throws RemoteException;
}
