package Multicast;

public class MCGroups {
    public final MulticastGroup MCCGroup;
    public final MulticastGroup MDBGroup;
    public final MulticastGroup MDRGroup;

    public MCGroups(MulticastGroup MCC, MulticastGroup MDB, MulticastGroup MDR) {
        this.MCCGroup = MCC;
        this.MDBGroup = MDB;
        this.MDRGroup = MDR;
    }
}
