package org.example.chess1.DataBase;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import javafx.scene.chart.PieChart;
import org.example.chess1.Game.Player;
import org.example.chess1.Game.Containers.StaticObjects;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class DataBase extends Config {
    private static Connection getDbConnection() throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection dbConnection;
        try {
            dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);
        } catch (CommunicationsException e) {
            return null;
        }
        return dbConnection;
    }

    //SQl
    public static boolean isThere (String userName) {
        try {
            Connection connection = getDbConnection();
            if (connection == null){
                return isThereTextFile(userName);
            }
            PreparedStatement prSt = connection.prepareStatement("SELECT * FROM users WHERE Name = ?");

            prSt.setString(1, userName);
            ResultSet resultSet = prSt.executeQuery();

            return resultSet.next();
        } catch (SQLException | ClassNotFoundException _) {
            return false;
        }
    }

    public static boolean setPlayer(String userName, String password) {
        String request = "SELECT * FROM users WHERE Name = ? AND Password = ?";
        try {
            Connection connection = getDbConnection();
            if (connection == null){
                return setPlayerTextFile(userName, password);
            }
            PreparedStatement prSt = connection.prepareStatement(request);

            prSt.setString(1, userName);
            prSt.setString(2, password);

            ResultSet resultSet = prSt.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("Name");
                String pass = resultSet.getString("Password");
                int wins = resultSet.getInt("Wins");
                int loses = resultSet.getInt("Loses");
                int ties = resultSet.getInt("Ties");
                int rank = resultSet.getInt("Level");
                int avatarIdx = resultSet.getInt("AvatarIdx");
                StaticObjects.player = new Player(name, pass, rank, wins, loses, ties, avatarIdx);
                return true;
            } else {
                return false;
            }
        } catch (SQLException | ClassNotFoundException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static void addUser(String name, String password){
        StaticObjects.player = new Player(name, password);

        addUserTextFile();

        String insert = "INSERT INTO users(Name,Password,Wins,Loses,Ties,Level,AvatarIdx)VALUES(?,?,?,?,?,?,?)";
        try {
            Connection connection = getDbConnection();
            if (connection == null) {
                return;
            }
            PreparedStatement prSt = connection.prepareStatement(insert);
            prSt.setString(1, name);
            prSt.setString(2, password);
            prSt.setInt(3, StaticObjects.player.wins);
            prSt.setInt(4, StaticObjects.player.loses);
            prSt.setInt(5, StaticObjects.player.ties);
            prSt.setInt(6, StaticObjects.player.rank);
            prSt.setInt(7, StaticObjects.player.avatarIdx);
            prSt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    public static void update(Player player) {
        if (player == null){
            return;
        }
        if (player.password.isEmpty() || player.password.length() > 45){
            return;
        }

        updateTextFile(player);

        String sql = "UPDATE users SET " +
                "Wins = " + player.wins +
                ", Loses = " + player.loses +
                ", Ties = " + player.ties +
                ", Level = " + player.rank +
                ", AvatarIdx = " + player.avatarIdx +
                " WHERE Name = ? AND Password = ?";
        try {
            Connection connection = getDbConnection();
            if (connection == null){
                return;
            }
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, player.name);
            statement.setString(2, player.password);

            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    public static ArrayList<Player> getAllPlayers() {
        String request = "SELECT * FROM users";
        try {
            Connection connection = getDbConnection();
            if (connection == null){
                return getAllPlayersTextFile();
            }
            ArrayList<Player> players = new ArrayList<>();
            PreparedStatement prSt = connection.prepareStatement(request);

            ResultSet resultSet = prSt.executeQuery();

            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                String pass = resultSet.getString("Password");
                int wins = resultSet.getInt("Wins");
                int loses = resultSet.getInt("Loses");
                int ties = resultSet.getInt("Ties");
                int rank = resultSet.getInt("Level");
                int avatarIdx = resultSet.getInt("AvatarIdx");
                players.add(new Player(name, pass, rank, wins, loses, ties, avatarIdx));
            }
            return players;
        } catch (SQLException | ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    //TextFiles
    private static boolean isThereTextFile(String userName) {
        try {
            ObjectInputStream in = new ObjectInputStream(DataBase.class.getResourceAsStream(fileName));
            ArrayList<Player> players = (ArrayList<Player>) in.readObject();
            in.close();

            if (players == null) {
                return false;
            }

            for (Player player : players){
                if (player.name.equals(userName)) {
                    return true;
                }
            }
        }catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private static boolean setPlayerTextFile(String userName, String password) {
        try {
            ObjectInputStream in = new ObjectInputStream(DataBase.class.getResourceAsStream(fileName));
            ArrayList<Player> players = (ArrayList<Player>) in.readObject();
            in.close();

            if (players == null){
                return false;
            }

            for (Player player: players) {
                if (player.password.equals(password) && player.name.equals(userName)){
                    StaticObjects.player = player;
                    return true;
                }
            }
        }catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private static void addUserTextFile() {
        try {
            ObjectInputStream in = new ObjectInputStream(DataBase.class.getResourceAsStream(fileName));
            ArrayList<Player> players = (ArrayList<Player>) in.readObject();
            in.close();

            ArrayList<Player> updatedList;
            if (players == null) {
                updatedList = new ArrayList<>();
            } else {
                updatedList = new ArrayList<>(players);
            }

            updatedList.add(StaticObjects.player);

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DataBase.class.getResource(fileName).getPath()));
            out.writeObject(updatedList);
            out.close();
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void updateTextFile(Player player) {
        try {
            ObjectInputStream in = new ObjectInputStream(DataBase.class.getResourceAsStream(fileName));
            ArrayList<Player> players = (ArrayList<Player>) in.readObject();
            in.close();

            if (players == null) {
                return;
            }

            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).name.equals(player.name) && players.get(i).password.equals(player.password)) {
                    players.set(i, player);
                    break;
                }
            }

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DataBase.class.getResource(fileName).getPath()));
            out.writeObject(players);
            out.close();
        }catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private static ArrayList<Player> getAllPlayersTextFile() {
        try {
            ObjectInputStream in = new ObjectInputStream(DataBase.class.getResourceAsStream(fileName));
            ArrayList<Player> players = (ArrayList<Player>) in.readObject();
            in.close();

            return players;
        }catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    //Add Functions
}
