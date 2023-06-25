package com.example.movidle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class HelloController {

    static class MovieListMovieInfo {
        public String[] movieInfo;

        ///////////////////////////////////////////////////////////////////////////
        // static class constructor
        ///////////////////////////////////////////////////////////////////////////
        public MovieListMovieInfo(String info1, String info2, String info3, String info4, String info5, String info6) {
            movieInfo = new String[]{info1, info2, info3, info4, info5, info6};
        }

        public String getInfo(int index) {
            return movieInfo[index];
        }

        public int getInfoCounter() {
            return (int) Arrays.stream(movieInfo).count();
        }
    }

    HashMap<String, MovieListMovieInfo> movieList = new HashMap<>();
    int randomMovieNumberKey;
    String userKey;
    String key;
    boolean foundMatch;//eşleşme bulunup bulunmama durumu tutuluyor

    @FXML
    private FlowPane answerPane;

    @FXML
    private Button clickButton;

    @FXML
    private TextField input;

    @FXML
    private Label alertLabel;

    @FXML
    void clickedButton() {

        // TODO: 24.06.2023 değerlerin eşleşme durumunu for döngüsü ile yaz
        String key = String.valueOf(randomMovieNumberKey);
        System.out.println(movieList.get(key).getInfoCounter());
        this.key = key; //local variable ile method içi variable eşitlendi
        foundMatch = false;


        // FIXME: 24.06.2023 büyük küçük harf duyarlılığı yok düzelt
        String tempUserKey = input.getText();
        movieList.forEach((s, movieNames) -> {
            if (tempUserKey.equals(movieNames.getInfo(0))) {
                userKey = s;
                foundMatch = true;
            }
        });
        if (!foundMatch) {
            alertLabel.setText("Girilen film sözlükte mevcut değil!");
            userKey = null;
            foundMatch = false;
        }
        if (userKey != null) {
            compareMovieInfo();
        }

        // TODO: 22.06.2023 tahmin button kullanıldıktan sonra database ile iletişim sağlanıp veriye göre 6 adet buton oluşturulacak buton sayısı tablonun sütununa bağlanacak
        // TODO: 23.06.2023 getinfo methodları kullanarak girilen veri ile kıyaslama yap
        // TODO: 23.06.2023 textfield autocomple textfield
    }

    @FXML
    void initialize() throws IOException {
        assert clickButton != null : "fx:id=\"clickButton\" was not injected: check your FXML file 'hello-view.fxml'.";
        assert input != null : "fx:id=\"input\" was not injected: check your FXML file 'hello-view.fxml'.";
        assert alertLabel != null : "fx:id=\"output\" was not injected: check your FXML file 'hello-view.fxml'.";
        randomNumberCreator();
        readDatabase();
    }

    void randomNumberCreator() {
        Random random = new Random();
        randomMovieNumberKey = random.nextInt(251);// TODO: 25.06.2023 sınırlamayı listenin uzunluğu ile değiştir +1 ekle
    }


    void readDatabase() throws IOException {
        // TODO: 23.06.2023 büyük küçük harf uyumluluğu touppercase ve lowercase
        // TODO: 23.06.2023 charset ayarla yabancı harfler için
            String filePath = "./imdb_top_250.csv";
            BufferedReader databaseReader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = databaseReader.readLine()) != null) {
                String[] data = line.split(";");
                String key = data[0]; // ilk sütun = key
                String info1 = data[1];
                String info2 = data[2];
                String info3 = data[3];
                String info4 = data[4];
                String info5 = data[5];
                String info6 = data[6];
                movieList.put(key, new MovieListMovieInfo(info1, info2, info3, info4, info5, info6));
            }
            removeTitleFromDatabase(); //liste okunduktan sonra tablonun başlıklarını sil
            databaseReader.close();

            ///////////////////////////////////////////////////////////////////////////
            // rastgele üretilen sayıya göre listeden bir film seçip konsola yazdır İŞİN BİTİNCE SİL
            ///////////////////////////////////////////////////////////////////////////
            String key = String.valueOf(randomMovieNumberKey);
            if (movieList.containsKey(key)) {
                MovieListMovieInfo movieInfo = movieList.get(key);
                System.out.println("Key: " + key);
                for (int i = 0; i < movieInfo.getInfoCounter(); i++) {
                    System.out.println("Info" + i + ": " + movieInfo.getInfo(i));
                }

            } else {
                System.out.println("Belirtilen key bulunamadı.");
            }

            ///////////////////////////////////////////////////////////////////////////
            // okunan bütün listeyi yazdır İŞİN BİTİNCE SİL
            ///////////////////////////////////////////////////////////////////////////


    }

    void removeTitleFromDatabase() {
        movieList.remove("No");
    }

    void compareMovieInfo() {
        for (int i = 0; i <= movieList.get(key).getInfoCounter(); i++) {
            if (movieList.get(key).getInfo(i).equals(movieList.get(userKey).getInfo(i))) {
                Button trueButton = new Button(movieList.get(key).getInfo(i));
                trueButton.getStyleClass().clear();
                trueButton.setStyle("-fx-background-color: green");
                answerPane.getChildren().add(trueButton);
            } else {
                Button falseButton = new Button(movieList.get(userKey).getInfo(i));
                falseButton.getStyleClass().clear();
                falseButton.setStyle("-fx-background-color: red");
                answerPane.getChildren().add(falseButton);
            }
        }
    }
}


