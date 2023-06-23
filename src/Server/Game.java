package Server;

import java.util.ArrayList;
public class Game {
    int maxPlayer = 4;
    int numberOfPlayers;
    int cardsPerPlayer = 5;
    static Turn reihenfolge;
    static Player firstPlayer;
    static Player currentPlayer;
    Card card;
    ArrayList<String> kartenstapel = new ArrayList<String>();




    // Karten an jeden Spieler austeilen
    private void dealCards() {
        int count = 0;
        kartenstapel = card.getKartenStapel();
        currentPlayer = firstPlayer;
        for (int i = 0; i < numberOfPlayers; i++) {
            while (count < 5) {
                currentPlayer.addCard(kartenstapel.get(count));
            }
            count = 0;
            currentPlayer = currentPlayer.getNextPlayer();
        }
    }

        public static void createPlayer (String playerIP, String playerName){
            reihenfolge.createPlayer(playerIP, playerName);
        }

        public static void main (String[]args){

        }


}

