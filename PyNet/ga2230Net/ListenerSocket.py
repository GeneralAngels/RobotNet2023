import socket
import struct

from .Packet import Packet
from .PacketBuilder import PacketBuilder


class ListenerSocket:
    """A socket handler for listening for data.
    Implements a server that listens for sender connections on all network
    interfaces.
    """
    def __init__(self, port: int, packet_builder: PacketBuilder) -> None:
        """
        :param port: the port to listen on
        :type port: int
        :param packet_builder: the packet builder to use
        :type packet_builder: PacketBuilder
        """
        self.server_socket: socket.socket = socket.socket(
            socket.AF_INET, socket.SOCK_DGRAM
        )

        self.server_socket.bind(("0.0.0.0", port))

        self.packet_builder = packet_builder

    def get_packet() -> Packet:
        """Gets a single packet sent from a sender.
        the packet will be wrapped around a Packet object.
        If there isnt an available packet on buffer it will wait for one.

        :return: a single packet sent
        :rtype: Packet
        """

        raw_packet = self.server_socket.recvfrom(1024)[0]

        header_length = int.from_bytes(raw_packet[0:2], "big")

        # struct strings are all chars + empty byte
        raw_header = raw_packet[2: 2 + header_length]

        header: str = (
            struct.unpack(f">{header_length}s", raw_header)[0]
        ).decode("utf-8")

        body_start = 2 + header_length
        return self.packet_builder.build_from_raw(
            header,
            raw_packet[body_start: body_start +
                       self.packet_builder.size_of(header)]
        )
