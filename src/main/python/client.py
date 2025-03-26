from sys import argv

from socket import socket, AF_INET, SOCK_STREAM

import json

def getMessage(client: socket) -> dict | None:
    buffer = bytearray()

    while True:
        chunk = client.recv(1024)
        buffer.extend(chunk)

        if len(chunk) == 0:
            return None
        if chunk[-1] == ord("\n"):
            message = buffer.decode().strip()
            return json.loads(message)

def sendMessage(client: socket, response: dict) -> None:
    message = json.dumps(response) + "\n"
    client.sendall(message.encode())

async def main() -> None:
    port = int(argv[1])
    client = socket(AF_INET, SOCK_STREAM)
    client.connect(("localhost", port))

    print(getMessage(client), flush=True)

    client.close()

if __name__ == "__main__":
    main()