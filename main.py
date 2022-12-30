from time import sleep

from PUNet.socket_handlers.SenderSocket import SenderSocket
from PUNet.thread_handlers.ListenerThread import ListenerThread
from PUNet.data_objects.DataObject import DataObject
from PUNet.data_objects.DataHeader import DataHeader


def listener():
    listener_thread: ListenerThread = ListenerThread(
        port=6666, local=False, daemon=True
    )

    listener_thread.start()
    print('listener started')

    while True:
        print(str(listener_thread.getData()))


def sender():
    sender_sock: SenderSocket = SenderSocket()

    while not sender_sock.is_connected():
        try:
            sender_sock.connect("10.22.30.2", 5800)
        except Exception as e:
            print(e)
    print("connected")
    while True:
        sleep(1)
        new_pack = DataObject(DataHeader.EXAMPLE, [1], [2.4, 6.7])
        sender_sock.send_data(new_pack)
        print(f'sent data: {str(new_pack)}, ser: {str(new_pack.serialize())}')


def main():
    # sender()
    listener()


if __name__ == "__main__":
    main()
