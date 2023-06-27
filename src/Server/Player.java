package Server;

import java.util.ArrayList;

public class Player {
    /* ArrayList<String> handCards = new ArrayList<String>(); */
    ArrayList<String> playerCards;
    Object[] keyObjects;
    String playerIP;
    boolean isConnected = true;
    String playerName;
    Player nextPlayer;
    public static Player lastPlayer;
    Server.Server.ClientHandler playerID;
    // maxSpieler -> Game

    public Player(String playerIP, String playerName, Server.Server.ClientHandler playerID) {
        this.playerIP = playerIP;
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

    /*
     * public ArrayList<String> getHandCards() {
     * return this.handCards;
     * }
     * 
     * public void sethandCards(ArrayList<String> handCards) {
     * this.handCards = handCards;
     * }
     */
    public ArrayList<String> getPlayerCards() {
        return playerCards;
    }

    public String getPlayerIP() {
        return this.playerIP;
    }

    public void setPlayerIP(String playerIP) {
        this.playerIP = playerIP;
    }

    public boolean isIsConnected() {
        return this.isConnected;
    }

    public boolean getIsConnected() {
        return this.isConnected;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
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

    public Server.Server.ClientHandler getPlayerID() {
        return this.playerID;
    }

    public void setPlayerID(Server.Server.ClientHandler playerID) {
        this.playerID = playerID;
    }

    /*
     * public void addCard(String card) {
     * handCards.add(card);
     * }
     */

    public Object[] getKeyObjects() {
        this.keyObjects = new Object[2];
        keyObjects[0] = playerName;
        keyObjects[1] = playerCards.size();
        return this.keyObjects;
    }

    public void setKeyObjects(String name) {
        keyObjects[0] = name;
    }

    // Klasse Player wird ausschließlich von Klasse Game genutzt und aufgerufen
    // public static void main(String[] args) { }
}
