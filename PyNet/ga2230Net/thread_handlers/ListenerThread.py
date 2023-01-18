from threading import Thread
from queue import Queue
import socket

from ..socket_handlers.ListenerSocket import ListenerSocket
from ..packets.Packet import Packet
from ..packets.PacketBuilder import PacketBuilder


class ListenerThread(Thread):
    """A thread handler for a listener that will continuously listen
    on a specified port and insert all data recieved into a queue
    """
    def __init__(self, port: int, packet_builder: PacketBuilder) -> None:
        """
        :param port: the port to listen on
        :type port: int
        :param packet_builder: the packet builder to use
        :type packet_builder: PacketBuilder
        """
        super().__init__()
        self.listener_socket: ListenerSocket = ListenerSocket(
            port, packet_builder
        )
        self.data_queue: "Queue[Packet]" = Queue()

        self.running = True

    def accept(self) -> None:
        """Accepts a connection from a single sender
        """
        self.listener_socket.accept()

    def listen(self, listen_count: int) -> None:
        """Starts listening for sender connections
        """
        self.listener_socket.listen(listen_count)

    def run(self) -> None:
        while self.running:
            try:
                self.data_queue.put(self.listener_socket.get_data())
            except socket.error as e:
                self.running = False

    def get_packet(self) -> Packet:
        """Retrieves a packet from the packet queue

        :return: a packet
        :rtype: Packet
        """
        return self.data_queue.get()

    def is_running(self) -> bool:
        """Returns whether the thread is running

        :return: whether the thread is running
        :rtype: bool
        """
        return self.running
