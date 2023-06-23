package Server;

import java.util.ArrayList;
public class Game {
    int maxPlayer = 4;
    int numberOfPlayers;
    int cardsPerPlayer = 5;
    static Player firstPlayer;
    static Player currentPlayer;
    Card card;
    ArrayList<String> kartenstapel = new ArrayList<String>();




    // Karten an jeden Spieler austeilen
    private void dealCards(){
        kartenstapel = card.getKartenStapel();
        
    }

    //testzwecke

    // zum Erzeugen von neuen Spielern verwenden. firstPlayer wichtig wenn alle Spieler eingegeben wurden
    public static void test(){
        createPlayer("1", "Annabella");
        createPlayer("2", "Berta");
        createPlayer("3", "Cäsar");
        createPlayer("4", "Delta");
        connectFirstAndLast();
        System.out.println(firstPlayer.getName() + " " + currentPlayer.getName());
        System.out.println(firstPlayer.getNextPlayer().getName());
    }
    public static void createPlayer(String playerIP,  String playerName){
        Player player = new Player(playerIP, playerName);
        if(player.getLastPlayer() == player){
            firstPlayer = player;
        }
    }

    public static void main(String[] args) {
        test();
    }

    // direkt nach Spielbeginn aufrufen, um Reihenfolgenschleife in Player zu schließen
    static public void connectFirstAndLast(){
        Player tempPlayer = firstPlayer;
        while(tempPlayer.getNextPlayer() != null){
            tempPlayer = tempPlayer.getNextPlayer();
        }
        tempPlayer.setNextPlayer(firstPlayer);
        currentPlayer = tempPlayer;
    }

}
