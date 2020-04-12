package StateLogging;

import Chunks.ChunkID;

public interface StoredMessageLogger {
    boolean withinDesiredRepDeg(ChunkID chunkID);
}
