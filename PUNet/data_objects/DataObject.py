from typing import List
import struct

from DataHeader import DataHeader


class DataObject:
    def __init__(self, header: DataHeader, values: List[int]) -> None:
        self.header: DataHeader = header
        self.values: List[int] = values

    def get_header(self) -> DataHeader:
        return self.header

    def get_values(self) -> List[int]:
        return self.values

    def serialize(self) -> bytes:
        return struct.pack(
            f"h{len(self.values)}i", self.header.value, *self.values
        )
