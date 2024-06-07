package org.example.chess1.Game;

import org.example.chess1.Game.Containers.Enum.Color;
import org.example.chess1.Game.Containers.Time;

import java.io.Serializable;
import java.util.Objects;

public class Player implements Serializable {
    //General
    public final String name; //логин игрока
    public final String password; //пароль
    public int rank, games, wins, loses, ties; //информация об играх

    public int avatarIdx; //порядковый номер автара

    //Temp
    public Time time; //время
    public volatile Color color; //цвет фигур
    public boolean isMoving; //ходил ли игрок

    public int localWins; //победы в данной партии
    public int localLoses; //поражения в данной партии

    public Player (String name, String password) {
        this.name = name;
        this.password = password;

        //default
        rank = 1500;
        wins = loses = ties = games = 0;

        localWins = localLoses = 0;
    }

    public Player(String name, String password, int rank, int wins, int loses, int ties, int avatarIdx) {
        this.name = name;
        this.password = password;
        this.rank = rank;
        this.wins = wins;
        this.loses = loses;
        this.ties = ties;
        this.avatarIdx = avatarIdx;

        localWins = localLoses = 0;

        this.games = this.wins + this.loses + this.ties;
    }

    @Override
    public String toString() {
        String str = "";
        str+=name + "\t";
        str+=rank + "\t";
        str+=color;

        return str;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + rank;
        result = 31 * result + games;
        result = 31 * result + wins;
        result = 31 * result + loses;
        result = 31 * result + ties;
        result = 31 * result + avatarIdx;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player player)) return false;
        if (rank != player.rank) return false;
        if (games != player.games) return false;
        if (wins != player.wins) return false;
        if (loses != player.loses) return false;
        if (ties != player.ties) return false;
        if (avatarIdx != player.avatarIdx) return false;
        if (!name.equals(player.name)) return false;
        return password.equals(player.password);
    }

    public void resetTempInfo() {
        time = null;
        color = null;
        isMoving = false;
        localWins = localLoses = 0;
    }
}
