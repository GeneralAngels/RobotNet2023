import socket
import struct

from ..data_objects.DataHeader import DataHeader
from ..data_objects.DataObject import DataObject
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

        if local:
            self.server_socket.bind(("127.0.0.1", port))
        else:
            self.server_socket.bind((socket.gethostname(), port))

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
        if self.client_socket is not None:
            recieved = self.client_socket.recv(2)
            header: DataHeader = DataHeader(
                struct.unpack(">h", recieved)[0]
            )

            ibody_length: int = NetworkConstants.headerPacketSizes[header][0]
            dbody_length: int = NetworkConstants.headerPacketSizes[header][1]

            ibody = struct.unpack(
                f">{ibody_length}i", self.client_socket.recv(4*ibody_length)
            )
            dbody = struct.unpack(
                f">{dbody_length}d", self.client_socket.recv(8*dbody_length)
            )

            return DataObject(header, ibody, dbody)
        else:
            raise SockethandlerException(
                "Must first astablish a connection \
                to sender before recieving data!"
            )
