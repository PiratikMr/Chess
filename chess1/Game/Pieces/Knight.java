package org.example.chess1.Game.Pieces;

import javafx.scene.image.Image;
import org.example.chess1.Game.Containers.Enum.Color;
import org.example.chess1.Game.Containers.Coordinates;

import java.util.HashMap;
import java.util.HashSet;

public class Knight extends Piece {
    public Knight(Coordinates coordinates, Color color) {
        super(coordinates, color);
        value = 3;
        setImage();
    }

    @Override public HashSet<Coordinates> getPossibleMoves(HashMap<Coordinates, Piece> board){
        HashSet<Coordinates> possibleMoves = new HashSet<>();

        //конь ходит буков г, во все стороны, максимум он может сделать 8 ходов
        //FreeMoves
        for (int i = 1; i < 3; i++) { //2 итерации от 1 до 2
            for (int j = -1; j < 2; j++) { //2 итеарции, с пропуском 0
                if (j==0) { continue; }
                for (int k = -1; k < 2; k++) { //2 итерции, с пропуском 0, всего 8
                    if (k==0) { continue; }
                    Coordinates cord = coordinates.shift(i*j, getValue(i)*k); //получаем координаты возможного хода
                    if (cord == null) { continue; } //если координата пустая, переходим к следующему ходу
                    if (!board.containsKey(cord) ||
                            (board.containsKey(cord) && board.get(cord).color.equals(Color.getOppositeColor(color)))){ //если тут ничего нет или есть, но фигура другого цвета
                        possibleMoves.add(coordinates.shift(i * j, getValue(i) * k)); //добавляем возможный ход
                    }
                }
            }
        }


        return possibleMoves;
    }

    private int getValue(int val){
        if (val == 1){
            return 2;
        } else {
            return 1;
        }
    }

    @Override public String toString(){
        if (color.equals(Color.WHITE)) {
            return "♘";
        } else {
            return "♞";
        }
    }

    @Override public void setImage() {
        super.setImage();
        if (color.equals(Color.WHITE)) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/KnightWhite.png")));
        }else{
            imageView.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/KnightBlack.png")));
        }
        imageTaken.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/KnightTaken.png")));
    }
}
