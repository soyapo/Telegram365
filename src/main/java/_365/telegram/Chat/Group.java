package _365.telegram.Chat;

import java.io.Serializable;
import java.util.*;

public class Group implements Serializable {
    private final UUID groupId;
    private final String name;
    private final String ownerUsername;
    private final boolean isPublic;
    private final Set<String> members = new HashSet<>();
    private final List<Message> history = new ArrayList<>();
    private UUID pinnedMessageId;
    private final Set<String> admins = new HashSet<>();
    private final Set<String> mutedMembers = new HashSet<>();
    private final Set<String> bannedMembers = new HashSet<>();

    public Group(String name, String ownerUsername, boolean isPublic) {
        this.groupId = UUID.randomUUID();
        this.name = name;
        this.ownerUsername = ownerUsername;
        this.isPublic = isPublic;
        this.members.add(ownerUsername);
    }

    public UUID getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public Set<String> getMembers() {
        return members;
    }

    public List<Message> getHistory() {
        return history;
    }

    public void addMember(String username) {
        members.add(username);
    }

    public void removeMember(String username) {
        members.remove(username);
    }

    public boolean isMember(String username) {
        return members.contains(username);
    }

    public void addMessage(Message message) {
        history.add(message);
    }

    public UUID getPinnedMessageId() {
        return pinnedMessageId;
    }

    public void setPinnedMessageId(UUID pinnedMessageId) {
        this.pinnedMessageId = pinnedMessageId;
    }

    public Set<String> getAdmins() {
        return admins;
    }

    public boolean isAdmin(String username) {
        return username.equals(ownerUsername) || admins.contains(username);
    }

    public boolean isOwner(String username) {
        return username.equals(ownerUsername);
    }

    public void promoteToAdmin(String username) {
        if (!username.equals(ownerUsername)) {
            admins.add(username);
        }
    }

    public void demoteFromAdmin(String username) {
        admins.remove(username);
    }

    public boolean isMuted(String username) {
        return mutedMembers.contains(username);
    }

    public boolean isBanned(String username) {
        return bannedMembers.contains(username);
    }

    public void muteMember(String username) {
        mutedMembers.add(username);
    }

    public void unmuteMember(String username) {
        mutedMembers.remove(username);
    }

    public void banMember(String username) {
        bannedMembers.add(username);
        members.remove(username);
        admins.remove(username);
    }

    public void unbanMember(String username) {
        bannedMembers.remove(username);
    }

    @Override
    public String toString() {
        return name + " (" + groupId + ") - " + (isPublic ? "Public" : "Private");
    }
}