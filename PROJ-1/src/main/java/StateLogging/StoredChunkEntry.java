package StateLogging;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class StoredChunkEntry implements Serializable, Comparable<StoredChunkEntry> {
    private static final long serialVersionUID = 4L;

    private final int desiredRepDeg;
    private final int chunkSize;
    private final Set<Integer> storers;

    public StoredChunkEntry(int desiredRepDeg, int chunkSize) {
        this.desiredRepDeg = desiredRepDeg;
        this.chunkSize = chunkSize;
        this.storers = new HashSet<>();
    }

    public void addStorer(int peerID) {
        storers.add(peerID);
    }

    public void removeStorer(int peerID) {
        storers.remove(peerID);
    }

    public int numStorers() {
        return storers.size();
    }

    public int getDesiredRepDeg() {
        return desiredRepDeg;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    @Override
    public int compareTo(StoredChunkEntry e2) {
        int e1surplus = (this.numStorers() - this.getDesiredRepDeg());
        int e2surplus = (e2.numStorers() - e2.getDesiredRepDeg());
        if (e1surplus == e2surplus) {
            if (this.getChunkSize() == e2.getChunkSize())
                return this.hashCode() - e2.hashCode();
            return e2.getChunkSize() - this.getChunkSize();
        }
        return e2surplus - e1surplus;
    }
}
