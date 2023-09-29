module com.example.tombstonetussle {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.tombstonetussle to javafx.fxml;
    exports com.example.tombstonetussle;
}