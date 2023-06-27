package com.example.movidle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;


public class MainClass {

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
    String userKey;
    String key;
    boolean foundMatch; //eşleşme bulunup bulunmama durumu tutuluyor
    int randomMovieNumberKey;
    int testNumber = 0;
    int lives = 5;
    int correctInfo = 0;

    @FXML
    private Label alertLabel;

    @FXML
    private GridPane answersGridPane;

    @FXML
    private Button clickButton;

    @FXML
    private TextField input;

    @FXML
    private Pane losePane;

    @FXML
    private Pane winPane;

    @FXML
    void ClickedButton() {
        String key = String.valueOf(randomMovieNumberKey);
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
        if (userKey != null && testNumber < lives) {
            addTestNumber();
            CompareMovieInfo();
        }
        // TODO: 23.06.2023 textfield autocomple textfield
    }

    @FXML
    void initialize() throws IOException {
        winPane.setVisible(false); // TODO: 28.06.2023 methodlara ayrılabilir sonra
        losePane.setVisible(false);
        StartingProgramAssets();
        RandomNumberCreator();
        ReadDatabase();
        ClearAllGridPaneCells();
    }

    public void ClickQuit() {
        Alert logoutalert = new Alert(Alert.AlertType.CONFIRMATION);
        logoutalert.setTitle("Çıkış");
        logoutalert.setHeaderText("Çıkış Yap?");
        logoutalert.setContentText("Gerçekten çıkmak istiyor musunuz?");
        if (logoutalert.showAndWait().get() == ButtonType.OK)
            Platform.exit();
    }

    private void StartingProgramAssets() {
        assert clickButton != null : "fx:id=\"clickButton\" was not injected: check your FXML file 'hello-view.fxml'.";
        assert input != null : "fx:id=\"input\" was not injected: check your FXML file 'hello-view.fxml'.";
        assert alertLabel != null : "fx:id=\"output\" was not injected: check your FXML file 'hello-view.fxml'.";
        assert answersGridPane != null : "fx:id=\"answerGridPane\" was not injected: check your FXML file 'hello-view.fxml'.";
    }

    void RandomNumberCreator() {
        Random random = new Random();
        randomMovieNumberKey = random.nextInt(251);// TODO: 25.06.2023 sınırlamayı listenin uzunluğu ile değiştir +1 ekle
    }


    void ReadDatabase() throws IOException {
        // TODO: 23.06.2023 büyük küçük harf uyumluluğu touppercase ve lowercase
        // TODO: 23.06.2023 charset ayarla yabancı harfler için bu durum autocomplete textfield ile çözülecek
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
        RemoveTitleFromDatabase(); //liste okunduktan sonra tablonun başlıklarını sil
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
    }

    void RemoveTitleFromDatabase() {
        movieList.remove("No");
    }

    void CompareMovieInfo() {
        SequentialTransition sequentialTransition = new SequentialTransition();
        Image upArrow = new Image(getClass().getResource("/img/up.png").toExternalForm());
        Image downArrow = new Image(getClass().getResource("/img/down.png").toExternalForm());
        correctInfo = 0;
        for (int i = 0; i < movieList.get(key).getInfoCounter(); i++) {
            Button button = CreateButtonSetOptionsAndAnimation(sequentialTransition, i);
            if (movieList.get(key).getInfo(i).equals(movieList.get(userKey).getInfo(i))) {
                button.setText(movieList.get(key).getInfo(i));
                button.setStyle("-fx-background-color: green");
                correctInfo++;
            } else {
                button.setText(movieList.get(userKey).getInfo(i));
                button.setStyle("-fx-background-color: red");
                if (i == 1) {
                    if (Integer.parseInt((movieList.get(key).getInfo(1))) > Integer.parseInt((movieList.get(userKey).getInfo(1)))) {
                        ImageView imageView = new ImageView(upArrow);
                        YearImageArrowsOptions(imageView, button);
                    } else if (Integer.parseInt((movieList.get(key).getInfo(1))) < Integer.parseInt((movieList.get(userKey).getInfo(1)))) {
                        ImageView imageView = new ImageView(downArrow);
                        YearImageArrowsOptions(imageView, button);
                    }
                }
            }
        }
        sequentialTransition.play();
        if (correctInfo >= 6) {
            winPane.setVisible(true);

        } // TODO: 28.06.2023 kazandınız veya kaybettiniz ekranına gecikme ekle animasyonlar tamamlandıktan sonra ekran gelsin
        if (testNumber >= lives) {
            losePane.setVisible(true);
        }
    }

    public Button CreateButtonSetOptionsAndAnimation(SequentialTransition sequentialTransition, int i) {
        Button button = new Button(movieList.get(key).getInfo(i));
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setFillHeight(button, true);
        GridPane.setFillWidth(button, true);
        button.setWrapText(true);
        button.setTextAlignment(TextAlignment.CENTER);
        button.setDisable(true);
        answersGridPane.add(button, i, testNumber);
        sequentialTransition.getChildren().add(FadeInTransitionEffect(button));
        return button;
    }

    FadeTransition FadeInTransitionEffect(Button button) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(700), button);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        return fadeIn;
    }

    private void YearImageArrowsOptions(ImageView imageView, Button button) {
        imageView.setOpacity(0.5);
        imageView.setFitWidth(35.0); // TODO: 27.06.2023 opacity hariç sabit ölçüler responsive yapıya çevrilecek
        imageView.setFitHeight(35.0);
        button.setContentDisplay(ContentDisplay.CENTER);
        button.setGraphic(imageView);
    }

    void addTestNumber() {
        testNumber++;
    }

    void ClearAllGridPaneCells() {
        ObservableList answersGridPaneChildren = answersGridPane.getChildren();
        answersGridPaneChildren.clear();
        testNumber = 0;
    }
}