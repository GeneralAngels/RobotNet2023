import socket
import struct

from ..data_objects.DataHeader import DataHeader
from ..data_objects.DataObject import DataObject
from ..Constants import NetworkConstants


class SenderSocket:
    def __init__(self) -> None:
        self.connected = False
        self.sock: socket.socket = socket.socket(
            socket.AF_INET, socket.SOCK_STREAM
        )

    def connect(self, ip: str, port: int) -> None:
        self.sock.connect((ip, port))
        self.connected = True

    def send_data(self, data: DataObject) -> None:
        if self.connected:
            self.sock.send(data.serialize())
        else:
            raise Exception
