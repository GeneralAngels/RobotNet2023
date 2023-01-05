from time import sleep
import struct

from PUNet.socket_handlers.SenderSocket import SenderSocket
from PUNet.thread_handlers.ListenerThread import ListenerThread
from PUNet.data_objects.ExampleObject import ExampleObject


def listener():
    listener_thread: ListenerThread = ListenerThread(
        port=6666, local=True, daemon=True
    )

    listener_thread.start()
    print('listener started')

    while True:
        print(str(listener_thread.getData()))


def sender():
    sender_sock: SenderSocket = SenderSocket()

    while not sender_sock.is_connected():
        try:
            sender_sock.connect("127.0.0.1", 6666)
        except Exception as e:
            print(e)
    print("connected")
    while True:
        sleep(1)
        new_pack = ExampleObject(1, 2.4, 6.7)
        sender_sock.send_data(new_pack)
        print(f'sent data: {str(new_pack)}, ser: {str(new_pack.serialize())}')


def main():
    sender()
    # listener()
    # example_object = ExampleObject.from_bytes(">" + struct.pack("idd", 5, 6.7, 8.7))
    # print(str(example_object))


if __name__ == "__main__":
    main()
