from typing import Any, Dict
import struct


class Packet:
    """A Type for all packets, holds a header and struct format.
    A packet should only be created using a PacketBuilder.
    """
    def __init__(self, header: str, fmt: str,
                 *fields: str) -> None:
        """
        Creates a new packet.

        :param header: the header of the packet
        :type header: str
        :param fmt: the struct format of the packet
        :type fmt: str
        :param fields: the fields of the packet
        :type fields: str
        """
        self.header = header
        self.fmt = fmt
        self.data: Dict[str, Any] = {field: None for field in fields}

    def serialize(self) -> bytes:
        """Serializes the packet into a byte array.
        the first 2 bytes are the length of the header,
        the next bytes are the header,
        and the last bytes are the data.

        :return: the serialized packet
        :rtype: str
        :raises ValueError: if the packet has invalid data
        """

        try :
            return struct.pack(
                f">h{len(self.header)}s{self.fmt}",  # format
                len(self.header), self.header.encode("utf-8"),  # header
                *self.data.values()  # data
            )
        except struct.error as e:
            raise ValueError(f"Packet {self.header} has invalid data: {e}")

    def get_field(self, field: str) -> Any:
        """Returns the value of the given field.

        :param field: the field to get the value of
        :type field: str
        :return: the value of the field
        :rtype: Any
        :raises ValueError: if the field is not found in the packet
        """
        if field not in self.data:
            raise ValueError(f"Field {field} not found in packet.")

        return self.data[field]

    def set_fields(self, **fields: Any) -> None:
        """Sets the values of the given fields.

        :raises ValueError: if a field is not found in the packet
        """
        for field, new_value in fields.items():
            if field not in self.data:
                raise ValueError(f"Field {field} not found in packet.")
            self.data[field] = new_value

    def __str__(self) -> str:
        return self.header + str(self.data.items())