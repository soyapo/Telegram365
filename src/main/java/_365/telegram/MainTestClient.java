package _365.telegram;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.UUID;

import _365.telegram.Chat.Message;

public class MainTestClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClientSocketHandler client = new ClientSocketHandler();

        if (!client.connect("localhost", 12345)) {
            System.out.println("Failed to connect to server.");
            return;
        }

        System.out.print("Enter your phone number: ");
        String phone = scanner.nextLine();
        client.sendMessage(new Message(phone, "SERVER", "", Message.MessageType.REGISTER_PHONE));
        sleep(1000);

        System.out.print("Enter the verification code: ");
        String code = scanner.nextLine();
        client.sendMessage(new Message(phone, "SERVER", code, Message.MessageType.VERIFY_CODE));

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your bio: ");
        String bio = scanner.nextLine();
        client.sendMessage(new Message(phone, "SERVER", username + "|" + bio, Message.MessageType.SET_PROFILE));

        client.setUserId(username);

        client.setOnMessageReceived(msg -> {
            switch (msg.getMessageType()) {
                case SYSTEM, LOGIN_RESPONSE -> System.out.println(msg.getContent());
                case PRIVATE, GROUP_MESSAGE, CHANNEL_MESSAGE -> {
                    System.out.println("\n[" + msg.getSenderId() + "] → " + msg.getReceiverId() + ": " + msg.getContent());
                    if (msg.isEdited()) System.out.println("(edited)");
                    if (msg.isDeleted()) System.out.println("[DELETED]");
                }
                case PIN_NOTIFICATION -> System.out.println(msg.getContent());
                case ADMIN_NOTIFICATION -> System.out.println(msg.getContent());
                case MODERATION_NOTIFICATION -> System.out.println(msg.getContent());
                case READ_RECEIPT -> System.out.println(msg.getContent());
                case VIEW_COUNTER_UPDATE -> System.out.println(msg.getContent());
                case MEDIA, MEDIA_WITH_TEXT -> {
                    System.out.println("Media from " + msg.getSenderId());
                    if (msg.getContent() != null && !msg.getContent().isEmpty()) {
                        System.out.println("Caption: " + msg.getContent());
                    }
                    if (msg.getMediaData() != null) {
                        try {
                            Files.createDirectories(Paths.get("media_received"));
                            String savePath = "media_received/" + msg.getMediaName();
                            Files.write(Paths.get(savePath), msg.getMediaData());
                            System.out.println("Saved to: " + savePath);
                        } catch (IOException e) {
                            System.out.println("Failed to save media: " + e.getMessage());
                        }
                    }
                }
                default -> System.out.println(msg.getMessageType() + ": " + msg.getContent());
            }
        });

        System.out.println("Logged in as " + username);
        printHelp();

        while (true) {
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("/exit"))
                break;

            try {
                if (input.equalsIgnoreCase("/help")) {
                    printHelp();
                }

                else if (input.startsWith("/pm")) {
                    System.out.print("To: ");
                    String to = scanner.nextLine();
                    System.out.print("Message: ");
                    String msg = scanner.nextLine();
                    client.sendMessage(new Message(username, to, msg, Message.MessageType.PRIVATE));
                }

                else if (input.startsWith("/create_group")) {
                    String[] parts = input.substring(13).trim().split("\\|");
                    client.sendMessage(new Message(username, "SERVER", parts[0] + "|" + parts[1], Message.MessageType.CREATE_GROUP));
                } else if (input.startsWith("/join_group")) {
                    client.sendMessage(new Message(username, "SERVER", input.substring(11).trim(), Message.MessageType.JOIN_GROUP));
                } else if (input.startsWith("/group_msg")) {
                    String[] parts = input.substring(11).trim().split(" ", 2);
                    client.sendMessage(new Message(username, parts[0], parts[1], Message.MessageType.GROUP_MESSAGE));
                }

                else if (input.startsWith("/create_channel")) {
                    String[] parts = input.substring(15).trim().split("\\|");
                    client.sendMessage(new Message(username, "SERVER", parts[0] + "|" + parts[1] + "|" + parts[2], Message.MessageType.CREATE_CHANNEL));
                } else if (input.startsWith("/join_channel")) {
                    client.sendMessage(new Message(username, "SERVER", input.substring(13).trim(), Message.MessageType.JOIN_CHANNEL));
                } else if (input.startsWith("/channel_msg")) {
                    String[] parts = input.substring(13).trim().split(" ", 2);
                    client.sendMessage(new Message(username, parts[0], parts[1], Message.MessageType.CHANNEL_MESSAGE));
                }

                else if (input.startsWith("/promote")) {
                    String[] parts = input.split(" ");
                    client.sendMessage(new Message(username, "SERVER", parts[1] + "|" + parts[2], Message.MessageType.PROMOTE_ADMIN));
                } else if (input.startsWith("/demote")) {
                    String[] parts = input.split(" ");
                    client.sendMessage(new Message(username, "SERVER", parts[1] + "|" + parts[2], Message.MessageType.DEMOTE_ADMIN));
                }

                else if (input.startsWith("/pin")) {
                    String[] parts = input.split(" ");
                    client.sendMessage(new Message(username, parts[1], parts[2], Message.MessageType.PIN_MESSAGE));
                } else if (input.startsWith("/unpin")) {
                    String id = input.substring(7).trim();
                    client.sendMessage(new Message(username, id, "", Message.MessageType.UNPIN_MESSAGE));
                }

                else if (input.startsWith("/mute")) {
                    String[] parts = input.split(" ");
                    client.sendMessage(new Message(username, "SERVER", parts[1] + "|" + parts[2], Message.MessageType.MUTE_MEMBER));
                } else if (input.startsWith("/unmute")) {
                    String[] parts = input.split(" ");
                    client.sendMessage(new Message(username, "SERVER", parts[1] + "|" + parts[2], Message.MessageType.UNMUTE_MEMBER));
                } else if (input.startsWith("/ban")) {
                    String[] parts = input.split(" ");
                    client.sendMessage(new Message(username, "SERVER", parts[1] + "|" + parts[2], Message.MessageType.BAN_MEMBER));
                } else if (input.startsWith("/unban")) {
                    String[] parts = input.split(" ");
                    client.sendMessage(new Message(username, "SERVER", parts[1] + "|" + parts[2], Message.MessageType.UNBAN_MEMBER));
                }

                else if (input.startsWith("/edit")) {
                    String[] parts = input.split(" ", 3);
                    client.sendMessage(new Message(username, parts[1], parts[2], Message.MessageType.EDIT_MESSAGE));
                } else if (input.startsWith("/delete")) {
                    String[] parts = input.split(" ");
                    client.sendMessage(new Message(username, parts[1], "", Message.MessageType.DELETE_MESSAGE));
                }

                else if (input.startsWith("/reply")) {
                    String[] parts = input.split(" ", 4);
                    Message replyMsg = new Message(username, parts[2], parts[3], Message.MessageType.PRIVATE);
                    replyMsg.setReplyToMessageId(UUID.fromString(parts[1]));
                    client.sendMessage(replyMsg);
                } else if (input.startsWith("/forward")) {
                    String[] parts = input.split(" ", 3);
                    Message fwdMsg = new Message(username, parts[2], "(Forwarded)", Message.MessageType.PRIVATE);
                    fwdMsg.setForwardedFromMessageId(UUID.fromString(parts[1]));
                    fwdMsg.setForwardedFromUser(username);
                    client.sendMessage(fwdMsg);
                }

                else if (input.startsWith("/send_media")) {
                    String[] parts = input.split(" ", 4);
                    String to = parts[1];
                    String filePath = parts[2];
                    String caption = parts.length == 4 ? parts[3] : "";

                    byte[] media = Files.readAllBytes(Paths.get(filePath));
                    String fileName = Paths.get(filePath).getFileName().toString();
                    String type = Files.probeContentType(Paths.get(filePath));

                    Message mediaMsg = new Message(username, to, caption, Message.MessageType.MEDIA, media, fileName, type);
                    client.sendMessage(mediaMsg);
                }

                else {
                    System.out.println("Unknown command. Use /help to view options.");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        client.disconnect();
        scanner.close();
        System.out.println("Disconnected.");
    }

    private static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    private static void printHelp() {
        System.out.println("""
                Commands:
                  /pm                           → Send private message
                  /create_group name|true/false
                  /join_group <groupId>
                  /group_msg <groupId> <msg>
                  /promote <groupId> <user>
                  /demote <groupId> <user>
                  /mute <groupId> <user>
                  /unmute <groupId> <user>
                  /ban <groupId> <user>
                  /unban <groupId> <user>
                  /pin <groupId> <messageId>
                  /unpin <groupId>
                  /create_channel name|desc|true/false
                  /join_channel <channelId>
                  /channel_msg <channelId> <msg>
                  /reply <msgId> <to> <msg>
                  /forward <msgId> <to>
                  /edit <msgId> <new content>
                  /delete <msgId>
                  /send_media <to> <filePath> [caption]
                  /help
                  /exit
                """);
    }
}