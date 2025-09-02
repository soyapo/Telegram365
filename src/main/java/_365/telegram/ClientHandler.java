package _365.telegram;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import _365.telegram.Chat.Channel;
import _365.telegram.Chat.Group;
import _365.telegram.Chat.Message;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Server server;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;

    private final Map<UUID, Message> messageHistory = new HashMap<>();

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

    public String getUsername() {
        return username;
    }

    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {

        }
    }

    @Override
    public void run() {
        try {
            Message firstMsg = (Message) in.readObject();

            if (firstMsg.getMessageType() == Message.MessageType.REGISTER_PHONE) {
                String phone = firstMsg.getSenderId();

                if (server.isPhoneRegistered(phone)) {
                    sendMessage(new Message("SERVER", phone,
                            "Phone number already registered", Message.MessageType.LOGIN_RESPONSE));
                    socket.close();
                    return;
                }

                String code = server.generateVerificationCode(phone);
                sendMessage(new Message("SERVER", phone,
                        "Verification code sent (check server console)", Message.MessageType.LOGIN_RESPONSE));

                Message codeMsg = (Message) in.readObject();
                if (codeMsg.getMessageType() != Message.MessageType.VERIFY_CODE ||
                        !server.verifyCode(phone, codeMsg.getContent())) {

                    sendMessage(new Message("SERVER", phone,
                            "Invalid verification code", Message.MessageType.LOGIN_RESPONSE));
                    socket.close();
                    return;
                }

                Message profileMsg = (Message) in.readObject();
                if (profileMsg.getMessageType() != Message.MessageType.SET_PROFILE) {
                    sendMessage(new Message("SERVER", phone,
                            "Invalid profile setup message", Message.MessageType.LOGIN_RESPONSE));
                    socket.close();
                    return;
                }

                String[] profileParts = profileMsg.getContent().split("\\|");
                String inputUsername = profileParts[0].trim();
                String bio = profileParts.length > 1 ? profileParts[1].trim() : "";

                User newUser = new User(phone);
                newUser.setUsername(inputUsername);
                newUser.setBio(bio);
                newUser.setStatus("Online");

                this.username = inputUsername;
                server.registerUser(phone, newUser);
                server.addOnlineUser(username, this);

                sendMessage(new Message("SERVER", phone,
                        "Registration complete! Welcome " + username, Message.MessageType.LOGIN_RESPONSE));
                System.out.println("User registered: " + username + " (" + phone + ")");

            } else {
                sendMessage(new Message("SERVER", null,
                        "Invalid initial request", Message.MessageType.SYSTEM));
                socket.close();
                return;
            }

            Message incoming;
            while ((incoming = (Message) in.readObject()) != null) {
                switch (incoming.getMessageType()) {

                    case PRIVATE: {
                        server.routeMessage(incoming);
                        messageHistory.put(incoming.getMessageId(), incoming);

                        Message original = server.findMessageById(incoming.getMessageId());
                        if (original != null && !original.hasRead(username)) {
                            original.addReader(username);

                            Message readNotification = new Message("SERVER", original.getSenderId(),
                                    username + " read your message (" + original.getMessageId() + ")",
                                    Message.MessageType.READ_RECEIPT);

                            ClientHandler sender = server.getOnlineUser(original.getSenderId());
                            if (sender != null) {
                                sender.sendMessage(readNotification);
                            }
                        }

                        break;
                    }

                    case CREATE_GROUP: {
                        String[] parts = incoming.getContent().split("\\|");
                        String groupName = parts[0].trim();
                        boolean isPublic = Boolean.parseBoolean(parts[1].trim());

                        UUID groupId = server.createGroup(groupName, username, isPublic);
                        sendMessage(new Message("SERVER", username,
                                "Group created: " + groupName + " (ID: " + groupId + ")",
                                Message.MessageType.SYSTEM));
                        messageHistory.put(incoming.getMessageId(), incoming);
                        break;
                    }

                    case JOIN_GROUP: {
                        UUID groupId = UUID.fromString(incoming.getContent());
                        Group group = server.getGroupById(groupId);

                        if (group.isBanned(username)) {
                            sendMessage(new Message("SERVER", username, "You are banned from this group", Message.MessageType.SYSTEM));
                            break;
                        }

                        if (group == null) {
                            sendMessage(new Message("SERVER", username, "Group not found", Message.MessageType.SYSTEM));
                            messageHistory.put(incoming.getMessageId(), incoming);
                        } else if (!group.isPublic()) {
                            sendMessage(new Message("SERVER", username, "Cannot join private group", Message.MessageType.SYSTEM));
                            messageHistory.put(incoming.getMessageId(), incoming);
                        } else {
                            group.addMember(username);
                            sendMessage(new Message("SERVER", username, "Joined group: " + group.getName(), Message.MessageType.SYSTEM));
                            messageHistory.put(incoming.getMessageId(), incoming);
                        }
                        break;
                    }

                    case GROUP_MESSAGE: {
                        UUID groupId = UUID.fromString(incoming.getReceiverId());
                        Group group = server.getGroupById(groupId);

                        if (group.isMuted(username)) {
                            sendMessage(new Message("SERVER", username, "You are muted in this group", Message.MessageType.SYSTEM));
                            break;
                        }

                        if (group != null && group.isMember(username)) {
                            group.addMessage(incoming);
                            server.broadcastToGroup(group, incoming);
                            messageHistory.put(incoming.getMessageId(), incoming);
                        } else {
                            sendMessage(new Message("SERVER", username, "You are not in this group", Message.MessageType.SYSTEM));
                            messageHistory.put(incoming.getMessageId(), incoming);
                        }
                        break;
                    }

                    case CREATE_CHANNEL: {
                        String[] parts = incoming.getContent().split("\\|");
                        String channelName = parts[0].trim();
                        String description = parts[1].trim();
                        boolean isPublic = Boolean.parseBoolean(parts[2].trim());

                        UUID channelId = server.createChannel(channelName, description, username, isPublic);
                        sendMessage(new Message("SERVER", username,
                                "Channel created: " + channelName + " (ID: " + channelId + ")",
                                Message.MessageType.SYSTEM));
                        messageHistory.put(incoming.getMessageId(), incoming);
                        break;
                    }

                    case JOIN_CHANNEL: {
                        UUID channelId = UUID.fromString(incoming.getContent());
                        Channel channel = server.getChannelById(channelId);

                        if (channel == null) {
                            sendMessage(new Message("SERVER", username, "Channel not found", Message.MessageType.SYSTEM));
                            messageHistory.put(incoming.getMessageId(), incoming);
                        } else if (!channel.isPublic()) {
                            sendMessage(new Message("SERVER", username, "Cannot join private channel", Message.MessageType.SYSTEM));
                            messageHistory.put(incoming.getMessageId(), incoming);
                        } else {
                            channel.addSubscriber(username);
                            sendMessage(new Message("SERVER", username, "Subscribed to channel: " + channel.getName(), Message.MessageType.SYSTEM));
                            messageHistory.put(incoming.getMessageId(), incoming);
                        }
                        break;
                    }

                    case CHANNEL_MESSAGE: {
                        UUID channelId = UUID.fromString(incoming.getReceiverId());
                        Channel channel = server.getChannelById(channelId);

                        if (channel == null) {
                            sendMessage(new Message("SERVER", username, "Channel not found", Message.MessageType.SYSTEM));
                            messageHistory.put(incoming.getMessageId(), incoming);
                        } else if (!channel.isAdmin(username)) {
                            sendMessage(new Message("SERVER", username, "Only admins can send messages", Message.MessageType.SYSTEM));
                            messageHistory.put(incoming.getMessageId(), incoming);
                        } else {
                            channel.addMessage(incoming);
                            server.broadcastToChannel(channel, incoming);
                            messageHistory.put(incoming.getMessageId(), incoming);
                        }

                        if (channel != null) {
                            Message msg = server.findMessageById(incoming.getMessageId());
                            if (msg != null) {
                                if (!msg.hasRead(username)) {
                                    msg.addReader(username);
                                    msg.incrementViewCount();

                                    Message viewUpdate = new Message("SERVER", msg.getSenderId(),
                                            msg.getViewCount() + " views on message " + msg.getMessageId(),
                                            Message.MessageType.VIEW_COUNTER_UPDATE);
                                    ClientHandler owner = server.getOnlineUser(msg.getSenderId());
                                    if (owner != null) {
                                        owner.sendMessage(viewUpdate);
                                    }
                                }
                            }
                        }

                        break;
                    }

                    case PROMOTE_ADMIN: {
                        String[] parts = incoming.getContent().split("\\|");
                        UUID groupId = UUID.fromString(parts[0].trim());
                        String targetUsername = parts[1].trim();

                        Group group = server.getGroupById(groupId);

                        if (group != null && group.isOwner(username)) {
                            if (!group.getMembers().contains(targetUsername)) {
                                sendMessage(new Message("SERVER", username, "is not a member", Message.MessageType.SYSTEM));
                            } else {
                                group.promoteToAdmin(targetUsername);
                                sendMessage(new Message("SERVER", username, targetUsername + " promoted to admin", Message.MessageType.ADMIN_NOTIFICATION));

                                ClientHandler target = server.getOnlineUser(targetUsername);
                                if (target != null) {
                                    target.sendMessage(new Message("SERVER", targetUsername, "You have been promoted to admin in " + group.getName(), Message.MessageType.ADMIN_NOTIFICATION));
                                }
                            }
                        } else {
                            sendMessage(new Message("SERVER", username, "You are not the group owner", Message.MessageType.SYSTEM));
                        }
                        break;
                    }

                    case DEMOTE_ADMIN: {
                        String[] parts = incoming.getContent().split("\\|");
                        UUID groupId = UUID.fromString(parts[0].trim());
                        String targetUsername = parts[1].trim();

                        Group group = server.getGroupById(groupId);

                        if (group != null && group.isOwner(username)) {
                            if (!group.getAdmins().contains(targetUsername)) {
                                sendMessage(new Message("SERVER", username, "user is not an admin", Message.MessageType.SYSTEM));
                            } else {
                                group.demoteFromAdmin(targetUsername);
                                sendMessage(new Message("SERVER", username, targetUsername + " demoted from admin", Message.MessageType.ADMIN_NOTIFICATION));

                                ClientHandler target = server.getOnlineUser(targetUsername);
                                if (target != null) {
                                    target.sendMessage(new Message("SERVER", targetUsername, "You have been demoted from admin in " + group.getName(), Message.MessageType.ADMIN_NOTIFICATION));
                                }
                            }
                        } else {
                            sendMessage(new Message("SERVER", username, "You are not the group owner", Message.MessageType.SYSTEM));
                        }
                        break;
                    }

                    case EDIT_MESSAGE: {
                        UUID messageId = UUID.fromString(incoming.getReceiverId());
                        String newContent = incoming.getContent();

                        Message original = server.findMessageById(messageId);

                        if (original == null) {
                            sendMessage(new Message("SERVER", username, "Message not found", Message.MessageType.SYSTEM));
                        } else if (!original.getSenderId().equals(username) &&
                                !server.canModerateMessage(original, username)) {
                            sendMessage(new Message("SERVER", username, "You cannot edit this message", Message.MessageType.SYSTEM));
                        } else if (original.isDeleted()) {
                            sendMessage(new Message("SERVER", username, "Message already deleted", Message.MessageType.SYSTEM));
                        } else {
                            original.setEdited(true);
                            Message editedCopy = new Message(original.getSenderId(), original.getReceiverId(), newContent, original.getMessageType());
                            editedCopy.setEdited(true);
                            editedCopy.setReplyToMessageId(original.getReplyToMessageId());
                            editedCopy.setForwardedFromMessageId(original.getForwardedFromMessageId());
                            editedCopy.setForwardedFromUser(original.getForwardedFromUser());

                            editedCopy.setDeleted(false);

                            server.replaceMessage(messageId, editedCopy);

                            server.broadcastMessageEdit(editedCopy);
                        }
                        break;
                    }

                    case DELETE_MESSAGE: {
                        UUID messageId = UUID.fromString(incoming.getReceiverId());

                        Message original = server.findMessageById(messageId);

                        if (original == null) {
                            sendMessage(new Message("SERVER", username, "Message not found", Message.MessageType.SYSTEM));
                        } else if (!original.getSenderId().equals(username) &&
                                !server.canModerateMessage(original, username)) {
                            sendMessage(new Message("SERVER", username, "You cannot delete this message", Message.MessageType.SYSTEM));
                        } else {
                            original.setDeleted(true);
                            server.broadcastMessageDelete(messageId, original.getReceiverId());
                        }
                        break;
                    }

                    case PIN_MESSAGE: {
                        try {
                            UUID messageIdToPin = UUID.fromString(incoming.getContent());
                            String contextId = incoming.getReceiverId();

                            try {
                                UUID groupId = UUID.fromString(contextId);
                                Group group = server.getGroupById(groupId);
                                if (group != null) {
                                    if (group.isAdmin(username)) {
                                        group.setPinnedMessageId(messageIdToPin);
                                        Message notification = new Message("SERVER", groupId.toString(),
                                                username + " pinned a message", Message.MessageType.PIN_NOTIFICATION);
                                        server.broadcastToGroup(group, notification);
                                    } else {
                                        sendMessage(new Message("SERVER", username, "You are not an admin", Message.MessageType.SYSTEM));
                                    }
                                    break;
                                }
                            } catch (IllegalArgumentException ignored) {}

                            try {
                                UUID channelId = UUID.fromString(contextId);
                                Channel channel = server.getChannelById(channelId);
                                if (channel != null) {
                                    if (channel.isAdmin(username)) {
                                        channel.setPinnedMessageId(messageIdToPin);
                                        Message notification = new Message("SERVER", channelId.toString(),
                                                username + " pinned a message", Message.MessageType.PIN_NOTIFICATION);
                                        server.broadcastToChannel(channel, notification);
                                    } else {
                                        sendMessage(new Message("SERVER", username, "You are not an admin", Message.MessageType.SYSTEM));
                                    }
                                    break;
                                }
                            } catch (IllegalArgumentException ignored) {}

                            String userB = contextId;
                            String chatKey = server.normalizeChatKey(username, userB);
                            server.getPrivateChatPins().put(chatKey, messageIdToPin);

                            Message pinNotice = new Message("SERVER", userB,
                                    username + " pinned a message", Message.MessageType.PIN_NOTIFICATION);

                            ClientHandler target = server.getOnlineUser(userB);
                            if (target != null)
                                target.sendMessage(pinNotice);
                            sendMessage(new Message("SERVER", username, "Message pinned", Message.MessageType.PIN_NOTIFICATION));

                        } catch (Exception e) {
                            sendMessage(new Message("SERVER", username, "invalid pin request", Message.MessageType.SYSTEM));
                        }
                        break;
                    }

                    case UNPIN_MESSAGE: {
                        try {
                            String contextId = incoming.getReceiverId();

                            try {
                                UUID groupId = UUID.fromString(contextId);
                                Group group = server.getGroupById(groupId);
                                if (group != null) {
                                    if (group.isAdmin(username)) {
                                        group.setPinnedMessageId(null);
                                        Message notification = new Message("SERVER", groupId.toString(),
                                                username + " unpinned the message", Message.MessageType.PIN_NOTIFICATION);
                                        server.broadcastToGroup(group, notification);
                                    } else {
                                        sendMessage(new Message("SERVER", username, "You are not an admin", Message.MessageType.SYSTEM));
                                    }
                                    break;
                                }
                            } catch (IllegalArgumentException ignored) {}

                            try {
                                UUID channelId = UUID.fromString(contextId);
                                Channel channel = server.getChannelById(channelId);
                                if (channel != null) {
                                    if (channel.isAdmin(username)) {
                                        channel.setPinnedMessageId(null);
                                        Message notification = new Message("SERVER", channelId.toString(),
                                                username + " unpinned the message", Message.MessageType.PIN_NOTIFICATION);
                                        server.broadcastToChannel(channel, notification);
                                    } else {
                                        sendMessage(new Message("SERVER", username, "You are not an admin", Message.MessageType.SYSTEM));
                                    }
                                    break;
                                }
                            } catch (IllegalArgumentException ignored) {}
                            String userB = contextId;
                            String chatKey = server.normalizeChatKey(username, userB);
                            server.getPrivateChatPins().remove(chatKey);

                            Message unpinNotice = new Message("SERVER", userB,
                                    username + " unpinned the message", Message.MessageType.PIN_NOTIFICATION);

                            ClientHandler target = server.getOnlineUser(userB);
                            if (target != null) target.sendMessage(unpinNotice);
                            sendMessage(new Message("SERVER", username, "Message unpinned", Message.MessageType.PIN_NOTIFICATION));

                        } catch (Exception e) {
                            sendMessage(new Message("SERVER", username, "Invalid unpin request", Message.MessageType.SYSTEM));
                        }
                        break;
                    }

                    case MUTE_MEMBER: {
                        String[] parts = incoming.getContent().split("\\|");
                        UUID groupId = UUID.fromString(parts[0].trim());
                        String target = parts[1].trim();

                        Group group = server.getGroupById(groupId);
                        if (group != null && group.isAdmin(username)) {
                            if (!group.isMember(target)) {
                                sendMessage(new Message("SERVER", username, "user is not a group member", Message.MessageType.SYSTEM));
                            } else {
                                group.muteMember(target);
                                sendMessage(new Message("SERVER", username, target + " has been muted", Message.MessageType.MODERATION_NOTIFICATION));
                                ClientHandler targetHandler = server.getOnlineUser(target);
                                if (targetHandler != null) {
                                    targetHandler.sendMessage(new Message("SERVER", target, "You have been muted in " + group.getName(), Message.MessageType.MODERATION_NOTIFICATION));
                                }
                            }
                        } else {
                            sendMessage(new Message("SERVER", username, "You are not allowed to mute users", Message.MessageType.SYSTEM));
                        }
                        break;
                    }

                    case UNMUTE_MEMBER: {
                        String[] parts = incoming.getContent().split("\\|");
                        UUID groupId = UUID.fromString(parts[0].trim());
                        String target = parts[1].trim();

                        Group group = server.getGroupById(groupId);
                        if (group != null && group.isAdmin(username)) {
                            group.unmuteMember(target);
                            sendMessage(new Message("SERVER", username, target + " has been unmuted", Message.MessageType.MODERATION_NOTIFICATION));
                            ClientHandler targetHandler = server.getOnlineUser(target);
                            if (targetHandler != null) {
                                targetHandler.sendMessage(new Message("SERVER", target, "You were unmuted in " + group.getName(), Message.MessageType.MODERATION_NOTIFICATION));
                            }
                        } else {
                            sendMessage(new Message("SERVER", username, "You are not allowed to unmute users", Message.MessageType.SYSTEM));
                        }
                        break;
                    }

                    case BAN_MEMBER: {
                        String[] parts = incoming.getContent().split("\\|");
                        UUID groupId = UUID.fromString(parts[0].trim());
                        String target = parts[1].trim();

                        Group group = server.getGroupById(groupId);
                        if (group != null && group.isAdmin(username)) {
                            group.banMember(target);
                            sendMessage(new Message("SERVER", username, target + " has been banned", Message.MessageType.MODERATION_NOTIFICATION));
                            ClientHandler targetHandler = server.getOnlineUser(target);
                            if (targetHandler != null) {
                                targetHandler.sendMessage(new Message("SERVER", target, "You have been banned from " + group.getName(), Message.MessageType.MODERATION_NOTIFICATION));
                            }
                        } else {
                            sendMessage(new Message("SERVER", username, "You are not allowed to ban users", Message.MessageType.SYSTEM));
                        }
                        break;
                    }

                    case UNBAN_MEMBER: {
                        String[] parts = incoming.getContent().split("\\|");
                        UUID groupId = UUID.fromString(parts[0].trim());
                        String target = parts[1].trim();

                        Group group = server.getGroupById(groupId);
                        if (group != null && group.isAdmin(username)) {
                            group.unbanMember(target);
                            sendMessage(new Message("SERVER", username, target + " has been unbanned", Message.MessageType.MODERATION_NOTIFICATION));
                        } else {
                            sendMessage(new Message("SERVER", username, "You are not allowed to unban users", Message.MessageType.SYSTEM));
                        }
                        break;
                    }

                    case MEDIA:
                    case MEDIA_WITH_TEXT: {
                        server.routeMessage(incoming);
                        if (incoming.getMediaData() != null) {
                            String filename = "received_" + incoming.getMediaName();
                            Files.write(Paths.get("media_received/" + filename), incoming.getMediaData());
                        }
                        break;
                    }

                    default: {

                        break;
                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Disconnected: " + username);
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