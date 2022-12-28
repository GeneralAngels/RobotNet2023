from typing import List
import struct

from .DataHeader import DataHeader


class DataObject:
    """A container for packet header and body.
    Implements self serialization
    """
    def __init__(self,
                 header: DataHeader, ivalues: List[int], dvalues: List[float]
                 ) -> None:
        """
        :param header: the packet type
        :type header: DataHeader
        :param ivalues: the int part of the packet body
        :type ivalues: List[int]
        :param dvalues: the double part of the packet body
        :type dvalues: List[double]
        """
        self.header: DataHeader = header
        self.ivalues: List[int] = ivalues
        self.dvalues: List[float] = dvalues

    def get_header(self) -> DataHeader:
        return self.header

    def get_ivalues(self) -> List[int]:
        return self.ivalues

    def get_dvalues(self) -> List[float]:
        return self.dvalues

    def serialize(self) -> bytes:
        """serializes the object into a packed struct bytes

        :return: the packed bytes
        :rtype: bytes
        """
        return struct.pack(
            f">h{len(self.ivalues)}i{len(self.dvalues)}d",
            self.header.value, *self.ivalues, *self.dvalues
        )

    def __str__(self) -> str:
        return f"{self.header.name}: {str(self.ivalues)}, {str(self.dvalues)}"
