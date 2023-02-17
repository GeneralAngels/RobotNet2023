from typing import Dict, List
import struct
import yaml
import os
from dataclasses import dataclass

from .Packet import Packet


@dataclass
class Packet_Representation:
    header: str
    fields: List[str]
    fmt: str
    single_instance: bool


class PacketBuilder:
    """A class that builds packets according to their chemes defined
     in a packet config file in the given directory.
    """
    def __init__(self, packet_directory: str) -> None:
        """Creates a new packet builder.

        :param packet_directory: the directory of the packets config files
        :type packet_directory: str
        """
        self.packet_directory = packet_directory

        self.packet_schemes: Dict[str, Packet_Representation] = {}
        self.packet_schemes = self._parse_packet_directory()

    def build_from_header(self, header: str) -> Packet:
        """Builds an empty packet with only empty fields.

        :param header: the header of the packet
        :type header: str
        :return: an empty packet
        :rtype: Packet
        :raises ValueError: if the header is not found in the packet schemes
        """
        if header not in self.packet_schemes:
            raise ValueError(f"Header {header} not found in packet schemes.")

        return Packet(header,
                      self.packet_schemes[header].fmt,
                      self.packet_schemes[header].single_instance,
                      *self.packet_schemes[header].fields)

    def build_from_raw(self, header: str, raw: bytes) -> Packet:
        """Builds a packet from raw bytes.

        :param header: the header of the packet
        :type header: str
        :param raw: the raw bytes of the packet body
        :type raw: bytes
        :return: the built packet
        :rtype: Packet
        :raises ValueError: if the header is not found in the packet schemes
        :raises ValueError: if the packet has invalid data
        """
        if header not in self.packet_schemes:
            raise ValueError(f"Header {header} not found in packet schemes.")

        new_pack = self.build_from_header(header)

        try:
            fields = dict(zip(self.fields_of(header),
                              struct.unpack(">" + self.format_of(header),
                                            raw)))
        except struct.error as e:
            raise ValueError(f"Packet {header} has invalid raw data: {e}")

        new_pack.set_fields(**fields)
        return new_pack

    def size_of(self, header: str) -> int:
        """Returns the number of bytes the packet has.

        :param header: the header of the packet
        :type header: str
        :return: the number of bytes the packet has
        :rtype: int
        :raises ValueError: if the header is not found in the packet schemes
        """
        if header not in self.packet_schemes:
            raise ValueError(f"Header {header} not found in packet schemes.")

        return struct.calcsize("=" + self.format_of(header))

    def format_of(self, header: str) -> str:
        """Returns the format of the packet struct.

        :param header: the header of the packet
        :type header: str
        :return: the format of the packet
        :rtype: str
        :raises ValueError: if the header is not found in the packet schemes
        """
        if header not in self.packet_schemes:
            raise ValueError(f"Header {header} not found in packet schemes.")

        return self.packet_schemes[header].fmt

    def fields_of(self, header: str) -> List[str]:
        """Returns the fields of the packet.

        :param header: the header of the packet
        :type header: str
        :return: the fields of the packet
        :rtype: List[str]
        :raises ValueError: if the header is not found in the packet schemes
        """
        if header not in self.packet_schemes:
            raise ValueError(f"Header {header} not found in packet schemes.")

        return self.packet_schemes[header].fields

    def _parse_packet_directory(self) -> dict[str, Packet_Representation]:
        """Returns a dictionary of all packet representations to their header.

        :return: a dictionary of all packet's stractures
        :rtype: dict[str, Packet_Representation]
        """
        general_dict = {}  # A dictionary of all packet's stractures.
        # Go over all of the files and connectes thier packet structures into
        # one dictionary.
        for filename in os.listdir(self.packet_directory):
            if filename.endswith(".packet"):
                conf_yaml = open(os.path.join(self.packet_directory, filename), "r")
                yaml_data = yaml.load(conf_yaml, Loader=yaml.FullLoader)
                conf_yaml.close()

                header = next(iter(yaml_data))
                fields = list(yaml_data[header].keys())
                single_instance = False
                if "single_instance" in fields:
                    single_instance = yaml_data[header]["single_instance"]
                    fields.remove("single_instance")
                fmt = "".join(yaml_data[header][field] for field in fields)

                rep = Packet_Representation(header, fields, fmt,
                                            single_instance)
                general_dict.update({header: rep})

        return general_dict
