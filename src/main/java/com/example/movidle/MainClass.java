package com.example.movidle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
    ///////////////////////////////////////////////////////////////////////////
    // database de bulunan yabancı dillerin alfabelerinin özel karakterlerini türkçe alfabeye uygun hale getirmek için ör: é = e
    ///////////////////////////////////////////////////////////////////////////
    public static class MovieNamesConvertToTurkish {
        public static String ConvertToTurkish(String filmAdi) {
            String normalized = Normalizer.normalize(filmAdi, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            String turkishMovieName = pattern.matcher(normalized).replaceAll("");
            return turkishMovieName;
        }
    }

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
    ArrayList<String> comboboxMovieNames = new ArrayList<>();
    ComboBox<String> comboBox = new ComboBox<>();
    String userKey;
    String key;
    int randomMovieNumberKey;
    boolean foundMatch; //eşleşme bulunup bulunmama durumu tutuluyor
    int correctInfo = 0;
    int testNumber = 0;
    int lives = 5;

    @FXML
    private Label alertLabel;

    @FXML
    private GridPane answersGridPane;

    @FXML
    private Pane losePane;

    @FXML
    private Pane winPane;

    @FXML
    private ComboBox<String> autoCompleteCombobox;

    @FXML
    void ClickedButton() {
        alertLabel.setText("");
        String key = String.valueOf(randomMovieNumberKey);
        this.key = key; //local variable ile method içi variable eşitlendi
        foundMatch = false;
        String tempUserKey = comboBox.getEditor().getText().toLowerCase();
        movieList.forEach((s, movieNames) -> {
            if (tempUserKey.equals(movieNames.getInfo(0).toLowerCase())) {
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
            comboBox.getEditor().clear();
        }
    }

    @FXML
    void initialize() throws IOException {
        comboboxMovieNames.clear();
        ReadDatabase();
        RestartGame();
    }

    @FXML
    void RestartGame() {
        winPane.setVisible(false);
        losePane.setVisible(false);
        comboBox = autoCompleteCombobox;
        comboBox.getEditor().clear();
        RandomNumberCreator();
        TestMethod();
        ClearAllGridPaneCells();
        ComboboxShowsFilteredValues();
    }

    public void ComboboxShowsFilteredValues() {
        comboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                comboBox.getItems().clear();
                comboBox.setItems(FXCollections.observableArrayList(comboboxMovieNames));
            } else {
                String filteredValue = newValue.toLowerCase();
                List<String> filteredItems = comboboxMovieNames.stream()
                        .filter(item -> item.toLowerCase().contains(filteredValue))
                        .collect(Collectors.toList());
                comboBox.setItems(FXCollections.observableArrayList(filteredItems));
            }
        });
    }

    void RandomNumberCreator() {
        Random random = new Random();
        randomMovieNumberKey = random.nextInt(movieList.size() + 1);
    }

    void ReadDatabase() throws IOException {
        String filePath = "./imdb_top_250.csv";
        BufferedReader databaseReader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = databaseReader.readLine()) != null) {
            String[] data = line.split(";");
            String key = data[0]; // ilk sütun = key
            String info1 = MovieNamesConvertToTurkish.ConvertToTurkish(data[1]);
            String info2 = data[2];
            String info3 = data[3];
            String info4 = data[4];
            String info5 = data[5];
            String info6 = data[6];
            movieList.put(key, new MovieListMovieInfo(info1, info2, info3, info4, info5, info6));
            comboboxMovieNames.add(info1);
        }
        RemoveTitleFromDatabase();
        databaseReader.close();
        comboBox.getItems().addAll(FXCollections.observableArrayList(comboboxMovieNames));
    }

    private void TestMethod() {
        ///////////////////////////////////////////////////////////////////////////
        // rastgele üretilen sayıya göre listeden bir film seçip konsola yazdır TEST AMAÇLI YAZILMIŞTIR
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
        comboboxMovieNames.remove("Title");
    }

    void CompareMovieInfo() {
        SequentialTransition sequentialTransition = new SequentialTransition();
        Image upArrow = new Image(getClass().getResource("/img/up.png").toExternalForm());
        Image downArrow = new Image(getClass().getResource("/img/down.png").toExternalForm());
        correctInfo = 0;
        for (int i = 0; i < movieList.get(key).getInfoCounter(); i++) {
            Button button = CreateButtonSetOptionsAndAnimation(sequentialTransition, i);
            if (movieList.get(key).getInfo(i).toLowerCase().equals(movieList.get(userKey).getInfo(i).toLowerCase())) {
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
        sequentialTransition.setOnFinished(event -> {
            if (correctInfo >= 6) {
                winPane.setVisible(true);
            } else {
                if (testNumber >= lives) {
                    losePane.setVisible(true);
                }
            }
        });
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
        imageView.setFitWidth(35.0);
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

    public void ClickQuit() {
        Alert logoutalert = new Alert(Alert.AlertType.CONFIRMATION);
        logoutalert.setTitle("Çıkış");
        logoutalert.setHeaderText("Çıkış Yap?");
        logoutalert.setContentText("Gerçekten çıkmak istiyor musunuz?");
        if (logoutalert.showAndWait().get() == ButtonType.OK)
            Platform.exit();
    }
}