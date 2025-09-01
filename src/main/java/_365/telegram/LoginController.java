package _365.telegram;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.util.Objects;

public class LoginController {
    // Switching between the base login page and the verification page
    public void SwitchLoginScene(ActionEvent event) throws IOException {
        Parent VerificationRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoginMenuVerification.fxml")));
        Stage VerificationStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene VerificationScene = new Scene(VerificationRoot);
        VerificationStage.setScene(VerificationScene);
        VerificationStage.show();
        TextFieldStyle();
    }


    @FXML
    private TextField PhoneField;
    @FXML
    public void TextFieldStyle(){
        PhoneField.setStyle("-fx-text-fill: #ffffff;");
    }

    @FXML
    private Line PhoneFieldUnderline;
    @FXML // Defining the dynamic color lines beneath the text fields
    private Line CountryFieldUnderline;

    @FXML // Functions for changing the line color of the phone field
    public void MouseEnteredPhone() {
        PhoneFieldUnderline.setStroke(Color.web("#2f6ea5"));
    }
    public void MouseExitedPhone(){
        PhoneFieldUnderline.setStroke(Color.WHITE);
    }

    @FXML // Functions for changing the line color of the country field
    public void MouseEnteredCountry() {
        CountryFieldUnderline.setStroke(Color.web("#2f6ea5"));
    }
    public void MouseExitedCountry(){
        CountryFieldUnderline.setStroke(Color.WHITE);
    }
}