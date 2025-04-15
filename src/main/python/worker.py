from sys import argv
from socket import socket, AF_INET, SOCK_STREAM
import json

def getMessage(client: socket, buffer: bytearray) -> dict | None:
    while b"\n" not in buffer:
        try:
            chunk = client.recv(bufsize=1024)
        except Exception as e:
            print(f"Error receiving data: {e}", flush=True)
        if len(chunk) == 0:
            return None
            
        buffer.extend(chunk)
    
    message, buffer = buffer.split(sep=b"\n", maxsplit=1)
    message = message.decode().strip()
    return json.loads(message)

def sendMessage(client: socket, response: dict) -> None:
    message = json.dumps(response) + "\n"
    client.sendall(message.encode())

def main() -> None:
    port = int(argv[1])
    client = socket(AF_INET, SOCK_STREAM)
    client.connect(("localhost", port))
    buffer = bytearray()

    print(getMessage(client=client, buffer=buffer), flush=True)

    client.close()

if __name__ == "__main__":
    main()