package _365.telegram;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Server server;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {

        }
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void run() {
        try {
            Message loginMsg = (Message) in.readObject();
            this.username = loginMsg.getSenderId();
            System.out.println(username + "connected");

            server.broadcast(new Message("Server", null, username + " joined", Message.MessageType.SYSTEM));

            Message message;
            while ((message = (Message) in.readObject()) != null) {
                System.out.println(username + ": " + message.getContent());
                server.routeMessage(message); // let  server decide what to do
            }
        } catch (IOException | ClassNotFoundException e) {

        } finally {
            server.removeClient(this);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
