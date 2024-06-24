package Peers;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Player extends Thread 
{

    public String peerName;
    private int peerPort;
    private String nextPeerHost;
    private int nextPeerPort;
    private PlayerChatFrame frame;

    public void setPeerFrame(PlayerChatFrame frame) {
        this.frame = frame;
    }

    public void setNextHost(String nextHost) {
        this.nextPeerHost = nextHost;
    }

    public void setNextPort(int nextPort) {
        this.nextPeerPort = nextPort;
    }

    public Player(String peerName, int peerPort, String nextPeerHost, int nextPeerPort) {
        this.peerName = peerName;
        this.peerPort = peerPort;
        this.nextPeerHost = nextPeerHost;
        this.nextPeerPort = nextPeerPort;

        new Thread(() -> {
            boolean sent = false;
            while (!sent) {
                try {
                    Message welcomeMessage = new Message(peerName, "All", "has joined the chat!", false);
                    sendMessage(welcomeMessage);
                    sent = true;
                } catch (Exception e) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }).start();
    }

    public void removeSelf() {
        Message goodbyeMessage = new Message(peerName, "All", "has left the chat!", false);
        sendMessage(goodbyeMessage);
        try ( Socket socket = new Socket("localhost", 4000);  PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("@goodbye " + peerName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
    boolean sent = false;
    while (!sent) {
        try ( Socket socket = new Socket(nextPeerHost, nextPeerPort);  PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            if (message.isPrivate()) {
                try ( Socket serverSocket = new Socket("localhost", 4000);  PrintWriter CoordinatorOut = new PrintWriter(serverSocket.getOutputStream(), true);  BufferedReader serverIn = new BufferedReader(
                        new InputStreamReader(serverSocket.getInputStream()))) {
                    CoordinatorOut.println("@check " + message.getRecipient());
                    String response = serverIn.readLine();
                    if ("EXISTS".equals(response)) 
                    {
                        message = new Message(message.getSender(), message.getRecipient(), encrypt(message.getContent()), message.isPrivate());
                    } else if ("LEFT".equals(response)) {
                        JOptionPane.showMessageDialog(frame, "This player has left the chat.");
                        return;
                    } else {
                        JOptionPane.showMessageDialog(frame, "This player not found, please enter a valid player name.");
                        return;
                    }
                }
            }
            out.println(message.toString());
            sent = true;
        } catch (IOException e) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
    
    

    public static String encrypt(String msg) {
    String encryptedMsg = "";
    for (int i = msg.length() - 1; i >= 0; i--) {
        encryptedMsg += (char) (msg.charAt(i) + 1);
    }
    encryptedMsg = "Encrypted: " + encryptedMsg;
    return encryptedMsg;
}

    public static String decrypt(String msg) {
    String decryptedMsg = "";
    if (msg.length() < 11) {
        return msg;
    }
    msg = msg.substring(11);
    for (int i = 0; i < msg.length(); i++) {
        decryptedMsg += (char) (msg.charAt(i) - 1);
    }
    decryptedMsg = new StringBuilder(decryptedMsg).reverse().toString();
    return decryptedMsg;
}

public static String checkDecrypt(String msg) {
    String recieved = "";
    if (msg.length() > 11) 
    {
        recieved = msg.substring(0, 11);
    }
    String newStr = msg;
    while (recieved.equals("Encrypted: ")) {
        newStr = decrypt(newStr);
        if (newStr.length() > 11) {
            recieved = newStr.substring(0, 11);
        } else {
            break;
        }
    }
    return newStr;
}

    public void run() {
    try ( ServerSocket serverSocket = new ServerSocket(this.peerPort)) {
        while (true) {
            try ( Socket clientSocket = serverSocket.accept();  BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String line = in.readLine();
                if (line != null && !line.equals("wrong msg")) {
                    Message message = new Message(line);
                    if (message.isPrivate()) {
                        if (message.getRecipient().equals(peerName) && !message.getSender().equals(peerName)) {
                            frame.appendMessage(message.getSender() + " (private): " + checkDecrypt(message.getContent()));
                        } else if (!message.getRecipient().equals(peerName) && !message.getSender().equals(peerName)) {
                            frame.appendMessage(message.getSender() + " (private) to " + message.getRecipient() + " : " + message.getContent());
                        }
                        if (!message.getSender().equals(peerName)) {
                            sendMessage(message);
                        } else {
                            if (message.getRecipient().equals(peerName)) {
                                frame.appendMessage("You (private) to Yourself: " + checkDecrypt(message.getContent()));
                            } else {
                                frame.appendMessage("You (private) to " + message.getRecipient() + " : " + checkDecrypt(message.getContent()));
                            }
                        }
                    } else {
                        if (!message.getSender().equals(peerName)) {
                            frame.appendMessage(message.getSender() + ": " + message.getContent());
                            sendMessage(message);
                        } else {
                            frame.appendMessage("You: " + message.getContent());
                        }
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Error in receiving message: " + e.getMessage());
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(frame, "Error in server socket: " + e.getMessage());
    }
}
}
