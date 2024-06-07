package org.example.chess1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.chess1.Controllers.BoardController;
import org.example.chess1.Controllers.ErrorController;
import org.example.chess1.Controllers.MenuController;
import org.example.chess1.Controllers.PieceChooserController;
import org.example.chess1.DataBase.DataBase;
import org.example.chess1.Game.Containers.Board;
import org.example.chess1.Game.Containers.Coordinates;
import org.example.chess1.Game.Containers.Enum.Color;
import org.example.chess1.Game.Containers.StaticObjects;
import org.example.chess1.Game.Habitat;
import org.example.chess1.Game.Pieces.Piece;
import org.example.chess1.Game.Pieces.Queen;

import java.io.IOException;

public class Main extends Application {
    private static Stage stage, ErrorStage, RegisterStage, StatStage, WatingStage, AvatarChooseStage, ColorChooserStage,
            AboutStage, PieceChooserStage, SetAddressStage;
    private static Scene mainScene;
    @Override
    public void start(Stage stage) throws IOException {
        Main.stage = stage;

        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/org/example/chess1/ico.png")));

        FXMLLoader file = new FXMLLoader(getClass().getResource("/org/example/chess1/FXML/start-view.fxml"));

        mainScene = new Scene(file.load());
        mainScene.getStylesheets().add(getClass().getResource("/org/example/chess1/CSS/Buttons.css").toExternalForm());

        stage.setTitle("РГР - Шахматы");
        stage.setScene(mainScene);
        stage.show();

        stage.setOnCloseRequest(_ -> {
            exitWaiting();
            if (MenuController.instance.client != null) {
                try {
                    MenuController.instance.client.close();
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (MenuController.instance.server != null) {
                try {
                    MenuController.instance.server.close();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            StaticObjects.saveData();
        });
    }

    public static void addError(String errorTitle, String error) {
        FXMLLoader file = new FXMLLoader(Main.class.getResource("/org/example/chess1/FXML/error-view.fxml"));

        ErrorStage = new Stage();
        ErrorStage.setTitle(errorTitle);
        ErrorController.message = error;
        ErrorStage.initModality(Modality.WINDOW_MODAL);
        ErrorStage.initOwner(stage);

        try {
            Scene scene = new Scene(file.load());
            ErrorStage.setScene(scene);
        } catch (IOException e){
            e.printStackTrace();
        }

        ErrorStage.show();
    }
    public static void addRegister() {
        FXMLLoader file = new FXMLLoader(Main.class.getResource("/org/example/chess1/FXML/register-view.fxml"));

        RegisterStage = new Stage();
        RegisterStage.setTitle("Регистрация");
        RegisterStage.initOwner(stage);

        try {
            Scene scene = new Scene(file.load());
            RegisterStage.setScene(scene);
        } catch (IOException e){
            e.printStackTrace();
        }

        RegisterStage.show();
    }
    public static void addStat() {
        FXMLLoader file = new FXMLLoader(Main.class.getResource("/org/example/chess1/FXML/stat-view.fxml"));

        StatStage = new Stage();
        StatStage.setTitle("Статистика игрока " + StaticObjects.player.name);
        StatStage.initModality(Modality.WINDOW_MODAL);
        StatStage.initOwner(stage);

        try {
            Scene scene = new Scene(file.load());
            StatStage.setScene(scene);
        } catch (IOException e){
            e.printStackTrace();
        }

        StatStage.show();
    }
    public static void addAvatarChoose() {
        FXMLLoader file = new FXMLLoader(Main.class.getResource("/org/example/chess1/FXML/avatarChoose-view.fxml"));

        AvatarChooseStage = new Stage();
        AvatarChooseStage.setTitle("выберите изображение профиля");
        AvatarChooseStage.initModality(Modality.WINDOW_MODAL);
        AvatarChooseStage.initOwner(stage);

        try {
            Scene scene = new Scene(file.load());
            AvatarChooseStage.setScene(scene);
        } catch (IOException e){
            e.printStackTrace();
        }

        AvatarChooseStage.show();
    }
    public static void addBoard() {
        FXMLLoader file = new FXMLLoader(Main.class.getResource("/org/example/chess1/FXML/board-view.fxml"));

        try {
            Scene scene = new Scene(file.load());
            stage.setScene(scene);
        } catch (IOException e){
            e.printStackTrace();
        }

        stage.show();
    }
    public static void addWaiting() {
        FXMLLoader file = new FXMLLoader(Main.class.getResource("/org/example/chess1/FXML/waiting-view.fxml"));

        WatingStage = new Stage();
        WatingStage.setTitle("Ожидание игрока ");
        WatingStage.initModality(Modality.WINDOW_MODAL);
        WatingStage.initOwner(stage);

        try {
            Scene scene = new Scene(file.load());
            WatingStage.setScene(scene);
        } catch (IOException e){
            e.printStackTrace();
        }

        WatingStage.show();

        WatingStage.setOnCloseRequest(_->{
            if (StaticObjects.enemy == null){
                try {
                    MenuController.instance.client.close();
                    MenuController.instance.server.close();
                } catch (IOException | InterruptedException | NullPointerException ignored) {}
            }
        });
    }
    public static void addColorChooser() {
        FXMLLoader file = new FXMLLoader(Main.class.getResource("/org/example/chess1/FXML/color-chooser.fxml"));

        ColorChooserStage = new Stage();
        ColorChooserStage.setTitle("Выберите цвет");
        ColorChooserStage.initModality(Modality.WINDOW_MODAL);
        ColorChooserStage.initOwner(stage);

        try {
            Scene scene = new Scene(file.load());
            ColorChooserStage.setScene(scene);
        } catch (IOException e){
            e.printStackTrace();
        }

        ColorChooserStage.show();
    }
    public static void addAbout() {
        FXMLLoader file = new FXMLLoader(Main.class.getResource("/org/example/chess1/FXML/about-view.fxml"));

        AboutStage = new Stage();
        AboutStage.setTitle("Инфа");
        AboutStage.initModality(Modality.WINDOW_MODAL);
        AboutStage.initOwner(stage);

        try {
            Scene scene = new Scene(file.load());
            AboutStage.setScene(scene);
        } catch (IOException e){
            e.printStackTrace();
        }

        AboutStage.show();
    }
    public static void addPieceChooser(Piece piece) {
        FXMLLoader file = new FXMLLoader(Main.class.getResource("/org/example/chess1/FXML/piece-chooser.fxml"));

        PieceChooserStage = new Stage();
        PieceChooserController.piece = piece;
        PieceChooserStage.setTitle("Выберите фигуру");
        PieceChooserStage.initStyle(StageStyle.UNDECORATED);
        PieceChooserStage.initModality(Modality.WINDOW_MODAL);
        PieceChooserStage.initOwner(stage);

        try {
            Scene scene = new Scene(file.load());
            PieceChooserStage.setScene(scene);
        } catch (IOException e){
            e.printStackTrace();
        }

        PieceChooserStage.show();
    }
    public static void addSetAddress() {
        FXMLLoader file = new FXMLLoader(Main.class.getResource("/org/example/chess1/FXML/set-address.fxml"));

        SetAddressStage = new Stage();
        SetAddressStage.setTitle("Задайте Сервер");
        SetAddressStage.initModality(Modality.WINDOW_MODAL);
        SetAddressStage.initOwner(stage);

        try {
            Scene scene = new Scene(file.load());
            SetAddressStage.setScene(scene);
        } catch (IOException e){
            e.printStackTrace();
        }

        SetAddressStage.show();
    }

    public static Stage getStage() { return stage; }

    //Exit
    public static void exitError() {  if (ErrorStage != null) ErrorStage.close(); }
    public static void exitRegister() { if (RegisterStage != null)   RegisterStage.close(); }
    public static void exitStat() { if (StatStage != null) StatStage.close(); }
    public static void exitBoard() {
        StaticObjects.resetTempInfo();
        BoardController.type = "";
        Habitat.type = null;

        try {
            if (MenuController.instance.client != null) {
                MenuController.instance.client.close();
            }
            Thread.sleep(300);
            if (MenuController.instance.server != null) {
                MenuController.instance.server.close();
            }
        } catch (IOException | InterruptedException ignored) {}
        stage.setScene(mainScene);
        stage.show();
    }
    public static void exitWaiting() {
        if (WatingStage != null)
            WatingStage.close();
    }
    public static void exitAvatarChoose() { if (AvatarChooseStage != null) AvatarChooseStage.close(); }
    public static void exitColorChoose() { if (ColorChooserStage != null) ColorChooserStage.close(); }
    public static void exitAbout() { if (AboutStage != null) AboutStage.close(); }
    public static void exitPieceChooser() { if (PieceChooserStage != null) {
        PieceChooserStage.close();
        PieceChooserController.piece = null;
    } }
    public static void exitSetAddress() { if (SetAddressStage != null) SetAddressStage.close(); }


    public static void main(String[] args) {
        launch();
    }
}