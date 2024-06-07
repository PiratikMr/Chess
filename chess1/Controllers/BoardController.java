package org.example.chess1.Controllers;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.example.chess1.Game.Containers.*;
import org.example.chess1.Game.Containers.Enum.*;
import org.example.chess1.Game.Habitat;
import org.example.chess1.Game.Pieces.Piece;
import org.example.chess1.Main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.*;

public class BoardController implements Initializable {
    public static String type;

    @FXML public AnchorPane boardPane;
    @FXML private HBox hBoxPlayersTakenPieces, hBoxEnemiesTakenPieces;
    @FXML public Label timeEnemyLabel, timePlayerLabel, actionEnemyLabel, actionPlayerLabel, nameEnemyLabel, namePlayerLabel;
    @FXML private ImageView enemyAvatarImageView, playerAvatarImageView;

    @FXML private MenuItem buttSaveGame;

    //historySpace
    @FXML public AnchorPane gameOverPane;
    @FXML private Label gameOverInfoLabel, gameOverScoreLabel;
    @FXML private Pane historyPain, historyScrollPane;

    @FXML private Button buttMoveBack, buttMoveForward, buttMoveToStart, buttMoveToFinish;
    private int idxCurrentMove = -1;

    @FXML public Button buttRevengeRequest;

    public static BoardController instance;

    public Habitat hab;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        gameOverPane.setVisible(false);
        historyPain.setVisible(true);

        boolean bool = Objects.equals(type, "loadGame");
        timePlayerLabel.setVisible(!bool);
        timeEnemyLabel.setVisible(!bool);
        buttSaveGame.setDisable(bool);

        if (Objects.equals(type, "loadGame")) {
            hab = new Habitat("loadGame");
            nameEnemyLabel.setText("");
            namePlayerLabel.setText("");
            createHistoryButtons();
        } else {
            historyScrollPane.getChildren().clear();
        }
    }

    @FXML private void onExitClicked() {
        if (hab.timer != null) { //если среда запушена
            hab.timer.stop(); //остановить
            if (hab.isPlaying) { //внести результаты игры игроку
                StaticObjects.setGameResult(StaticObjects.enemy.color);
            }
        }

        Habitat.type = null; //обнуление типа среды игры
        Main.exitBoard(); //выход из окна
    }

    //Menu
    @FXML private void onButtSaveGameClicked() {
        FileChooser fileChooser = new FileChooser(); //выбор файла для сохранения
        fileChooser.setTitle("Сохранить");
        fileChooser.setInitialDirectory(new java.io.File("D:\\_NSTU\\_sem4\\TaMP\\RGR"));
        java.io.File file = fileChooser.showSaveDialog(Main.getStage());

        if (file != null) {
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file)); //создание выходного потока
                out.writeObject(Board.getInstance()); //запись в выходной поток данной доски
                out.close(); //закрытие потока
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //Revenge
    private void onButtRevengeClicked() throws IOException {
        Random rand = new Random();
        StaticObjects.player.color = Color.values()[rand.nextInt(2)]; //выбор случайного цвета фигур
        StaticObjects.enemy.color = null; //обнуление цвета противника
        StaticObjects.setInfo(new Time(StaticObjects.time)); //постановка данных цвета, времени для игроков
        MenuController.instance.client.sendRevengeRequest(); //отправка запроса реванша

        buttRevengeRequest.setDisable(true); //выключение кнопки реванша
    }

    //HistoryOrient
    @FXML private void onButtMoveToStartClicked() { idxCurrentMove = -1; setBoardByIdx(false); }

    @FXML private void onButtMoveBackClicked() { idxCurrentMove--; setBoardByIdx(false); }

    @FXML private void onButtMoveForwardClicked() {
        idxCurrentMove++;
        if (idxCurrentMove >= Board.getInstance().history.coordinates.size()) {
            idxCurrentMove--;
        }
        setBoardByIdx(false);
    }

    @FXML private void onButtMoveToFinishClicked() {
        idxCurrentMove = Board.getInstance().history.coordinates.size()-1;
        setBoardByIdx(false);
    }

    private void setBoardByIdx(boolean isReverse) {
        HashMap<Coordinates, Piece> board;
        if (idxCurrentMove == -1) { //если -1, загружаем доску изначального положения
            board = new HashMap<>(Board.getInstance().history.getFirstBoard());
            showTakenPieces(Board.getInstance().history.getFirstBoard());
        } else { //загружаем доску определенного хода
            board = new HashMap<>(Board.getInstance().history.boards.get(idxCurrentMove));
            showTakenPieces(Board.getInstance().history.boards.get(idxCurrentMove));
        }

        removeAdditionalShapes(ShapeType.values()); //удаляем все дополнительные обозначения (шах, возможные ходы, предыдущий ход)
        boardPane.getChildren().removeIf(node -> node instanceof  StackPane && !board.containsValue((Piece) node)); //удаление тех фигур, которых нет на данной доске
        for (Map.Entry<Coordinates, Piece> entry: board.entrySet()){ //проход по всем фигурам данной доски
            if (type == null) { //если обычная игра, то фигура получает обработчик нажатий на нее
                entry.getValue().setConfig();
            } else { //если плеер, то фигура не получает обработчик
                entry.getValue().setImage();
                entry.getValue().setPosition();
            }
            entry.getValue().startAnimation(entry.getKey(), isReverse); //старт анимации перехода от одной позиции к другой

            //если игра окончена или данная доска - не доска актуального момента игры, то выключаем обработчик фигур
            entry.getValue().setMouseTransparent(idxCurrentMove != Board.getInstance().history.coordinates.size() - 1 || !hab.isPlaying);


            if (!boardPane.getChildren().contains(entry.getValue())) { //добавление фигуры если ее нет на данной доске
                boardPane.getChildren().add(entry.getValue());
            }
        }
        if (idxCurrentMove == Board.getInstance().history.boards.size()-1 && type == null) { //если данная доска - доска на данный момент игры, добавить
            //шах, если есть и последний ход
            Board.getInstance().updateBoard();
            drawShapes(null);
            return;
        }
        setDisabledHistoryButtons(); //на основе изменения индекса данной доски включаем и выключаем нужные кнопки плеера
    }

    //RotateBoard
    @FXML private void onButtRotateBoardClicked() {
        Piece.reverseShow = !Piece.reverseShow; //изменение параметра разворота на обратный
        hab.setBoardImage(); //изменение картинки относительно поворота
        setBoardByIdx(true); //изменение положения фигур
    }

    //DrawingFunctions
    public void drawShapes(Piece piece) {
        Platform.runLater(()->{
            removeAdditionalShapes(ShapeType.values()); //удаление графической помощи
            createLastMove(piece); //создание последнего хода
            showTakenPieces(null); //показ перевес фигур
            createHistoryButtons(); //обновление истории
            Piece King = Board.isThereCheck(null);
            if (King != null) {
                Board.getInstance().checkedKing = King;
                BoardController.instance.createCheck(King); //создание шаха
            }
        });
    }

    public void showTakenPieces(HashMap<Coordinates, Piece> board){
        hBoxPlayersTakenPieces.getChildren().clear(); //отчистка полей
        hBoxEnemiesTakenPieces.getChildren().clear();

        HashMap<Coordinates, Piece> newBoard; //копия полученной доски для изменений
        if (board != null) { //если параметр пустой, то берется доска данного момента игры
            newBoard = board;
        } else {
            newBoard = Board.instance.board;
        }
        HashMap<Coordinates, Piece> takenPieces = new HashMap<>(newBoard); //копирование полученной доски

        //цикл сравнения фигур, если у фигуры есть пара (та же фигура, другой цвет), то эти фигуры удаляются из массива.
        for (Map.Entry<Coordinates, Piece> entry1: newBoard.entrySet()) {
            for (Map.Entry<Coordinates, Piece> entry2: newBoard.entrySet()) {
                if (entry1.getValue() != entry2.getValue()) {
                    if (!entry1.getValue().color.equals(entry2.getValue().color) && entry1.getValue().getClass() == entry2.getValue().getClass()) {
                        if (takenPieces.containsKey(entry1.getKey()) && takenPieces.containsKey(entry2.getKey())) {
                            takenPieces.remove(entry1.getKey());
                            takenPieces.remove(entry2.getKey());
                            break;
                        }
                    }
                }
            }
        }

        int PlayerCount = 0; //счетчик пешек игрока
        int EnemyCount = 0;

        if (takenPieces.isEmpty()) { //если в массиве не осталось фигур, то у игроков равные фигуры
            return;
        }

        ArrayList<Piece> pieces = new ArrayList<>(takenPieces.values()); //сортировка фигур по ценности
        pieces.sort(Comparator.comparingInt(o -> o.value));
        Collections.reverse(pieces);

        boolean isReverse = type != null && Piece.reverseShow; //переменная отвечающая за переворот доски и смену полей взятых фигур

        for (Piece piece: pieces) { //добавление в поля фигур, относительно цвета
            Color color = piece.color;
            if (isReverse) {
                color = Color.getOppositeColor(piece.color);
            }
            if (color.equals(StaticObjects.player.color)) {
                PlayerCount += piece.value;
                if (hBoxPlayersTakenPieces.getChildren().size() < 9) {
                    hBoxPlayersTakenPieces.getChildren().add(piece.imageTaken);
                }
            } else {
                EnemyCount += piece.value;
                if (hBoxEnemiesTakenPieces.getChildren().size() < 9) {
                    hBoxEnemiesTakenPieces.getChildren().add(piece.imageTaken);
                }
            }
        }

        if (PlayerCount == EnemyCount) { //если одинаковые счетчики, то перевеса нет
            return;
        }
        Label label = new Label(STR."+\{Math.abs(PlayerCount-EnemyCount)}"); //текстовое поле с перевесом
        label.setStyle("-fx-text-fill: #d5d5d5;");
        label.setFont(Font.font(20));
        if (PlayerCount > EnemyCount) {
            hBoxPlayersTakenPieces.getChildren().add(label);
        } else {
            hBoxEnemiesTakenPieces.getChildren().add(label);
        }
    }

    public void createPossibleMoves(Piece piece, HashSet<Coordinates> possibleMoves){
        int x = Piece.getX(piece.coordinates.file); //определение позиции фигуры
        int y = Piece.getY(piece.coordinates.rank);

        Rectangle rectangle = new Rectangle(x, y, Piece.size, Piece.size); //создание квадрата под выбранной фигурой
        rectangle.setFill(javafx.scene.paint.Color.GREEN);
        rectangle.setOpacity(0.4);
        rectangle.setMouseTransparent(true);
        rectangle.setId(String.valueOf(ShapeType.CURR_POS));
        boardPane.getChildren().add(2, rectangle);

        Paint paint; //выбор краски в зависимости от хода игрока
        if (StaticObjects.getMovingColor().equals(piece.color)){
            paint = javafx.scene.paint.Color.GREEN;
        } else {
            paint = javafx.scene.paint.Color.PURPLE;
        }

        HashMap<Coordinates, Piece> board = Board.getInstance().board;
        for (Coordinates move: possibleMoves) { //проход по массиву возможных ходов
            StackPane pane = new StackPane(); //создание панели
            pane.setId(String.valueOf(ShapeType.POSS_MOVE));
            pane.setPrefHeight(Piece.size);
            pane.setPrefWidth(Piece.size);

            if (StaticObjects.getMovingColor().equals(piece.color)) { //добавление обработчика нажатия
                pane.setOnMouseClicked(event -> { //при нажатии смена позиции фигуры на выбранную область
                    piece.changeCord(Piece.getCurrentCoordinates(event));
                    piece.setPosition();
                });
            }
            x = Piece.getX(move.file);
            y = Piece.getY(move.rank);

            if (!board.containsKey(move)) { //если клетка пустая
                int rad = 10;
                Circle circle = new Circle(rad); //создание круга
                circle.setOpacity(0.5);
                circle.setFill(paint);

                pane.setOnMouseEntered(_->{ //добавление обработчиков входа и выхода мыши в и за пределы данной клетки
                    pane.getChildren().removeIf(node -> node instanceof Circle); //изменение круга на квадрат
                    Rectangle rect = new Rectangle(Piece.size, Piece.size, paint);
                    rect.setOpacity(0.5);
                    pane.getChildren().add(rect);
                });
                pane.setOnMouseExited(_->{
                    pane.getChildren().removeIf(node -> node instanceof Rectangle); //изменение обратно
                    pane.getChildren().addAll(circle);
                });

                pane.getChildren().add(circle);

            }
            else { //если клетка занята противником
                double shift = 15.0; //отрисовка графика для клетки противника
                Polygon[] polygons = new Polygon[4];
                for (int i = -1, j = 1, count = 0; count < 4; count++, i*=-1){
                    if (count == 2) {
                        j = -1;
                    }
                    Polygon polygon = new Polygon(0.0, 0.0, i*shift, 0.0, 0.0, j*shift);
                    polygon.setFill(paint);
                    polygon.setOpacity(0.7);
                    StackPane.setAlignment(polygon, switch (count) {
                        case 0 -> Pos.TOP_RIGHT;
                        case 1 -> Pos.TOP_LEFT;
                        case 2 -> Pos.BOTTOM_RIGHT;
                        case 3 -> Pos.BOTTOM_LEFT;
                        default -> null;
                    });
                    polygons[count] = polygon;
                }
                pane.getChildren().addAll(polygons);
                pane.setOnMouseEntered(_->{
                    pane.getChildren().removeIf(node -> node instanceof Polygon);
                    Rectangle rect = new Rectangle(Piece.size, Piece.size);
                    rect.setFill(javafx.scene.paint.Color.RED);
                    rect.setOpacity(0.5);
                    pane.getChildren().add(rect);
                });
                pane.setOnMouseExited(_->{
                    pane.getChildren().removeIf(node -> node instanceof Rectangle);
                    pane.getChildren().addAll(polygons);
                });
            }
            pane.setLayoutX(x);
            pane.setLayoutY(y);
            boardPane.getChildren().addFirst(pane);
            pane.toFront();
        }

        piece.toFront();
    }

    public void createCheck(Piece king){
        if (king == null) {
            king = Board.isThereCheck(null);
            if (king == null) {
                return;
            }
        }

        BoardController.instance.boardPane.getChildren().removeIf(node -> node instanceof Ellipse); //удаление старого шаха

        int x = Piece.getX(king.coordinates.file)+Piece.boardSize/16; //отрисовка размытого круга на месте короля под шахом
        int y = Piece.getY(king.coordinates.rank)+Piece.boardSize/16;
        Ellipse ellipse = new Ellipse(x, y, 30, 30);
        ellipse.setFill(javafx.scene.paint.Color.RED);
        ellipse.setOpacity(0.9);
        ellipse.setMouseTransparent(true);
        ellipse.setEffect(new GaussianBlur(20));
        ellipse.setId(String.valueOf(ShapeType.CHECK));
        BoardController.instance.boardPane.getChildren().add(1, ellipse);
    }

    public void createLastMove(Piece piece) {
        Coordinates newCord, oldCord;
        if (piece == null) {
            newCord = Board.getInstance().history.getCordFromHistory();
            oldCord = Board.getInstance().board.get(newCord).oldCoordinates; //получаем старые координаты фигуры
        } else {
            newCord = piece.coordinates;
            oldCord = piece.oldCoordinates;
        }

        for (int i = 0; i < 2; i++) { //рисование квадрата на старых координатах и новых
            Coordinates cord;
            if (i==0) {
                cord = oldCord;
            } else {
                cord = newCord;
            }
            Rectangle rect = new Rectangle(Piece.size, Piece.size, javafx.scene.paint.Color.YELLOW);
            rect.setMouseTransparent(true);
            rect.setOpacity(0.3);
            rect.setId(String.valueOf(ShapeType.LAST_MOVE));
            rect.setLayoutX(Piece.getX(cord.file));
            rect.setLayoutY(Piece.getY(cord.rank));

            boardPane.getChildren().add(1, rect);
        }
    }

    public void removeAdditionalShapes(ShapeType...nodes) {
        BoardController.instance.boardPane.getChildren().removeIf(node -> {
            if (node.getId() != null) {
                for (ShapeType type: nodes) {
                    if (node.getId().equals(String.valueOf(type))){
                        return true;
                    }
                }
            }
            return false;
        });
    }

    private void createHistoryButtons() {
        historyScrollPane.getChildren().clear();
        History history = Board.getInstance().history;

        int count = (history.coordinates.size() % 2 == 0) ? history.coordinates.size() / 2 : history.coordinates.size() / 2 + 1;
        int startCount = count;
        for (int i = history.coordinates.size()-1; i >= 0; i--, count--){
            HBox hBox = new HBox();
            hBox.setLayoutY((startCount-count)*30);
            historyScrollPane.getChildren().addFirst(hBox);

            Label label = new Label(String.valueOf(count));
            hBox.getChildren().addFirst(label);

            if (history.coordinates.size() % 2 == 0 || i != history.coordinates.size()-1) {
                Button button = new Button(history.coordinates.get(i-1));
                button.setId("HistoryButton");
                int finalI = i;
                button.setOnMouseClicked(_->{
                    idxCurrentMove = finalI-1;
                    setBoardByIdx(false);
                });
                hBox.getChildren().add(button);

                Button button1 = new Button(history.coordinates.get(i));
                button1.setId("HistoryButton");
                button1.setOnMouseClicked(_->{
                    idxCurrentMove = finalI;
                    setBoardByIdx(false);
                });
                hBox.getChildren().add(button1);
                i--;
            } else {
                Button button1 = new Button(history.coordinates.get(i));
                button1.setId("HistoryButton");
                int finalI = i;
                button1.setOnMouseClicked(_->{
                    idxCurrentMove = finalI;
                    setBoardByIdx(false);
                });
                hBox.getChildren().add(button1);
            }
        }
    }


    //Add func
    public void setHabitat(){
        this.hab = new Habitat(); //создание новой среды
        idxCurrentMove = -1; //начало игры, ни одного хода не было
        setDisabledHistoryButtons();

        historyPain.setVisible(true); //показ истории

        historyScrollPane.getChildren().clear(); //отчистка истории
        hBoxEnemiesTakenPieces.getChildren().clear(); //отчистка взятых фигур
        hBoxPlayersTakenPieces.getChildren().clear();

        gameOverPane.setVisible(false); //скрытие панели окончания игры
        setInfo(); //выставление информации об игроках
        changeTurn(); //выставление очередей игроков

        buttRevengeRequest.setOnMouseClicked(_ ->{ //определение функции на нажатие на кнопку реванша
            try {
                onButtRevengeClicked();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        buttRevengeRequest.setDisable(false); //включения кнопки реванша
    }

    public void changeTurn() {
        actionPlayerLabel.setVisible(StaticObjects.player.isMoving); //показ действия игрока относительно очереди
        actionEnemyLabel.setVisible(StaticObjects.enemy.isMoving);

        String active = "-fx-background-color: #b0ffa6"; //определение цвета таймера игрока относительно, ходит игрок или нет
        String passive = "-fx-background-color: white";

        timePlayerLabel.setStyle(StaticObjects.player.isMoving ? active : passive);
        timeEnemyLabel.setStyle(StaticObjects.enemy.isMoving ? active : passive);
    }

    private void setInfo(){
        String unRegisterPlayer = ""; //определение зарегистрированные ли игроки
        String unRegisterEnemy = "";
        if (StaticObjects.player.password.isEmpty() || StaticObjects.player.password.length() >= 45){
            unRegisterPlayer = "?"; //если не зарегистрированные, то помечаются ?
        }
        if (StaticObjects.enemy.password.isEmpty() || StaticObjects.player.password.length() >= 45){
            unRegisterEnemy = "?";
        }
        namePlayerLabel.setText(STR."\{StaticObjects.player.name}(\{StaticObjects.player.rank}\{unRegisterPlayer})"); //заполнение имени и ранка игрока
        nameEnemyLabel.setText(STR."\{StaticObjects.enemy.name}(\{StaticObjects.enemy.rank}\{unRegisterEnemy})");

        //установка аватара игрока
        enemyAvatarImageView.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Avatars/avatar" + StaticObjects.enemy.avatarIdx + ".png")));
        playerAvatarImageView.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Avatars/avatar" + StaticObjects.player.avatarIdx + ".png")));
    }

    public void revengeRequest() {
        buttRevengeRequest.setDisable(false); //включение кнопки реванша

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), buttRevengeRequest); //включение анимации показа о запросе реванша
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.2);
        fadeTransition.setCycleCount(10);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();

        buttRevengeRequest.setOnMouseClicked(_->{ //переопределение обработки нажатия на кнопку реванша
            fadeTransition.stop(); // остановка анимации
            buttRevengeRequest.setOpacity(1.0); // установка непрозрачности на 100
            try {
                MenuController.instance.client.sendRevengeAccept(); //отправка положительного ответа на реванш
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            setHabitat(); //постановка доски
        });
    }

    public void gameOver(Color winner, GameOverType type) throws IOException {
        hab.timer.stop(); //остановка среды игры
        hab.isPlaying = false;
        Board.getInstance().setTransparentAllPieces(true); //выключение обработчиков всех фигур

        StaticObjects.setGameResult(winner); //подсчет результата игры для игроков

        actionPlayerLabel.setVisible(false); //выключение действий игроков
        actionEnemyLabel.setVisible(false);

        StaticObjects.player.time.stop(); //остановка таймеров игроков
        StaticObjects.enemy.time.stop();

        String info;

        if (winner == null) { //если нет победителя - ничья
            info = "ничья";
        } else {
            gameOverInfoLabel.setStyle("-fx-text-fill: " + (winner.equals(StaticObjects.player.color) ? "#77ff77" : "#ff7777") + ";"); //установка цвета, относительно прогрыша или выигрыша
            info = switch (type) { //установка типа победы
                case WINNER -> "Мат";
                case TIME_STOPPED -> "Время вышло";
                case PLAYER_LEFT -> "Игрок отключился";
            };
            info += " · Победили " + winner;
        }

        gameOverScoreLabel.setText(StaticObjects.player.localWins + "-" + StaticObjects.player.localLoses); //выставление счета
        gameOverInfoLabel.setText(info); //выставление информации об окончании игры


        gameOverPane.setVisible(true); //скрыть историю, показать поле окончания игры
        historyPain.setVisible(false);
        buttRevengeRequest.setVisible(StaticObjects.time.type != GameTypes.MY_GAME && type != GameOverType.PLAYER_LEFT); //если игрок вышел, кнопка реванша не нужна
    }

    public void updateHistoryOrient() {
        idxCurrentMove = Board.getInstance().history.coordinates.size()-1; //переход на данный момент игры
        setDisabledHistoryButtons(); //выставление кнопок плеера
    }

    private void setDisabledHistoryButtons() {
        buttMoveToStart.setDisable(idxCurrentMove == -1);
        buttMoveToFinish.setDisable(idxCurrentMove == Board.getInstance().history.coordinates.size()-1);
        buttMoveBack.setDisable(idxCurrentMove == -1);
        buttMoveForward.setDisable(idxCurrentMove == Board.getInstance().history.coordinates.size()-1);
    }
}
