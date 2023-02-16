package org.ga2230net;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.LinkedHashMap;

public class MappedListenerThread extends Thread {
    private final ReentrantLock lock;
    private final Map<String, Queue<Packet>> packetTable;
    private final ListenerSocket listenerSocket;
    private boolean running;

    public MappedListenerThread(int port, PacketBuilder builder) throws IOException {
        lock = new ReentrantLock();
        packetTable = new LinkedHashMap<>();
        listenerSocket = new ListenerSocket(port, builder);
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
     * @return an array of all packets left in the packet queue
     */
    public Packet[] getPackets(String header) {
        lock.lock();
        try {
            Queue<Packet> pq = packetTable.get(header);
            Packet[] packets = new Packet[pq.size()];
            for(int i = 0; i < packets.length; i++){
                packets[i] = pq.poll();
            }
            return packets;
        } finally {
            lock.unlock();
        }
    }

    /**
     * flushes the packet queue and thus deleting all packets in it
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
            if (!pq) {
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
