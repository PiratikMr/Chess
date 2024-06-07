package org.example.chess1.Game.Containers.Enum;

import java.io.Serializable;

public enum Color implements Serializable {
    WHITE, BLACK;

    public static Color getOppositeColor(Color color){
        if (color == null) {
            return null;
        }
        if (color == WHITE){
            return BLACK;
        } else {
            return WHITE;
        }
    }

    @Override public String toString() {
        if (this.equals(WHITE)){
            return "Белые";
        }
        if (this.equals(BLACK)){
            return "Черные";
        }
        return null;
    }
}
