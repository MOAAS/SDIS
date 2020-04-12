package Multicast;

import Messages.Message;
import Messages.MessageParser;
import StateLogging.PeerState;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MulticastThread extends Thread {

    private static final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(15);


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
                if (message.senderId == peerID)
                    continue;
                threadPool.execute(new Thread(new MessageWorker(message, peerID, peerState)));
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
