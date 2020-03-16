public class MessageMaker {
    public static final String VERSION = "0.1";
    public final static char CR  = (char) 0x0D;
    public final static char LF  = (char) 0x0A;
    public final static String CRLF  = "" + CR + LF;


    public String makePUTCHUNKmessage(int senderID,int fileID, int chunkNo, int repDeg, String body){
        String message = VERSION + " PUTCHUNK " + senderID + " " + fileID + " " + chunkNo + " " + repDeg + " " + CRLF + CRLF + body;
        return message;
    }
    public String makeSTOREDmessage(int senderID,int fileID, int chunkNo, int repDeg){
        String message = VERSION + " STORED " + senderID + " " + fileID + " " + chunkNo + " " + CRLF + CRLF;
        return message;
    }
    public String makeGETCHUNKmessage(int senderID,int fileID, int chunkNo, int repDeg){
        String message = VERSION + " GETCHUNK  " + senderID + " " + fileID + " " + chunkNo + " "+ CRLF + CRLF;
        return message;
    }
    public String makeCHUNKmessage(int senderID,int fileID, int chunkNo, int repDeg, String body){
        String message = VERSION + " CHUNK  " + senderID + " " + fileID + " " + chunkNo + " " + CRLF + CRLF + body;
        return message;
    }
    public String makeDELETEmessage(int senderID,int fileID, int chunkNo, int repDeg){
        String message = VERSION + " DELETE  " + senderID + " " + fileID + " " + CRLF + CRLF;
        return message;
    }
    public String makeREMOVEDmessage(int senderID,int fileID, int chunkNo, int repDeg){
        String message = VERSION + " REMOVED  " + senderID + " " + fileID + " " + chunkNo + " " + CRLF + CRLF;
        return message;
    }

}
