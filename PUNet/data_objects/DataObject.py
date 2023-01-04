from __future__ import annotations
from typing import List
import struct
from abc import ABC, abstractmethod, abstractclassmethod

from .DataHeader import DataHeader


class DataObject(ABC):
    """A container for packet header and body.
    Implements self serialization
    """
    def __init__(self) -> None:
        pass

    @classmethod
    def from_bytes(cls, bytes: bytes) -> DataObject:
        """constructs a DataObject from the raw unpacked bytes"""
        return cls(*struct.unpack(cls.struct_format(), bytes))

    @abstractclassmethod
    def header(cls) -> DataHeader:
        raise NotImplementedError

    @abstractclassmethod
    def struct_format(cls) -> str:
        raise NotImplementedError

    @abstractmethod
    def as_list(self) -> List[any]:
        """a function that returns a list representing the DataObject values
        the values are indexed like the format

        :return: a list representing the DataObject
        :rtype: List[any]
        """
        raise NotImplementedError

    def serialize(self) -> bytes:
        """serializes the object into a packed struct bytes

        :return: the packed bytes
        :rtype: bytes
        """
        return struct.pack(format, *self.as_list())

    def __str__(self) -> str:
        return f"[{str(self.header())}]: {self.as_list()}"
