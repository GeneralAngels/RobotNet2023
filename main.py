from time import sleep
import struct

from PUNet.socket_handlers.SenderSocket import SenderSocket
from PUNet.thread_handlers.ListenerThread import ListenerThread
from PUNet.Packet import Packet
from PUNet.PacketBuilder import PacketBuilder


def listener():
    builder: PacketBuilder = PacketBuilder("packets")
    listener_thread: ListenerThread = ListenerThread(
        port=6666, packet_builder=builder
    )

    listener_thread.start()
    print('listener started')

    while True:  # mimics mainloop
        pack: Packet = listener_thread.get_packet()
        print(str(pack))

        match pack.header:
            case "EXAMPLE_PACKET":
                f1: int = pack.get_field("ifield1")
                f2: float = pack.get_field("dfield2")
                print(f1, f2)


def sender():
    sender_sock: SenderSocket = SenderSocket()
    builder: PacketBuilder = PacketBuilder("packets")

    while not sender_sock.is_connected():
        try:
            sender_sock.connect("127.0.0.1", 6666)
        except Exception as e:
            print(e)
    print("connected")

    while True:
        sleep(1)
        new_pack: Packet = builder.build_from_header(
            "EXAMPLE_PACKET"
        )
        new_pack.set_fields(ifield1=2, dfield2=4.7)
        sender_sock.send_data(new_pack)
        print(f'sent data: {str(new_pack)}, ser: {str(new_pack.serialize())}')


def tests():
    builder: PacketBuilder = PacketBuilder("packets")
    print(builder.packet_schemes)
    new_pack: Packet = builder.build_from_header(
        "EXAMPLE_PACKET"
    )
    new_pack.set_fields(ifield1=2, dfield2=4.7)

    ser = new_pack.serialize()
    print("Serialized: " + str(ser))

    header_length = struct.unpack(">h", ser[0:2])[0]
    print("raw header length: " + str(header_length))

    # struct strings are all chars + empty byte
    raw_header = ser[2:2 + header_length]
    print("raw header: " + str(raw_header))
    print(ser)
    header: str = (
        struct.unpack(f">{header_length}s", raw_header)[0]
    ).decode("utf-8")
    print("header: " + str(header))

    print("raw body: " + str(ser[
        2 + header_length: 2 + header_length + builder.size_of(header)]))
    print("format: " + builder.format_of(header))
    print("size: " + str(builder.size_of(header)))
    n = builder.build_from_raw(
        header,
        ser[2 + header_length: 2 + header_length + builder.size_of(header)]
    )
    print(str(n))


def main():
    sender()
    listener()
    # tests()


if __name__ == "__main__":
    main()
