import socket
import struct

from PUNet.data_objects.DataHeader import DataHeader
from PUNet.data_objects.DataObject import DataObject
from PUNet.Constants import NetworkConstants


class ListenerSocket:
    def __init__(self, port: int) -> None:
        self.server_socket: socket.socket = socket.socket(
            socket.AF_INET, socket.SOCK_STREAM
        )

        self.client_socket: socket.socket

        self.server_socket.bind((socket.gethostname(), port))

    def accept(self) -> None:
        self.client_socket, addr = self.server_socket.accept()

    def get_data(self) -> DataObject:
        if self.client_socket is not None:
            header: DataHeader = DataHeader(
                struct.unpack("h", self.client_socket.recv(2))
            )

            body_length: int = NetworkConstants.headerPacketSizes[header]
            body = struct.unpack(
                f"{body_length}i", self.client_socket.recv(4*body_length)
            )

            return DataObject(header, body)
        else:  # TODO imlement custom exception
            raise Exception
