from __future__ import annotations

from threading import Thread, Lock
from queue import Queue
import socket

from .ListenerSocket import ListenerSocket
from .Packet import Packet
from .PacketBuilder import PacketBuilder


class ListenerThread(Thread):
    """A thread handler for a listener that will continuously listen
    on a specified port and insert all data received into a queue
    """

    __instance: ListenerThread = None

    def __new__(cls, port: int, packet_builder: PacketBuilder) -> None:
        """
        :param port: the port to listen on
        :type port: int
        :param packet_builder: the packet builder to use
        :type packet_builder: PacketBuilder
        """
        if cls.__instance is None:
            cls.__instance = super(ListenerThread, cls).__new__(cls)
            cls.__instance.listener_socket: ListenerSocket = ListenerSocket(
                port, packet_builder
            )
            cls.__instance.packet_queue: Queue = Queue()
            cls.__instance.running = True
            cls.__instance.mutex = Lock()
        return cls.__instance

    def __init__(self, *args, **kwargs) -> None:
        super().__init__()

    def get_instance(cls) -> ListenerThread:
        """Returns the instance of the listener thread

        :return: the instance of the listener thread
        :rtype: ListenerThread
        """
        return cls.__instance

    def run(self) -> None:
        while self.running:
            try:
                new_packet: Packet = self.listener_socket.get_packet()
                self._add_to_queue(new_packet)
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
            num_of_packets = self.packet_queue.qsize()

        with self.mutex:
            packets: list[Packet] = [
                self.packet_queue.get(False) for _ in range(num_of_packets)
            ]

        return packets

    def get_packet(self) -> Packet:
        """Retrieves a packet from the packet queue

        :return: a packet
        :rtype: Packet
        """
        return self.get_packets(1)[0]

    def flush_packets_queue(self) -> None:
        """Flushes the packet queue
        """
        with self.mutex:
            self.packet_queue.queue.clear()

    def is_running(self) -> bool:
        """Returns whether the thread is running

        :return: whether the thread is running
        :rtype: bool
        """
        return self.running

    def _add_to_queue(self, packet: Packet) -> None:
        """Adds a packet to the packet queue

        :param packet: the packet to add
        :type packet: Packet
        """

        with self.mutex:
            if packet.is_single_instance():
                for packet_in_queue in self.packet_queue.queue:
                    if packet_in_queue.header == packet.header:
                        self.packet_queue.queue.remove(packet_in_queue)
                        break
            self.packet_queue.put(packet)
