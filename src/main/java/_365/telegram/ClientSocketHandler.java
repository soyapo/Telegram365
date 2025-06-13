package _365.telegram;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class ClientSocketHandler {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Thread listenerThread;
    private Consumer<Message> onMessageReceived;

    private String userId;

    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            startListening();
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void startListening() {
        listenerThread = new Thread(() -> {
            try {
                while (true) {
                    Message incoming = (Message) in.readObject();
                    if (onMessageReceived != null) {
                        onMessageReceived.accept(incoming);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void setOnMessageReceived(Consumer<Message> callback) {
        this.onMessageReceived = callback;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void disconnected() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {

        }
    }
}
