import socket

from ..data_objects.DataObject import DataObject
from .SockethandlerException import SockethandlerException


class SenderSocket:
    """A socket handler for sending data.
    Implements a client that sends data to listeners.
    """
    def __init__(self) -> None:
        self.connected = False
        self.sock: socket.socket = socket.socket(
            socket.AF_INET, socket.SOCK_STREAM
        )

    def connect(self, ip: str, port: int) -> None:
        """Connects the sender to a listener on a specified ip and port.

        :param ip: the ip of the listener
        :type ip: str
        :param port: the port of the listener
        :type port: int
        """
        self.sock.connect((ip, port))
        self.connected = True

    def send_data(self, data: DataObject) -> None:
        """Sends a packet to a the listener.

        :param data: the packet wrapped around a DataObject
        :type data: DataObject
        :raises SockethandlerException:
        if the sender isn't connected to any listener
        it will raise an Exception
        """
        if self.connected:
            self.sock.send(data.serialize())
        else:
            raise SockethandlerException(
                "Must first astablish a connection to listener before sending!"
            )