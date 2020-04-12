import java.io.PrintStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class TestApp {
    public static void main(String[] args) {
        String hostName = "localhost";
        if (args.length < 2) {
            printUsage();
            return;
        }
        String peerID = args[0];
        String operation = args[1];

        try {
            Registry registry = LocateRegistry.getRegistry(hostName);
            TestAppRemoteInterface stub = (TestAppRemoteInterface) registry.lookup(peerID);

            switch (operation.toUpperCase()) {
                case "BACKUP": stub.backupFile(args[2], Integer.parseInt(args[3])); break;
                case "RESTORE": stub.restoreFile(args[2]); break;
                case "DELETE": stub.deleteFile(args[2]); break;
                case "RECLAIM": stub.reclaimDiskSpace(Integer.parseInt(args[2])); break;
                case "STATE": stub.getInternalState().print(new PrintStream("peer" + peerID + ".log" )); break;
                default: printUsage(); return;
            }
            System.out.println("Successfully completed operation.");
        } catch (ArrayIndexOutOfBoundsException e) {
            printUsage();
        }
        catch (Exception e) {
            System.err.println("Client error: " + e.toString());
        }
    }

    private static void printUsage() {
        System.out.println("Usage: java TestApp <peerID> BACKUP <FILENAME> <REP_DEGREE>");
        System.out.println("Usage: java TestApp <peerID> RESTORE <FILENAME>");
        System.out.println("Usage: java TestApp <peerID> DELETE <FILENAME>");
        System.out.println("Usage: java TestApp <peerID> RECLAIM <DISK_SPACE>");
        System.out.println("Usage: java TestApp <peerID> STATE");
    }
}