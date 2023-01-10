from time import sleep

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

    while True:
        pack: Packet = listener_thread.get_packet()
        print(str(pack))

        match pack.header:
            case "test":
                print(pack.get_field("test"))


def sender():
    sender_sock: SenderSocket = SenderSocket()
    builder: PacketBuilder = PacketBuilder("packets")

    while not sender_sock.is_connected():
        try:
            sender_sock.connect("10.22.30.2", 5800)
        except Exception as e:
            print(e)
    print("connected")
    while True:
        sleep(1)
        new_pack: Packet = builder.build_from_fields(
            "test", test=1, test2=2
        )
        sender_sock.send_data(new_pack)
        print(f'sent data: {str(new_pack)}, ser: {str(new_pack.serialize())}')


def main():
    # sender()
    listener()


if __name__ == "__main__":
    main()
