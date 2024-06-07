package org.example.chess1.Game.Pieces;

import javafx.scene.image.Image;
import org.example.chess1.Game.Containers.Enum.Color;
import org.example.chess1.Game.Containers.Coordinates;
import org.example.chess1.Game.Containers.Enum.File;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class King extends Piece{
    public boolean isLongCastle; //есть ли длинная рокировка
    public boolean isCastle; //есть ли короткая рокировка

    public King(Coordinates coordinates, Color color) {
        super(coordinates, color);
        value = 0;
        isCastle = true;
        isLongCastle = true;
        setImage();
    }

    @Override
    public HashSet<Coordinates> getPossibleMoves(HashMap<Coordinates, Piece> board) {
        HashSet<Coordinates> possibleMoves = new HashSet<>(); //создание и инициализация HashSet

        //Король ходит во все стороны вокруг его местоположения на одну клетку
        //Все король будет иметь максимум 8 ходов
        //AllPossibleMoves
        for (int j = 0; j < 2; j++) { //цикл из 2 итераций
            for (int count = 0, i=1; count < 4; count++,i*=-1) { //цикл из 4 итераций, всего 8
                int sign;
                Coordinates cord;
                if (count<2){ sign = 1; } else if (j==0) { sign = -1; } else { sign = 0; }

                if (j == 0) { //проход во все стороны от данной клетки
                    cord = coordinates.shift(sign, i);
                } else {
                    cord = coordinates.shift(i*sign, i*(sign-1));
                }

                if (cord == null) {continue;} //если координата пустая, переходим к другому направлению
                if (!board.containsKey(cord) || (board.containsKey(cord) && board.get(cord).color.equals(Color.getOppositeColor(color)))){
                    //если на данной клетки нет фигуры или есть другого цвета

                    int rank = this.color.equals(Color.WHITE) ? 1 : 8;
                    if (isLongCastle && cord.equals(new Coordinates(File.d, rank))) { //если есть длинная рокировка и клетка d свободна
                        Coordinates longCastle1 = new Coordinates(File.c, rank);
                        Coordinates longCastle2 = new Coordinates(File.b, rank);
                        if (!board.containsKey(longCastle1) && !board.containsKey(longCastle2)) { //если клетки между турой и королем свободны, то может быть ракировка
                            possibleMoves.add(longCastle1);
                        }
                    }

                    if (isCastle && cord.equals(new Coordinates(File.f, rank))) { //короткая рокировка
                        Coordinates castle = new Coordinates(File.g, rank);
                        if (!board.containsKey(castle)) {
                            possibleMoves.add(castle);
                        }
                    }
                    possibleMoves.add(cord);
                }
            }
        }

        for (Map.Entry<Coordinates, Piece> piece: board.entrySet()) { //проход по всем фигурам доски
            if (piece.getValue().color.equals(Color.getOppositeColor(color))) { //если фигура противоположного цвета
                HashSet<Coordinates> piecePossibleMoves;
                if (piece.getValue() instanceof Pawn) { //так как у пешки, в отличии о других фигур боевые только диагональные клетки, то проверяем их отдельно
                    piecePossibleMoves = new HashSet<>();
                    for (int i = -1; i < 2; i++) { //2 итерации
                        if (i == 0) { continue; }
                        if (piece.getKey().shift(i, shift) != null) {
                            piecePossibleMoves.add(piece.getKey().shift(i, shift*-1)); //добавляем возможные боевые ходы пешки
                        }
                    }
                } else if (!(piece.getValue() instanceof King)) { //если любая другая фигура, но не король
                    piecePossibleMoves = piece.getValue().getPossibleMoves(board); //получаем ходы фигуры
                } else { //отдельный else для короля, для избежания бесконченой рекурсии
                    piecePossibleMoves = getKingPossibilities(piece.getKey()); //получаем все возможные варианты короля
                }
                possibleMoves.removeIf(piecePossibleMoves::contains); //удаляем все возможные ходы короля, которые сопадают с фигурами другого цвета
                int rank = color.equals(Color.WHITE) ? 1 : 8;
                if (!possibleMoves.contains(new Coordinates(File.d, rank))) { //если клетки под ударом, то ровкировка не возможна
                    possibleMoves.remove(new Coordinates(File.c, rank));
                }
                if (!possibleMoves.contains(new Coordinates(File.f, rank))) {
                    possibleMoves.remove(new Coordinates(File.g, rank));
                }
            }
        }

        return possibleMoves;
    }

    private HashSet<Coordinates> getKingPossibilities(Coordinates cord){
        HashSet<Coordinates> possibleMoves = new HashSet<>();

        for (int j = 0; j < 2; j++) {
            for (int count = 0, i=1; count < 4; count++,i*=-1) {
                int sign;
                if (count<2){ sign = 1; } else if (j==0) { sign = -1; } else { sign = 0; }
                Coordinates newCord;
                if (j==0) {
                    newCord = cord.shift(sign, i);
                } else {
                    newCord = cord.shift(i*sign, i*(sign-1));
                }
                if (newCord == null) {continue;}
                possibleMoves.add(newCord);
            }
        }

        return possibleMoves;
    }

    @Override public String toString(){
        if (color.equals(Color.WHITE)) {
            return "♔";
        } else {
            return "♚";
        }
    }

    @Override public void setImage() {
        super.setImage();
        if (color.equals(Color.WHITE)) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/KingWhite.png")));
        }else{
            imageView.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/KingBlack.png")));
        }
    }
}
