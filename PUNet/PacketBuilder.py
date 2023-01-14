from typing import Dict, List
import struct

import yaml
import os

from PUNet.Packet import Packet


class PacketBuilder:
    def __init__(self, packet_directory: str) -> None:
        self.packet_directory = packet_directory

        # header: {field: type}
        self.packet_schemes: Dict[str, Dict[str, chr]] = {}
        self.packet_schemes = self._get_conf_dict()

    def build_from_header(self, header: str) -> Packet:
        """Builds an empty packet with only empty fields.

        :param header: the header of the packet
        :type header: str
        :return: the empty packet
        :rtype: Packet
        """
        return Packet(header, self.format_of(header), *self.fields_of(header))

    def build_from_raw(self, header: str, raw: bytes) -> Packet:
        """Builds a packet from raw bytes.

        :param header: the header of the packet
        :type header: str
        :param raw: the raw bytes of the packet body
        :type raw: bytes
        :return: the built packet
        :rtype: Packet
        """
        new_pack = self.build_from_header(header)
        fields = dict(zip(self.fields_of(header),
                      struct.unpack(">" + self.format_of(header), raw)))
        new_pack.set_fields(**fields)
        return new_pack

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
        return "".join(self.packet_schemes[header].values())

    def fields_of(self, header: str) -> List[str]:
        """Returns the fields of the packet.

        :param header: the header of the packet
        :type header: str
        :return: the fields of the packet
        :rtype: List[str]
        """
        return self.packet_schemes[header].keys()

    def _get_conf_dict(self) -> dict[str, dict[str, chr]]:
        """Returns a dictionary of all packet's stractures from the
         parsed packet configs.

        :return: a dictionary of all packet's stractures
        :rtype: dict[str, dict[str, chr]]
        """
        general_dict = {}  # A dictionary of all packet's stractures.
        # Go over all of the files and connectes thier packet structures into
        # one dictionary.
        for filename in os.listdir(self.packet_directory):
            if filename.endswith(".packet"):
                conf_yaml = open(self.packet_directory + '\\' + filename, "r")
                yaml_data = yaml.load(conf_yaml, Loader=yaml.FullLoader)
                conf_yaml.close()
                general_dict.update(yaml_data)

        return general_dict
