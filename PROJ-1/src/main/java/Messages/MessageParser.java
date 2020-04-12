package Messages;

import Chunks.Chunk;

public class MessageParser {
    public static Message parseMessage(String message){
        //System.out.println(message);
        String[] headerTokens = message.split(Message.HEADER_ENDING, 2)[0].split(" ");
        String version, fileId, type, body;
        int chunkNo, repDeg, senderId;

        try {
            version = headerTokens[0];
            senderId = Integer.parseInt(headerTokens[2]);
            type = headerTokens[1];
            fileId = headerTokens[3];

            if (!version.equals(Message.VERSION_VANILLA) && !version.equals(Message.VERSION_ENHANCED))
                return new NullMessage("INVALID VERSION: " + version);
        } catch (NumberFormatException e) {
            return new NullMessage("INVALID SENDER ID: " + headerTokens[2]);
        }

        switch (type){
            case "PUTCHUNK":
                chunkNo = Integer.parseInt(headerTokens[4]);
                repDeg = Integer.parseInt(headerTokens[5]);
                body = message.split(Message.HEADER_ENDING, 2)[1];
                return new PutChunkMessage(version, senderId, repDeg, new Chunk(fileId, chunkNo, Message.toBytes(body)));
            case "STORED":
                chunkNo = Integer.parseInt(headerTokens[4]);
                return new StoredMessage(version, senderId,fileId, chunkNo);
            case "GETCHUNK":
                chunkNo = Integer.parseInt(headerTokens[4]);
                if (version.equals(Message.VERSION_ENHANCED)) {
                    String[] address = message.split(Message.HEADER_ENDING, 2)[1].split(":");
                    return new GetChunkMessage(version, senderId, fileId, chunkNo, address[0], Integer.parseInt(address[1]));
                }
                return new GetChunkMessage(version, senderId,fileId,chunkNo);
            case "CHUNK":
                chunkNo = Integer.parseInt(headerTokens[4]);
                body = message.split(Message.HEADER_ENDING, 2)[1];
                return new ChunkMessage(version, senderId, new Chunk(fileId, chunkNo, Message.toBytes(body)));
            case "DELETE":
                return new DeleteMessage(version, senderId,fileId);
            case "CONFIRMDELETION":
                return new ConfirmDeletionMessage(version, senderId,fileId);
            case "REMOVED":
                chunkNo = Integer.parseInt(headerTokens[4]);
                return new RemovedMessage(version, senderId,fileId,chunkNo);
            case "START":
                return new StartMessage(version, senderId);
            default: return new NullMessage("INVALID MESSAGE TYPE: " + type);
        }
    }

}
