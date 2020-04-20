package Multicast;

import Messages.Message;
import Messages.MessageParser;
import Messages.StoredMessage;
import StateLogging.PeerState;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MulticastThread extends Thread {

    private static final ScheduledExecutorService threadPoolStored = Executors.newScheduledThreadPool(10);
    private static final ScheduledExecutorService threadPoolGeneral = Executors.newScheduledThreadPool(15);


    public MulticastThread(String threadName, int peerID, MulticastGroup group, PeerState peerState) {
        super(() -> {
            System.out.println("Starting thread: " + threadName);
            while (!Thread.interrupted()) {
                Message message;
                try {
                    message = MessageParser.parseMessage(group.receiveFromGroup());
                } catch (IndexOutOfBoundsException | NumberFormatException e) {
                    System.out.println(threadName + ": Invalid message syntax. Ignoring... ");
                    continue;
                }
                if (peerState.getVersion().equals(Message.VERSION_VANILLA) && message.version.equals(Message.VERSION_ENHANCED)) {
                    System.out.println(threadName + ": I am not running version 1.1, Ignoring... ");
                    continue;
                }
                if (message.senderId == peerID)
                    continue;
                ScheduledExecutorService threadPool = message instanceof StoredMessage ? threadPoolStored : threadPoolGeneral;
                threadPool.execute(new MessageWorker(message, peerID, peerState));
            }
        });
    }

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
