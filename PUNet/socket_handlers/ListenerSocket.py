import socket
import struct

from .SockethandlerException import SockethandlerException
from ..Packet import Packet
from ..PacketBuilder import PacketBuilder


class ListenerSocket:
    """A socket handler for listening for data.
    Implements a server that listens for sender connections.
    """
    def __init__(self, port: int, packet_builder: PacketBuilder) -> None:
        """
        :param port: the port to listen on
        :type port: int
        :param local: whether to run the server on localhost
        :type local: bool
        """
        self.server_socket: socket.socket = socket.socket(
            socket.AF_INET, socket.SOCK_STREAM
        )

        self.client_socket: socket.socket
        self.server_socket.bind(("127.0.0.1", port))

        self.packet_builder = packet_builder

    def accept(self) -> None:
        """Accepts a single sender connection to recieve data from
        """
        self.server_socket.listen(1)
        self.client_socket, addr = self.server_socket.accept()

    def get_data(self) -> Packet:
        """Gets a single packet sent from a sender.
        the packet will be wrapped around a Packet object.
        If there isnt an available packet on buffer it will wait for one.

        :raises SockethandlerException:
        if the listener didn't accept any clients,
        it will raise an Exception
        :return: a single packet sent
        :rtype: Packet
        """
        if self.client_socket is not None:
            header_length = int.from_bytes(self.client_socket.recv(2), "big")
            print("header length: " + str(header_length))
            # struct strings are all chars + empty byte
            raw_header = self.client_socket.recv(header_length)
            header: str = (
                struct.unpack(f">{header_length}s", raw_header)[0]
            ).decode("utf-8")

            return self.packet_builder.build_from_raw(
                header, self.client_socket.recv(
                    self.packet_builder.size_of(header)
                )
            )
        else:
            raise SockethandlerException(
                "Must first astablish a connection \
                to sender before recieving data!"
            )
