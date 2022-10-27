module com.example.triangle {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.triangle to javafx.fxml;
    exports com.example.triangle;
}