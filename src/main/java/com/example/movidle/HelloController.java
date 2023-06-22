package com.example.movidle;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class HelloController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addButton;

    @FXML
    private Button clickButton;

    @FXML
    private FlowPane answerPane;

    @FXML
    private TextField input;

    @FXML
    private Label output;

    @FXML
    void clickedButton() {
    output.setText(input.getText());
        // TODO: 22.06.2023 tahmin butonu kullanıldıktan sonra database ile iletişim sağlanıp veriye göre 6 adet buton oluşturulacak buton sayısı tablonun sütununa bağlanacak
        
    clickButton.setOnAction(event -> {
        for (int i = 1; i <= 6; i++) {
            Button newButton = new Button("Yeni Buton " + i);
            answerPane.getChildren().add(newButton);
        }
    });
    }



    @FXML
    void initialize() {
        assert clickButton != null : "fx:id=\"clickButton\" was not injected: check your FXML file 'hello-view.fxml'.";
        assert input != null : "fx:id=\"input\" was not injected: check your FXML file 'hello-view.fxml'.";
        assert output != null : "fx:id=\"output\" was not injected: check your FXML file 'hello-view.fxml'.";

    }

    @FXML
    void readDatabase(){
        // TODO: 22.06.2023 database okunacak - hashmap kullanımı araştırılacak
    }

}
