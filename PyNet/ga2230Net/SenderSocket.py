import socket

from Packet import Packet


class SenderSocket:
    """A socket handler for sending data.
    Implements a client that sends data to listeners.
    """
    def __init__(self, listener_ip: str, listener_port: int) -> None:
        """Creates a new sender socket

        :param listener_ip: the ip of the listener
        :type listener_ip: str
        :param listener_port: the port of the listener
        :type listener_port: int
        """
        self.sock: socket.socket = socket.socket(
            socket.AF_INET, socket.SOCK_DGRAM
        )
        self.listener_address = (listener_ip, listener_port)

    def send_packet(self, packet: Packet) -> None:
        """Sends a packet to a the listener.

        :param data: the packet
        :type data: Packet
         listener
        """
        self.sock.sendto(packet.serialize(), self.listener_address)
