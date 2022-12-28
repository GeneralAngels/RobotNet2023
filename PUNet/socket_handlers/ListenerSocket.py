import socket
import struct

from ..data_objects.DataHeader import DataHeader
from ..data_objects.DataObject import DataObject
from ..Constants import NetworkConstants


class ListenerSocket:
    def __init__(self, port: int, local: bool) -> None:
        self.server_socket: socket.socket = socket.socket(
            socket.AF_INET, socket.SOCK_STREAM
        )

        self.client_socket: socket.socket

        if local:
            self.server_socket.bind(("127.0.0.1", port))
        else:
            self.server_socket.bind((socket.gethostname(), port))

    def accept(self) -> None:
        self.server_socket.listen(1)
        self.client_socket, addr = self.server_socket.accept()

    def get_data(self) -> DataObject:
        if self.client_socket is not None:
            recieved = self.client_socket.recv(2)
            header: DataHeader = DataHeader(
                struct.unpack(">h", recieved)[0]
            )

            body_length: int = NetworkConstants.headerPacketSizes[header]
            recieved = self.client_socket.recv(4*body_length)
            body = struct.unpack(
                f">{body_length}i", recieved
            )

            return DataObject(header, body)
        else:  # TODO imlement custom exception
            raise Exception
