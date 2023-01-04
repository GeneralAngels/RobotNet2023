package RIONet;

import java.io.IOException;
import java.lang.Thread;

import RIONet.Constants.NetworkConstants;
import RIONet.data_objects.DataHeader;
import RIONet.data_objects.DataObject;
import RIONet.data_objects.ExampleData;
import RIONet.socket_handlers.SenderSocket;
import RIONet.socket_handlers.SocketHandlerException;
import RIONet.thread_handlers.ListenerThread;

public class Main { // TODO implement logging
        public static void main(String[] args) {
                listener();
                // sender();
        }

        public static void sender() {
                SenderSocket senderSocket = new SenderSocket();
                String ip = "127.0.0.1";
                int port = 6666;

                try {
                        senderSocket.connect(ip, port);
                } catch (IOException e) {
                        System.out.println(String.format("failed to connect to listener on (%s, %d): " + e, ip, port));
                }

                while (true) {
                        ExampleData newPack = new ExampleData(1, 2.3, 5.6);

                        try {
                                senderSocket.sendData(newPack);
                                System.out.println("sent: " + newPack.toString());
                        } catch (IOException e) {
                                System.out.println("faied to send packet to listener: " + e);
                        } catch (SocketHandlerException e) {
                                System.out.println(e);
                        }

                        try {
                                Thread.sleep(1000);
                        } catch (InterruptedException e) {}
                }

        }

        public static void listener() {
                ListenerThread listenerThread = null;
                while (listenerThread == null) {
                        try {
                                listenerThread = new ListenerThread(NetworkConstants.DEFAULT_PORT);
                                System.out.println("started listener");
                        } catch (IOException e) {
                                System.out.println(String.format("An error has accured while trying to start a socket on port %d: ", NetworkConstants.DEFAULT_PORT) + e);
                        }
                }

                listenerThread.start();

                while (true) {
                        DataObject data = listenerThread.getData();
                        if (data != null) {
                                System.out.println(data.toString());
                        }
                }
        }
}