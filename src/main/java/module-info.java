module _365.telegram {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens _365.telegram to javafx.fxml;
    exports _365.telegram;
}