from threading import Thread
from queue import Queue
import socket

from PUNet.socket_handlers.ListenerSocket import ListenerSocket
from PUNet.data_objects.DataObject import DataObject


class ListenerThread(Thread):
    def __init__(self, port: int, local: bool, daemon: bool) -> None:
        super().__init__(daemon=daemon)
        self.listener_socket: ListenerSocket = ListenerSocket(port, local)
        self.data_queue: "Queue[DataObject]" = Queue()

        self.running = True

    def run(self) -> None:
        self.listener_socket.accept()
        while self.running:
            try:
                self.data_queue.put(self.listener_socket.get_data())
            except socket.error as e:
                print("Failed to recieve data from sender: " + e)

    def getData(self) -> DataObject:
        return self.data_queue.get()

    def stop(self) -> None:
        self.running = False
