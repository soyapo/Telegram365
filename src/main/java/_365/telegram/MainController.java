package _365.telegram;

import _365.telegram.db.DatabaseManager;
import _365.telegram.db.UserDao;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.awt.*;

public class MainController {
    User user;

    @FXML private void initialize() {
        DatabaseManager.connect();
        user = UserDao.getUserByPhone(LoginController.GetPhoneInput());
        user.setStatus("Online");
        DatabaseManager.close();

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
    }

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
