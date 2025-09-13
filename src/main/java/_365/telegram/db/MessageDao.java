package _365.telegram.db;

import _365.telegram.Message;
import _365.telegram.Message.MessageType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageDao {
    public static void createMessage(Message message) {
        String sql = "INSERT INTO messages (id, sender_id, receiver_id, chat_type, content, reply_to, file_path) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, message.getMessageId());
            stmt.setObject(2, UUID.fromString(message.getSenderId()));
            stmt.setObject(3, UUID.fromString(message.getReceiverId()));
            stmt.setString(4, getChatTypeString(message.getMessageType()));
            stmt.setString(5, message.getContent());
            stmt.setString(7, message.getMediaName());

            if (message.getReplyToMessageId() != null) {
                stmt.setObject(6, message.getReplyToMessageId());
            } else {
                stmt.setNull(6, Types.OTHER);
            }

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("error creating message: " + e.getMessage());
        }
    }

    public static List<Message> getMessagesForChat(UUID uuid1, UUID uuid2) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE is_deleted = FALSE AND (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) ORDER BY timestamp ASC;";



        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, uuid1);
            stmt.setObject(2, uuid2);
            stmt.setObject(3, uuid2);
            stmt.setObject(4, uuid1);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }

        } catch (SQLException e) {
            System.err.println("error fetching messages for chat: " + e.getMessage());
        }
        return messages;
    }

    public static void editMessage(UUID messageId, String newContent) {
        String sql = "UPDATE messages SET content = ?, is_edited = true, edited_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newContent);
            stmt.setObject(2, messageId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error editing message: " + e.getMessage());
        }
    }

    public static void deleteMessage(UUID messageId) {
        String sql = "UPDATE messages SET is_deleted = true, content = '[This message was deleted]' WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, messageId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("error deleting message: " + e.getMessage());
        }
    }

    private static String getChatTypeString(MessageType messageType) {
        return switch (messageType) {
            case PRIVATE -> "private";
            case GROUP_MESSAGE -> "group";
            case CHANNEL_MESSAGE -> "channel";
            default -> "system";
        };
    }

    private static Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        UUID messageId = (UUID) rs.getObject("id");
        UUID senderId = (UUID) rs.getObject("sender_id");
        UUID receiverId = (UUID) rs.getObject("receiver_id");
        String chatType = rs.getString("chat_type");
        String content = rs.getString("content");
        UUID replyToId = (UUID) rs.getObject("reply_to");
        boolean isEdited = rs.getBoolean("is_edited");
        boolean isDeleted = rs.getBoolean("is_deleted");
        UUID forwardedFrom = (UUID) rs.getObject("forwarded_from");
        LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();
        LocalDateTime editedAt = (LocalDateTime) rs.getObject("edited_at");
        String mediaName = rs.getString("file_path");

        MessageType messageType = switch (chatType) {
            case "private" -> MessageType.PRIVATE;
            case "group" -> MessageType.GROUP_MESSAGE;
            case "channel" -> MessageType.CHANNEL_MESSAGE;
            default -> MessageType.SYSTEM;
        };

        Message message = new Message(senderId.toString(), receiverId.toString(), content, messageType, mediaName);
        message.setReplyToMessageId(replyToId);
        message.setEdited(isEdited);
        message.serTimestamp(timestamp);

        return message;
    }
}