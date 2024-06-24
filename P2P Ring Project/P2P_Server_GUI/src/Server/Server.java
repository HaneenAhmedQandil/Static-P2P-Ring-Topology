package Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static ArrayList<Client> clients = new ArrayList<>();
    public static ArrayList<String> everJoinedClients = new ArrayList<>();

    public static boolean isClientExists(String name) {
        for (Client client : clients) {
            if (client.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static void removeClient(String name) {
        for (int i = 0; i < Server.clients.size(); i++) {
            Client client = Server.clients.get(i);
            if (client.getName().equals(name)) {
                Client previousClient = Server.clients.get((i - 1 + clients.size()) % clients.size());
                Client nextClient = Server.clients.get((i + 1) % clients.size());
                previousClient.setNextClient(nextClient);
                Server.clients.remove(i);
                break;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4000);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            String clientRequest = in.readLine();

            String[] parts = clientRequest.split(" ");
            if (parts[0].equals("@goodbye") && parts.length > 1) {
                String clientName = parts[1];
                removeClient(clientName);
                System.out.println("Player " + clientName + " has left the game."); 
            } else if (parts[0].equals("@check") && parts.length > 1) {
                String recipientName = parts[1];
                if (isClientExists(recipientName)) {
                    out.println("EXISTS");
                } else if (everJoinedClients.contains(recipientName)) {
                    out.println("LEFT");
                } else {
                    out.println("NOT EXISTS");
                }
            } else {
                String clientName = clientRequest;
                int clientPort = clientSocket.getPort();
                if (isClientExists(clientName)) {
                    out.println("EXISTS");
                } else {
                    clients.add(new Client(clientName, clientPort));
                    everJoinedClients.add(clientName);
                    out.println("OK");
                    System.out.println("Player " + clientName + " has joined the game.");  
                }
            }
            clientSocket.close();
        }
    }

}

class Client {

    private String clientName;
    private int clientPort;
    private Client nextClient;

    public Client(String name, int clientPort) {
        this.clientName = name;
        this.clientPort = clientPort;
    }

    public String getName() {
        return clientName;
    }

    public int getPort() {
        return clientPort;
    }

    public Client getNextClient() {
        return nextClient;
    }

    public void setNextClient(Client nextClient) {
        this.nextClient = nextClient;
    }
}
