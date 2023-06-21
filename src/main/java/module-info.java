module com.example.movidle {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.movidle to javafx.fxml;
    exports com.example.movidle;
}