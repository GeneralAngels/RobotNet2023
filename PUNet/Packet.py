from typing import Any, Dict
import struct


class Packet:
    """A Type for all packets, holds a header and struct format.
    A packet should only be created using a PacketBuilder.
    """
    def __init__(self, header: str, fmt: str,
                 *fields: Any) -> None:
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
        """
        return struct.pack(">hs" + self.fmt,
                           len(self.header), self.header, *self.data.values())

    def get_field(self, field: str) -> Any:
        return self.data[field]

    def set_fields(self, **fields: Any) -> None:
        for field, new_value in fields.items():
            self.data[field] = new_value

    def __str__(self) -> str:
        return self.header + str(self.data.items())
