package org.ga2230net;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.LinkedHashMap;

/**
 * A thread that listens for incoming packets from a sender and adds them to a queue
 */
public class MappedListenerThread extends Thread {
    private final ReentrantLock lock;
    private final Map<String, Queue<Packet>> packetTable;
    private final ListenerSocket listenerSocket;
    private boolean running;
    private static MappedListenerThread listenerThread;

    /**
     * create a new listener thread
     * @param port the port to listen on
     * @param builder the packet builder to use to build packets
     * @throws IOException if an error occurs while creating the listener sockets
     */
    private MappedListenerThread(int port, PacketBuilder builder) throws IOException {
        lock = new ReentrantLock();
        packetTable = new LinkedHashMap<>();
        listenerSocket = new ListenerSocket(port, builder);
    }

    /**
     * initializes the single ListenerThread instance
     * @param port the port to listen on
     * @param builder the builder to use for parsing raw packets
     * @throws IOException if an IO error occurred
     */
    public static void initListener(int port, PacketBuilder builder) throws IOException {
        listenerThread = new MappedListenerThread(port, builder);
    }


    /**
     * @return the single ListenerThread instance, null if the listener wasn't initialized
     */
    public static MappedListenerThread getInstance() {
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
     * get the next n packets from the received packets queue of the given header
     *
     * @return a Packet array from the header's packet queue, returns null if
     *         the queue is empty
     */
    public Packet[] getPackets(String header, int numOfPackets) {
        lock.lock();
        try {
            Queue<Packet> pq = packetTable.get(header);
            Packet[] packets = new Packet[numOfPackets];
            for(int i = 0; i < numOfPackets; i++){
                packets[i] = pq.poll();
            }
            return packets;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return an array of all packets left in the packet queue of the given header
     */
    public Packet[] getPackets(String header) {
        Packet[] packets = null;
        lock.lock();
        try {
            Queue<Packet> pq = packetTable.get(header);
            if (pq != null) {
                packets = new Packet[pq.size()];
                for(int i = 0; i < packets.length; i++){
                    packets[i] = pq.poll();
                }
            }
        } finally {
            lock.unlock();
        }
        return packets;
    }

    /**
     * flushes the packet queue of the given header and thus deleting all packets in it
     */
    public void flushPacketsQueue(String header) {
        lock.lock();
        try {
            Queue<Packet> pq = packetTable.get(header);
            pq.clear();
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
            Queue<Packet> pq = packetTable.get(packet.getHeader());
            if (pq == null) {
                pq = new LinkedList<>();
                pq.add(packet);
                packetTable.put(packet.getHeader(), pq);
            } else {
                if (packet.isSingleInstance()) {
                    for (Packet p : pq) {
                        if (p.getHeader().equals(packet.getHeader())) {
                            pq.remove(p);
                            break;
                        }
                    }
                }
                pq.add(packet);
            }
        } finally {
            lock.unlock();
        }
    }
}
