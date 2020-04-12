package Multicast;

import Chunks.Chunk;
import Messages.PutChunkMessage;
import StateLogging.PeerState;
import StateLogging.StoredMessageLogger;

public class ChunkBackup {
    private static final int TIMEOUT_MS = 1000;
    private static final int MAX_TRIES = 5;

    private final Chunk chunk;
    private final int peerID;
    private final PeerState peerState;

    public ChunkBackup(Chunk chunkToBackup, int peerID, PeerState peerState) {
        this.chunk = chunkToBackup;
        this.peerID = peerID;
        this.peerState = peerState;
    }

    public void backup(int repDeg, StoredMessageLogger storedLogger) throws Exception {
        int waitingInterval = TIMEOUT_MS;
        for (int i = 0; i < MAX_TRIES; i++) {
            peerState.getMCGroups().MDBGroup.sendToGroup(new PutChunkMessage(peerState.getVersion(), peerID, repDeg, this.chunk).toString());
            MulticastThread.sleep(waitingInterval);
            if (storedLogger.withinDesiredRepDeg(this.chunk.getChunkID())) {
                Log("Successfully backed up chunk: " + chunk);
                return;
            }
            waitingInterval *= 2;
            Log("Failed to store chunk: Not enough STORED messages received. Retrying in " + waitingInterval / 1000 + " seconds...");
        }
        throw new Exception("Not enough STORED messages received for chunk: " + chunk);
    }

    private void Log(String message) {
        System.out.println("ChunkBackup (Peer " + peerID + "): " + message);
    }
}
