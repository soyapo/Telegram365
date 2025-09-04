package _365.telegram;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import _365.telegram.SharedCSH;

public class Client extends Application {

    private Stage main_stage;
    private ClientSocketHandler CSH;

    @Override
    public void start(Stage stage) throws IOException {
        this.main_stage = stage;
        this.CSH = new ClientSocketHandler();

        boolean connected = CSH.connect("localhost", 12345);
        if(!connected){
            System.out.println("Failed");
            return;
        }
        SharedCSH.setClientSocketHandler(CSH);
        showLoginScene();
    }

    public void showLoginScene() throws IOException {
        FXMLLoader BaseLoader = new FXMLLoader(Client.class.getResource("LoginMenuBase.fxml"));
        Scene LoginMenuBase = new Scene(BaseLoader.load(), 1080, 720);
        main_stage.setTitle("Telegram 365");
        main_stage.setScene(LoginMenuBase);
        main_stage.setResizable(false);
        main_stage.show();
    }

}