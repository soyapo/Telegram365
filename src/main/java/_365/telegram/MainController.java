package _365.telegram;

import _365.telegram.db.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class MainController {
    User user;
    User RecUser;
    String CurrentChatType;
    String MediaPath;

    private ClientSocketHandler CSH;

    @FXML private void initialize() {
        // -- connect to database to retrieve data --
        DatabaseManager.connect();

        // -- set user --
        user = UserDao.getUserByPhone(LoginController.GetPhoneInput());
        user.setStatus("Online");

        CSH = SharedCSH.getClientSocketHandler();
        if (CSH.connect("localhost", 12345)) {
            CSH.setOnMessageReceived(this::handleIncomingMessage);
        }

        CSH.sendMessage(new Message(user.getUsername(),"SERVER", "", Message.MessageType.REGISTER_PHONE));

        // -- Sidebar init --
        SideBarProfile.setImage(new Image("file:src/main/java/_365/telegram/Media/Profiles/" + user.getProfilePath()));
        Circle clip = new Circle(75, 75, 75);
        SideBarProfile.setClip(clip);
        SideBarProfile.setOnMouseClicked(e -> {ShowBigImage("file:src/main/java/_365/telegram/Media/Profiles/" + user.getProfilePath());});

        SideBarName.setText(user.getUsername());

        for (Node child : SideBar.getChildren()) {
            if (child instanceof HBox hbox) {
                hbox.setOnMouseEntered(e -> hbox.setStyle("-fx-background-color: #232e3c;"));
                hbox.setOnMouseExited(e -> hbox.setStyle(""));
            }
        }

        // -- Chatlist --
        UpdateChatBox();

        // --centerbar init--

        ChatBars.setVisible(false);
        MessageBox.setOnKeyTyped(e -> {
            String text = MessageBox.getText();
            if(text.isEmpty())
                SendMicButton.setImage(new Image(getClass().getResource("/_365/telegram/media/Icons/microphone-8-32.png").toExternalForm()));
            else
                SendMicButton.setImage(new Image(getClass().getResource("/_365/telegram/media/Icons/telegram-32.png").toExternalForm()));
        });


        ChatList.setCellFactory(lv -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message msg, boolean empty) {
                super.updateItem(msg, empty);
                setBackground(Background.EMPTY); // make cell transparent
                setStyle("-fx-background-color: transparent;");

                if (empty || msg == null) {
                    setGraphic(null);
                } else {
                    VBox bubble = new VBox();
                    bubble.setPrefWidth(260);
                    bubble.setMaxWidth(260);

                    Label content = new Label(msg.getContent());
                    content.setStyle("-fx-font-family: Ebrima; -fx-font-size: 14px;");
                    content.setWrapText(true);
                    bubble.getChildren().add(content);

                    LocalDateTime dt = msg.getTimestamp();
                    Label time = new Label(String.format("%02d:%02d", dt.getHour(), dt.getMinute()));
                    time.setStyle("-fx-font-family: Ebrima; -fx-font-size: 9px; -fx-padding: 10 0 0 220;");
                    bubble.getChildren().add(time);

                    if(msg.getMediaName() != null){
                        ImageView image = new ImageView(new Image("file:src/main/java/_365/telegram/Media/Sent/" + msg.getMediaName()));
                        image.setFitWidth(260);
                        image.setFitHeight(260);
                        image.setOnMouseClicked(e -> {ShowBigImage("file:src/main/java/_365/telegram/Media/Sent/" + msg.getMediaType());});
                        bubble.getChildren().add(image);
                    }

                    // align left or right
                    HBox wrapper = new HBox(bubble);
                    if (msg.getSenderId().equals(user.getUserId().toString())) {
                        wrapper.setAlignment(Pos.CENTER_RIGHT);
                        bubble.setStyle("-fx-background-color: lightblue; -fx-padding: 8; -fx-background-radius: 10;");
                    } else {
                        wrapper.setAlignment(Pos.CENTER_LEFT);
                        bubble.setStyle("-fx-background-color: lightgray; -fx-padding: 8; -fx-background-radius: 10;");
                    }

                    setGraphic(wrapper);
                }
            }
        });

        ChatList.setItems(messagesObservableList);

        messagesObservableList.addListener((ListChangeListener<Message>) change -> {
            ChatList.scrollTo(messagesObservableList.size() - 1);
        });



        clip = new Circle(60, 60, 60);
        InfoBarProfile.setClip(clip);
        InfoBarProfile.setOnMouseClicked(e -> {ShowBigImage("file:src/main/java/_365/telegram/Media/Profiles/" + RecUser.getProfilePath());});


        // -- Close database connection --
        DatabaseManager.close();
    }

    private void handleIncomingMessage(Message msg) {

        // Always update UI on JavaFX thread
        Platform.runLater(() -> {
            if(msg.getContent() == "Online users changed") {
                ClientHandler target = Server.onlineUsers.get(RecUser.getUsername());
                if(target != null) {
                    InfoBarStatus.setText("Online");
                    CenterBarStatus.setText("Online");
                }
                else{
                    InfoBarStatus.setText("Offline");
                    CenterBarStatus.setText("Offline");
                }
                return;
            }
            System.out.println(msg.getSenderId() + ": " + msg.getContent());
            messagesObservableList.add(new Message(msg.getSenderId(), msg.getReceiverId(), msg.getContent(), Message.MessageType.PRIVATE));
            UpdateChatBox();
        });
    }

    @FXML private ListView<ChatListItem> ChatListContainer;

    private HBox GenerateChatBox(ChatListItem chat) {

        DatabaseManager.connect();

        String ReceiverPhone = UserDao.getUserPhoneByID(chat.getChatId());
        ImageView pfp = new ImageView(new Image("file:src/main/java/_365/telegram/Media/Profiles/" + ReceiverPhone + ".png")); // adjust path
        pfp.setFitWidth(40);
        pfp.setFitHeight(40);
        Circle clip = new Circle(20, 20, 20);
        pfp.setClip(clip);

        Label name = new Label(chat.getChatName());
        name.setStyle("-fx-font-family: Ebrima; -fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #ffffff");

        Label lastMsg = new Label(chat.getLastMessageContent());
        lastMsg.setStyle("-fx-text-fill: gray;");

        VBox textContainer = new VBox(4, name, lastMsg);

        // --- Time ---
        LocalDateTime ts = chat.getLastMessageTimestamp();
        String formattedTime;
        if (ts.toLocalDate().isEqual(LocalDateTime.now().toLocalDate())) {
            // today → show hour:minute
            formattedTime = ts.toLocalTime().withSecond(0).withNano(0).toString();
        } else {
            // older → show month-day
            String FormattedMonth = ts.getMonth().name().substring(0, 3).toLowerCase();
            FormattedMonth = FormattedMonth.substring(0, 1).toUpperCase() + FormattedMonth.substring(1, 3);
            formattedTime = FormattedMonth + " " + ts.getDayOfMonth();
        }
        Label time = new Label(formattedTime);
        time.setStyle("-fx-text-fill: darkgray; -fx-font-size: 11;");

        VBox rightBox = new VBox(time);
        rightBox.setAlignment(Pos.TOP_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox box = new HBox(10, pfp, textContainer, rightBox);

        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #17212b;");
        box.setAlignment(Pos.CENTER_LEFT);
        box.setHgrow(textContainer, Priority.ALWAYS);
        box.setCursor(Cursor.HAND);

        box.setOnMouseEntered(e -> box.setStyle("-fx-background-color: #232e3c;"));
        box.setOnMouseExited(e -> box.setStyle("-fx-background-color: #17212b;"));

        box.setOnMouseClicked(e -> ShowChat(ReceiverPhone));

        DatabaseManager.close();

        return box;
    }


    // -- Sidebar --

    @FXML VBox SideBar;
    @FXML private ImageView SideBarProfile;
    @FXML private Label SideBarName;
    @FXML private void OpenSideBar(){
        SideBar.toFront();
    }
    @FXML private void CloseSideBar() { SideBar.toBack(); }

    @FXML private HBox BigImageContainer;
    @FXML private ImageView BigImage;

    @FXML private void ShowBigImage(String Path){
        BigImageContainer.toFront();
        BigImage.setImage(new Image(Path));
    }
    @FXML private void HideBigImage(){
        BigImage.setImage(null);
        BigImageContainer.toBack();
    }

    @FXML TextField LeftBarSearchBox;
    @FXML ImageView LeftBarSearchIcon;

    @FXML private void SearchUser(){
        DatabaseManager.connect();

        String username = LeftBarSearchBox.getText();
        if(UserDao.usernameExists(username))
            ShowChat(UserDao.getPhoneByUsername(username));

        DatabaseManager.close();
    }



    // --center bar--
    @FXML HBox ChatBars;
    @FXML VBox CenterBar;

    @FXML Label CenterBarUsername;
    @FXML Label CenterBarStatus;

    @FXML Label InfoBarBio;
    @FXML Label InfoBarPhone;
    @FXML Label InfoBarStatus;
    @FXML Label InfoBarUsername1;
    @FXML Label InfoBarUsername2;
    @FXML ImageView InfoBarProfile;

    @FXML TextField MessageBox;
    @FXML ImageView SendMicButton;

    @FXML private void ShowChat(String Phone){
        DatabaseManager.connect();

        RecUser = UserDao.getUserByPhone(Phone);

        CenterBarUsername.setText(RecUser.getUsername());
        CenterBarStatus.setText(RecUser.getStatus());

        InfoBarBio.setText(RecUser.getBio());
        InfoBarStatus.setText(RecUser.getStatus());
        InfoBarPhone.setText("+98 " + Phone.substring(0, 3) + " " + Phone.substring(3, 6) + " " + Phone.substring(6, 10));
        InfoBarUsername1.setText(RecUser.getUsername());
        InfoBarUsername2.setText("@" + RecUser.getUsername());
        InfoBarProfile.setImage(new Image("file:src/main/java/_365/telegram/Media/Profiles/" + Phone + ".png"));

        ListMessages();

        ChatBars.setVisible(true);
        DatabaseManager.close();
    }


    @FXML ListView<Message> ChatList;
    private ObservableList<Message> messagesObservableList = FXCollections.observableArrayList();

    @FXML private void ListMessages(){
        DatabaseManager.connect();
        List<Message> messages = MessageDao.getMessagesForChat(user.getUserId(), RecUser.getUserId());

        messagesObservableList.clear();
        messagesObservableList.addAll(messages);
        if (!messagesObservableList.isEmpty()) {
            ChatList.scrollTo(messagesObservableList.size() - 1);
        }
    }

    @FXML private void SendMessage(){
        String Content = MessageBox.getText();

        if(Content.isEmpty())
            return;

        messagesObservableList.add(new Message(user.getUserId().toString(), RecUser.getUserId().toString(), Content, Message.MessageType.PRIVATE));
        CSH.sendMessage(new Message(user.getUsername(), RecUser.getUsername(), Content, Message.MessageType.PRIVATE));

        DatabaseManager.connect();
        if(MediaPath != null)
            MessageDao.createMessage(new Message(user.getUserId().toString(), RecUser.getUserId().toString(), Content, Message.MessageType.PRIVATE, MediaPath));
        else
            MessageDao.createMessage(new Message(user.getUserId().toString(), RecUser.getUserId().toString(), Content, Message.MessageType.PRIVATE));
        UpdateChatBox();

        SendMicButton.setImage(new Image(getClass().getResource("/_365/telegram/media/Icons/microphone-8-32.png").toExternalForm()));

        MessageBox.setText(null);
        MediaPath = null;
        ClipButton.setImage(new Image(getClass().getResource("/_365/telegram/media/Icons/attach-32.png").toExternalForm()));
    }

    @FXML void UpdateChatBox() {
        DatabaseManager.connect();
        List<ChatListItem> ActiveChats = ChatDao.getChatListForUser(user.getUserId());
        ChatListContainer.setStyle("-fx-background-color: #17212b;");

        if (ChatListContainer != null) {
            ChatListContainer.getItems().setAll(ActiveChats);

            ChatListContainer.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(ChatListItem chat, boolean empty) {
                    super.updateItem(chat, empty);
                    setBackground(Background.EMPTY); // make cell transparent
                    setStyle("-fx-background-color: transparent;");

                    if (empty || chat == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        setGraphic(GenerateChatBox(chat));
                        setPadding(Insets.EMPTY); // no gaps // use your HBox generator
                    }
                }
            });
        }
        DatabaseManager.close();
    }

    @FXML ImageView ClipButton;

    @FXML private void PhotoPicker(){
        FileChooser picker = new FileChooser();
        picker.setTitle("Select an image for your profile picture");
        picker.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) CenterBar.getScene().getWindow();
        File pfp = picker.showOpenDialog(stage);
        Path destinationDir = Path.of("src/main/java/_365/telegram/Media/Sent");

        if (pfp != null) {
            if (pfp.length() > 10 * 1024 * 1024) {
                ClipButton.setImage(new Image(getClass().getResource("/_365/telegram/media/Icons/red.png").toExternalForm()));
            } else {
                try{
                    String DateForString = LocalDateTime.now().toString().replace(":", "");
                    Path destination = destinationDir.resolve(DateForString + ".png");
                    Files.copy(pfp.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                    MediaPath = DateForString + ".png";

                    ClipButton.setImage(new Image(getClass().getResource("/_365/telegram/media/Icons/green.png").toExternalForm()));
                }
                catch (IOException e) {
                    System.out.println("IOException: " + e);
                }
            }
        } else {
            ClipButton.setImage(new Image(getClass().getResource("/_365/telegram/media/Icons/red.png").toExternalForm()));
        }
    }
}
