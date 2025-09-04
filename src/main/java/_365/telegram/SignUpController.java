package _365.telegram;

import java.awt.event.ActionEvent;
import java.io.File;

import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class SignUpController {

    private static final long MAX_FILE_SIZE = 1024 * 1024 * 10; // 10 MB
    private String BioStyle = "-fx-text-fill: white; -fx-control-inner-background: #17212b; -fx-padding: 0px; -fx-border-radius: 5px; -fx-background-color: #17212b; -fx-border-width: 1; -fx-border-color: ";

    @FXML private TextField SignupName;
    @FXML private TextField SignupPhone;
    @FXML private TextArea SignupBio;

    @FXML private Line SignupNameUnderline;
    @FXML private Line SignupPhoneUnderline;

    @FXML private Button FileChooseButton;

    @FXML private void initialize(){
        SignupPhone.setText("+98" + LoginController.GetPhoneInput());
    }

    @FXML private void MouseEnteredName() { SignupNameUnderline.setStroke(Color.web("2f6ea5")); }
    @FXML private void MouseExitedName() { SignupNameUnderline.setStroke(Color.WHITE);}
    @FXML private void MouseEnteredPhone() { SignupPhoneUnderline.setStroke(Color.web("2f6ea5"));}
    @FXML private void MouseExitedPhone() { SignupPhoneUnderline.setStroke(Color.WHITE);}
    @FXML private void MouseEnteredBio() { SignupBio.setStyle(BioStyle + "#2f6ea5;"); }
    @FXML private void MouseExitedBio() { SignupBio.setStyle(BioStyle + "#ffffff;"); }

//    private void ProfilePicker(ActionEvent event){
//        FileChooser picker = new FileChooser();
//        picker.setTitle("Select an image for your profile picture");
//        picker.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
//        );
//
//            Stage stage = (Stage) FileChooseButton.getScene().getWindow();
//        File pfp = picker.showOpenDialog(stage);
//
//        if (pfp != null) {
//            if (pfp.length() > MAX_FILE_SIZE) {
//                System.out.println("File too large! Please select an image under 10 MB.");
//            } else {
//                System.out.println("Selected: " + file.getAbsolutePath());
//                // you can now use the image, e.g.:
//                // Image image = new Image(file.toURI().toString());
//            }
//        } else {
//            System.out.println("No file selected.");
//        }
//    }


}
