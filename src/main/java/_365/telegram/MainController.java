package _365.telegram;

import _365.telegram.db.ChatDao;
import _365.telegram.db.ChatListItem;
import _365.telegram.db.DatabaseManager;
import _365.telegram.db.UserDao;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class MainController {
    User user;

    @FXML private void initialize() {
        // -- connect to database to retrieve data --
        DatabaseManager.connect();

        // -- set user --
        user = UserDao.getUserByPhone(LoginController.GetPhoneInput());
        user.setStatus("Online");

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

        List<ChatListItem> ActiveChats = ChatDao.getChatListForUser(user.getUserId());
        ChatListContainer.setStyle("-fx-background-color: #17212b;");

        if(ChatListContainer != null){
            ChatListContainer.getItems().setAll(ActiveChats);

            ChatListContainer.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(ChatListItem chat, boolean empty) {
                    super.updateItem(chat, empty);

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


        // -- Close database connection --
        DatabaseManager.close();
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
        HBox.setHgrow(textContainer, Priority.ALWAYS);
        box.setCursor(Cursor.HAND);

        box.setOnMouseEntered(e -> box.setStyle("-fx-background-color: #232e3c;"));
        box.setOnMouseExited(e -> box.setStyle("-fx-background-color: #17212b;"));

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
}
