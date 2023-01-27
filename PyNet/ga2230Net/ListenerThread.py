from threading import Thread
from queue import Queue
import socket

from .ListenerSocket import ListenerSocket
from .Packet import Packet
from .PacketBuilder import PacketBuilder


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
        self.packet_queue: "Queue[Packet]" = Queue()

        self.running = True

    def accept(self) -> None:
        """Accepts a connection from a single sender
        """
        self.listener_socket.accept()

    def listen(self, listen_count: int) -> None:
        """Starts listening for sender connections
        """
        self.listener_socket.listen(listen_count)

#bolbol

    def run(self) -> None:
        while self.running:
            try:
                self.packet_queue.put(self.listener_socket.get_packet())
            except socket.error:
                self.running = False

    def get_packet(self, num_of_packets: int) -> list[Packet]:
        """Retrieves a list of packets from the packet queue

        :param num_of_packets: the number of packets to retrieve
        :type num_of_packets: int
        :return: a packet
        :rtype: Packet
        """

        lst_of_packets: list[Packet] = [
            self.packet_queue.get() for _ in range(num_of_packets)
            ]

        return lst_of_packets

    def is_running(self) -> bool:
        """Returns whether the thread is running

        :return: whether the thread is running
        :rtype: bool
        """
        return self.running
