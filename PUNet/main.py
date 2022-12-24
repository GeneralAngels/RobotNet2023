from time import sleep

from socket_handlers.SenderSocket import SenderSocket
from data_objects.DataObject import DataObject
from data_objects.DataHeader import DataHeader


def main():
    sender_sock: SenderSocket = SenderSocket()
    sender_sock.connect("127.0.0.1", 6666)
    while True:
        sleep(1)
        new_pack = DataObject(DataHeader.EXAMPLE, [1, 2])
        sender_sock.send_data(new_pack)


if __name__ == "__main__":
    main()
