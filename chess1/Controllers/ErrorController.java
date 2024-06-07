package org.example.chess1.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.chess1.Main;

import java.net.URL;
import java.util.ResourceBundle;

public class ErrorController implements Initializable {
    @FXML private Button buttOk;
    @FXML private Label errorMessage;
    public static String message;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorMessage.setText(message);
    }

    @FXML private void onButtOkClick() {
        Main.exitError();
    }
}
