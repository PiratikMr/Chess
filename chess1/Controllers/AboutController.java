package org.example.chess1.Controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.example.chess1.Main;

public class AboutController {
    @FXML private ImageView mainGifView;
    private FadeTransition fadeOut;

    @FXML private void onGifMouseEntered() {
        animation(0.5, 1.0, 0.0);
    }

    @FXML private void onGifMouseExited() {
        animation(0.1, 0.0, 1.0);
    }

    @FXML private void onExitClicked() {
        Main.exitAbout();
    }

    //Add Functions
    private void animation(double time, double from, double to) {
        if (fadeOut != null) {
            fadeOut.stop();
        }
        fadeOut = new FadeTransition(Duration.seconds(time), mainGifView);
        fadeOut.setFromValue(from);
        fadeOut.setToValue(to);
        fadeOut.setOnFinished(_ -> mainGifView.setOpacity(to));
        fadeOut.play();
    }
}
