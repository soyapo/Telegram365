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
        all_chats.receiver_id,
        all_chats.chat_name,
        all_chats.chat_type,
        all_chats.last_message_content,
        u.username AS last_message_sender_name,
        all_chats.last_message_timestamp
    FROM (
        -- Private chats
        SELECT
            m.partner_id AS receiver_id,
            u.username AS chat_name,
            'private' AS chat_type,
            m.content AS last_message_content,
            m.sender_id AS last_message_sender_id,
            m.timestamp AS last_message_timestamp
        FROM (
            SELECT DISTINCT ON (partner_id)
                *,
                CASE WHEN sender_id = ? THEN receiver_id ELSE sender_id END AS partner_id
            FROM messages
            WHERE chat_type = 'private' AND (sender_id = ? OR receiver_id = ?)
            ORDER BY partner_id, "timestamp" DESC
        ) m
        JOIN users u ON u.id = m.partner_id

        UNION ALL

        -- Group chats
        SELECT
            g.id AS receiver_id,
            g.name AS chat_name,
            'group' AS chat_type,
            m.content AS last_message_content,
            m.sender_id AS last_message_sender_id,
            m.timestamp AS last_message_timestamp
        FROM (
            SELECT DISTINCT ON (receiver_id)
                *
            FROM messages
            WHERE chat_type = 'group'
              AND receiver_id IN (SELECT group_id FROM group_members WHERE user_id = ?)
            ORDER BY receiver_id, "timestamp" DESC
        ) m
        JOIN groups g ON m.receiver_id = g.id

        UNION ALL

        -- Channel chats
        SELECT
            c.id AS receiver_id,
            c.name AS chat_name,
            'channel' AS chat_type,
            m.content AS last_message_content,
            m.sender_id AS last_message_sender_id,
            m.timestamp AS last_message_timestamp
        FROM (
            SELECT DISTINCT ON (receiver_id)
                *
            FROM messages
            WHERE chat_type = 'channel'
              AND receiver_id IN (SELECT channel_id FROM channel_subscribers WHERE user_id = ?)
            ORDER BY receiver_id, "timestamp" DESC
        ) m
        JOIN channels c ON m.receiver_id = c.id
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


            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UUID chatId = (UUID) rs.getObject("receiver_id");
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