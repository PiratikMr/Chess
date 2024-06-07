package org.example.chess1.Game.Containers;

import org.example.chess1.Controllers.BoardController;
import org.example.chess1.DataBase.DataBase;
import org.example.chess1.Game.Containers.Enum.Color;
import org.example.chess1.Game.Containers.Enum.GameTypes;
import org.example.chess1.Game.Pieces.Piece;
import org.example.chess1.Game.Player;

import java.util.HashMap;
import java.util.Random;

public class StaticObjects {
    public volatile static Player player;
    public volatile static Player enemy;

    public static Time time;

    public static void changeTurn() {
        if (player.isMoving){
            player.time.start();
            enemy.time.pause();
        }
        else {
            player.time.pause();
            enemy.time.start();
        }

        BoardController.instance.changeTurn();
    }

    //Get Set
    public static void setDefPlayer(){
        if (player == null){
            player = new Player("Аноним", generatePassword());
        }
    }

    public static void setDefEnemy(){
        if (enemy == null){
            enemy = new Player("Аноним", generatePassword());
        }
    }

    public static void setInfo(Time time) {
        if (player.color != null){
            enemy.color = Color.getOppositeColor(player.color);
        } else {
            player.color = Color.getOppositeColor(enemy.color);
        }

        StaticObjects.player.isMoving = StaticObjects.player.color.equals(Color.WHITE);
        StaticObjects.enemy.isMoving = StaticObjects.enemy.color.equals(Color.WHITE);

        System.out.println("player: " + StaticObjects.player + "\nenemy: " + StaticObjects.enemy);

        setTime(time);
    }

    public static void setTime(Time time) {
        if (time == null)
            return;
        StaticObjects.time = new Time(time);
        if (player != null){
            player.time = new Time(time);
        }
        if (enemy != null){
            enemy.time = new Time(time);
        }
    }

    public static void setGameResult(Color winner) {
        if (player.color.equals(winner)) {
            player.localWins++;
        } else if (enemy.color.equals(winner)) {
            player.localLoses++;
        }

        if (StaticObjects.time.type.equals(GameTypes.MY_GAME)){
            return;
        }

        player.games++;

        if (winner == null){
            player.ties++;
            return;
        }

        int shift = getShiftRank();

        if (player.color.equals(winner)){
            player.wins++;
            enemy.rank -= (enemy.rank < shift) ? enemy.rank/shift : shift;
            player.rank += shift;
        } else {
            player.loses++;
            enemy.rank += shift;
            player.rank -= (player.rank < shift) ? player.rank/shift : shift;
        }

        saveData();
    }

    public static Color getMovingColor() {
        Color color = null;
        if (player.isMoving) {
            color = player.color;
        } else if (enemy.isMoving) {
            color = enemy.color;
        }
        return color;
    }

    public static void resetTempInfo(){
        player.resetTempInfo();
        time = null;
        enemy = null;
    }


    //Add func
    public static void saveData() {
        if (player != null) {
            if (!player.password.isEmpty() && player.password.length() <= 45) {
                DataBase.update(player);
            }
        }
    }

    private static int getShiftRank() {
        int shift = Math.abs(player.rank - enemy.rank + 1)/(Math.min(player.rank, enemy.rank)+1) * 10;
        return Math.max(shift, 10);
    }

    private static String generatePassword() {
        int length = 50;
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int choice = random.nextInt(3);
            switch (choice) {
                case 0:
                    sb.append((char) (random.nextInt(26) + 'a'));
                    break;
                case 1:
                    sb.append((char) (random.nextInt(26) + 'A'));
                    break;
                case 2:
                    sb.append((char) (random.nextInt(10) + '0'));
                    break;
            }
        }

        return sb.toString();
    }
}
