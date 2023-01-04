from typing import Dict, Tuple

from .data_objects.DataHeader import DataHeader


class NetworkConstants:  # TODO put sizes in config
    headerPacketSizes: Dict[DataHeader, Tuple[int, int]] = {
        DataHeader.EXAMPLE: (1, 2),
    }
