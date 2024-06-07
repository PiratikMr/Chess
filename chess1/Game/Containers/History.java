package org.example.chess1.Game.Containers;

import org.example.chess1.Game.Containers.Enum.File;
import org.example.chess1.Game.Pieces.Piece;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class History implements Serializable {
    public final ArrayList<String> coordinates = new ArrayList<>();
    public final ArrayList<HashMap<Coordinates, Piece>> boards = new ArrayList<>();

    private HashMap<Coordinates, Piece> firstBoard;

    public History() {}

    public void add(String cord, HashMap<Coordinates, Piece> board) {
        coordinates.add(cord);
        HashMap<Coordinates, Piece> boardClone = new HashMap<>(board);
        for (Map.Entry<Coordinates, Piece> entry: boardClone.entrySet()) {
            entry.getValue().coordinates = entry.getKey();
            entry.getValue().setPosition();
        }
        boards.add(boardClone);
    }

    public void setFirstBoard(HashMap<Coordinates, Piece> board) {
        firstBoard = new HashMap<>(board);
        for (Map.Entry<Coordinates, Piece> entry: firstBoard.entrySet()) {
            entry.getValue().coordinates = entry.getKey();
            entry.getValue().setPosition();
        }
    }
    public HashMap<Coordinates, Piece> getFirstBoard() { return firstBoard; }

    public Coordinates getCordFromHistory() {
        if (!coordinates.isEmpty()) {
            String cord = coordinates.getLast();

            if (cord.equals("O-O") || cord.equals("O-O-O")){ //если предыдущие ходы - рокировка, то изначальные координаты известны
                int rank = 8 - 7 * (coordinates.size() % 2);
                return new Coordinates(cord.equals("O-O") ? File.g : File.c, rank);
            }

            while (cord.length() > 2) {
                cord = cord.substring(1);
            }
            return new Coordinates(File.valueOf(String.valueOf(cord.charAt(0))), Integer.parseInt(cord.substring(1)));
        } else {
            return null;
        }
    }
}
