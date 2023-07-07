package Server;

import java.util.ArrayList;

public class Player {
    ArrayList<String> playerCards;

    String playerName;
    Player nextPlayer;
    public static Player lastPlayer;
    Object playerID;
    // maxSpieler -> Game

    public Player(String playerName, Object playerID) {
        this.playerName = playerName;
        this.playerID = playerID;

        // Array der eigenen Karten
        this.playerCards = new ArrayList<String>();

        // nextPlayer
        this.nextPlayer = null;

        if (lastPlayer != null) {
            lastPlayer.setNextPlayer(this);
        }

        lastPlayer = this;
    }

    // Getter & Setter

    public ArrayList<String> getPlayerCards() {
        return playerCards;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Player getNextPlayer() {
        return this.nextPlayer;
    }

    public void setNextPlayer(Player nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    public Object getPlayerID() {
        return this.playerID;
    }

    public void setPlayerID(Object playerID) {
        this.playerID = playerID;
    }

}
