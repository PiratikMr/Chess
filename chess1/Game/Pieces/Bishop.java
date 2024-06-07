package org.example.chess1.Game.Pieces;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.chess1.Game.Containers.Enum.Color;
import org.example.chess1.Game.Containers.Coordinates;

import java.util.HashMap;
import java.util.HashSet;

public class Bishop extends Piece {
    public Bishop(Coordinates coordinates, Color color) {
        super(coordinates, color);
        value = 3;
        setImage();
    }

    @Override
    public HashSet<Coordinates> getPossibleMoves(HashMap<Coordinates, Piece> board) {
        HashSet<Coordinates> possibleMoves = new HashSet<>(); //создание и инициализация HashSet

        //Слон может ходить по диагонали во всех 4-ех направлениях + в одном из направлений он может пойти максимум на 7 клеток
        //если он находиться в углу. Значит должен быть двумерный цикл в 4 направлениях и по 7 ходов

        for (int count = 0, i=1; count < 4; count++,i*=-1) { //цикл из 4 итераций, i каждую итерацию меняется знаком
            int sign;
            if (count<2){ sign = 1; } else { sign = -1; } //определение знака
            for (int j = 1; j<8 ;j++) { //цикл из 7 итераций
                Coordinates cord = coordinates.shift(j*sign, j*i); //идем в одном из направлений
                if (cord == null) {break;} //если дошли до конца доски, то переходим к следующему направлениях
                synchronized (board) {
                    if (board.containsKey(cord) && board.get(cord).color.equals(color)) { //если на пути стоит своя фигура, то переходим к следующему напрявлению
                        break;
                    }
                    if (board.containsKey(cord) && board.get(cord).color.equals(Color.getOppositeColor(color))) { //если на пути фигура противника
                        possibleMoves.add(cord); //добавляем в возможные и переходим к следующему направлению
                        break;
                    }
                }
                possibleMoves.add(cord); //добавляем к возможным, если фигур на пути нет
            }
        }

        return possibleMoves;
    }

    @Override public String toString(){
        if (color.equals(Color.WHITE)) {
            return "♗";
        } else {
            return "♝";
        }
    }

    @Override public void setImage(){
        super.setImage();
        if (color.equals(Color.WHITE)) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/BishopWhite.png")));
        }else{
            imageView.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/BishopBlack.png")));
        }
        imageTaken.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/BishopTaken.png")));
    }
}
