package Server;

import java.util.ArrayList;

public class Game {
    int maxPlayer = 4;
    static int numberOfPlayers = 4;
    static int cardsPerPlayer = 5;
    static Player firstPlayer;
    static Player currentPlayer;
    static Turn reihenfolge = new Turn();
    static Card card = new Card();
    static Card topCard;
    static ArrayList<String> kartenstapel = new ArrayList<String>();
    static boolean gameRunning = false;
    static int creatorCount = 0;

    // Karten an jeden Spieler austeilen
    private static void dealCards() {
        int count = 0;
        kartenstapel = card.getKartenStapel();
        currentPlayer = firstPlayer;
        for (int i = 0; i < numberOfPlayers; i++) {
            while (count < cardsPerPlayer * (i + 1)) {
                currentPlayer.addCard(kartenstapel.get(count));
                System.out.println(currentPlayer.getPlayerName() + " " + kartenstapel.get(count));
                count++;
            }
            currentPlayer = currentPlayer.getNextPlayer();
        }
    }

    public static void createPlayer(String playerIP, String playerName, Server.Server.ClientHandler playerID) {
        System.out.println("Ich bin in createPlayer");
        reihenfolge.createPlayer(playerIP, playerName, playerID);
        System.out.println("Hallo " + playerName);
    }

    public ArrayList<Object> getAllKeyObjects() {
        return reihenfolge.getAllKeyObjects();
    }

    public static void test() {

    }

    public static void main(String[] args) {
        test();
    }

    // Setter und Getter

}

// [Clienthandler , [ist-Drann , [karten] , [(als Map)Spielername , Anzahl
// Karten von Spieler] , Oberste Spielkarte]
