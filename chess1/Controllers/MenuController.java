package org.example.chess1.Controllers;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.example.chess1.Colors;
import org.example.chess1.Game.Containers.*;
import org.example.chess1.Game.Containers.Enum.Color;
import org.example.chess1.Game.Containers.Enum.GameStartType;
import org.example.chess1.Game.Containers.Enum.GameTypes;
import org.example.chess1.Game.Player;
import org.example.chess1.Main;
import org.example.chess1.Server.Client;
import org.example.chess1.Server.Server;
import org.example.chess1.Server.StaticInfo;

import java.io.*;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    @FXML public Button buttSignIn; //кнопка входа в аккаунт
    @FXML public Label userNameLabel; //текстовое поле никнейм игрока
    @FXML public Pane userProfile, waitingPane; //поля профиля игрока и ожидания игроков
    @FXML public ImageView userImageView; //изображение игрока

    @FXML private GridPane fastStartGridPane; //сетка для кнопок быстрого старта

    @FXML private VBox waitingListVBox; //вертикальное поле для добавления кнопок ожидания

    public static MenuController instance; //статическое поле содержащие экземпляр данного класса

    //Server
    public Client client; //клиент игрока
    public Server server; //сервер

    //WaitingListTimer
    private AnimationTimer timer; //таймер для запроса о возможных серверах
    private final long timeUpdateWaitingList = 4; //время обновления серверов, сек

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setInstance();
        setFastStartButtons();
    }

    @FXML
    private void onButtSignInClick() {
        Main.addRegister();
    }

    //Menu
    @FXML private void onButtAboutClicked() {
        Main.addAbout();
    }
    @FXML private void onButtLoadGameClicked() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть");
        fileChooser.setInitialDirectory(new File("D:\\_NSTU\\_sem4\\TaMP\\RGR"));

        File file = fileChooser.showOpenDialog(Main.getStage());
        if (file != null) {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                Board.instance = (Board) in.readObject();
                in.close();

                BoardController.type = "loadGame";
                Main.addBoard();

                BoardController.instance.updateHistoryOrient();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @FXML private void onButtSetAddressClicked() {
        Main.addSetAddress();
    }


    @FXML private void onWaitingListChanged() {
        StaticObjects.setDefPlayer(); //Идентификация игрока, если тот не вошел в аккаунт

        if (timer != null) { //остановка таймера, если тот запущен
            timer.stop();
            timer = null;
            return;
        }

        timer = new AnimationTimer() { //таймер
            private long lastUpdate;
            @Override
            public void handle(long l) {
                if (l - lastUpdate >= timeUpdateWaitingList*1_000_000_000L) { //если время обновления прошло
                    waitingListVBox.getChildren().clear(); //очистка списка игр
                    client = new Client(GameStartType.WAITING_LIST);
                    client.start(); //проход по всем достпным портам в поиске свободных игр
                    lastUpdate = l;
                }
            }
        };
        timer.start();
    }


    //Profile
    @FXML private void onProfileMouseClicked() {
        Main.addStat();
    }


    //Drawing Functions
    private void setFastStartButtons() {
        String[][] text_arr = {{"1+0\nBullet", "2+1\nBullet", "3+0\nBlitz"}, //заполнение массивов текста и значений для каждой кнопки
                {"3+2\nBlitz", "5+0\nBlitz", "5+3\nBlitz"},
                {"10+0\nRapid", "10+5\nRapid", "15+10\nRapid"},
                {"30+0\nClassical", "30+20\nClassical", "\nСвоя игра"}};
        Time[][] time_arr = {{new Time(60, 0, GameTypes.BULLET), new Time(120, 1, GameTypes.BULLET), new Time(180, 0, GameTypes.BLITZ)},
                {new Time(180, 2, GameTypes.BLITZ), new Time(300, 0, GameTypes.BLITZ), new Time(300, 3, GameTypes.BLITZ)},
                {new Time(600, 0, GameTypes.BLITZ), new Time(600, 5, GameTypes.RAPID), new Time(900, 10, GameTypes.RAPID)},
                {new Time(1800, 0, GameTypes.CLASSICAL), new Time(1800, 20, GameTypes.CLASSICAL), new Time(-1, 0, GameTypes.MY_GAME)}};

        for (int i = 0; i < 4; i++) { //проход по всему двумерному массиву
            for (int j = 0; j < 3; j++) {
                Button button = new Button(); //создание кнопки
                button.setId("FastStartButton");
                button.getStyleClass().add("FastStartButton");

                Text textTime; //создание текста на кнопке
                Text textType;
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);

                String[] info = text_arr[i][j].split("\n");  //заполнение текста кнопки
                if (!info[0].isEmpty()) {
                    textTime = new Text(info[0]);
                    textTime.setFont(Font.font("Roboto", FontWeight.EXTRA_LIGHT, 33));
                    textTime.setFill(javafx.scene.paint.Color.WHITE);
                    vBox.getChildren().add(textTime);
                }

                textType = new Text(info[1]);
                textType.setFont(Font.font("Roboto",FontWeight.EXTRA_LIGHT, 25));
                textType.setFill(javafx.scene.paint.Color.WHITE);

                vBox.getChildren().add(textType);

                button.setGraphic(vBox);

                int finalI = i; //добавление обработчиков каждой кнопке
                int finalJ = j;
                if (i == 3 && j == 2) { //если кнопка - своя игра
                    button.setOnAction(_->{
                        StaticObjects.setDefPlayer();
                        StaticObjects.enemy = new Player("Бот", "."); //определения противника как бота
                        StaticObjects.time = time_arr[finalI][finalJ]; //выставление нулевого времени

                        Main.addColorChooser(); //выбор цвета, и последующий запуск игры
                    });
                } else { //если другие кнопки
                    button.setOnAction(_ -> {
                        StaticObjects.setDefPlayer();
                        StaticObjects.setTime(time_arr[finalI][finalJ]); //выставление нужного времени
                        client = new Client(GameStartType.FAST_START); //запуск клиента
                        client.start();

                        while (client.flag) { //проход по всем доступным портам в поисках похожей быстрой игры от других игроков
                            if (client.isFoundFastGame) { //если нашел, подключение к данному серверу
                                int port = client.foundFastGamePort;
                                client = new Client(port);
                                client.start();
                                return;
                            }
                        }

                        Random rand = new Random();
                        StaticObjects.player.color = Color.values()[rand.nextInt(2)];
                        StaticObjects.player.isMoving = StaticObjects.player.color.equals(Color.WHITE); //рандомный выбор цвета фигур игрока

                        server = new Server(); //создание нового сервера
                        server.start();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        client = new Client(StaticInfo.portUsing); //создание клиента и добавление его к данному серверу
                        client.start();

                        Main.addWaiting(); //открытие окна ожидания
                    });
                }
                fastStartGridPane.add(button, j, i); //добавление кнопки
            }
        }
    }

    public void addButtonToWaitingList(Player player, int port) {
        GridPane gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: " + Colors.base + Colors.baseTransparent + ";");

        for (int i = 0; i < 4; i++) { //создание таблицы
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(25);
            gridPane.getColumnConstraints().add(column);
        }

        gridPane.add(getLabel(player.name), 0,0); //заполнение таблицы данными
        gridPane.add(getLabel(String.valueOf(player.rank)), 1,0);
        gridPane.add(getLabel(player.time.toString()), 2,0);
        gridPane.add(getLabel(player.time.type.toString()), 3,0);

        gridPane.setOnMouseEntered(_-> gridPane.setStyle("-fx-background-color: " + Colors.mouseEnter + ";")); //добавление обработчиков при наведении мыши на таблицу
        gridPane.setOnMouseExited(_-> gridPane.setStyle("-fx-background-color: " + Colors.base + Colors.baseTransparent + ";"));
        gridPane.setOnMouseClicked(_->{ //добавление обработчика нажатия на кнопку
            client = new Client(port); //создания клиента с нужным портом и запуск игры
            client.start();
        });
        Platform.runLater(()->{
            waitingListVBox.getChildren().add(gridPane);
        });
    }


    //Add func
    public void setProfile() {
        buttSignIn.setVisible(false);
        userProfile.setVisible(true);
        userNameLabel.setText(StaticObjects.player.name);
        userImageView.setImage(new Image(getClass().getResourceAsStream("/org/example/chess1/Avatars/avatar" + StaticObjects.player.avatarIdx + ".png")));
    }

    public void setInstance() {
        instance = this;
    }

    public void setGame(Player enemy) {
        if (timer != null) {
            timer.stop();
        }
        StaticObjects.enemy = enemy;

        StaticObjects.setInfo(enemy.time);
        StaticObjects.enemy.time = StaticObjects.time;
    }

    private Label getLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Verdana", 18));
        label.setTextFill(javafx.scene.paint.Color.WHITE);
        return label;
    }

    public void gameStart() {
        if (timer != null) {
            timer.stop();
        }
        Platform.runLater(()->{ Main.exitWaiting(); Main.addBoard(); BoardController.instance.setHabitat();});
    }
}
