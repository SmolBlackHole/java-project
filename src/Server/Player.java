package Server;

import java.util.ArrayList;

public class Player {
    ArrayList<Card> kartenstapel = new ArrayList<Card>();
    String playerIP;
    boolean isConnected = true;
    String playerName;
    Player nextPlayer;
    private static Player lastPlayer;
    // yourTurn -> Variable notwendig?
    // maxSpieler -> Game


    public Player(String playerIP,  String playerName){
        this.playerIP = playerIP;
        this.playerName = playerName;
        // nextPlayer
        this.nextPlayer = null;

        if (lastPlayer != null) {
            lastPlayer.setNextPlayer(this);
        }

        lastPlayer = this;
    }


    // Getter & Setter
    private void setNextPlayer(Player player){
        this.nextPlayer = player;
    }

    public Player getNextPlayer() {
        return nextPlayer;
    }

    public static Player getLastPlayer() {
        return lastPlayer;
    }

    public String getName() {
        return playerName;
    }


    // Liste Reihenfolge

    public static void main(String[] args) {
        // Testdaten
        Player player1 = new Player("1", "Erster");
        Player player2 = new Player("2", "Zweiter");
        Player player3 = new Player("3", "Dritter");


        boolean isGameStarting = true;
        if (isGameStarting) {
            Player firstPlayer = Player.getLastPlayer().getNextPlayer();
            System.out.println("First player: " + firstPlayer.getName());
        }
    }
}
