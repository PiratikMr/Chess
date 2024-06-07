package org.example.chess1.Game.Pieces;

import javafx.scene.image.Image;
import org.example.chess1.Game.Containers.Enum.Color;
import org.example.chess1.Game.Containers.Coordinates;

import java.util.HashMap;
import java.util.HashSet;

public class Rock extends Piece {
    public Rock(Coordinates coordinates, Color color) {
        super(coordinates, color);
        value = 5;
        setImage();
    }

    @Override
    public HashSet<Coordinates> getPossibleMoves(HashMap<Coordinates, Piece> board) {
        HashSet<Coordinates> possibleMoves = new HashSet<>();

        //ладья может ходить в 4 направлениях по прямой до конца доски, если нет преграды

        for (int count = 0, i=1; count < 4; count++,i*=-1) { //4 итерации, направления
            int sign;
            if (count<2){ sign = 1; } else { sign = 0; }

            for (int j = 1; j<8 ;j++) { //8 итераций, ходы
                Coordinates cord = coordinates.shift(i*j*sign, i*j*(sign-1)); //получаем коодинаты возможного хода
                if (cord == null) {break;} //если конец доски, переходим к другому направлению
                if (board.containsKey(cord) && board.get(cord).color.equals(color)){ //если по пути фигура нашшего цвета, выбирем другое направление
                    break;
                }
                if (board.containsKey(cord) && board.get(cord).color.equals(Color.getOppositeColor(color))){ //если фигура не нашего цвета
                    possibleMoves.add(cord); //добавляем в возможные и переходим к другому направлению
                    break;
                }
                possibleMoves.add(cord);
            }
        }

        return possibleMoves;
    }

    @Override public String toString(){
        if (color.equals(Color.WHITE)) {
            return "♖";
        } else {
            return "♜";
        }
    }

    @Override public void setImage() {
        super.setImage();
        if (color.equals(Color.WHITE)) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/RockWhite.png")));
        }else{
            imageView.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/RockBlack.png")));
        }
        imageTaken.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/RockTaken.png")));
    }

}
