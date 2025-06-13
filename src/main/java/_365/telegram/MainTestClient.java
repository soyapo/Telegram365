package _365.telegram;

import java.util.Scanner;

public class MainTestClient {
    public static void main(String[] args) {
        ClientSocketHandler client = new ClientSocketHandler();

        if (!client.connect("localhost", 12345)) {
            System.out.println("failed");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your userId: ");
        String userId = scanner.nextLine();
        client.setUserId(userId);

        Message loginMessage = new Message(userId, "SERVER", "LOGIN", Message.MessageType.SYSTEM);
        client.sendMessage(loginMessage);

        client.setOnMessageReceived(msg -> {
            System.out.println(msg.getSenderId() + ": " + msg.getContent());
            System.out.println("Enter reciever ID: ");
            System.out.flush();
        });

        while (true) {
            String receiver = scanner.nextLine();

            System.out.print("Enter Message: ");
            System.out.flush();
            String content = scanner.nextLine();

            Message msg = new Message(userId, receiver, content, Message.MessageType.PRIVATE);
            client.sendMessage(msg);
            System.out.println("Enter reciever ID: ");
        }
    }
}
