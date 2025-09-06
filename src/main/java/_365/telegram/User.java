package _365.telegram;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private UUID userId;
    private final String phoneNumber;
    private String username;
    private String bio;
    private String profilePath;
    private String status = "Offline";

    public User(UUID userId, String phoneNumber, String username, String bio, String profilePath) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.bio = bio;
        this.profilePath = profilePath;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getStatus() {
        return status;
    }

    public String getProfilePath() { return profilePath; }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return username + " (" + phoneNumber + ")";
    }
}