package org.example.chess1.Game;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.example.chess1.Controllers.BoardController;
import org.example.chess1.Controllers.MenuController;
import org.example.chess1.Game.Containers.*;
import org.example.chess1.Game.Containers.Enum.Color;
import org.example.chess1.Game.Containers.Enum.GameOverType;
import org.example.chess1.Game.Containers.Enum.GameTypes;
import org.example.chess1.Game.Pieces.Piece;

import java.io.IOException;
import java.util.*;


public class Habitat {
    public static String type; //тип игры
    public AnimationTimer timer; //таймер
    public static final int FPS = 120; //фпс, обновление, кадры в секунду

    public boolean isPlaying; //идет ли игра

    public Habitat(String type) {
        Habitat.type = type;
        StaticObjects.setDefPlayer();
        StaticObjects.player.color = Color.WHITE;
        gameSession();
    }

    public Habitat() {
        gameSession();
    }

    private void gameSession() {
        Piece.setProportions(); //установка размеров доски

        isPlaying = true;
        Platform.runLater(()-> BoardController.instance.boardPane.getChildren().clear()); //отчитска поля доски
        setBoardImage(); //выставление изображения относительно цвета, белый - нормальное, черный - перевернутое
        if (type == null) { //еси не плеер
            Board.getInstance().reset(); //обнуляем доску
            Board.getInstance().setDefaultBoard(); //выставляем стандартное расположение доски
            setPieces(); //выставляем фигуры

            timer = new AnimationTimer() { //таймер
                private long lastUpdate; //последнее обновление
                private boolean lastMoving = StaticObjects.player.isMoving; //последний ход

                @Override
                public void handle(long l) {
                    if (l - lastUpdate >= 1_000_000_000 / FPS) {
                        BoardController.instance.timePlayerLabel.setText(StaticObjects.player.time.toString()); //обновление текста времени на таймере
                        BoardController.instance.timeEnemyLabel.setText(StaticObjects.enemy.time.toString());
                        try {
                            isTimeStopped(); //если время остановлено у одного из игроков, конец игры
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        lastUpdate = l;
                    }
                    if (StaticObjects.player.isMoving != lastMoving) { //если был сделан чей-то ход
                        Board.getInstance().updateBoard(); //обновляем доску
                        if (isPlaying) {
                            StaticObjects.changeTurn(); //меняем параметры ходов у игроков
                        }
                        sendBoard(); //отправляем доску противнику с обновлениями
                        lastMoving = StaticObjects.player.isMoving;
                    }
                }
            };
            timer.start();
        } else {
            setPieces(); //если плеер, то только ставим фигуры
        }
    }


    //Set
    public void setBoardImage() {
        ImageView imageView;
        if ((StaticObjects.player.color.equals(Color.BLACK) || Piece.reverseShow) && (StaticObjects.player.color.equals(Color.WHITE) || !Piece.reverseShow)) {
            imageView = new ImageView(new Image(getClass().getResourceAsStream("/org/example/chess1/boardForBlack.png")));
        } else {
            imageView = new ImageView(new Image(getClass().getResourceAsStream("/org/example/chess1/boardForWhite.png")));
        }
        imageView.setFitWidth(BoardController.instance.boardPane.getPrefWidth());
        imageView.setFitHeight(BoardController.instance.boardPane.getPrefHeight());
        Platform.runLater(()->{
            BoardController.instance.boardPane.getChildren().removeIf(node -> node instanceof ImageView);
            BoardController.instance.boardPane.getChildren().addFirst(imageView);
        });
    }

    public static void setPieces() {
        Platform.runLater(()->{
            BoardController.instance.boardPane.getChildren().removeIf(node -> node instanceof StackPane);
            HashMap<Coordinates, Piece> board = Board.getInstance().board;
            synchronized (board) {
                for (Piece piece: board.values()) {
                    if (type == null) {
                        piece.setConfig();
                    } else {
                        piece.setImage();
                        piece.setPosition();
                    }
                    BoardController.instance.boardPane.getChildren().add(piece);
                }
            }
        });
    }


    //Add Functions
    private void isTimeStopped() throws IOException {
        if (StaticObjects.player.time.isStopped() || StaticObjects.enemy.time.isStopped()){
            timer.stop();
            Color winner;
            if (!StaticObjects.player.time.isStopped()){
                winner = StaticObjects.player.color;
            } else {
                winner = StaticObjects.enemy.color;
            }
            BoardController.instance.gameOver(winner, GameOverType.TIME_STOPPED);
        }
    }

    public void sendBoard() {
        if (!StaticObjects.player.isMoving && !StaticObjects.time.type.equals(GameTypes.MY_GAME)) {
            try {
                MenuController.instance.client.sendBoard(Board.getInstance());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
