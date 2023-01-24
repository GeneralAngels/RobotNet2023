package org.ga2230net.thread_handlers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import org.ga2230net.socket_handlers.ListenerSocket;
import org.ga2230net.packets.Packet;
import org.ga2230net.packets.PacketBuilder;

/**
 * A thread that listens for incoming packets from a sender and adds them to a queue
 */
public class ListenerThread extends Thread {
    private final Queue<Packet> packetQueue;
    private final ListenerSocket listenerSocket;
    private final ReentrantLock lock;
    private boolean running;

    /**
     * create a new listener thread
     * @param port the port to listen on
     * @param builder the packet builder to use to build packets
     * @throws IOException if an error occurs while creating the listener sockets
     */
    public ListenerThread(int port, PacketBuilder builder) throws IOException {
        lock = new ReentrantLock();
        packetQueue = new LinkedList<>();
        listenerSocket = new ListenerSocket(port, builder);
    }

    public void run() {
        while (running) {
            try {
                Packet packet = listenerSocket.getPacket();
                lock.lock();

                try {
                    packetQueue.add(packet);
                } finally {
                    lock.unlock();
                }
            } catch (IOException e) {
                running = false;
            }
        }
    }

    /**
     * get the next packet from the received packet queue
     *
     * @return a Packet from the threads packet queue, returns null if
     *         the queue is empty
     */
    public Packet getPacket() {
        lock.lock();
        try {
            return packetQueue.poll();
        } finally {
            lock.unlock();
        }
    }

    public boolean isRunning() {
        return running;
    }
}
