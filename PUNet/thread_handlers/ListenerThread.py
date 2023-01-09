from threading import Thread
from queue import Queue
import socket

from PUNet.socket_handlers.ListenerSocket import ListenerSocket
from PUNet.Packet import Packet
from PUNet.PacketBuilder import PacketBuilder


class ListenerThread(Thread):
    """A thread handler for a listener that will continuously listen
    on a specified port and insert all data recieved into a queue
    """
    def __init__(self, port: int, packet_builder: PacketBuilder) -> None:
        """
        :param port: the port to listen on
        :type port: int
        :param local: whether to run the listener on localhost
        :type local: bool
        :param daemon: whether to run the thread as daemon
        :type daemon: bool
        """
        super().__init__()
        self.listener_socket: ListenerSocket = ListenerSocket(
            port, packet_builder
        )
        self.data_queue: "Queue[Packet]" = Queue()

        self.running = True

    def run(self) -> None:
        self.listener_socket.accept()
        while self.running:
            try:
                self.data_queue.put(self.listener_socket.get_data())
            except socket.error as e:
                print("Failed to recieve data from sender: " + e)

    def get_packet(self) -> Packet:
        """Retrieves data from the data queue

        :return: a DataObject wrapped packet
        :rtype: DataObject
        """
        return self.data_queue.get()
