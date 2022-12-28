package RIONet;

import java.io.IOException;

import RIONet.Constants.NetworkConstants;
import RIONet.data_objects.DataHeader;
import RIONet.data_objects.DataObject;
import RIONet.thread_handlers.ListenerThread;

public class Main {
        public static void main(String[] args) {
                ListenerThread listenerThread = null;
                while (listenerThread == null) {
                        try {
                                listenerThread = new ListenerThread(NetworkConstants.DEFAULT_PORT);
                                System.out.println("started listener");
                        } catch (IOException e) {
                                System.out.println(String.format("An error has accured while trying to start a socket on port %d: ", NetworkConstants.DEFAULT_PORT) + e);
                        }
                }

                while (true) {
                        DataObject data = listenerThread.getData();

                        DataHeader header = data.getHeader();
                        System.out.println(header.name());
                        if (data != null) {
                                for (int value : data.getValues()) {
                                        System.out.print(value + ", ");
                                }
                                System.out.println();
                        }
                }
        }
}