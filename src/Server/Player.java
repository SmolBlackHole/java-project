package Server;


public class Player {
    Player nextPlayer;
    String playerIP;
    boolean isConnected;
    String playerName;
    Player platzhalter = new Player ("1", true, "Platzhalter", null);
    // yourTurn -> Variable notwendig?
    // maxSpieler -> Server.java


    public Player(String playerIP, boolean isConnected, String playerName, Player nextPlayer){
        playerIP = "1.2.3";
        isConnected = true;
        playerName = "Wait";
        nextPlayer = platzhalter;
    }


    // Liste Reihenfolge
}
