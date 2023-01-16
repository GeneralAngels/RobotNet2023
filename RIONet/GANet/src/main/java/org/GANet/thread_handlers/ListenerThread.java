package org.GANet.thread_handlers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import org.GANet.socket_handlers.ListenerSocket;
import org.GANet.packets.Packet;
import org.GANet.packets.PacketBuilder;

/**
 * A thread that listens for incoming packets from a sender and adds them to a queue
 */
public class ListenerThread extends Thread {

    private Queue<Packet> packetQueue;
    private ListenerSocket listenerSocket;

    ReentrantLock lock;

    /**
     * create a new listener thread
     * @param port the port to listen on
     * @param packetBuilder the packet builder to use to build packets
     * @throws IOException if an error occurs while creating the listener socket
     */
    public ListenerThread(int port, PacketBuilder packetBuilder) throws IOException {
        lock = new ReentrantLock();
        packetQueue = new LinkedList<Packet>();
        listenerSocket = new ListenerSocket(port, packetBuilder);
    }

    /**
     * accept a connection from a single sender
     * @throws IOException if an error occurs while accepting the connection
     */
    public void accept() throws IOException {
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
            }
        }
    }

    /**
     * get the next packet from the recieved packet queue
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
}
