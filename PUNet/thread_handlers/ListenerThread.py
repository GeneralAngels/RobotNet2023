from threading import Thread
from queue import Queue
import socket

from PUNet.socket_handlers.ListenerSocket import ListenerSocket
from PUNet.data_objects.DataObject import DataObject


class ListenerThread(Thread):
    def __init__(self, port: int) -> None:
        super().__init__()
        self.listener_socket: ListenerSocket = ListenerSocket(port)
        self.data_queue: "Queue[DataObject]" = Queue()

    def run(self) -> None:
        while True:
            try:
                self.data_queue.put(self.listener_socket.get_data())
            except socket.error as e:
                print("Failed to recieve data from sender: " + e)

    def getData(self) -> DataObject:
        return self.data_queue.get()
