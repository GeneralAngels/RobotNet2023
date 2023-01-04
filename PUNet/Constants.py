from typing import Dict, Tuple

from .data_objects.DataHeader import DataHeader


class NetworkConstants:  # TODO put sizes in config
    headerPacketFormats: Dict[DataHeader, str] = {
        DataHeader.EXAMPLE: 'idd',
    }
