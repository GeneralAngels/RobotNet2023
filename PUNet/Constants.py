from typing import Dict

from .data_objects.DataHeader import DataHeader


class NetworkConstants:  # TODO put sizes in config
    headerPacketSizes: Dict[DataHeader, int] = {
        DataHeader.EXAMPLE: 2,
    }
