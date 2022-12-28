package RIONet.thread_handlers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import RIONet.socket_handlers.ListenerSocket;
import RIONet.data_objects.DataObject;

/** Add your docs here. */
public class ListenerThread extends Thread {

    private Queue<DataObject> dataQueue;
    private ListenerSocket listenerSocket;

    public ListenerThread(int port) throws IOException {
        dataQueue = new LinkedList<DataObject>();
        listenerSocket = new ListenerSocket(port);

        listenerSocket.accept();
    }

    public void run() {
        while (true) {
            try {
                DataObject data = listenerSocket.getData();
                dataQueue.add(data);
            } catch (IOException e) {
                System.out.println("An error accured while recieving data from sender: " + e);
            }
        }
    }

    /**
     * get the next DataObject from the recieved task queue
     * 
     * @return DataObject a DataObject from the threads tasks queue, returns null if
     *         the queue is empty
     */
    public DataObject getData() {
        return dataQueue.poll();
    }
}
