package Server;

import java.util.ArrayList;

public class Player {
    ArrayList<Card> kartenstapel = new ArrayList<Card>();
    String playerIP;
    boolean isConnected = true;
    String playerName;
    Player nextPlayer;
    public static Player lastPlayer;

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
    public void setNextPlayer(Player player){
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
    // Klasse Player wird ausschließlich von Klasse Game genutzt und aufgerufen
    //  public static void main(String[] args) {    }
}
