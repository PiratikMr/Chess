package org.example.chess1.Game.Pieces;

import javafx.scene.image.Image;
import org.example.chess1.Game.Containers.Board;
import org.example.chess1.Game.Containers.Enum.Color;
import org.example.chess1.Game.Containers.Coordinates;

import java.util.HashMap;
import java.util.HashSet;

public class Pawn extends Piece {
    public Pawn(Coordinates coordinates, Color color) {
        super(coordinates, color);
        value = 1;
        setImage();
    }

    @Override public HashSet<Coordinates> getPossibleMoves(HashMap<Coordinates, Piece> board){
        HashSet<Coordinates> possibleMoves = new HashSet<>();

        //пешка ходит вперед на 1 ход и рубит по диагонали вперед на 1 клетку, если пека стоит в начале, она может походить сразу на две клетки вперед
        //FreeMoves
        if (!board.containsKey(coordinates.shift(0,shift))){ //если впереди пешки ничего нет
            possibleMoves.add(coordinates.shift(0,shift)); //добавляем ход
            if (coordinates.rank == getPossibleRank()){ //если пешка находится в начале
                if (!board.containsKey(coordinates.shift(0,shift*2))){ //и вторая клетка свободна
                    possibleMoves.add(coordinates.shift(0,shift*2)); //добавляем ход
                }
            }
        }

        //TakeMoves
        for (int i=-1;i<2;i++) { //2 итерации, 0 скип
            if (i==0) { continue; }
            if (board.containsKey(coordinates.shift(i, shift))) { //если по диагонали есть фигура
                if (board.get(coordinates.shift(i, shift)).color.equals(Color.getOppositeColor(color))) { //и эта фигура другого цвета
                    possibleMoves.add(coordinates.shift(i, shift)); //то это возможный ход
                }
            }
        }

        //En passant
        Piece piece = board.get(Board.getInstance().history.getCordFromHistory());
        if (piece != null &&  piece.oldCoordinates!=null) {
            if (piece instanceof Pawn && piece.oldCoordinates.equals(piece.coordinates.shift(0, 2 * shift))
                    && coordinates.rank == (shift > 0 ? 5 : 4) && Math.abs(coordinates.file.ordinal() - piece.coordinates.file.ordinal()) == 1) {
                possibleMoves.add(piece.coordinates.shift(0, shift));
            }
        }


        return possibleMoves;
    }

    private int getPossibleRank(){
        if (shift > 0){
            return 2;
        } else {
            return 7;
        }
    }

    @Override public String toString(){
        if (color.equals(Color.WHITE)){
            return "♙";
        } else {
            return "♟";
        }
    }

    @Override public void setImage() {
        super.setImage();
        if (color.equals(Color.WHITE)) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/PawnWhite.png")));
        }else{
            imageView.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/PawnBlack.png")));
        }
        imageTaken.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/PawnTaken.png")));
    }
}
