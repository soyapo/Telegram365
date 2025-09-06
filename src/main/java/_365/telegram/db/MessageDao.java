package _365.telegram.db;

import _365.telegram.Message;
import _365.telegram.Message.MessageType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageDao {
    public static void createMessage(Message message) {
        String sql = "INSERT INTO messages (id, sender_id, chat_id, chat_type, content, reply_to_message_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, message.getMessageId());
            stmt.setObject(2, UUID.fromString(message.getSenderId()));
            stmt.setObject(3, UUID.fromString(message.getReceiverId()));
            stmt.setString(4, getChatTypeString(message.getMessageType()));
            stmt.setString(5, message.getContent());

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

    public static List<Message> getMessagesForChat(UUID chatId, int limit, int offset) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE chat_id = ? AND is_deleted = false ORDER BY timestamp DESC LIMIT ? OFFSET ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, chatId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);

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
        UUID id = (UUID) rs.getObject("id");
        UUID senderId = (UUID) rs.getObject("sender_id");
        UUID chatId = (UUID) rs.getObject("chat_id");
        String chatType = rs.getString("chat_type");
        String content = rs.getString("content");
        UUID replyToId = (UUID) rs.getObject("reply_to_message_id");
        boolean isEdited = rs.getBoolean("is_edited");

        MessageType messageType = switch (chatType) {
            case "private" -> MessageType.PRIVATE;
            case "group" -> MessageType.GROUP_MESSAGE;
            case "channel" -> MessageType.CHANNEL_MESSAGE;
            default -> MessageType.SYSTEM;
        };

        Message message = new Message(senderId.toString(), chatId.toString(), content, messageType);
        message.setReplyToMessageId(replyToId);
        message.setEdited(isEdited);

        return message;
    }
}