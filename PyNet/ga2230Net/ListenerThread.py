from threading import Thread
from queue import Queue
import socket
from typing import List

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

    def run(self) -> None:
        while self.running:
            try:
                new_packet: Packet = self.listener_socket.get_packet()
                with self.packet_queue.mutex:
                    self.packet_queue.put(new_packet)
            except socket.error:
                self.running = False

    def get_packets(self, num_of_packets: int = None) -> list[Packet]:
        """Retrieves a list of packets from the packet queue.
        Returns all packets if a number wasnt specified.

        :param num_of_packets: the number of packets to retrieve
        :type num_of_packets: int
        :return: a packet
        :rtype: Packet
        """
        if num_of_packets is None:
            num_of_packets = len(self.packet_queue)

        with self.packet_queue.mutex:
            packets: list[Packet] = [
                self.packet_queue.get() for _ in range(num_of_packets)
            ]

        return packets

    def flush_packets_queue(self) -> None:
        """Flushes the packet queue
        """
        with self.packet_queue.mutex:
            self.packet_queue.queue.clear()

    def is_running(self) -> bool:
        """Returns whether the thread is running

        :return: whether the thread is running
        :rtype: bool
        """
        return self.running
