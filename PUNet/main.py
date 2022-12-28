from time import sleep

from .socket_handlers.SenderSocket import SenderSocket
from .thread_handlers.ListenerThread import ListenerThread
from .data_objects.DataObject import DataObject
from .data_objects.DataHeader import DataHeader


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
    sender_sock.connect("127.0.0.1", 6666)
    while True:
        sleep(1)
        new_pack = DataObject(DataHeader.EXAMPLE, [1], [2.4, 6.7])
        sender_sock.send_data(new_pack)
        print(f'sent data: {str(new_pack)}')


def main():
    # sender()
    listener()


if __name__ == "__main__":
    main()
