package _365.telegram;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AuthCodeController {
    @FXML
    private Label AuthCodeBox;

    @FXML
    private void initialize() {
        AuthCodeBox.setText(LoginController.GetVerificationCode());
    }
}
