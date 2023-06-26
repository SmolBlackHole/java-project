package Server;

import java.util.ArrayList;

public class Turn {
    // Regelt Player und Reihenfolgen
    static Player firstPlayer;
    static Player prevPlayer;
    ArrayList<Object> keyObjects;
    // int playerCount;

    public Turn() {
    }

    public static void createPlayer(String playerIP, String playerName, Server.Server.ClientHandler playerID) {
        Player player = new Player(playerIP, playerName, playerID);
        if (firstPlayer == null) {
            firstPlayer = player;
        }
    }

    public Player searchIP(String ipaddress) {
        Player tempPlayer = firstPlayer;
        while (!(ipaddress == tempPlayer.getPlayerName())) {
            tempPlayer = tempPlayer.nextPlayer;
        }
        return tempPlayer;

    }

    // direkt nach Spielbeginn aufrufen, um Reihenfolgeschleife in Player zu
    // schließen
    static public void connectFirstAndLast() {
        Player tempPlayer = firstPlayer;
        while (tempPlayer.getNextPlayer() != null) {
            tempPlayer = tempPlayer.getNextPlayer();
        }
        tempPlayer.setNextPlayer(firstPlayer);
        prevPlayer = tempPlayer;
    }

    public void listAllPlayers() {
        Player temp = firstPlayer;
        while (!(temp.nextPlayer == firstPlayer || temp.nextPlayer == null)) {
            System.out.println(temp.getPlayerName());
            temp = temp.nextPlayer;

        }
        System.out.println(temp.getPlayerName());
    }

    public ArrayList<Object> getAllKeyObjects() {
        Player temp = firstPlayer;
        while (!(temp.nextPlayer == firstPlayer || temp.nextPlayer == null)) {
            keyObjects.add(temp.getKeyObjects());
            temp = temp.nextPlayer;

        }
        return keyObjects;
    }

    public static void test() {

    }

    public static void main(String[] args) {
        test();
    }

    // Setter und Getter

    public Player getFirstPlayer() {
        return firstPlayer;
    }

}
