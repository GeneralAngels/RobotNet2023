from typing import Dict, Any, List
import struct

from PUNet.Packet import Packet


class PacketBuilder:
    def __init__(self, packet_directory: str) -> None:
        self.packet_directory = packet_directory

        # header: {field: type}
        self.packets: Dict[str, Dict[str, chr]] = {}
        self.init_packets()

    def init_packets(self) -> None:
        pass

    def build_from_fields(self, header: str, **fieldes: Any) -> Packet:
        """Builds a packet from it's fields.

        :param header: the header of the packet
        :type header: str
        :return: the built packet
        :rtype: Packet
        """
        return Packet(header, self.format_of(header), **fieldes)

    def build_from_raw(self, header: str, raw: bytes) -> Packet:
        """Builds a packet from raw bytes.

        :param header: the header of the packet
        :type header: str
        :param raw: the raw bytes of the packet
        :type raw: bytes
        :return: the built packet
        :rtype: Packet
        """
        return Packet(struct.unpack(self.format_of(header), raw))

    def size_of(self, header: str) -> int:
        """Returns the number of bytes the packet has.

        :param header: the header of the packet
        :type header: str
        :return: the number of bytes the packet has
        :rtype: int
        """
        return struct.calcsize(self.format_of(header))

    def format_of(self, header: str) -> str:
        """Returns the format of the packet struct.

        :param header: the header of the packet
        :type header: str
        :return: the format of the packet
        :rtype: str
        """
        return self.packets[header]["format"]

    def fields_of(self, header: str) -> List[str]:
        """Returns the fields of the packet.

        :param header: the header of the packet
        :type header: str
        :return: the fields of the packet
        :rtype: List[str]
        """
        return self.packets[header].keys()

        
