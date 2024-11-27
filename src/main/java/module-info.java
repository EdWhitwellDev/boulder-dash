module com.example.boulderdash {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.boulderdash to javafx.fxml;
    exports com.example.boulderdash;
}