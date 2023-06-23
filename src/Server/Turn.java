package Server;

public class Turn {
    // Regelt Player und Reihenfolgen
    static Player firstPlayer;
    static Player prevPlayer;
    // int playerCount;

    public Turn(){
    }

    public static void createPlayer (String playerIP, String playerName){
        Player player = new Player(playerIP, playerName);
        if (firstPlayer == null) {
            firstPlayer = player;
            System.out.println(firstPlayer.getName());
        }
    }

    public void searchIP (String ipaddress) {
        Player tempPlayer = firstPlayer;

    }

    // direkt nach Spielbeginn aufrufen, um Reihenfolgeschleife in Player zu schließen
    static public void connectFirstAndLast () {
        Player tempPlayer = firstPlayer;
        while (tempPlayer.getNextPlayer() != null) {
            tempPlayer = tempPlayer.getNextPlayer();
        }
        tempPlayer.setNextPlayer(firstPlayer);
        prevPlayer = tempPlayer;
    }

    public static void test () {
        createPlayer("1", "Annabella");
        createPlayer("2", "Berta");
        createPlayer("3", "Cäsar");
        createPlayer("4", "Delta");
        System.out.println(firstPlayer.getName());
        connectFirstAndLast();
        System.out.println(firstPlayer.getName() + " " + prevPlayer.getName());
        System.out.println(firstPlayer.getNextPlayer().getName());
    }

    public static void main(String[] args) {
        test();
    }

    //Setter und Getter

    public Player getFirstPlayer (){
        return firstPlayer;
    }

}
