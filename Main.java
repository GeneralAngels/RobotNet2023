import java.io.DataInputStream;
import java.io.IOException;
import java.lang.Thread;
import java.util.Arrays;

import RIONet.socket_handlers.SenderSocket;
import RIONet.socket_handlers.SocketHandlerException;
import RIONet.thread_handlers.ListenerThread;
import RIONet.Packet;
import RIONet.PacketBuilder;
import RIONet.socket_utils.StructUtils;

public class Main {
        public static void main(String[] args) {
                listener();
                sender();
        }

        public static void sender() {
                PacketBuilder builder = new PacketBuilder("packets");
                SenderSocket senderSocket = new SenderSocket();
                String ip = "127.0.0.1";
                int port = 6666;

                try {
                        senderSocket.connect(ip, port);
                } catch (IOException e) {
                        System.out.println(String.format("failed to connect to listener on (%s, %d): " + e, ip, port));
                }

                while (true) {
                        Packet packet = builder.buildFromHeader("EXAMPLE_PACKET");
                        packet.setField("ifield1", 2);
                        packet.setField("dfield2", 4.8);

                        try {
                                senderSocket.sendData(packet);
                                System.out.println("sent: " + packet.toString());
                        } catch (IOException e) {
                                System.out.println("faied to send packet to listener: " + e);
                        } catch (SocketHandlerException e) {
                                System.out.println(e);
                        }

                        try {
                                Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                }

        }

        public static void listener() {
                PacketBuilder builder = new PacketBuilder("packets");

                ListenerThread listenerThread = null;
                while (listenerThread == null) {
                        try {
                                System.out.println("creating listener");
                                listenerThread = new ListenerThread(6666, builder);
                                System.out.println("created listener");
                        } catch (IOException e) {
                                System.out.println(
                                                "An error has accured while trying to start a socket on port 6666" + e);
                        }
                }

                try {
                        listenerThread.accept();
                } catch (IOException e) {
                        System.out.println(
                                        "An error has accured while trying to accept client connection on port 6666"
                                                        + e);
                }

                listenerThread.start();
                System.out.println("started listener");

                while (true) {
                        Packet packet = listenerThread.getPacket();
                        if (packet != null) {
                                System.out.println(packet.toString());
                                int ifield1 = packet.getField("ifield1");
                                double dfield2 = packet.getField("dfield2");

                                System.out.println("ifield1: " + ifield1);
                                System.out.println("dfield2: " + dfield2);
                        }
                }
        }
}