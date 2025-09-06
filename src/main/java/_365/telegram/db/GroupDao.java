package _365.telegram.db;

import _365.telegram.Group;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GroupDao {
    public static void createGroup(Group group) {
        String insertGroupSql = "INSERT INTO groups (id, name, owner_id, is_public, invite_code) VALUES (?, ?, ?, ?, ?)";
        String insertMemberSql = "INSERT INTO group_members (group_id, user_id, is_admin) VALUES (?, ?, ?)";
        Connection conn = null;

        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement groupStmt = conn.prepareStatement(insertGroupSql)) {
                groupStmt.setObject(1, group.getGroupId());
                groupStmt.setString(2, group.getName());
                groupStmt.setObject(3, UUID.fromString(group.getOwnerUsername()));
                groupStmt.setBoolean(4, group.isPublic());
                groupStmt.setString(5, UUID.randomUUID().toString().substring(0, 8));
                groupStmt.executeUpdate();
            }

            try (PreparedStatement memberStmt = conn.prepareStatement(insertMemberSql)) {
                memberStmt.setObject(1, group.getGroupId());
                memberStmt.setObject(2, UUID.fromString(group.getOwnerUsername()));
                memberStmt.setBoolean(3, true);
                memberStmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            System.err.println("error creating group: " + e.getMessage());
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

    public Optional<Group> getGroupById(UUID groupId) {
        String sql = "SELECT * FROM groups WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, groupId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Group group = mapResultSetToGroup(rs);
                List<UUID> memberIds = getGroupMemberIds(groupId);
                for (UUID memberId : memberIds) {
                    group.addMember(memberId.toString());
                }
                return Optional.of(group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static void addMemberToGroup(UUID groupId, UUID userId) {
        String sql = "INSERT INTO group_members (group_id, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, groupId);
            stmt.setObject(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding member to group: " + e.getMessage());
        }
    }

    public static void removeMemberFromGroup(UUID groupId, UUID userId) {
        String sql = "DELETE FROM group_members WHERE group_id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, groupId);
            stmt.setObject(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error removing member from group: " + e.getMessage());
        }
    }

    public static List<UUID> getGroupMemberIds(UUID groupId) {
        List<UUID> memberIds = new ArrayList<>();
        String sql = "SELECT user_id FROM group_members WHERE group_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, groupId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                memberIds.add((UUID) rs.getObject("user_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memberIds;
    }

    public static void setAdminStatus(UUID groupId, UUID userId, boolean isAdmin) {
        String sql = "UPDATE group_members SET is_admin = ? WHERE group_id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isAdmin);
            stmt.setObject(2, groupId);
            stmt.setObject(3, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("error updating admin status: " + e.getMessage());
        }
    }

    private Group mapResultSetToGroup(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String name = rs.getString("name");
        UUID ownerId = (UUID) rs.getObject("owner_id");
        boolean isPublic = rs.getBoolean("is_public");

        Group group = new Group(name, ownerId.toString(), isPublic);

        return group;
    }
}