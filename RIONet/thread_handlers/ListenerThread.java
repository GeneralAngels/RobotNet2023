package RIONet.thread_handlers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import RIONet.socket_handlers.ListenerSocket;
import RIONet.socket_handlers.SocketHandlerException;
import RIONet.Packet;
import RIONet.PacketBuilder;

/** Add your docs here. */
public class ListenerThread extends Thread {

    private Queue<Packet> packetQueue;
    private ListenerSocket listenerSocket;

    ReentrantLock lock;

    public ListenerThread(int port, PacketBuilder packetBuilder) throws IOException {
        lock = new ReentrantLock();
        packetQueue = new LinkedList<Packet>();
        listenerSocket = new ListenerSocket(port, packetBuilder);

        listenerSocket.accept();
    }

    public void run() {
        while (true) {
            try {
                Packet packet = listenerSocket.getPacket();
                lock.lock();

                try {
                    packetQueue.add(packet);
                } finally {
                    lock.unlock();
                }
            } catch (IOException e) {
                System.out.println("An error accured while recieving data from sender: " + e);
            } catch (SocketHandlerException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * get the next packet from the recieved packet queue
     *
     * @return a Packet from the threads packet queue, returns null if
     * the queue is empty
     */
    public Packet getPacket() {
        lock.lock();
        try {
            return packetQueue.poll();
        } finally {
            lock.unlock();
        }
    }
}
