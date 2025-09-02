package _365.telegram.Login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.util.Objects;

public class LoginController {
    public void SwitchLoginScene(ActionEvent event) throws IOException {
        Parent VerificationRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("LoginMenuVerification.fxml")));
        Stage VerificationStage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene VerificationScene = new Scene(VerificationRoot);
        VerificationStage.setScene(VerificationScene);
        VerificationStage.show();
    }

    @FXML
    private Line PhoneFieldUnderline;
    @FXML
    private Line CountryFieldUnderline;


    @FXML
    public void MouseEnteredPhone() {
        PhoneFieldUnderline.setStroke(Color.web("#2f6ea5"));
    }
    public void MouseExitedPhone(){
        PhoneFieldUnderline.setStroke(Color.WHITE);
    }

    @FXML
    public void MouseEnteredCountry() {
        CountryFieldUnderline.setStroke(Color.web("#2f6ea5"));
    }
    public void MouseExitedCountry(){
        CountryFieldUnderline.setStroke(Color.WHITE);
    }
}