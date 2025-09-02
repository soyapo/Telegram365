module _365.telegram {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;
    requires java.sql;

    opens _365.telegram to javafx.fxml;
    exports _365.telegram;
    exports _365.telegram.Login;
    opens _365.telegram.Login to javafx.fxml;
}