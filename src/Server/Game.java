package Server;

import java.util.ArrayList;
public class Game {
    int maxPlayer = 4;
    int numberOfPlayers;
    int cardsPerPlayer = 5;
    static Player firstPlayer;
    static Player currentPlayer;
    static Turn reihenfolge = new Turn();
    static Card card;
    static ArrayList<String> kartenstapel = new ArrayList<String>();




    // Karten an jeden Spieler austeilen
    private static void dealCards() {
        int count = 0;
        kartenstapel = card.getKartenStapel();
        currentPlayer = firstPlayer;
        for (int i = 0; i<5 ; i++) {
            while (count < 5) {
                currentPlayer.addCard(kartenstapel.get(count));
                System.out.println(currentPlayer + " " + kartenstapel.get(count));
            }
            count = 0;
            currentPlayer = currentPlayer.getNextPlayer();
        }
    }

        public static void createPlayer (String playerIP, String playerName){
            reihenfolge.createPlayer(playerIP, playerName);
        }

        public static void main (String[]args){
            createPlayer("1", "Annabella");
            createPlayer("2", "Berta");
            createPlayer("3", "Cäsar");
            createPlayer("4", "Delta");
            System.out.println(reihenfolge);
            firstPlayer = reihenfolge.getFirstPlayer();
            dealCards();
        }


}

