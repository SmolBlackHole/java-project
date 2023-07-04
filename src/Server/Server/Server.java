package Server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import Server.Game;

public class Server {
    private static ServerSocket serverSocket;
    private static Server server;
    private Game game;
    private static int maxPlayer;
    private static boolean fullLobby;
    public static String data;
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // Methode, die, solange der Socket nicht geschlossen ist, ständig auf
    // Verbindungsanfragen wartet
    public void startServer() {
        try {
            while (!serverSocket.isClosed() && !fullLobby) {
                // hier werden die Verbindungsanfragen von Clients akzeptiert25566
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                System.out.println("Ein wilder Chatbesucher ist erschienen");
                // um diesen Prozess durchgehend im Hintergrund laufen zu lassen, benutzen wir
                // hier einen Thread
                Thread thread = new Thread(clientHandler);
                thread.start();

                clientHandlers.add(clientHandler);

                game.createPlayer(clientHandler.getIP(), clientHandler.getUsername(), clientHandler);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (!serverSocket.isClosed()) {
                System.out.println("ich bin raus!!!");
                start();
            }

        } catch (IOException ignored) {

        }
    }

    public void start() {
        game = new Game();
        game.startGame((int) clientHandlers.size());

        /*
         * for (int i = 0; i < 4; i++) {
         * // jeder Player soll die aktuellen infos kriegen
         * for (ClientHandler clientHandler : clientHandlers) {
         * createList(clientHandler);
         * }
         * game.playerMove();
         * 
         * }
         */
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // C8->7G#ist-Drann|karten|ObersteSpielkarte|AnzahlSpieler|Spielername1|AnzahlKarten|Spielername2|AnzahlKarten|Spielername3|AnzahlKarten|.....
    public void createList(ClientHandler clientHandler) {
        data = "";
        boolean isTurn = false;
        if (game.getCurrentPlayer() == game.getPlayerObject(clientHandler)) {
            isTurn = true;
        }

        System.out.println(clientHandler.getUsername() + ": " + isTurn + " , "
                + game.getPlayerObject(clientHandler).getPlayerCards() + " , "
                + game.getAllKeyObjects() + " , " + game.getTopCard());
        data = "C8->7G#" + isTurn + "|" + game.getPlayerObject(clientHandler).getPlayerCards() + "|"
                + game.getAllKeyObjects() + " , " + game.getTopCard() + "|" + maxPlayer + "|"
                + clientHandler.getUsername() + "|" + game.getPlayerObject(clientHandler).getPlayerCards() + "|";
        System.out.println(data);

    }

    public static void CheckMaxPlayer() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean wasFull = false;
                while (serverSocket != null) {

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (maxPlayer == (int) clientHandlers.size()) {
                        fullLobby = true;
                        wasFull = true;
                    }
                    if (clientHandlers.size() < maxPlayer && wasFull) {
                        fullLobby = false;
                        server.startServer();
                    }
                }
            }
            // startet den Thread
        }).start();
    }

    public static void main(String[] args) throws IOException {

        // hier wird vor dem Starten um eine Porteingabe gebeten
        Scanner portscanner = new Scanner(System.in);
        System.out.println("Port Eingeben");
        int port = 25565;

        Scanner playerscanner = new Scanner(System.in);
        System.out.println("Max Spieler");
        maxPlayer = 2;
        // hier wird ein neuer Socket erstellt
        ServerSocket serverSocket = new ServerSocket(port);
        Server tempserver = new Server(serverSocket);
        server = tempserver;
        CheckMaxPlayer();
        server.startServer();
    }
}