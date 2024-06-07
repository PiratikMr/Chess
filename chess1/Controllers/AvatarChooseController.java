package org.example.chess1.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.example.chess1.Game.Containers.StaticObjects;
import org.example.chess1.Main;

import java.net.URL;
import java.util.ResourceBundle;

public class AvatarChooseController implements Initializable {
    @FXML private Button buttOk;
    @FXML private Pane basePane;

    private int idx; //

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setAvatars();
    }

    @FXML private void onButtOkClicked() {
        if (idx == 0) { Main.exitAvatarChoose(); return; }

        StaticObjects.player.avatarIdx = idx;
        StaticObjects.saveData();
        MenuController.instance.setProfile();
        StatController.instance.setStats();
        StatController.instance.update();
        Main.exitAvatarChoose();
    }

    //Add Functions
    private void setAvatars() {
        GridPane gridPane = new GridPane();

        for (int i = 0; i < 7; i++) {
            Image image = new Image(getClass().getResourceAsStream("/org/example/chess1/Avatars/avatar" + i + ".png"));
            ImageView imageView = new ImageView(image);
            imageView.setOpacity(0.5);

            imageView.setFitHeight(100);
            imageView.setFitWidth(100);

            int finalI = i;
            imageView.setOnMouseEntered(_ ->imageView.setOpacity(1.0));
            imageView.setOnMouseExited(_ ->imageView.setOpacity(0.5));

            imageView.setOnMouseClicked(_ ->{
                idx = finalI;
                imageView.setOpacity(1.0);
                imageView.setOnMouseExited(_ ->{});
                for (Node node: gridPane.getChildren()){
                    if (node != imageView){
                        node.setOnMouseExited(_ ->node.setOpacity(0.5));
                        node.setOpacity(0.5);
                    }
                }
            });
            gridPane.add(imageView, i%6, i/6);
        }


        basePane.getChildren().add(gridPane);
    }
}
