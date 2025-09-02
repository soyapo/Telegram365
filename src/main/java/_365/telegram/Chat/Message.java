package _365.telegram.Chat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Message implements Serializable {
    private final UUID messageId;
    private final String senderId;
    private final String receiverId;
    private final String content;
    private final MessageType messageType;
    private final LocalDateTime timestamp;
    private boolean edited = false;
    private boolean deleted = false;
    private LocalDateTime editedAt;

    private UUID replyToMessageId;

    private UUID forwardedFromMessageId;
    private String forwardedFromUser;

    private Set<String> readers = new HashSet<>();
    private int viewCount = 0;

    private byte[] mediaData;
    private String mediaName;
    private String mediaType;

    public Message(String senderId, String receiverId, String content, MessageType messageType) {
        this.messageId = UUID.randomUUID();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.messageType = messageType;
        this.timestamp = LocalDateTime.now();
    }
    public Message(String senderId, String receiverId, String content, MessageType messageType,
                   byte[] mediaData, String mediaName, String mediaType) {
        this(senderId, receiverId, content, messageType);
        this.mediaData = mediaData;
        this.mediaName = mediaName;
        this.mediaType = mediaType;
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

    public MessageType getMessageType() {
        return messageType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public byte[] getMediaData() {
        return mediaData;
    }

    public String getMediaName() {
        return mediaName;
    }

    public String getMediaType() {
        return mediaType;
    }

    public UUID getReplyToMessageId() {
        return replyToMessageId;
    }

    public void setReplyToMessageId(UUID replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }

    public UUID getForwardedFromMessageId() {
        return forwardedFromMessageId;
    }

    public void setForwardedFromMessageId(UUID forwardedFromMessageId) {
        this.forwardedFromMessageId = forwardedFromMessageId;
    }

    public String getForwardedFromUser() {
        return forwardedFromUser;
    }

    public void setForwardedFromUser(String forwardedFromUser) {
        this.forwardedFromUser = forwardedFromUser;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
        this.editedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public Set<String> getReaders() {
        return readers;
    }

    public void addReader(String username) {
        readers.add(username);
    }

    public boolean hasRead(String username) {
        return readers.contains(username);
    }

    public int getViewCount() {
        return viewCount;
    }

    public void incrementViewCount() {
        viewCount++;
    }

    @Override
    public String toString() {
        if (deleted) {
            return "[DELETED]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[").append(timestamp).append("] ");

        if (forwardedFromUser != null) {
            sb.append("(Forwarded from ").append(forwardedFromUser).append(") ");
        }

        sb.append(senderId).append(" → ").append(receiverId).append(": ").append(content);

        if (replyToMessageId != null) {
            sb.append(" ( Reply to ").append(replyToMessageId).append(")");
        }

        if (edited) {
            sb.append(" (edited)");
        }

        return sb.toString();
    }

    public enum MessageType {
        PRIVATE, GROUP, CHANNEL, SYSTEM,

        REGISTER_PHONE, VERIFY_CODE, SET_PROFILE, LOGIN_RESPONSE,

        CREATE_GROUP, JOIN_GROUP, GROUP_MESSAGE,

        CREATE_CHANNEL, JOIN_CHANNEL, CHANNEL_MESSAGE,

        PIN_MESSAGE, UNPIN_MESSAGE, PIN_NOTIFICATION,

        PROMOTE_ADMIN, DEMOTE_ADMIN, ADMIN_NOTIFICATION,

        MUTE_MEMBER, UNMUTE_MEMBER, BAN_MEMBER, UNBAN_MEMBER, MODERATION_NOTIFICATION,

        EDIT_MESSAGE, DELETE_MESSAGE, MESSAGE_EDIT_NOTIFICATION, MESSAGE_DELETE_NOTIFICATION,

        MEDIA, MEDIA_WITH_TEXT,

        READ_RECEIPT, VIEW_COUNTER_UPDATE
    }
}