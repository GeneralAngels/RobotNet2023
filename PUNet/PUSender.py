import socket
import random
import struct

from time import sleep
from dataclasses import dataclass
from enum import Enum


class Sources(Enum):
    PU='pu'
    RIO='rio'


class Instructions(Enum):
    GOTO=0
    SHOOT=1
    FUCKTHISGUYSMOM=2


@dataclass
class Packet:
    source: str
    instruction: int
    val: int


def main():
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    sock.connect(("localhost", 7777))

    while True:
        packet = Packet(
            source=bytes(Sources.PU.value, "utf-8"),    
            instruction=random.choice(list(Instructions)).value,
            val= random.randint(0, 1024)
        )
        print(packet.__dict__.values())
        p = struct.pack('>shh', *packet.__dict__.values())
        sock.send(p)
        print(p)
        sleep(1)


if __name__ == "__main__":
    main()
