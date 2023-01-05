import struct
from typing import List

from .DataObject import DataObject
from .DataHeader import DataHeader


class ExampleObject(DataObject):
    def __init__(self, *values: any) -> None:
        super().__init__()
        self.dummyint: int = values[0]
        self.dummydouble1: float = values[1]
        self.dummydouble2: float = values[2]

    @classmethod
    def get_header(cls) -> DataHeader:
        return DataHeader.EXAMPLE

    @classmethod
    def get_struct_format(cls) -> str:
        return "idd"

    def as_list(self) -> List[any]:
        return [self.get_header().value, self.dummyint, self.dummydouble1, self.dummydouble2]
