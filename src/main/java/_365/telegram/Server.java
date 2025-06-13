package _365.telegram;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private final List<ClientHandler> clients = new ArrayList<>();

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("port: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void broadcast(Message message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public synchronized void routeMessage(Message message) {
        switch (message.getMessageType()) {
            case PRIVATE:
                for (ClientHandler client : clients) {
                    if (client.getUsername() != null && client.getUsername().equals(message.getReceiverId())) {
                        client.sendMessage(message);
                        break;
                    }
                }
                break;

            case GROUP:
                // TODO
            case CHANNEL:
                // TODO
                broadcast(message);
                break;

            case SYSTEM:
                broadcast(message);
                break;
        }
    }

    public synchronized void removeClient(ClientHandler handler) {
        clients.remove(handler);
        System.out.println(handler.getUsername() + " left.");
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start(12345);
    }
}
