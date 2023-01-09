from typing import Any

from PUNet.Packet import Packet


class PacketBuilder:
    def __init__(self, packet_directory: str) -> None:
        self.packet_directory = packet_directory

        self.packets = {}
        self.init_packets()

    def init_packets(self) -> None:
        pass

    def build_from_values(header: str, *values: Any) -> Packet:
        pass

    def build_from_fields(header: str, **fieldes: Any) -> Packet:
        pass

    def build_from_raw(header: str, raw: bytes) -> Packet:
        pass

    def size_of(header: str) -> int:
        pass

    def format_of(header: str) -> str:
        pass
