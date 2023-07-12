package Server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import Server.Bot;
import Server.Game;

public class Server {
    private static ServerSocket serverSocket;
    private static Server server;
    public static Game game;
    public static int maxPlayer;
    private static boolean fullLobby;
    public static String data;
    public static ArrayList < ClientHandler > clientHandlers = new ArrayList < > ();
    public static ArrayList < Bot > bots = new ArrayList < > ();

    public Server(ServerSocket serverSocket) {
        Server.serverSocket = serverSocket;
    }

    // Methode, die, solange der Socket nicht geschlossen ist, ständig auf
    // Verbindungsanfragen wartet
    public void startServer() {
        System.out.println("Der Server startet jetzt mit Port: " + serverSocket.getLocalPort() + " und " + maxPlayer +
                " maximalen Spielern");
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
                boolean nametaken = false;
                for (ClientHandler p: clientHandlers) {
                    if (p.getUsername().equals((clientHandler.getUsername()))) {
                        clientHandler.sendObject("M<:!ds5" + "Username schon vergeben\n");
                        clientHandler.socket.close();
                        nametaken = true;
                        break;
                    }
                }
                if (!nametaken) {
                    clientHandlers.add(clientHandler);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (!serverSocket.isClosed()) {
                System.out.println("Alle Spieler sind da.\n Das Spiel startet.");
                start();
            }

        } catch (IOException ignored) {

        }
    }

    public void start() {
        for (ClientHandler clientHandler: clientHandlers) {
            clientHandler.gameRunning = true;
            game.createPlayer(clientHandler.getUsername(), clientHandler);
        }

        game = new Game();
        game.startGame((int) clientHandlers.size());

        do {
            // jeder Player soll die aktuellen infos kriegen
            game.special();
            game.playerCard = null;
            for (ClientHandler clientHandler: clientHandlers) {
                clientHandler.sendObject(createList(clientHandler));
                System.out.println(clientHandler.getUsername() + " " + createList(clientHandler));
            }

            for (Bot bot: bots) {
                boolean isTurn = false;
                if (game.getCurrentPlayer() == game.getPlayerObject(bot)) {
                    isTurn = true;
                    bot.createList(isTurn, game.getPlayerObject(bot).getPlayerCards(), game.getTopCard());
                    System.out.println("Der Bot hat die Karte: " + game.playerCard + " gewählt!!");
                }
            }
            game.play();
        } while (!game.checkForWinner());

        System.out.println("Gewinner ist: " + game.getWinner());
        for (ClientHandler clientHandler: clientHandlers) {
            clientHandler.sendObject(createList(clientHandler) + game.getWinner());
            System.out.println(clientHandler.getUsername() + " " + createList(clientHandler) + " " + game.getWinner());
        }

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
    public String createList(ClientHandler clientHandler) {
        data = "";
        boolean isTurn = false;
        if (game.getCurrentPlayer() == game.getPlayerObject(clientHandler)) {
            isTurn = true;
        }

        data = "C8->7G#" + isTurn + "|" + game.getPlayerObject(clientHandler).getPlayerCards() + "|" +
                game.getTopCard() + "|" + maxPlayer + "|";

        for (ClientHandler player: clientHandlers) {
            data += player.getUsername() + "|" + game.getPlayerObject(player).getPlayerCards().size() + "|";
            if (game.getCurrentPlayer() == game.getPlayerObject(player)) {
                data += true + "|";
            } else {
                data += false + "|";
            }
        }
        for (Bot player: bots) {
            data += player.getUsername() + "|" + game.getPlayerObject(player).getPlayerCards().size() + "|";
            if (game.getCurrentPlayer() == game.getPlayerObject(player)) {
                data += true + "|";
            } else {
                data += false + "|";
            }
        }
        data = data + " ";
        return data;

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
        maxPlayer = 2;
        Scanner playerscanner = new Scanner(System.in);
        System.out.println("Max Spieler");
        // hier wird ein neuer Socket erstellt
        ServerSocket serverSocket = new ServerSocket(port);
        Server tempserver = new Server(serverSocket);
        server = tempserver;
        CheckMaxPlayer();

        System.out.println("Testnachricht");
        server.startServer();
    } //    K<;?dHs0
}