package _365.telegram.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatDao {
    public static List<ChatListItem> getChatListForUser(UUID userId) {
        List<ChatListItem> chatList = new ArrayList<>();

        String sql = """
            SELECT
                all_chats.chat_id,
                all_chats.chat_name,
                all_chats.chat_type,
                all_chats.last_message_content,
                u.username AS last_message_sender_name,
                all_chats.last_message_timestamp
            FROM (
                SELECT
                    CASE WHEN m.sender_id = ? THEN m.chat_id ELSE m.sender_id END AS chat_id,
                    other_user.username AS chat_name,
                    'private' AS chat_type,
                    m.content AS last_message_content,
                    m.sender_id AS last_message_sender_id,
                    m.timestamp AS last_message_timestamp
                FROM (
                    SELECT DISTINCT ON (CASE WHEN sender_id = ? THEN chat_id ELSE sender_id END)
                        *
                    FROM messages
                    WHERE chat_type = 'private' AND (sender_id = ? OR chat_id = ?)
                    ORDER BY (CASE WHEN sender_id = ? THEN chat_id ELSE sender_id END), timestamp DESC
                ) m
                JOIN users other_user ON other_user.id = (CASE WHEN m.sender_id = ? THEN m.chat_id ELSE m.sender_id END)

                UNION ALL

                SELECT
                    g.id AS chat_id,
                    g.name AS chat_name,
                    'group' AS chat_type,
                    m.content AS last_message_content,
                    m.sender_id AS last_message_sender_id,
                    m.timestamp AS last_message_timestamp
                FROM (
                    SELECT DISTINCT ON (chat_id)
                        *
                    FROM messages
                    WHERE chat_type = 'group' AND chat_id IN (SELECT group_id FROM group_members WHERE user_id = ?)
                    ORDER BY chat_id, timestamp DESC
                ) m
                JOIN groups g ON m.chat_id = g.id

                UNION ALL

                SELECT
                    c.id AS chat_id,
                    c.name AS chat_name,
                    'channel' AS chat_type,
                    m.content AS last_message_content,
                    m.sender_id AS last_message_sender_id,
                    m.timestamp AS last_message_timestamp
                FROM (
                    SELECT DISTINCT ON (chat_id)
                        *
                    FROM messages
                    WHERE chat_type = 'channel' AND chat_id IN (SELECT channel_id FROM channel_subscribers WHERE user_id = ?)
                    ORDER BY chat_id, timestamp DESC
                ) m
                JOIN channels c ON m.chat_id = c.id
            ) AS all_chats
            JOIN users u ON u.id = all_chats.last_message_sender_id
            ORDER BY all_chats.last_message_timestamp DESC;
            """;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            stmt.setObject(2, userId);
            stmt.setObject(3, userId);
            stmt.setObject(4, userId);
            stmt.setObject(5, userId);
            stmt.setObject(6, userId);
            stmt.setObject(7, userId);
            stmt.setObject(8, userId);


            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UUID chatId = (UUID) rs.getObject("chat_id");
                String chatName = rs.getString("chat_name");
                String chatType = rs.getString("chat_type");
                String lastMessageContent = rs.getString("last_message_content");
                String lastMessageSenderName = rs.getString("last_message_sender_name");
                LocalDateTime lastMessageTimestamp = rs.getTimestamp("last_message_timestamp").toLocalDateTime();

                ChatListItem item = new ChatListItem(chatId, chatName, chatType, lastMessageContent, lastMessageSenderName, lastMessageTimestamp);
                chatList.add(item);
            }

        } catch (SQLException e) {
            System.err.println("Error showing chats for this user: " + userId);
            e.printStackTrace();
        }

        return chatList;
    }
}