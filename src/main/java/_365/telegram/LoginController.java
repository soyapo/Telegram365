package _365.telegram;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.util.Objects;

import _365.telegram.db.DatabaseManager;
import _365.telegram.db.UserDao;


@SuppressWarnings("ALL")
public class LoginController {

    @FXML private TextField PhoneField;
    @FXML private TextField CodeField;

    @FXML private Line CodeFieldUnderline;
    @FXML private Line PhoneFieldUnderline;
    @FXML private Line CountryFieldUnderline;

    @FXML private void MouseEnteredCode(){ CodeFieldUnderline.setStroke(Color.web("#2f6ea5")); }
    @FXML private void MouseExitedCode(){ CodeFieldUnderline.setStroke(Color.WHITE); }
    @FXML private void MouseEnteredPhone(){ PhoneFieldUnderline.setStroke(Color.web("#2f6ea5")); }
    @FXML private void MouseExitedPhone(){ PhoneFieldUnderline.setStroke(Color.WHITE); }
    @FXML private void MouseEnteredCountry(){ CountryFieldUnderline.setStroke(Color.web("#2f6ea5")); }
    @FXML private void MouseExitedCountry(){ CountryFieldUnderline.setStroke(Color.WHITE); }


    private ClientSocketHandler CSH = SharedCSH.getClientSocketHandler();

    private static String PhoneInput;
    private static String ServerVerificationCode;
    private static String UserVerificationCode;

    public static String GetPhoneInput() { return PhoneInput; }
    public static String GetVerificationCode(){ return ServerVerificationCode; }

    private void SwitchScenes(ActionEvent e, String FXMLPath) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(FXMLPath)));
        Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    public void SwitchLoginScene(ActionEvent event) throws IOException {
        SwitchScenes(event,"LoginMenuVerification.fxml");
    }
    private void SwitchAuthScene(ActionEvent event) throws IOException {
        SwitchScenes(event,"LoginMenuAuthenticate.fxml");
    }
    private void SwitchSignUpScene(ActionEvent event) throws IOException {
        SwitchScenes(event, "SignupMenu.fxml");
    }
    private void SwitchMainScene(ActionEvent event) throws IOException {
        SwitchScenes(event,"Main.fxml");
    }

    private void ShowCodeScene() throws IOException {
        Parent Root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("VerificationCode.fxml")));
        Stage stage = new Stage();
        Scene VerificationScene = new Scene(Root);
        stage.setScene(VerificationScene);
        stage.setResizable(false);
        stage.show();
    }


    public void SubmitNumber(ActionEvent event) {
        PhoneInput = PhoneField.getText();
        if (PhoneInput.matches("^9\\d{9}$")) {
            try {
                SwitchAuthScene(event);
                ServerVerificationCode = Server.generateVerificationCode(PhoneInput);
                ShowCodeScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            PhoneFieldUnderline.setStroke(Color.RED);
    }

    public void VerifyCode(ActionEvent event) {
        UserVerificationCode = CodeField.getText();
        if(!UserVerificationCode.equals(ServerVerificationCode)){
            CodeFieldUnderline.setStroke(Color.RED);
        }
        else{
            DatabaseManager.connect();
            if(!UserDao.phoneNumberExists(PhoneInput)) {
                try { SwitchSignUpScene(event); }
                catch (IOException e) { e.printStackTrace(); }
            }
            else{
                try { SwitchMainScene(event); }
                catch (IOException e) { e.printStackTrace(); }
            }
        }
    }
}