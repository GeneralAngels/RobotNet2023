package RIONet.thread_handlers;

import java.util.Queue;

import RIONet.data_objects.DataObject;
import RIONet.socket_handlers.SenderSocket;

import java.util.LinkedList;

public class SenderThread extends Thread {

    private Queue<DataObject> dataOutputQueue;
    private SenderSocket senderSocket;

    private String listenerIP;
    private int listenerPort;

    public SenderThread(String listenerIP, int listenerPort) {
        dataOutputQueue = new LinkedList<DataObject>();
        senderSocket = new SenderSocket();

        this.listenerIP = listenerIP;
        this.listenerPort = listenerPort;
    }

    public void run() {
        senderSocket.connect(listenerIP, listenerPort);
        while (true) {
            senderSocket.sendData(dataOutputQueue.poll());
        }
    }

    public void putData(DataObject data) {
        dataOutputQueue.add(data);
    }
}