from typing import Any, Dict
import struct


class Packet:
    """A Type for all packets, holds a header and struct format.
    A packet should only be created using a PacketBuilder.
    """
    def __init__(self, header: str, fmt: str,
                 **data: Any) -> None:
        self.header = header
        self.fmt = fmt
        self.data: Dict[str, Any] = data

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

    def get_item(self, key: str) -> Any:
        return self.data[key]

    def set_item(self, key: str, new_value: Any) -> None:
        self.data[key] = new_value

    def __str__(self) -> str:
        return self.header + str(self.data.items())
