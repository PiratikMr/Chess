package org.example.chess1.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.chess1.Game.Containers.Board;
import org.example.chess1.Game.Containers.Coordinates;
import org.example.chess1.Game.Containers.Enum.Color;
import org.example.chess1.Game.Containers.StaticObjects;
import org.example.chess1.Game.Pieces.*;
import org.example.chess1.Main;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class PieceChooserController implements Initializable {
    @FXML private ImageView imageQueen, imageRock, imageBishop, imageKnight;

    public volatile static Piece piece;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setColor();
    }

    //Choose Piece
    @FXML private void onButtQueenClicked() { Piece newPiece = new Queen(piece.coordinates, piece.color); setPiece(newPiece); }
    @FXML private void onButtRockClicked() { Piece newPiece = new Rock(piece.coordinates, piece.color); setPiece(newPiece); }
    @FXML private void onButtBishopClicked() { Piece newPiece = new Bishop(piece.coordinates, piece.color); setPiece(newPiece); }
    @FXML private void onButtKnightClicked() { Piece newPiece = new Knight(piece.coordinates, piece.color); setPiece(newPiece); }


    //Add Functions
    public static void setPiece(Piece newPiece) {
        Platform.runLater(()->{
            Board.getInstance().board.replace(piece.coordinates, piece, newPiece);
            Board.getInstance().history.boards.getLast().replace(piece.coordinates, piece, newPiece);

            newPiece.oldCoordinates = new Coordinates(piece.oldCoordinates);
            BoardController.instance.boardPane.getChildren().remove(piece);
            BoardController.instance.boardPane.getChildren().add(newPiece);

            BoardController.instance.createCheck(null);

            StaticObjects.player.isMoving = !StaticObjects.player.isMoving;
            StaticObjects.enemy.isMoving = !StaticObjects.enemy.isMoving;

            Main.exitPieceChooser();
        });

    }

    private void  setColor() {
        String colorStr = piece.color.equals(Color.WHITE) ? "White" : "Black";
        imageQueen.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/Queen" + colorStr + ".png")));
        imageRock.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/Rock" + colorStr + ".png")));
        imageBishop.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/Bishop" + colorStr + ".png")));
        imageKnight.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Pieces/Knight" + colorStr + ".png")));
    }
}
