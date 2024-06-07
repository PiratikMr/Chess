package org.example.chess1.Server;

import javafx.application.Platform;
import org.example.chess1.Controllers.BoardController;
import org.example.chess1.Controllers.MenuController;
import org.example.chess1.Game.Containers.*;
import org.example.chess1.Game.Containers.Enum.Color;
import org.example.chess1.Game.Containers.Enum.GameOverType;
import org.example.chess1.Game.Containers.Enum.GameStartType;
import org.example.chess1.Game.Habitat;
import org.example.chess1.Game.Player;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class Client {
    protected Connection connection; //сокет, с потоками вывода и ввода, есть возможность прочитать и записать сообщение

    public volatile boolean flag ,isFoundFastGame ; //флаг
    public volatile int foundFastGamePort; //порт найденной игры

    private final int port; //порт
    private GameStartType type; //тип клиента

    public Client(int Port) {
        flag = true;
        port = Port;
    }
    public Client(GameStartType type) {
        this.type = type;
        flag = true;
        port = -1;
    }

    public void start() {
        SocketThread socketThread = new SocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
    }

    //Sent Message
    public void sendRevengeRequest() throws IOException {
        connection.send(new Message(MessageType.REVENGE_REQUEST, StaticObjects.player.color));
    }

    public void sendRevengeAccept() throws IOException {
        connection.send(new Message(MessageType.REVENGE_ACCEPT, StaticObjects.player));
    }

    public void sendBoard(Board board) throws IOException {
        connection.send(new Message(MessageType.BOARD, board));
    }

    //Thread of Handler
    public class SocketThread extends Thread {
        @Override public void run() {
            if (port == -1){
                serversPassing();
            } else {
                serverConnect();
            }
        }

        private void serversPassing() {
            for (int i=StaticInfo.portFrom; i<StaticInfo.portTo; i++) { //цикл по доступным портам
                try (Socket socket = new Socket(StaticInfo.address, i)) { //создаем новый сокет, если подключился к серверу
                    connection = new Connection(socket);
                    Player player = getInfo(); //получаем информацию об игроке, который ищет игру
                    if (player != null){
                        switch (type) {
                            case WAITING_LIST: //если мы находимcя в списке ожидания
                                int finalI = i;
                                Platform.runLater(()-> {
                                    MenuController.instance.addButtonToWaitingList(player, finalI); //создаем кнопку присоединения к этой игре
                                });
                                break;
                            case FAST_START: //если запустили быструю игру
                                if (StaticObjects.player.time.equals(player.time)){ //если время в этой игре такое же, как и нажатое время
                                    foundFastGamePort = i; //сохраняем порт игры
                                    isFoundFastGame = true; //и выставляем флаг о найдено игре
                                    return;
                                }
                                break;
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            }
            System.out.println("over");
            flag = false;
        }

        private void serverConnect() {
            try (Socket socket = new Socket(StaticInfo.address, port)) {
                connection = new Connection(socket);
                clientHandShake();
                mainLoop();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }

        public Player getInfo() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() != MessageType.PLAYER){
                    continue;
                }

                connection.send(new Message(MessageType.CHECK));
                return (Player) message.getData();
            }
        }

        public void clientHandShake() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();

                if (message.getType() != MessageType.PLAYER) {
                    continue;
                }

                connection.send(new Message(MessageType.PLAYER, StaticObjects.player));
                return;
            }
        }

        private void mainLoop() throws IOException, ClassNotFoundException {
            while (flag) {
                Message message = connection.receive(); //получаем сообщение

                if (message == null || message.getType() == null){
                    continue;
                }

                switch (message.getType()) {
                    case BOARD: //если пришла доска
                        Board.instance = (Board) message.getData(); //меняем данную доску на полученную
                        Habitat.setPieces(); //выставляем фигуры
                        BoardController.instance.drawShapes(null);
                        StaticObjects.player.isMoving = !StaticObjects.player.isMoving; //меняем ход игроков
                        StaticObjects.enemy.isMoving = !StaticObjects.enemy.isMoving;
                        break;
                    case START: //сообщение о старте игры
                        MenuController.instance.setGame((Player) message.getData()); //выставляем данные, относительно противника
                        MenuController.instance.gameStart(); //начинаем игру
                        break;
                    case REVENGE_REQUEST: //запрос о реванше
                        StaticObjects.enemy.color = (Color) message.getData(); //получаем цвет противника
                        StaticObjects.player.color = null;
                        StaticObjects.setInfo(new Time(StaticObjects.time)); //обновляем цвета
                        BoardController.instance.revengeRequest(); //меняем обработчик кнопки на принятие реванша
                        break;
                    case REVENGE_ACCEPT: //принятие реванша
                        Platform.runLater(()->BoardController.instance.setHabitat()); //запускаем игру
                        break;
                    case REVENGE_CANCEL: //непринятие реванша
                        break;
                    case CLOSE: //закрытие соединения
                        if (BoardController.instance.hab.isPlaying) { //если игра идет
                            Platform.runLater(() -> { //то конец игре, игрок вышел из игры
                                try {
                                    BoardController.instance.gameOver(StaticObjects.player.color, GameOverType.PLAYER_LEFT);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                        BoardController.instance.buttRevengeRequest.setVisible(false); //кнопка реванша не нужна
                        return;  //поток закрывается, противник отключился
                }
            }
        }
    }

    //Add Functions
    public void close() throws IOException, InterruptedException {
        flag = false;
        try {
            connection.send(new Message(MessageType.CLOSE, null));
            Thread.sleep(400);
            connection.close();
        } catch (NullPointerException | SocketException e){
            System.out.println("сокет закрыт");
        }
    }
}
