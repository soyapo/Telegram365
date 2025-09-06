package _365.telegram.db;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatListItem {

    private final UUID chatId;
    private final String chatName;
    private final String chatType;
    private final String lastMessageContent;
    private final String lastMessageSenderName;
    private final LocalDateTime lastMessageTimestamp;

    public ChatListItem(UUID chatId, String chatName, String chatType, String lastMessageContent, String lastMessageSenderName, LocalDateTime lastMessageTimestamp) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.chatType = chatType;
        this.lastMessageContent = lastMessageContent;
        this.lastMessageSenderName = lastMessageSenderName;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public UUID getChatId() {
        return chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public String getChatType() {
        return chatType;
    }

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public String getLastMessageSenderName() {
        return lastMessageSenderName;
    }

    public LocalDateTime getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    @Override
    public String toString() {
        return "ChatListItem{" +
                "chatName='" + chatName + '\'' +
                ", chatType='" + chatType + '\'' +
                ", lastMessageContent='" + lastMessageContent + '\'' +
                ", lastMessageTimestamp=" + lastMessageTimestamp +
                '}';
    }
}