package org.example.chess1.Controllers;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.example.chess1.Colors;
import org.example.chess1.DataBase.Config;
import org.example.chess1.DataBase.DataBase;
import org.example.chess1.Game.Containers.StaticObjects;
import org.example.chess1.Game.Player;
import org.example.chess1.Main;
import org.example.chess1.Server.Client;
import org.example.chess1.Server.StaticInfo;

import javax.crypto.Cipher;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;

public class StatController implements Initializable {
    @FXML private Label userNameLabel, userGamesLabel, userWinsLabel, userLosesLabel, userTiesLabel, userRankLabel;
    @FXML private ImageView userImageView, avatarSettings;
    @FXML private Pane avatarPane, playerStatPain;

    @FXML private Circle statCircle;
    @FXML private Label winsRateLabel;

    @FXML private VBox vBoxPlayers;

    public static StatController instance;

    private ArrayList<Player> players;

    private boolean isReverseSortRank = true, isReverseSortPerCent;
    private Timeline timeline;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        setStats();
        setAvatarPane();
        players = DataBase.getAllPlayers();
        System.out.println(players);
        setPlayers();
    }

    @FXML private void onButtSignOutClicked() {
        StaticObjects.saveData();
        StaticObjects.player = null;
        MenuController.instance.userProfile.setVisible(false);
        MenuController.instance.buttSignIn.setVisible(true);
        Main.exitStat();
    }

    //Sort Functions
    @FXML private void onPerCentSortClicked() {
        players.sort(Comparator.comparingDouble(o -> o.wins == 0 ? 0 : (double)o.wins/o.games));

        if (!isReverseSortPerCent) {
            Collections.reverse(players);
        }
        isReverseSortPerCent = !isReverseSortPerCent;
        isReverseSortRank = false;
        animation();
    }
    @FXML private void onRankSortClicked() {
        players.sort(Comparator.comparingInt(o -> o.rank));
        if (!isReverseSortRank) {
            Collections.reverse(players);
        }
        isReverseSortRank = !isReverseSortRank;
        isReverseSortPerCent = false;
        animation();
    }


    @FXML private void onSelectionChanged() {
        timeline.stop();
        timeline.play();
    }

    //Drawing Functions
    public void setStats() {
        userImageView.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Avatars/avatar" + StaticObjects.player.avatarIdx + ".png")));

        userNameLabel.setText(StaticObjects.player.name);
        userGamesLabel.setText(String.valueOf(StaticObjects.player.games));
        userWinsLabel.setText(String.valueOf(StaticObjects.player.wins));
        userLosesLabel.setText(String.valueOf(StaticObjects.player.loses));
        userTiesLabel.setText(String.valueOf(StaticObjects.player.ties));
        userRankLabel.setText(String.valueOf(StaticObjects.player.rank));

        double centerX = statCircle.getLayoutX();
        double centerY = statCircle.getLayoutY();
        double radius = statCircle.getRadius();

        double wins;
        if (StaticObjects.player.games != 0) {
            wins = (double) StaticObjects.player.wins / StaticObjects.player.games;
        } else {
            wins = 0;
        }
        DecimalFormat df = new DecimalFormat("##.#%");
        winsRateLabel.setText(df.format(wins));

        String styleWins = "-fx-fill: transparent; -fx-stroke: #ff7700; -fx-stroke-width: 10";

        Arc arc1 = new Arc(centerX, centerY, radius, radius, 110, 0);
        arc1.setType(ArcType.OPEN);
        arc1.setStyle(styleWins);

        playerStatPain.getChildren().addAll(arc1);

        timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(arc1.lengthProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds(0.6), new KeyValue(arc1.lengthProperty(), wins*360, Interpolator.EASE_BOTH))
        );
        timeline.setCycleCount(1);

        timeline.play();
    }

    private void setPlayers() {
        vBoxPlayers.getChildren().removeIf(node -> !node.getId().equals("info"));
        for (Player player: players) {
            GridPane gridPane = new GridPane();
            gridPane.setPrefHeight(35);
            gridPane.setMinHeight(35);

            gridPane.setId("player");

            gridPane.setStyle("-fx-background-color: " + Colors.base + Colors.baseTransparent + ";");

            for (int i = 0; i < 4; i++) {
                int perCent = switch (i){
                    case 0 -> 10;
                    case 1 -> 50;
                    case 2, 3 -> 20;
                    default -> 0;
                };
                ColumnConstraints column = new ColumnConstraints();
                column.setPercentWidth(perCent);
                column.setHalignment(HPos.CENTER);
                gridPane.getColumnConstraints().add(column);
            }

            gridPane.add(getImageView(player.avatarIdx), 0,0); //заполнение таблицы данными
            gridPane.add(getLabel(player.name), 1,0);
            gridPane.add(getLabel(String.valueOf(player.rank)), 2,0);
            DecimalFormat df = new DecimalFormat("##.#%");
            gridPane.add(getLabel(String.valueOf(player.games == 0 ? 0 +"%" : df.format((double) player.wins/ player.games))), 3,0);
            if (player.equals(StaticObjects.player)) {
                gridPane.setStyle("-fx-background-color: " + Colors.mouseEnter + Colors.baseTransparent  + ";");
            } else {
                gridPane.setOnMouseEntered(_-> gridPane.setStyle("-fx-background-color: " + Colors.mouseEnter + ";"));
                gridPane.setOnMouseExited(_-> gridPane.setStyle("-fx-background-color: " + Colors.base + Colors.baseTransparent + ";"));
            }

            vBoxPlayers.getChildren().add(gridPane);
        }
    }


    //Add Functions
    private void setAvatarPane() {
        avatarPane.setOnMouseEntered(_->{
            userImageView.setOpacity(0.8);
            avatarSettings.setVisible(true);
        });
        avatarPane.setOnMouseExited(_->{
            userImageView.setOpacity(1);
            avatarSettings.setVisible(false);
        });
        avatarPane.setOnMouseClicked(_-> Main.addAvatarChoose());
    }

    private void animation() {
        for (int i = 0; i < vBoxPlayers.getChildren().size(); i++) {
            if (vBoxPlayers.getChildren().get(i).getId().equals("info")){
                continue;
            }
            GridPane gridPane = (GridPane) vBoxPlayers.getChildren().get(i);
            int size = (int) gridPane.getHeight();
            TranslateTransition transition = new TranslateTransition(Duration.millis(200), gridPane);
            int idx = 0;
            for (Player player: players) {
                if (player.name.equals(((Label) gridPane.getChildren().get(1)).getText())){
                    idx = players.indexOf(player);
                    break;
                }
            }
            transition.setToY((idx - i)*size+37);
            transition.play();
        }
    }

    public void update() {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).equals(StaticObjects.player)){
                players.set(i, StaticObjects.player);
                break;
            }
        }
        setPlayers();
    }

    private ImageView getImageView(int idx) {
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/org/example/chess1/Avatars/avatar" + idx + ".png")));
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);
        return imageView;
    }
    private Label getLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Verdana", 18));
        label.setTextFill(javafx.scene.paint.Color.WHITE);
        return label;
    }
}
