from __future__ import annotations

from threading import Thread, Lock
from queue import Queue, Empty
import socket

from typing import List, Dict

from .ListenerSocket import ListenerSocket
from .Packet import Packet
from .PacketBuilder import PacketBuilder


class MappedListenerThread(Thread):
    """A thread handler for a listener that will continuously listen
      on a specified port and insert all data received into a
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
            cls.__instance.builder: PacketBuilder = packet_builder
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
            except Exception as e:
                print(e)
                self.running = False

    def get_packets(self, header: str,
                    num_of_packets: int = None) -> List[Packet]:
        """Retrieves a list of packets from the packet map according
          to the given header.
        Returns all packets if a number wasn't specified.

        :param header: the header of the packets
        :type header: str
        :param num_of_packets: the number of packets to retrieve
        :type num_of_packets: int
        :return: a list of packets of a specified header
        :rtype: List[Packet]
        :raises ValueError: if the header is not found in the packet schemes
        """

        if header not in self.builder.packet_schemes.keys():
            raise ValueError(f"Header {header} not found in packet schemes.")

        if num_of_packets is None:
            num_of_packets = self.packet_map[header].qsize()

        if header not in self.packet_map.keys():
            return []

        packets = []
        with self.mutex:
            for _ in range(num_of_packets):
                try:
                    packets.append(self.packet_map[header].get(block=False))
                except Empty:
                    break

        return packets

    def get_packet(self, header: str) -> Packet:
        """Retrieves a single packet from the packet queue of the
          specified header

        :return: a packet
        :rtype: Packet
        """
        return next(iter(self.get_packets(header, 1)), None)

    def flush_packets(self, header: str = None) -> None:
        """Flushes the packet queue of the specified packet header
        if a header wasn't specified, it will flush all packet queues in the
          map
        """

        if header not in self.packet_map.keys():
            return

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
        if packet.header not in self.packet_map.keys():
            self.packet_map[packet.header] = Queue()
        with self.mutex:
            if packet.is_single_instance():
                self.packet_map[packet.header].get()
            self.packet_map[packet.header].put(packet)
