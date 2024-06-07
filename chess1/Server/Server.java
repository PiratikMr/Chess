package org.example.chess1.Server;

import org.example.chess1.Game.Containers.StaticObjects;
import org.example.chess1.Game.Player;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

public class Server {
    public final HashMap<Player, Connection> connections = new HashMap<>(); //данные о клиентах, в виде ключа профиль игрока
    private boolean flag; //флаг

    public void start() {
        new Thread(()->{ //новый поток
            for (int i = StaticInfo.portFrom; i<StaticInfo.portTo; i++) { //поход по доустпным портам
                try (ServerSocket serverSocket = new ServerSocket(i, 2, InetAddress.getByName(StaticInfo.address))) { //попытка создать сервер на данном порте
                    StaticInfo.portUsing = i; //сохраннеие порта

                    System.out.println("Сервер запущен");

                    flag = true;
                    serverSocket.setSoTimeout(1000);
                    while (connections.size() < 2 && flag) { //если флаг и игроков меньше 2, то ожидаем подключения
                        System.out.println("Ожидаю");
                        try {
                            Socket socket = serverSocket.accept(); //если подключился клиент
                            Handler handler = new Handler(socket); //создаем отдельный поток для его обработки
                            handler.start();
                        } catch (SocketTimeoutException ignored) {}
                    }
                    if (connections.size() == 2) { //если два клиента, сервер закрывается и становиться приватным, никто больше не подключиться
                        System.out.println("сервер приватный");
                    } else if (!flag) {
                        System.out.println("сервер закрыт");
                    }

                    break;

                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();
    }

    //Thread of Handler each Client
    public class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) { this.socket = socket; }

        @Override public void run() {
            System.out.println("Подключился клиент " + socket.getRemoteSocketAddress());
            try (Connection connection = new Connection(socket)) {
                Player player = checkClient(connection);
                if (player == null){
                    System.out.println("Собрал инфу и отключился");
                    return;
                }
                synchronized (connections) {
                    connections.put(player, connection);
                }
                mainLoop(connection);
                synchronized (connections) {
                    connections.remove(player, connection);

                }
            } catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
            System.out.println("Соединение с " + socket.getRemoteSocketAddress() + "закрыто");
        }

        private Player checkClient(Connection connection) throws IOException, ClassNotFoundException {
            connection.send(new Message(MessageType.PLAYER, StaticObjects.player)); //Отправка подключившемуся клиенту игрока - создателя сервеар - хоста

            while (true) { //ждем ответ от клиента
                Message message = connection.receive(); //получаем сообщение
                if (message.getType() == null){
                    continue;
                }
                switch (message.getType()){
                    case PLAYER: //игрок
                        return (Player) message.getData(); //возращаем игрока
                    case CHECK: //если клиент просто собирает инфу
                        return null; //Null
                }
            }
        }

        private void mainLoop(Connection connection) throws IOException, ClassNotFoundException {
            boolean flagStart = true;
            while (flag) {
                while (flagStart){
                    synchronized (connections) {
                        if (connections.size() == 2) { //если 2 клиента
                            System.out.println("старт");
                            System.out.println(connections);

                            for (Player player : connections.keySet()) { //проход по всем клиентам
                                if (!connections.get(player).equals(connection)) { //отправка противнике сообщение о старте
                                    connection.send(new Message(MessageType.START, player));
                                }
                            }
                            flagStart = false;
                            break;
                        }
                        if (connections.isEmpty()){
                            flagStart = false;
                            break;
                        }
                    }
                }
                synchronized (connections) {
                    if (connections.isEmpty()) {
                        break;
                    }
                }
                Message message;
                try {
                    message = connection.receive(); //получаем сообщение
                } catch (IOException e) {
                    System.out.println("не удается получить сообщение");
                    break;
                }

                if (message == null) {
                    continue;
                }

                switch (message.getType()) {
                    case BOARD, REVENGE_REQUEST, REVENGE_ACCEPT, REVENGE_CANCEL: //если доска, запрос на реванш, ответ на реванш
                        sendAllExcept(connection, message); //отправляе всем кроме себя
                        break;
                    case CLOSE: //закрытие
                        sendAllExcept(connection, message); //отправка сообщени всем кроме себя
                        return; //закртие обработчика, отсоединение клиента
                }
            }
        }

        private void sendAllExcept(Connection connection, Message message) throws IOException {
            for (Connection conn : connections.values()) {
                if (!conn.equals(connection)) {
                    conn.send(message);
                }
            }
        }
    }

    //Add Functions
    public void close() throws InterruptedException {
        flag = false;
        Thread.sleep(300);
        connections.clear();
    }
}
