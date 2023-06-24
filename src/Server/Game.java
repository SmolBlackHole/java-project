package Server;

import java.util.ArrayList;
public class Game {
    int maxPlayer = 4;
    static int numberOfPlayers = 4;
    int cardsPerPlayer = 5;
    static Player firstPlayer;
    static Player currentPlayer;
    static Turn reihenfolge = new Turn();
    static Card card = new Card();
    static ArrayList<String> kartenstapel = new ArrayList<String>();




    // Karten an jeden Spieler austeilen
    private static void dealCards() {
        int count = 0;
        System.out.println(card.getKartenStapel());
        kartenstapel = card.getKartenStapel();
        currentPlayer = firstPlayer;
        System.out.println("Statement wird erreicht");
        for (int i = 0; i < numberOfPlayers ; i++) {
            while (count < 5*(i+1)) {
                currentPlayer.addCard(kartenstapel.get(count));
                System.out.println(currentPlayer.getName() + " " + kartenstapel.get(count));
                count++;
            }
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
            System.out.println("Test");
            dealCards();
        }


}

