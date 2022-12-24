package RIONet.thread_handlers;

import java.util.LinkedList;
import java.util.Queue;

import RIONet.socket_handlers.ListenerSocket;
import RIONet.data_objects.DataObject;

/** Add your docs here. */
public class ListenerThread extends Thread {

    private Queue<DataObject> taskQueue;
    private ListenerSocket listenerSocket;

    public ListenerThread(int port) {
        taskQueue = new LinkedList<DataObject>();
        listenerSocket = new ListenerSocket(port);
    }

    public void run() {
        while (true) {
            taskQueue.add(listenerSocket.getData());
        }
    }

    /**
     * get the next DataObject from the recieved task queue
     * 
     * @return DataObject a DataObject from the threads tasks queue, returns null if
     *         the queue is empty
     */
    public DataObject getData() {
        return taskQueue.poll();
    }
}
