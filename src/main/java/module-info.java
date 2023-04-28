module com.example.guichat {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.guichat to javafx.fxml;
    exports com.example.guichat;
}