package RIONet;

import RIONet.Constants.NetworkConstants;
import RIONet.data_objects.DataObject;
import RIONet.thread_handlers.ListenerThread;

public class Main { // TODO: add threads to tarshim
        public static void main(String[] args) {
                ListenerThread listenerThread = new ListenerThread(NetworkConstants.DEFAULT_PORT);

                while (true) {
                        DataObject data = listenerThread.getData();

                        for (int value : data.getValues()) {
                                System.out.print(value + ", ");
                        }
                        System.out.println();
                }
        }
}