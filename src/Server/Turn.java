package Server;

public class Turn {
    // Regelt Player und Reihenfolgen
    static Player firstPlayer;
    static Player prevPlayer;
    // int playerCount;

    public static void createPlayer(String playerName, Server.Server.ClientHandler playerID) {
        Player player = new Player(playerName, playerID);
        if (firstPlayer == null) {
            firstPlayer = player;
        }
    }

    // direkt nach Spielbeginn aufrufen, um Reihenfolgeschleife in Player zu
    // schließen
    public void connectFirstAndLast() {
        Player tempPlayer = firstPlayer;
        while (tempPlayer.getNextPlayer() != null) {
            tempPlayer = tempPlayer.getNextPlayer();
        }
        tempPlayer.setNextPlayer(firstPlayer);
        prevPlayer = tempPlayer;
    }

    public void listAllPlayers() {
        Player temp = firstPlayer;
        do {
            System.out.println(temp.getPlayerName());
            temp = temp.nextPlayer;

        } while (temp != firstPlayer);
        System.out.println(temp.getPlayerName());
    }

    // Setter und Getter

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public Player getPlayerObject(Server.Server.ClientHandler clientHandler) {
        Player temp = firstPlayer;
        while (clientHandler != temp.getPlayerID()) {
            temp = temp.nextPlayer;
        }
        return temp;
    }

}
