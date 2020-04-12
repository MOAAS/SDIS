package StateLogging;

import java.io.Serializable;
import java.util.*;

public class FileBackupEntry implements Serializable {
    private static final long serialVersionUID = 5L;

    private final String pathName;
    private int desiredRepDeg;
    private final int numChunks;
    private Boolean waitingForDeletion = false;

    private final List<Set<Integer>> storers;

    public FileBackupEntry(String pathName, int desiredRepDeg, int numChunks) {
        this.pathName = pathName;
        this.desiredRepDeg = desiredRepDeg;
        this.numChunks = numChunks;
        this.storers = new ArrayList<>();
        for (int i = 0; i < numChunks; i++)
            this.storers.add(new HashSet<>());
    }

    public void addStorer(int chunkNo, int peerID) {
        this.storers.get(chunkNo).add(peerID);
    }

    public void removeStorer(int chunkNo, int peerID) {
        this.storers.get(chunkNo).remove(peerID);
    }

    public void removeStorer(int peerID) {
        for(Set<Integer> set : storers)
            set.remove(peerID);
    }

    public int numStorers(int chunkNo) {
        return this.storers.get(chunkNo).size();
    }

    public int getMaxPerceivedRepDeg(){
        int maxPerceivedRepDeg = 0;
        for(Set<Integer> set : storers)
            maxPerceivedRepDeg = Math.max(maxPerceivedRepDeg,set.size());
        return maxPerceivedRepDeg;
    }

    public int getDesiredRepDeg() {
        return this.desiredRepDeg;
    }

    public String getPathName() {
        return pathName;
    }

    public int getNumChunks() { return numChunks; }


    public void addToDeletion() {
        waitingForDeletion = true;
    }
    public void removeFromDeletion() {
        waitingForDeletion = false;
    }
    public Boolean waitingForDeletion(){
        return waitingForDeletion;
    }

    public void setDesiredDegree(int desiredDegree) {
        this.desiredRepDeg = desiredDegree;
    }
}
