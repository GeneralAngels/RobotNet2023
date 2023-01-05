import socket
import struct

from ..data_objects.DataHeader import DataHeader
from ..data_objects.DataObject import DataObject
from ..data_objects.ExampleObject import ExampleObject
from ..Constants import NetworkConstants
from .SockethandlerException import SockethandlerException


class ListenerSocket:
    """A socket handler for listening for data.
    Implements a server that listens for sender connections.
    """
    def __init__(self, port: int, local: bool) -> None:
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
        print(socket.gethostbyname(socket.gethostname()))
        if local:
            self.server_socket.bind(("127.0.0.1", port))
        else:
            self.server_socket.bind((
                "10.22.30.185", port
            ))

    def accept(self) -> None:
        """Accepts a single sender connection to recieve data from
        """
        self.server_socket.listen(1)
        self.client_socket, addr = self.server_socket.accept()

    def get_data(self) -> DataObject:
        """Gets a single packet sent from a sender.
        the packet will be wrapped around a DataObject.
        If there isnt an available packet on buffer it will wait for one.

        :raises SockethandlerException:
        if the listener didn't accept any clients,
        it will raise an Exception
        :return: a single packet sent
        :rtype: DataObject
        """
        if self.client_socket is None:
            raise SockethandlerException(
                "Must first astablish a connection \
                to sender before recieving data!"
            )

        header_raw = self.client_socket.recv(2)
        header: DataHeader = DataHeader(
            struct.unpack(">h", header_raw)[0]
        )
        body_length = struct.calcsize(
            NetworkConstants.headerPacketFormats[header]
        )

        body_raw = self.client_socket.recv(body_length)
        print(body_raw)
        match header:
            case DataHeader.EXAMPLE:
                return ExampleObject.from_bytes(body_raw)
            case _:
                return None
