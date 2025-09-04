package _365.telegram;

import _365.telegram.db.DatabaseManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.*;

public class Server {
    private final List<ClientHandler> clients = new ArrayList<>();
    private static final Map<String, String> pendingCodes = new HashMap<>();
    private final Map<String, User> usersByPhone = new HashMap<>();
    private final Map<String, ClientHandler> onlineUsers = new HashMap<>();
    private final Map<UUID, Group> groupsById = new HashMap<>();
    private final Map<UUID, Channel> channelsById = new HashMap<>();
    private final Map<String, UUID> userToGroupByInviteCode = new HashMap<>();
    private final Map<String, UUID> privateChatPins = new HashMap<>();
    private final Map<UUID, Message> allMessages = new HashMap<>();

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
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

    public synchronized boolean isPhoneRegistered(String phone) {
        return usersByPhone.containsKey(phone);
    }

    public static synchronized String generateVerificationCode(String phone) {
        Random generator = new Random(Instant.now().toEpochMilli());
        String code = String.valueOf((int) ((Math.abs(generator.nextInt() % 9000)) + 1000));
        pendingCodes.put(phone, code);
        System.out.println("[Server] Verification code for " + phone + ": " + code);
        return code;
    }

    public synchronized boolean verifyCode(String phone, String code) {
        return code.equals(pendingCodes.get(phone));
    }

    public synchronized void registerUser(String phone, User user) {
        usersByPhone.put(phone, user);
        pendingCodes.remove(phone);
    }

    public synchronized void addOnlineUser(String username, ClientHandler handler) {
        onlineUsers.put(username, handler);
    }

    public synchronized void removeClient(ClientHandler handler) {
        clients.remove(handler);
        if (handler.getUsername() != null) {
            onlineUsers.remove(handler.getUsername());
            System.out.println(handler.getUsername() + " disconnected.");
        }
    }

    public synchronized UUID createGroup(String name, String owner, boolean isPublic) {
        Group group = new Group(name, owner, isPublic);
        groupsById.put(group.getGroupId(), group);
        return group.getGroupId();
    }

    public synchronized Group getGroupById(UUID id) {
        return groupsById.get(id);
    }

    public synchronized UUID createChannel(String name, String description, String owner, boolean isPublic) {
        Channel channel = new Channel(name, description, owner, isPublic);
        channelsById.put(channel.getChannelId(), channel);
        return channel.getChannelId();
    }

    public synchronized Channel getChannelById(UUID id) {
        return channelsById.get(id);
    }

    public synchronized void broadcastToChannel(Channel channel, Message message) {
        for (String subscriber : channel.getSubscribers()) {
            ClientHandler handler = onlineUsers.get(subscriber);
            if (handler != null) {
                handler.sendMessage(message);
                allMessages.put(message.getMessageId(), message);
            }
        }
    }

    public synchronized void broadcast(Message message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
            allMessages.put(message.getMessageId(), message);
        }
    }

    public synchronized void broadcastToGroup(Group group, Message message) {
        for (String member : group.getMembers()) {
            ClientHandler handler = onlineUsers.get(member);
            if (handler != null) {
                handler.sendMessage(message);
                allMessages.put(message.getMessageId(), message);
            }
        }
    }

    public Message findMessageById(UUID id) {
        return allMessages.get(id);
    }

    public void replaceMessage(UUID oldId, Message newMsg) {
        allMessages.put(oldId, newMsg);
    }

    public void broadcastMessageEdit(Message editedMsg) {
        Message notification = new Message("SERVER", editedMsg.getReceiverId(),
                "Message edited", Message.MessageType.MESSAGE_EDIT_NOTIFICATION);

        routeMessage(editedMsg);
        routeMessage(notification);
    }

    public void broadcastMessageDelete(UUID messageId, String receiver) {
        Message notification = new Message("SERVER", receiver,
                messageId.toString(), Message.MessageType.MESSAGE_DELETE_NOTIFICATION);
        routeMessage(notification);
    }

    public boolean canModerateMessage(Message msg, String byUser) {
        if (msg.getMessageType() == Message.MessageType.GROUP_MESSAGE) {
            Group group = getGroupById(UUID.fromString(msg.getReceiverId()));
            return group != null && group.isAdmin(byUser);
        }
        return false;
    }

    public String normalizeChatKey(String u1, String u2) {
        return u1.compareTo(u2) < 0 ? u1 + ":" + u2 : u2 + ":" + u1;
    }

    public Map<String, UUID> getPrivateChatPins() {
        return privateChatPins;
    }

    public ClientHandler getOnlineUser(String username) {
        return onlineUsers.get(username);
    }

    public synchronized void routeMessage(Message message) {
        switch (message.getMessageType()) {
            case PRIVATE:
                String receiver = message.getReceiverId();
                ClientHandler target = onlineUsers.get(receiver);
                if (target != null) {
                    target.sendMessage(message);
                    allMessages.put(message.getMessageId(), message);
                } else {
                    System.out.println("User " + receiver + " not online.");
                }
                break;

            case GROUP:
            case CHANNEL:
                broadcast(message);
                break;

            case SYSTEM:
                broadcast(message);
                break;
        }
    }

    public static void main(String[] args) {
        DatabaseManager.connect();
        Server server = new Server();
        server.start(12345);
    }
}