module com.example.boulderdash {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.boulderdash to javafx.fxml;
    exports com.example.boulderdash;
    exports com.example.boulderdash.Actors;
    opens com.example.boulderdash.Actors to javafx.fxml;
    exports com.example.boulderdash.enums;
    opens com.example.boulderdash.enums to javafx.fxml;
    exports com.example.boulderdash.Tiles;
    opens com.example.boulderdash.Tiles to javafx.fxml;
}