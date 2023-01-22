package org.ga2230net.thread_handlers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import org.ga2230net.socket_handlers.ListenerSocket;
import org.ga2230net.socket_handlers.SocketType;
import org.ga2230net.socket_handlers.TCPListener;
import org.ga2230net.packets.Packet;
import org.ga2230net.packets.PacketBuilder;
import org.ga2230net.socket_handlers.UDPListener;

/**
 * A thread that listens for incoming packets from a sender and adds them to a queue
 */
public class ListenerThread extends Thread {

    private final Queue<Packet> packetQueue;
    private final ListenerSocket listenerSocket;

    ReentrantLock lock;

    private boolean running;

    /**
     * create a new listener thread
     * @param port the port to listen on
     * @param packetBuilder the packet builder to use to build packets
     * @throws IOException if an error occurs while creating the listener socket
     */
    public ListenerThread(int port, PacketBuilder packetBuilder, SocketType type) throws IOException {
        lock = new ReentrantLock();
        packetQueue = new LinkedList<Packet>();
        if (type == SocketType.TCP) {
            listenerSocket = new TCPListener(port, packetBuilder);
        } else {
            listenerSocket = new UDPListener(port, packetBuilder);
        }
    }

    /**
     * accept a connection from a single sender
     * @throws IOException if an error occurs while accepting the connection
     */
    public void accept() throws IOException {
        listenerSocket.accept();
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
