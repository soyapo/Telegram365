package _365.telegram;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private final UUID userId;
    private final String phoneNumber;
    private String username;
    private String bio;
    private String status = "Offline";

    public User(String phoneNumber) {
        this.userId = UUID.randomUUID();
        this.phoneNumber = phoneNumber;
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