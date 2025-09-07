package _365.telegram.db;

import java.sql.*;
import java.util.UUID;
import _365.telegram.User;

public class UserDao {

    public static void insertUser(String phone, String username, String bio, String profilePicture) {
        String sql = "INSERT INTO users (id, phone, username, bio, profile_picture) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            UUID userId = UUID.randomUUID();
            stmt.setObject(1, userId);
            stmt.setString(2, phone);
            stmt.setString(3, username);
            stmt.setString(4, bio);
            stmt.setString(5, profilePicture);
            stmt.executeUpdate();
            System.out.println("user inserted with ID: " + userId);
        } catch (SQLException e) {
            System.err.println("failed to insert user: " + e.getMessage());
        }
    }

    public static User getUserByPhone(String phone) {
        String sql = "SELECT id, phone, username, bio, profile_picture FROM users WHERE phone = ?";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("phone"),
                        rs.getString("username"),
                        rs.getString("bio"),
                        rs.getString("profile_picture")
                );
            }
        } catch (SQLException e) {
            System.err.println("getUserByPhone failed: " + e.getMessage());
        }
        return null;
    }

    public static User getUserByID(UUID ID) {
        String sql = "SELECT id, phone, username, bio, profile_picture FROM users WHERE id = ?";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, ID.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("phone"),
                        rs.getString("username"),
                        rs.getString("bio"),
                        rs.getString("profile_picture")
                );
            }
        } catch (SQLException e) {
            System.err.println("getUserByPhone failed: " + e.getMessage());
        }
        return null;
    }

    public static UUID getUserIdByPhone(String phone) {
        String sql = "SELECT id FROM users WHERE phone = ?";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return (UUID) rs.getObject("id");
            }
        } catch (SQLException e) {
            System.err.println("getUserIdByPhone failed: " + e.getMessage());
        }
        return null;
    }

    public static String getUserPhoneByID(UUID uuid) {
        String sql = "SELECT phone FROM users WHERE id = ?";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setObject(1, uuid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getString("phone");
        } catch (SQLException e) {
            System.err.println("getUserPhoneByID failed: " + e.getMessage());
        }
        return null;
    }


    public static String getUsernameById(UUID userId) {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setObject(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            System.err.println("getUsernameById failed: " + e.getMessage());
        }
        return null;
    }

    public static boolean phoneNumberExists (String phone) {
        //DatabaseManager.connect();
        String sql = "SELECT 1 FROM users WHERE phone = ?";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("phoneNumberExists failed: " + e.getMessage());
        }
        return false;
    }

    public static boolean usernameExists (String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (PreparedStatement stmt = DatabaseManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("usernameExists failed: " + e.getMessage());
        }
        return false;
    }
}
