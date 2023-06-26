package Server;

import java.util.ArrayList;

public class Player {
    ArrayList<String> kartenstapel = new ArrayList<String>();
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
        this.keyObjects = new Object[2];
        keyObjects[0] = playerName;
        keyObjects[1] = kartenstapel.size();
        // nextPlayer
        this.nextPlayer = null;

        if (lastPlayer != null) {
            lastPlayer.setNextPlayer(this);
        }

        lastPlayer = this;
    }

    // Getter & Setter

    public ArrayList<String> getKartenstapel() {
        return this.kartenstapel;
    }

    public void setKartenstapel(ArrayList<String> kartenstapel) {
        this.kartenstapel = kartenstapel;
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

    public void addCard(String card) {
        kartenstapel.add(card);
    }

    public Object[] getKeyObjects() {
        return this.keyObjects;
    }

    public void setKeyObjects(String name) {
        keyObjects[0] = name;
    }

    // Klasse Player wird ausschließlich von Klasse Game genutzt und aufgerufen
    // public static void main(String[] args) { }
}
