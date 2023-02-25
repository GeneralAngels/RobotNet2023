package org.ga2230net;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A thread that listens for incoming packets from a sender and adds them to a queue
 */
public class ListenerThread extends Thread {
    private final Queue<Packet> packetQueue;
    private final ListenerSocket listenerSocket;
    private final ReentrantLock lock;
    private boolean running;
    private static ListenerThread listenerThread = null;

    /**
     * create a new listener thread
     * @param port the port to listen on
     * @param builder the packet builder to use to build packets
     * @throws IOException if an error occurs while creating the listener sockets
     */
    private ListenerThread(int port, PacketBuilder builder) throws IOException {
        lock = new ReentrantLock();
        packetQueue = new LinkedList<>();
        listenerSocket = new ListenerSocket(port, builder);
    }

    /**
     * initializes the single ListenerThread instance
     * @param port the port to listen on
     * @param builder the builder to use for parsing raw packets
     * @throws IOException if an IO error occurred
     */
    public static void initListener(int port, PacketBuilder builder) throws  IOException {
        listenerThread = new ListenerThread(port, builder);
    }

    /**
     * @return the single ListenerThread instance, null if the listener wasn't initialized
     */
    public static ListenerThread getInstance() {
        return listenerThread;
    }

    public void run() {
        running = true;
        while (running) {
            try {
                Packet packet = listenerSocket.getPacket();
                addPacket(packet);
            } catch (IOException e) {
                e.printStackTrace();
                running = false;
            }
        }
    }

    /**
     * get the next n packets from the received packets queue
     *
     * @return a Packet array from the threads packet queue, returns null if
     *         the queue is empty
     */
    public Packet[] getPackets(int numOfPackets) {
        lock.lock();
        try {
            Packet[] packets = new Packet[numOfPackets];
            for(int i = 0; i < numOfPackets; i++){
                packets[i] = packetQueue.poll();
            }
            return packets;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return an array of all packets left in the packet queue
     */
    public Packet[] getPackets() {
        lock.lock();
        try {
            Packet[] packets = new Packet[packetQueue.size()];
            for(int i = 0; i < packets.length; i++){
                packets[i] = packetQueue.poll();
            }
            return packets;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return the latest packet recieved in the queue
     */
    public Packet getPacket() {
        lock.lock();
        try {
            return packetQueue.poll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * flushes the packet queue and thus deleting all packets in it
     */
    public void flushPacketsQueue() {
        lock.lock();
        try {
            packetQueue.clear();
        } finally {
            lock.unlock();
        }
    }

    public boolean isRunning() {
        return running;
    }

    private void addPacket(Packet packet) {
        lock.lock();
        try {
            if (packet.isSingleInstance()) {
                for (Packet p: packetQueue) {
                    if (p.getHeader().equals(packet.getHeader())) {
                        packetQueue.remove(p);
                        break;
                    }
                }
            }
            packetQueue.add(packet);
        } finally {
            lock.unlock();
        }
    }
}
