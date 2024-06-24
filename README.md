# Static-P2P-Ring-Topology
A hybrid Peer-to-Peer chat system leveraging ring topology for decentralized communication, enhanced with a central server for efficient peer management.

## Features

- **Decentralized Communication:** Utilizes a ring topology for direct peer-to-peer messaging.
- **Central Server:** Simplifies peer management, handling joining and leaving processes.
- **Encryption/Decryption:** Messages are encrypted for privacy, with a simple shift-back method for decryption.
- **GUI Support:** Includes GUI components for easy interaction with the chat system.

## Decryption Method

1. **Shift Back:** Each character in the encrypted message is shifted back by one position in the Unicode table by subtracting 1 from its Unicode value.
2. **Reverse:** The message is then reversed to obtain the original message.

## Classes Overview

- **Server:** Manages clients, maintaining lists of active and historical clients, and handles client requests.
- **Client:** Represents a chat client with a name, port number, and reference to the next client in the ring.
- **LoginFrame:** GUI for clients to join the chat, checking for name uniqueness and initiating peer creation.
- **Message:** Represents a chat message with ID, sender, recipient, content, and privacy flag.
- **PeerFrame:** GUI for sending/receiving messages, with functionalities to leave chat and view instructions.
- **Peer1, Peer2, Peer3, Peer4:** Entry points for clients, creating a LoginFrame for chat participation.
- **Peer:** Core of the peer-to-peer system, handling message forwarding, encryption/decryption, and peer management.

