package org.example.chess1.Game.Containers;

import org.example.chess1.Game.Containers.Enum.Color;
import org.example.chess1.Game.Containers.Enum.File;
import org.example.chess1.Game.Pieces.King;
import org.example.chess1.Game.Pieces.Pawn;
import org.example.chess1.Game.Pieces.Piece;

import java.io.Serializable;
import java.util.Objects;

public class Coordinates implements Serializable {
    public File file;
    public int rank;

    public Coordinates(File file, int rank){
        this.file = file;
        this.rank = rank;
    }
    public Coordinates (Coordinates cord) {
        this.file = cord.file;
        this.rank = cord.rank;
    }


    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return rank == that.rank && file == that.file;
    }

    @Override public int hashCode() {
        return Objects.hash(file, rank);
    }

    @Override public String toString() {
        return STR."\{file}\{rank}";
    }

    public Coordinates shift(int shiftFile, int shiftRank){
        if (file.ordinal()+shiftFile > 7 || file.ordinal()+shiftFile < 0 || rank + shiftRank > 8 || rank + shiftRank < 1){
            return null;
        }
        return new Coordinates(File.values()[file.ordinal()+shiftFile],rank+shiftRank);
    }

    public String toMoveString(Piece piece){
        if (piece instanceof King) {
            if (piece.oldCoordinates.equals(new Coordinates(File.e, piece.color.equals(Color.WHITE) ? 1 : 8)) && piece.coordinates.equals(new Coordinates(File.c, piece.color.equals(Color.WHITE) ? 1 : 8))) {
                return "O-O-O";
            }
            if (piece.oldCoordinates.equals(new Coordinates(File.e, piece.color.equals(Color.WHITE) ? 1 : 8)) && piece.coordinates.equals(new Coordinates(File.g, piece.color.equals(Color.WHITE) ? 1 : 8))) {
                return "O-O";
            }
        }
        return piece.toString() + piece.coordinates;
    }

    public String toTakeString(Piece piece){
        if (piece instanceof Pawn){
            return STR."\{piece.oldCoordinates.file}x\{piece.coordinates}";
        }
        return STR."\{piece.toString()}x\{piece.coordinates}";
    }
}
