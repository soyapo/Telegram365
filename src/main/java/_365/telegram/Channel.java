package _365.telegram;

import java.io.Serializable;
import java.util.*;

public class Channel implements Serializable {
    private final UUID channelId;
    private final String name;
    private final String description;
    private final String ownerUsername;
    private final boolean isPublic;

    private final Set<String> admins = new HashSet<>();
    private final Set<String> subscribers = new HashSet<>();
    private final List<Message> messageHistory = new ArrayList<>();

    private UUID pinnedMessageId;

    public Channel(String name, String description, String ownerUsername, boolean isPublic) {
        this.channelId = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.ownerUsername = ownerUsername;
        this.isPublic = isPublic;
        this.admins.add(ownerUsername);
        this.subscribers.add(ownerUsername);
    }

    public UUID getChannelId() {
        return channelId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public Set<String> getAdmins() {
        return admins;
    }

    public Set<String> getSubscribers() {
        return subscribers;
    }

    public List<Message> getMessageHistory() {
        return messageHistory;
    }

    public void addSubscriber(String username) {
        subscribers.add(username);
    }

    public void addMessage(Message msg) {
        messageHistory.add(msg);
    }

    public boolean isAdmin(String username) {
        return admins.contains(username);
    }

    public UUID getPinnedMessageId() {
        return pinnedMessageId;
    }

    public void setPinnedMessageId(UUID pinnedMessageId) {
        this.pinnedMessageId = pinnedMessageId;
    }

    @Override
    public String toString() {
        return name + " (" + channelId + ") - " + (isPublic ? "Public" : "Private");
    }
}
