from __future__ import annotations

from threading import Thread, Lock
from queue import Queue
import socket

from typing import List, Dict

from .ListenerSocket import ListenerSocket
from .Packet import Packet
from .PacketBuilder import PacketBuilder


class MappedListenerThread(Thread):
    """A thread handler for a listener that will continuously listen
      on a specified port and insert all data recieved into a
      map of headers to packet queues
    """

    __instance: MappedListenerThread = None

    def __new__(cls, port: int, packet_builder: PacketBuilder) -> None:
        """
        :param port: the port to listen on
        :type port: int
        :param packet_builder: the packet builder to use
        :type packet_builder: PacketBuilder
        """
        if cls.__instance is None:
            cls.__instance = super(MappedListenerThread, cls).__new__(cls)
            cls.__instance.listener_socket: ListenerSocket = ListenerSocket(
                port, packet_builder
            )
            cls.__instance.packet_map: Dict[Queue] = {}
            cls.__instance.running = True
            cls.__instance.mutex = Lock()
        return cls.__instance

    def __init__(self, *args, **kwargs) -> None:
        super().__init__()

    def get_instance(cls) -> MappedListenerThread:
        """Returns the instance of the listener thread

        :return: the instance of the listener thread
        :rtype: MappedListenerThread
        """
        return cls.__instance

    def run(self) -> None:
        while self.running:
            try:
                new_packet: Packet = self.listener_socket.get_packet()
                self._add_to_queue(new_packet)
            except socket.error:
                self.running = False

    def get_packets(self, header: str,
                    num_of_packets: int = None) -> List[Packet]:
        """Retrieves a list of packets from the packet map according
          to the given header.
        Returns all packets if a number wasnt specified.

        :param header: the header of the packets
        :type header: str
        :param num_of_packets: the number of packets to retrieve
        :type num_of_packets: int
        :return: a list of packets of a specified header
        :rtype: List[Packet]
        """
        if num_of_packets is None:
            num_of_packets = self.packet_map[header].qsize()

        with self.mutex:
            packets: List[Packet] = [
                self.packet_map[header].get(False)
                for _ in range(num_of_packets)
            ]

        return packets

    def get_packet(self, header: str) -> Packet:
        """Retrieves a single packet from the packet queue of the
          specified header

        :return: a packet
        :rtype: Packet
        """
        return self.get_packets(header, 1)[0]

    def flush_packets(self, header: str = None) -> None:
        """Flushes the packet queue of the specified packet header
        if a header wasnt specified, it will flush all packet queues in the map
        """
        with self.mutex:
            if header is None:
                for q in self.packet_map.values():
                    q.queue.clear()
            else:
                self.packet_map[header].queue.clear()

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
                self.packet_map[packet.header].get()
            self.packet_map[packet.header].put(packet)
