package _365.telegram;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum MessageType {
        PRIVATE, GROUP, CHANNEL, SYSTEM
    }

    public enum MessageStatus {
        SENT, DELIVERED, READ
    }

    private final UUID messageId;
    private final String senderId;
    private final String receiverId;
    private final String content;
    private final LocalDateTime timestamp;
    private final MessageType messageType;
    private MessageStatus status;


    public Message(String senderId, String receiverId, String content, MessageType messageType) {
        this.messageId = UUID.randomUUID();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.messageType = messageType;
        this.status = MessageStatus.SENT;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public MessageStatus getStatus() {
        return status;
    }

    // setters
    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + senderId + ": " + content;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Message))
            return false;
        Message other = (Message) obj;
        return messageId.equals(other.messageId);
    }

    @Override
    public int hashCode() {
        return messageId.hashCode();
    }
}
