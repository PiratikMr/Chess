package org.example.chess1.Game.Containers;

import javafx.application.Platform;
import org.example.chess1.Controllers.BoardController;
import org.example.chess1.Game.Containers.Enum.Color;
import org.example.chess1.Game.Containers.Enum.File;
import org.example.chess1.Game.Containers.Enum.GameOverType;
import org.example.chess1.Game.Pieces.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class Board implements Serializable {
    public static Board instance; //переменная хранящая экземпляр данной доски
    public HashMap<Coordinates, Piece> board = new HashMap<>(); //доска на данный момент
    public final History history = new History(); //история ход и досок

    public Piece checkedKing = null;

    public static Board getInstance(){
        if (instance == null){
            instance = new Board();
        }
        return instance;
    }
    private Board(){}


    //Set Board
    public void setDefaultBoard(){
        board.clear();
        Color color = Color.WHITE;
        //Pawns
        for (File file: File.values()){
            setPiece(new Pawn(new Coordinates(file, 2), color), new Coordinates(file, 2));
            setPiece(new Pawn(new Coordinates(file, 7), Color.getOppositeColor(color)), new Coordinates(file, 7));
        }

        //Rocks
        setPiece(new Rock(new Coordinates(File.a, 1), color), new Coordinates(File.a, 1));
        setPiece(new Rock(new Coordinates(File.h, 1), color), new Coordinates(File.h, 1));
        setPiece(new Rock(new Coordinates(File.a, 8), Color.getOppositeColor(color)), new Coordinates(File.a, 8));
        setPiece(new Rock(new Coordinates(File.h, 8), Color.getOppositeColor(color)), new Coordinates(File.h, 8));

        //Knights
        setPiece(new Knight(new Coordinates(File.b, 1), color), new Coordinates(File.b, 1));
        setPiece(new Knight(new Coordinates(File.g, 1), color), new Coordinates(File.g, 1));
        setPiece(new Knight(new Coordinates(File.b, 8), Color.getOppositeColor(color)), new Coordinates(File.b, 8));
        setPiece(new Knight(new Coordinates(File.g, 8), Color.getOppositeColor(color)), new Coordinates(File.g, 8));

        //Bishops
        setPiece(new Bishop(new Coordinates(File.c, 1), color), new Coordinates(File.c, 1));
        setPiece(new Bishop(new Coordinates(File.f, 1), color), new Coordinates(File.f, 1));
        setPiece(new Bishop(new Coordinates(File.c, 8), Color.getOppositeColor(color)), new Coordinates(File.c, 8));
        setPiece(new Bishop(new Coordinates(File.f, 8), Color.getOppositeColor(color)), new Coordinates(File.f, 8));

        //Queens
        setPiece(new Queen(new Coordinates(File.d, 1), color), new Coordinates(File.d, 1));
        setPiece(new Queen(new Coordinates(File.d, 8), Color.getOppositeColor(color)), new Coordinates(File.d, 8));

        //Kings
        setPiece(new King(new Coordinates(File.e, 1), color), new Coordinates(File.e, 1));
        setPiece(new King(new Coordinates(File.e, 8), Color.getOppositeColor(color)), new Coordinates(File.e, 8));

        history.setFirstBoard(board);
    }


    //Examine
    public static Piece isThereCheck(HashMap<Coordinates, Piece> map){
        HashMap<Coordinates, Piece> board;
        if (map == null){
            board = Board.getInstance().board;
        } else {
            board = map;
        }
        synchronized (board) {
            for (Piece piece: board.values()) {
                HashSet<Coordinates> possibleMoves = piece.getPossibleMoves(board);
                for (Coordinates cord: possibleMoves){
                    if (board.get(cord) instanceof King){
                        return board.get(cord);
                    }
                }
            }
        }
        return null;
    }

    public boolean isThereCheckMate(Color checkedKing) {
        if (checkedKing == null){ //если шаха нет, то проверка на пат
            boolean isThereMovesForWhite = false, isThereMovesForBlack = false;
            synchronized (board) {
                for (Piece piece: board.values()) { //проход по всем фигурам
                    HashSet<Coordinates> possibleMoves = piece.getFilterPossibleMoves(); //получение возможных ходов, с учетом анализа на 1 ход вперед
                    if (!possibleMoves.isEmpty()) { //если ходы есть
                        if (piece.color.equals(Color.WHITE)){ //то переменная отвечающая за существование ходов становиться true
                            isThereMovesForWhite = true;
                        } else {
                            isThereMovesForBlack = true;
                        }
                    }
                    if (isThereMovesForWhite && isThereMovesForBlack){ //если обе переменные true, то пата нет
                        return false;
                    }
                }
            }
            if (!isThereMovesForWhite){ //если у белых нет ходов
                if (StaticObjects.player.color.equals(Color.WHITE)) { //и игрок играет за белых, то возвращаем ходит ли игрок или нет
                    //если ходит, а ходов нет, то это пат
                    //если не ходит и ходов нет, то пока что еще не пат
                    return StaticObjects.player.isMoving;
                } else {
                    return !StaticObjects.player.isMoving;
                }
            }

            if (StaticObjects.player.color.equals(Color.BLACK)) { //то же самое только с черными
                return StaticObjects.player.isMoving;
            } else {
                return !StaticObjects.player.isMoving;
            }
        }

        synchronized (board) {
            for (Piece piece : board.values()) { //проход по всем фигурам
                if (piece.color.equals(checkedKing)) { //если фигура такого же цвета как и король под шахом
                    HashSet<Coordinates> possibleMoves = piece.getFilterPossibleMoves(); //получаем возможные ходы с анализом на 1 ход вперед
                    if (!possibleMoves.isEmpty()) { //если есть ходы
                        return false; //то мата нет
                    }
                }
            }
        }
        return true; //если нет ходов, то мат есть
    }


    //Add Functions
    public void updateBoard() {
        BoardController.instance.updateHistoryOrient();
        if (BoardController.instance.hab.isPlaying) {
            if (isThereCheckMate(checkedKing == null ? null : checkedKing.color)) { //если поставлен мат или пат
                try {
                    BoardController.instance.gameOver(Color.getOppositeColor(checkedKing == null ? null : checkedKing.color), GameOverType.WINNER); //конец игры
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        checkedKing = null;
    }

    public void setTransparentAllPieces(boolean bool) {
        synchronized (board) {
            for (Piece piece: board.values()){
                piece.setMouseTransparent(bool);
            }
        }
    }

    public void setPiece(Piece piece, Coordinates coordinates){
        piece.coordinates = coordinates;
        board.put(coordinates, piece);
    }

    public void change(Piece piece, boolean isTake){
        synchronized (board) {
            board.put(piece.coordinates, piece);
        }
        synchronized (history) {
            if (isTake) { //если взятие фигуры
                history.add(piece.coordinates.toTakeString(piece), Board.getInstance().board); //занесение в историю, с координатой как взятие хода
            } else {
                history.add(piece.coordinates.toMoveString(piece), Board.getInstance().board); //занесение в историю, с координатой как просто ход
            }
        }
    }

    public Piece remove(Coordinates cord) {
        if (!board.containsKey(cord)){
            return null;
        }
        Piece piece;
        synchronized (board) {
            piece = board.remove(cord);
        }
        BoardController.instance.boardPane.getChildren().remove(piece);
        return piece;
    }

    public void reset() {
        instance = new Board();
    }
}
