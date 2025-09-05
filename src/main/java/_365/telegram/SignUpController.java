package _365.telegram;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import _365.telegram.db.DatabaseManager;
import _365.telegram.db.UserDao;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class SignUpController {

    // Maximum PfP file size
    private static final long MAX_FILE_SIZE = 1024 * 1024 * 10; // 10 MB

    //Final SignUp Values
    private String Username;
    private String Bio;
    private String ProfilePath;
    private final String Phone = LoginController.GetPhoneInput();

    // FX styles for Bio TextArea and Picker's Label
    private final String BioStyle = "-fx-text-fill: white; -fx-control-inner-background: #17212b; -fx-padding: 0px; -fx-border-radius: 5px; -fx-background-color: #17212b; -fx-border-width: 1; -fx-border-color: ";
    private final String PickerStyle = "-fx-padding: 10px; -fx-border-radius: 7px; -fx-background-radius: 7px; -fx-border-color: ";


    @FXML private TextField SignupName;
    @FXML private TextField SignupPhone;
    @FXML private TextArea SignupBio;

    // Lines
    @FXML private Line SignupNameUnderline;
    @FXML private Line SignupPhoneUnderline;

    @FXML private Label UsernameWarning;
    @FXML private Label BioWarning;

    // Profile photo elements
    @FXML private Label FileChooseLabel;
    @FXML private Button FileChooseButton;
    @FXML private ImageView FileChooseImageView;

    @FXML private void initialize(){
        SignupPhone.setText("+98" + Phone);
        FileChooseButton.toFront(); // Pull the button to the front of the Label, so it can be clicked
        FileChooseImageView.toFront();
    }

    @FXML private void MouseEnteredName() { SignupNameUnderline.setStroke(Color.web("2f6ea5")); }
    @FXML private void MouseExitedName() { SignupNameUnderline.setStroke(Color.WHITE);}
    @FXML private void MouseEnteredPhone() { SignupPhoneUnderline.setStroke(Color.web("2f6ea5"));}
    @FXML private void MouseExitedPhone() { SignupPhoneUnderline.setStroke(Color.WHITE);}
    @FXML private void MouseEnteredBio() { SignupBio.setStyle(BioStyle + "#2f6ea5;"); }
    @FXML private void MouseExitedBio() { SignupBio.setStyle(BioStyle + "#ffffff;"); }
    @FXML private void MouseEnteredPicker() { FileChooseLabel.setStyle(PickerStyle + "#2f6ea5;"); }
    @FXML private void MouseExitedPicker() { FileChooseLabel.setStyle(PickerStyle + "#ffffff"); }

    @FXML private void ProfilePicker(){
        FileChooser picker = new FileChooser();
        picker.setTitle("Select an image for your profile picture");
        picker.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) FileChooseButton.getScene().getWindow();
        File pfp = picker.showOpenDialog(stage);
        Path destinationDir = Path.of("src/main/java/_365/telegram/Media/Profiles");

        if (pfp != null) {
            if (pfp.length() > MAX_FILE_SIZE) {
                FileChooseLabel.setText("Large > 10MB");
                FileChooseLabel.setStyle(PickerStyle + "RED;");
            } else {
               try{
                   String SelectedExtension = pfp.getName().substring(pfp.getName().lastIndexOf("."));
                   Path destination = destinationDir.resolve(Phone + SelectedExtension);
                   Files.copy(pfp.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                   ProfilePath = Phone + SelectedExtension;

                   Image img = new Image(destination.toUri().toString());
                   FileChooseImageView.setImage(img);
                   FileChooseLabel.setText("Success!");
                   FileChooseLabel.setStyle(PickerStyle + "GREEN;");
               }
               catch (IOException e) {
                   System.out.println("IOException: " + e);
               }
            }
        } else {
            FileChooseLabel.setText("Not Selected");
            FileChooseLabel.setStyle(PickerStyle + "RED;");
        }
    }

    @FXML private void SignUp(){
        DatabaseManager.connect();

        Username = SignupName.getText();
        int l = Username.length();
        boolean Exists = UserDao.usernameExists(Username);

        if(Exists || !Username.matches("^[A-Za-z0-9_]{3,20}$")){
            SignupNameUnderline.setStroke(Color.RED);
            if (UserDao.usernameExists(Username))
                UsernameWarning.setText("Username already exists");
            else if (l < 3)
                UsernameWarning.setText("Username must be at least 3 characters long");
            else if (l > 20)
                UsernameWarning.setText("Username must be at most 20 characters long");
            else
                UsernameWarning.setText("Invalid characters used");
            return;
        }

        SignupNameUnderline.setStroke(Color.GREEN);
        UsernameWarning.setText("");

        Bio = SignupBio.getText();
        l = Bio.length();

        if(l > 100){
            SignupBio.setStyle(BioStyle + "RED;");
            BioWarning.setText("Bio must be at most 100 characters long");
            return;
        }

        SignupBio.setStyle(BioStyle + "GREEN;");
        BioWarning.setText("");

        UserDao.insertUser(Phone, Username, Bio, ProfilePath);
        DatabaseManager.close();


        try{
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Main.fxml")));
            Stage stage = (Stage) FileChooseButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        }
        catch (IOException e){
            System.out.println("IOException: " + e);
        }
    }
}
