package _365.telegram;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

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

        showLoginScene();
    }

    public void showLoginScene() throws IOException {
        FXMLLoader BaseLoader = new FXMLLoader(Client.class.getResource("LoginMenuBase.fxml"));
        main_stage.setScene(new Scene(BaseLoader.load()));
        LoginController controller = BaseLoader.getController();
        controller.initSocketHandler(CSH);
        main_stage.setTitle("Telegram 365");
        //stage.setScene(LoginMenuBase);
        main_stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}