import socket
import struct
import select
from typing import List

from . import SockethandlerException
from ..packets import Packet
from ..packets import PacketBuilder


class ListenerSocket:
    """A socket handler for listening for data.
    Implements a server that listens for sender connections on all network interfaces.
    """
    def __init__(self, port: int, packet_builder: PacketBuilder) -> None:
        """
        :param port: the port to listen on
        :type port: int
        :param packet_builder: the packet builder to use
        :type packet_builder: PacketBuilder
        """
        self.server_socket: socket.socket = socket.socket(
            socket.AF_INET, socket.SOCK_STREAM
        )

        self.client_sockets: List[socket.socket] = []
        self.server_socket.bind(("0.0.0.0", port))

        self.packet_builder = packet_builder

    def accept(self) -> None:
        """Accepts a single sender connection to recieve data from
        """
        self.client_sockets.append(self.server_socket.accept()[0])

    def listen(self, listen_count: int) -> None:
        """Starts listening for sender connections
        """
        self.server_socket.listen(listen_count)

    def get_data(self) -> Packet:
        """Gets a single packet sent from a sender.
        the packet will be wrapped around a Packet object.
        If there isnt an available packet on buffer it will wait for one.

        :raises SockethandlerException: if the listener didn't accept any clients
        :return: a single packet sent
        :rtype: Packet
        """
        if len(self.client_sockets) == 0:
            raise SockethandlerException(
                "Must first accept a connection \
                from sender before recieving data!"
            )

        rlist, _, _ = select.select(self.client_sockets, [], [])
        sock: socket.socket = rlist[0] # the first socket that has data

        header_length = int.from_bytes(sock.recv(2), "big")

        # struct strings are all chars + empty byte
        raw_header = sock.recv(header_length)
        header: str = (
            struct.unpack(f">{header_length}s", raw_header)[0]
        ).decode("utf-8")

        return self.packet_builder.build_from_raw(
            header, sock.recv(
                self.packet_builder.size_of(header)
            )
        )
