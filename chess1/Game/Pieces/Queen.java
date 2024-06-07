package org.example.chess1.Game.Pieces;

import javafx.scene.image.Image;
import org.example.chess1.Game.Containers.Enum.Color;
import org.example.chess1.Game.Containers.Coordinates;

import java.util.HashMap;
import java.util.HashSet;

public class Queen extends Piece {
    public Queen(Coordinates coordinates, Color color) {
        super(coordinates, color);
        value = 9;
        setImage();
    }

    @Override
    public HashSet<Coordinates> getPossibleMoves(HashMap<Coordinates, Piece> board) {
        HashSet<Coordinates> possibleMoves = new HashSet<>();

        //ходы ферзя можно полуить из суммы возможных ходов ладьи и слона

        possibleMoves.addAll(new Rock(coordinates, color).getPossibleMoves(board)); //созадаем фигура с такими же координтами и цветом и полуаем возможные ходы
        possibleMoves.addAll(new Bishop(coordinates, color).getPossibleMoves(board));

        return possibleMoves;
    }

    @Override public String toString(){
        if (color.equals(Color.WHITE)) {
            return "♕";
        } else {
            return "♛";
        }
    }

    @Override public void setImage() {
        super.setImage();
        if (color.equals(Color.WHITE)) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/QueenWhite.png")));
        }else{
            imageView.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/QueenBlack.png")));
        }
        imageTaken.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/QueenTaken.png")));
    }
}
