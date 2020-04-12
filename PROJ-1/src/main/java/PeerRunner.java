import Messages.Message;
import Multicast.MCGroups;
import Multicast.MulticastGroup;

public class PeerRunner {
    public static void main(String[] args) {
        if (args.length != 9) {
            printUsage();
            return;
        }
        String protocolVersion = args[0];
        String accessPoint = args[2];
        String MCAddress = args[3];
        String MDBAddress = args[5];
        String MDRAddress = args[7];
        int peerID, MCPort, MDBPort, MDRPort;

        if (!protocolVersion.equals(Message.VERSION_VANILLA) && !protocolVersion.equals(Message.VERSION_ENHANCED)) {
            System.out.println("Invalid protocol version: Must be " + Message.VERSION_VANILLA + " or " + Message.VERSION_ENHANCED);
        }
        try {
            peerID = Integer.parseInt(args[1]);
            MCPort = Integer.parseInt(args[4]);
            MDBPort = Integer.parseInt(args[6]);
            MDRPort = Integer.parseInt(args[8]);
            if (peerID < 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Invalid peerID -> must be an integer");
            return;
        }
        new Peer(protocolVersion, peerID, accessPoint, new MCGroups(new MulticastGroup(MCAddress, MCPort), new MulticastGroup(MDBAddress, MDBPort), new MulticastGroup(MDRAddress, MDRPort))).run();
    }

    private static void printUsage() {
        System.out.println("Usage: java PeerRunner <version> <peerID> <accessPoint> <MCAddress> <MCPort> <MDBAddress> <MDBPort> <MDRAddress> <MDRPort>");
    }
}
