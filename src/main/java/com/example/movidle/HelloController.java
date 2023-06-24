package com.example.movidle;

import java.io.BufferedReader;
import java.io.FileReader;
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

        ///////////////////////////////////////////////////////////////////////////
        // getter methodlar kullanılmayacaksa sil
        ///////////////////////////////////////////////////////////////////////////
        public String getInfo0() {
            return movieInfo[0];
        }

        public String getInfo1() {
            return movieInfo[1];
        }

        public String getInfo2() {
            return movieInfo[2];
        }

        public String getInfo3() {
            return movieInfo[3];
        }

        public String getInfo4() {
            return movieInfo[4];
        }

        public String getInfo5() {
            return movieInfo[5];
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
        MovieListMovieInfo movieInfo = movieList.get(key);
        this.key = key; //local variable ile method içi variable eşitlendi
        foundMatch = false;


        // FIXME: 24.06.2023 büyük küçük harf duyarlılığı yok düzelt
        String tempUserKey = input.getText();
        movieList.forEach((s, movieNames) -> {
            if (tempUserKey.equals(movieNames.getInfo0())) {
                userKey = s;
                foundMatch = true;
            }
        });
        if (!foundMatch) {
            alertLabel.setText("Girilen film sözlükte mevcut değil!");
            userKey = null;
            foundMatch = false;
        }

        //MovieListMovieInfo userMovieInfo = movieList.get(userKey);
        //System.out.println(userMovieInfo.getInfo0());
        if (userKey != null) {
            compareMovieNames();
            compareMovieYears();
            compareMovieGenres();
            compareMovieOrigins();
            compareMovieDirectors();
            compareMovieStars();
        }



        /*if(userMovieInfo.getInfo0() == movieInfo.getInfo0()){
            Button newButton = new Button(movieInfo.getInfo0());
            //newButton.setDisable(true);
            newButton.getStyleClass().clear();
            newButton.setStyle("-fx-background-color: green");
            answerPane.getChildren().add(newButton);
        }
        else {
            System.out.println("boş");
        }*/

        // TODO: 22.06.2023 tahmin button kullanıldıktan sonra database ile iletişim sağlanıp veriye göre 6 adet buton oluşturulacak buton sayısı tablonun sütununa bağlanacak
        // TODO: 23.06.2023 getinfo methodları kullanarak girilen veri ile kıyaslama yap
        // TODO: 23.06.2023 textfield autocomple textfield

        /*for (int i = 1; i <= 6; i++) {
        //    Button newButton = new Button("Yeni Buton " + i);
        //    //newButton.setDisable(true);
        //    answerPane.getChildren().add(newButton);
        }*/
    }

    @FXML
    void initialize() {
        assert clickButton != null : "fx:id=\"clickButton\" was not injected: check your FXML file 'hello-view.fxml'.";
        assert input != null : "fx:id=\"input\" was not injected: check your FXML file 'hello-view.fxml'.";
        assert alertLabel != null : "fx:id=\"output\" was not injected: check your FXML file 'hello-view.fxml'.";
        randomNumberCreator();
        readDatabase();

    }

    void randomNumberCreator() {
        System.out.println(movieList.size());
        Random random = new Random();
        int randomNumber = random.nextInt(251);// TODO: 25.06.2023 sınırlamayı listenin uzunluğu ile değiştir +1 ekle
        randomMovieNumberKey = randomNumber;
    }


    void readDatabase() {
        // TODO: 23.06.2023 büyük küçük harf uyumluluğu touppercase ve lowercase
        // TODO: 23.06.2023 charset ayarla yabancı harfler için
        try {
            String filePath = "./imdb_top_250.csv";
            BufferedReader databaseReader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = databaseReader.readLine()) != null) {
                String[] data = line.split(";");
                String key = data[0]; // ilk sütun = gameKey
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
                System.out.println("Info0: " + movieInfo.getInfo0());
                System.out.println("Info1: " + movieInfo.getInfo1());
                System.out.println("Info2: " + movieInfo.getInfo2());
                System.out.println("Info3: " + movieInfo.getInfo3());
                System.out.println("Info4: " + movieInfo.getInfo4());
                System.out.println("Info5: " + movieInfo.getInfo5());

            } else {
                System.out.println("Belirtilen key bulunamadı.");
            }

            ///////////////////////////////////////////////////////////////////////////
            // okunan bütün listeyi yazdır İŞİN BİTİNCE SİL
            ///////////////////////////////////////////////////////////////////////////
            /*for (Map.Entry<String, MovieListMovieInfo> entry : movieList.entrySet()) {
                String key = entry.getKey();
                MovieListMovieInfo movieInfo = entry.getValue();
                System.out.println("Key: " + key);
                System.out.println("Movie Informations: " + movieInfo.getInfo0()
                        + " - " + movieInfo.getInfo1()
                        + " - " + movieInfo.getInfo2()
                        + " - " + movieInfo.getInfo3()
                        + " - " + movieInfo.getInfo4()
                        + " - " + movieInfo.getInfo5());
                System.out.println("------------------------");
            }*/
        } catch (Exception exception) {
            System.out.println("liste boş");
        }
    }

    void removeTitleFromDatabase() {
        movieList.remove("No");
    }

    void compareMovieNames() {
        if (movieList.get(key).getInfo0().equals(movieList.get(userKey).getInfo0())) {
            Button trueButton = new Button(movieList.get(key).getInfo0());
            trueButton.getStyleClass().clear();
            trueButton.setStyle("-fx-background-color: green");
            answerPane.getChildren().add(trueButton);
        } else {
            Button falseButton = new Button(movieList.get(userKey).getInfo0());
            falseButton.getStyleClass().clear();
            falseButton.setStyle("-fx-background-color: red");
            answerPane.getChildren().add(falseButton);
        }
    }

    void compareMovieYears() {
        if (movieList.get(key).getInfo1().equals(movieList.get(userKey).getInfo1())) {
            Button trueButton = new Button(movieList.get(key).getInfo1());
            trueButton.getStyleClass().clear();
            trueButton.setStyle("-fx-background-color: green");
            answerPane.getChildren().add(trueButton);
        } else {
            Button falseButton = new Button(movieList.get(userKey).getInfo1());
            falseButton.getStyleClass().clear();
            falseButton.setStyle("-fx-background-color: red");
            answerPane.getChildren().add(falseButton);
        }
    }
    void compareMovieGenres() {
        if (movieList.get(key).getInfo2().equals(movieList.get(userKey).getInfo2())) {
            Button trueButton = new Button(movieList.get(key).getInfo2());
            trueButton.getStyleClass().clear();
            trueButton.setStyle("-fx-background-color: green");
            answerPane.getChildren().add(trueButton);
        } else {
            Button falseButton = new Button(movieList.get(userKey).getInfo2());
            falseButton.getStyleClass().clear();
            falseButton.setStyle("-fx-background-color: red");
            answerPane.getChildren().add(falseButton);
        }
    }
    void compareMovieOrigins() {
        if (movieList.get(key).getInfo3().equals(movieList.get(userKey).getInfo3())) {
            Button trueButton = new Button(movieList.get(key).getInfo3());
            trueButton.getStyleClass().clear();
            trueButton.setStyle("-fx-background-color: green");
            answerPane.getChildren().add(trueButton);
        } else {
            Button falseButton = new Button(movieList.get(userKey).getInfo3());
            falseButton.getStyleClass().clear();
            falseButton.setStyle("-fx-background-color: red");
            answerPane.getChildren().add(falseButton);
        }
    }
    void compareMovieDirectors() {
        if (movieList.get(key).getInfo4().equals(movieList.get(userKey).getInfo4())) {
            Button trueButton = new Button(movieList.get(key).getInfo3());
            trueButton.getStyleClass().clear();
            trueButton.setStyle("-fx-background-color: green");
            answerPane.getChildren().add(trueButton);
        } else {
            Button falseButton = new Button(movieList.get(userKey).getInfo4());
            falseButton.getStyleClass().clear();
            falseButton.setStyle("-fx-background-color: red");
            answerPane.getChildren().add(falseButton);
        }
    }
    void compareMovieStars() {
        if (movieList.get(key).getInfo5().equals(movieList.get(userKey).getInfo5())) {
            Button trueButton = new Button(movieList.get(key).getInfo5());
            trueButton.getStyleClass().clear();
            trueButton.setStyle("-fx-background-color: green");
            answerPane.getChildren().add(trueButton);
        } else {
            Button falseButton = new Button(movieList.get(userKey).getInfo5());
            falseButton.getStyleClass().clear();
            falseButton.setStyle("-fx-background-color: red");
            answerPane.getChildren().add(falseButton);
        }
    }

}


