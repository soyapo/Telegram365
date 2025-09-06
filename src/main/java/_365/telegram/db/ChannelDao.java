package _365.telegram.db;

import _365.telegram.Channel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChannelDao {
    public static void createChannel(Channel channel) {
        String insertChannelSql = "INSERT INTO channels (id, name, description, owner_id, is_public) VALUES (?, ?, ?, ?, ?)";
        String insertSubscriberSql = "INSERT INTO channel_subscribers (channel_id, user_id) VALUES (?, ?)";
        Connection conn = null;

        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement channelStmt = conn.prepareStatement(insertChannelSql)) {
                channelStmt.setObject(1, channel.getChannelId());
                channelStmt.setString(2, channel.getName());
                channelStmt.setString(3, channel.getDescription());
                channelStmt.setObject(4, UUID.fromString(channel.getOwnerUsername()));
                channelStmt.setBoolean(5, channel.isPublic());
                channelStmt.executeUpdate();
            }

            try (PreparedStatement subscriberStmt = conn.prepareStatement(insertSubscriberSql)) {
                subscriberStmt.setObject(1, channel.getChannelId());
                subscriberStmt.setObject(2, UUID.fromString(channel.getOwnerUsername()));
                subscriberStmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            System.err.println("error creating channel: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Optional<Channel> getChannelById(UUID channelId) {
        String sql = "SELECT * FROM channels WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, channelId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Channel channel = mapResultSetToChannel(rs);
                List<UUID> subscriberIds = getChannelSubscriberIds(channelId);
                for (UUID subId : subscriberIds) {
                    channel.addSubscriber(subId.toString());
                }
                return Optional.of(channel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static void addSubscriber(UUID channelId, UUID userId) {
        String sql = "INSERT INTO channel_subscribers (channel_id, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, channelId);
            stmt.setObject(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("error adding subscriber to channel: " + e.getMessage());
        }
    }

    public static void removeSubscriber(UUID channelId, UUID userId) {
        String sql = "DELETE FROM channel_subscribers WHERE channel_id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, channelId);
            stmt.setObject(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("error removing subscriber from channel: " + e.getMessage());
        }
    }

    public static List<UUID> getChannelSubscriberIds(UUID channelId) {
        List<UUID> subscriberIds = new ArrayList<>();
        String sql = "SELECT user_id FROM channel_subscribers WHERE channel_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, channelId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                subscriberIds.add((UUID) rs.getObject("user_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subscriberIds;
    }

    private Channel mapResultSetToChannel(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        UUID ownerId = (UUID) rs.getObject("owner_id");
        boolean isPublic = rs.getBoolean("is_public");

        Channel channel = new Channel(name, description, ownerId.toString(), isPublic);

        return channel;
    }
}