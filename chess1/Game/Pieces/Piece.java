package org.example.chess1.Game.Pieces;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.example.chess1.Controllers.BoardController;
import org.example.chess1.Game.Containers.*;
import org.example.chess1.Game.Containers.Enum.Color;
import org.example.chess1.Game.Containers.Enum.File;
import org.example.chess1.Game.Containers.Enum.ShapeType;
import org.example.chess1.Main;

import java.io.Serializable;
import java.util.*;

public abstract class Piece extends StackPane implements Serializable {
    public static int sizeTaken = 35; //размер взятой фигуры
    public static int size, boardSize, deltaX, deltaY; //размер клетки, доски и смещение доски
    public static boolean reverseShow = false; //переменная переворота доски

    public Coordinates coordinates, oldCoordinates; //координаты данные и предыдущего хода
    public Color color; //цвет фигуры
    protected int shift; //числовое значение относительно цвета, для облегчения написания кода и гибкости
    public int value; //ценность фигуры

    public transient ImageView imageView, imageTaken; //изображение фигуры и взятой фигуры
    private transient TranslateTransition transition; //переход фигуры от одной клетки к другой

    public Piece(Coordinates cord, Color color) {
        this.coordinates = cord;
        this.color = color;

        if (color.equals(Color.WHITE)) { shift = 1; } else { shift = -1; }

        setConfig();
    }

    public HashSet<Coordinates> getPossibleMoves(HashMap<Coordinates, Piece> board){
        return null;
    }
    public HashSet<Coordinates> getFilterPossibleMoves() {
        HashSet<Coordinates> possibleMoves = getPossibleMoves(Board.getInstance().board); //получаем возможные ходы
        Iterator<Coordinates> iter = possibleMoves.iterator();
        while (iter.hasNext()) { //итератором проходимся по всему массиву
            HashMap<Coordinates, Piece> abstractBoard = new HashMap<>(Board.getInstance().board); //создаем копию доски

            abstractBoard.remove(coordinates); //перемещаем фигуру на копии на возможный ход
            abstractBoard.put(iter.next(), this);

            Piece king = Board.isThereCheck(abstractBoard);
            if (king != null) { //если на такой доске будет шах фигуре нашего цвета, то такой ход не возможен
                if (king.color.equals(color)) {
                    iter.remove(); //удаляем такой ход
                }
            }
        }
        return possibleMoves;
    }

    //Set
    public void setConfig() {
        setImage();
        setPosition();
        if (color.equals(StaticObjects.player.color) || StaticObjects.time.toString().isEmpty()) {
            setMouseFunctions();
        }
    }

    public void setPosition(){
        super.setLayoutY(getY(coordinates.rank));
        super.setLayoutX(getX(coordinates.file));
    }

    public void setImage() {
        imageView = new ImageView();
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);

        imageTaken = new ImageView();
        imageTaken.setFitHeight(sizeTaken);
        imageTaken.setFitWidth(sizeTaken);

        super.getChildren().clear();
        super.getChildren().add(imageView);
    }

    public void setMouseFunctions() {
        super.setOnMousePressed(event -> { //нажатие
            BoardController.instance.removeAdditionalShapes(ShapeType.POSS_MOVE, ShapeType.CURR_POS); //удаление возможных ходов и данной позиции
            super.toFront(); //вывод фигуры вперед
            super.setMouseTransparent(true); //прозрачность для мыши
            dragPosition(event); //при нажатии перемещается центром в позицию мыши
            HashSet<Coordinates> possibleMoves = getFilterPossibleMoves(); //получаем возможные ходы
            super.setUserData(possibleMoves); //сохраняем в данные
            BoardController.instance.createPossibleMoves(this, possibleMoves); //создаем возможные ходы на доске
        });
        super.setOnMouseDragged(this::dragPosition); //при введении - ведем фигуру
        super.setOnMouseReleased(event -> { //при отпускании
            Coordinates newCord = getCurrentCoordinates(event); //получаем координаты по доске мыши

            HashSet<Coordinates> possibleMoves = (HashSet<Coordinates>) super.getUserData(); //забираем возможные ходы

            if (possibleMoves != null && StaticObjects.getMovingColor().equals(color)) { //если мышь находиться на доске и сейчас ход игрока с цветои этой фигуры
                if (possibleMoves.contains(newCord)) { //если данная координата есть в возможных ходах
                    changeCord(newCord); //меняем координаты фигуры на новые
                }
            }
            setPosition(); //обновляем позицию
            super.setMouseTransparent(false); //не прозрачность для мыши
        });
    }

    public static void setProportions(){
        boardSize = (int)BoardController.instance.boardPane.getPrefWidth();
        deltaX = (int)BoardController.instance.boardPane.getLayoutX();
        deltaY = (int)BoardController.instance.boardPane.getLayoutY();
        size = boardSize/8;
    }

    //Get
    public static int getX(File file) {
        int x;
        if (isBlackShow()) {
            x = 14-2*file.ordinal();
        } else {
            x= 2*file.ordinal();            //file.ordinal()*boardSize/8;
        }                                      //(7-file.ordinal())*board

        return (boardSize*x) / 16;      //(boardSize*(2*file.ordinal()+1) - size*8)/16
    }
    public static int getY(int rank){
        int y;
        if (isBlackShow()) {
            y = 2*rank-2;
        } else {
            y = 16-2*rank;
        }
        return (boardSize*y) / 16;
    }

    public static Coordinates getCurrentCoordinates(MouseEvent event) {
        int rank = (int)(event.getSceneY()- deltaY)*8/boardSize;
        File file = File.values()[(int)(event.getSceneX()- deltaX)*8/boardSize];

        if (isBlackShow()) {
            rank++;
            file = File.values()[7-file.ordinal()];
        } else {
            rank = 8-rank;
        }

        return new Coordinates(file, rank);
    }


    //Add Functions
    public void changeCord(Coordinates newCord) {
        oldCoordinates = new Coordinates(coordinates.file, coordinates.rank);
        coordinates = new Coordinates(newCord.file, newCord.rank);
        HashMap<Coordinates, Piece> board = Board.getInstance().board;

        synchronized (board) {
            board.remove(oldCoordinates);
        }

        BoardController.instance.drawShapes(this);

        //Castle
        if (this instanceof King) {
            int rank = color.equals(Color.WHITE) ? 1 : 8;

            if (oldCoordinates.equals(new Coordinates(File.e, rank)) && coordinates.equals(new Coordinates(File.c, rank))) {
                synchronized (board) {
                    for (Piece piece: board.values()) {
                        if (piece instanceof Rock && piece.color.equals(color) && piece.coordinates.equals(new Coordinates(File.a, rank))) {
                            piece.oldCoordinates = new Coordinates(piece.coordinates);
                            piece.coordinates = new Coordinates(File.d, rank);
                            Board.getInstance().board.remove(piece.oldCoordinates);
                            synchronized (board){
                                board.put(piece.coordinates, piece);
                            }
                            piece.setPosition();
                            break;
                        }
                    }
                }
            }
            if (oldCoordinates.equals(new Coordinates(File.e, rank)) && coordinates.equals(new Coordinates(File.g, rank))) {
                synchronized (board) {
                    for (Piece piece: board.values()) {
                        if (piece instanceof Rock && piece.color.equals(color) && piece.coordinates.equals(new Coordinates(File.h, rank))) {
                            piece.oldCoordinates = new Coordinates(piece.coordinates);
                            piece.coordinates = new Coordinates(File.f, rank);
                            Board.getInstance().board.remove(piece.oldCoordinates);
                            synchronized (board){
                                board.put(piece.coordinates, piece);
                            }
                            piece.setPosition();
                            break;
                        }
                    }
                }
            }
            ((King) this).isCastle = false;
            ((King) this).isLongCastle = false;
        }
        if (this instanceof Rock) {
            if ((this.oldCoordinates.rank == 1 || this.oldCoordinates.rank == 8) && (this.oldCoordinates.file == File.a || this.oldCoordinates.file == File.h)) {
                synchronized (board) {
                    for (Piece piece : board.values()) {
                        if (piece instanceof King && piece.color.equals(this.color)) {
                            if (this.oldCoordinates.file == File.a) {
                                ((King) piece).isLongCastle = false;
                            } else {
                                ((King) piece).isCastle = false;
                            }
                            break;
                        }
                    }
                }
            }
        }

        //En Passant
        boolean isEnPassant = false;
        if (this instanceof Pawn ) {
            Piece piece = Board.getInstance().board.get(Board.getInstance().history.getCordFromHistory());
            if (piece instanceof Pawn && piece.oldCoordinates.equals(piece.coordinates.shift(0, 2*shift))
                    && oldCoordinates.rank == (shift > 0 ? 5 : 4) && Math.abs(oldCoordinates.file.ordinal() - piece.coordinates.file.ordinal()) == 1) {
                Board.getInstance().board.remove(piece.coordinates);
                Platform.runLater(()->BoardController.instance.boardPane.getChildren().remove(piece));
                isEnPassant = true;
            }
        }

        Piece piece = Board.getInstance().remove(coordinates);
        Board.getInstance().change(this, piece != null || isEnPassant);

        if (this instanceof Pawn && (coordinates.rank == 1 || coordinates.rank == 8)) {
            Main.addPieceChooser(this);
        } else {
            StaticObjects.player.isMoving = !StaticObjects.player.isMoving;
            StaticObjects.enemy.isMoving = !StaticObjects.enemy.isMoving;
        }
    }

    @Override public String toString(){
        return null;
    }

    public void startAnimation(Coordinates cord, boolean isReverse) {
        if (transition != null) {
            transition.stop();
        }
        if (!isReverse) {
            transition = new TranslateTransition(Duration.seconds(0.1), this);
        } else {
            transition = new TranslateTransition(Duration.millis(0.1), this);
        }
        transition.setToX(Piece.getX(cord.file) - Piece.getX(coordinates.file));
        transition.setToY(Piece.getY(cord.rank) - Piece.getY(coordinates.rank));

        transition.play();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Piece piece)) return false;
        if (shift != piece.shift) return false;
        if (value != piece.value) return false;
        if (!coordinates.equals(piece.coordinates)) return false;
        if (!Objects.equals(oldCoordinates, piece.oldCoordinates))
            return false;
        if (color != piece.color) return false;
        return imageView.equals(piece.imageView);
    }

    protected void dragPosition(MouseEvent event){
        super.setLayoutX(event.getSceneX() - deltaX - size/2);
        super.setLayoutY(event.getSceneY() - deltaY - size/2);
    }
    protected static boolean isBlackShow() {
        return (StaticObjects.player.color.equals(Color.BLACK) || reverseShow)
                && (StaticObjects.player.color.equals(Color.WHITE) || !reverseShow);
    }
}
