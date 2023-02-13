package org.ga2230net;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A thread that listens for incoming packets from a sender and adds them to a queue
 */
public class ListenerThread extends Thread {
    private final PriorityQueue<Packet> packetQueue;
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
        packetQueue = new PriorityQueue<>();
        listenerSocket = new ListenerSocket(port, builder);
    }

    public void run() {
        running = true;
        while (running) {
            try {
                Packet packet = listenerSocket.getPacket();
                addPacket(packet);
            } catch (IOException e) {
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
            for(int i = 0; i < packetQueue.size(); i++){
                packets[i] = packetQueue.poll();
            }
            return packets;
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
