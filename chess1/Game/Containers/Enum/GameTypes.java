package org.example.chess1.Game.Containers.Enum;

public enum GameTypes {
    BULLET, BLITZ, RAPID, CLASSICAL, MY_GAME;

    @Override
    public String toString() {
        if (this.equals(BULLET)) { return "Пуля"; }
        if (this.equals(BLITZ)) { return "Блиц"; }
        if (this.equals(RAPID)) { return "Быстрые"; }
        if (this.equals(CLASSICAL)) { return "Классические"; }
        return "";
    }
}
