package org.example.chess1.Controllers;

import javafx.fxml.FXML;
import org.example.chess1.Game.Containers.Enum.Color;
import org.example.chess1.Game.Containers.StaticObjects;
import org.example.chess1.Main;

import java.util.Random;

public class ColorChooserController {
    private Color color;

    @FXML private void onButtRandClicked () {
        Random rand = new Random();
        color = Color.values()[rand.nextInt(2)];
        exit();
    }

    @FXML private void onButtWhiteClicked() {
        color = Color.WHITE;
        exit();
    }

    @FXML private void onButtBlackClicked() {
        color = Color.BLACK;
        exit();
    }

    //Add Functions
    private void exit() {
        StaticObjects.player.color = color;
        StaticObjects.setInfo(StaticObjects.time);
        MenuController.instance.gameStart();
        Main.exitColorChoose();
    }
}
