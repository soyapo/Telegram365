package _365.telegram;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Client extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader BaseLoader = new FXMLLoader(Client.class.getResource("LoginMenuBase.fxml"));
        Scene LoginMenuBase = new Scene(BaseLoader.load(), 1080, 720);
        stage.setTitle("Telegram 365");
        stage.setScene(LoginMenuBase);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}