package Server;

import java.util.ArrayList;
import java.util.Scanner;

import static Server.Card.getCardDeck;
import static Server.Card.mixCards;

public class Game {
    int maxPlayer = 4;
    static int numberOfPlayers = 4;
    static int cardsPerPlayer = 5;
    static Player firstPlayer;
    static Player currentPlayer;
    static Turn reihenfolge = new Turn();
    static Card card = new Card();
    static Card topCard;
    static ArrayList<String> cardDeck = new ArrayList<String>();
    static boolean gameRunning = false;
    static int creatorCount = 0;
    private String winner;

    // Nessa

    // Start Game
    public Game() {

        // Karten mischen
        mixCards();
        System.out.println("Gemischter cardDeck: \n" + getCardDeck() + "\n");

        // Karten austeilen
        // dealCards();
        // System.out.println("\n" + "Restliche Karten: \n" + getCardDeck() + "\n");

        // oberste Karte vom Stapel anzeigen lassen
        // showTopCard();

        // Spiel spielen bis es einen Gewinner gibt
        System.out.println("\n" + "--PLAY-----------------------------------------");
        // play();

    }

    public void startGame() {
        firstPlayer = reihenfolge.getFirstPlayer();
        currentPlayer = firstPlayer;
        dealCards();

        while (winner == null) {

            playerTurn();
            checkForWinner();
        }

    }

    public void playerTurn() {

    }

    // Oberste Karte anzeigen lassen
    public static String showTopCard() {
        System.out.println("Oberste Karte: " + getCardDeck().get(0));
        return getCardDeck().get(0);
    }

    // 5 Karten an jeden Spieler austeilen
    private void dealCards() {
        currentPlayer = firstPlayer;
        int cards = 0;

        for (int player = 0; player < numberOfPlayers; player++) {

            while (cards < cardsPerPlayer) {
                currentPlayer.getPlayerCards().add(getCardDeck().get(0));
                getCardDeck().remove(0);
                cards++;
            }

            System.out.println(currentPlayer.getPlayerName());
            System.out.println(currentPlayer.getPlayerCards());
            cards = 0;

            currentPlayer = currentPlayer.getNextPlayer();
        }
    }

    // Spieler legt eine Karte von seiner Hand auf den Stapel
    public void putPlayerCardToCardDeck(String playerCard) {
        getCardDeck().add(0, playerCard);
        currentPlayer.getPlayerCards().remove(playerCard);

        System.out.println(currentPlayer.getPlayerCards());
    }

    // Karten ziehen
    private void drawCard() {
        for (int i = 0; i < drawCards; i++) {
            currentPlayer.playerCards.add(getCardDeck().get(getCardDeck().size() - 1));
            getCardDeck().remove(getCardDeck().get(getCardDeck().size() - 1));
        }
        drawCards = 0;

        if (showTopCard().contains("7")){
            play();
        }
    }


    private void checkForWinner() {
        if (currentPlayer.getPlayerCards().isEmpty()) {
            winner = currentPlayer.getPlayerName();
            System.out.println("Gewinner ist: " + winner);
        } 
        

    // TEST /////////////////////////////////////
    static Scanner scanner = new Scanner(System.in);
    public static String playerCard;
    //////////////////////////////////////////

    private void play() {
        while (winner == null) {
            System.out.println(currentPlayer.getPlayerName());
            System.out.println(currentPlayer.getPlayerCards());
            System.out.println("Lege einer deiner Karten");
            playerCard = scanner.nextLine();

            if (playerCard.isEmpty()) {
                drawCard();
            } else {
                putPlayerCardToCardDeck(playerCard);
            }
            System.out.println("\n");

            checkForWinner();

            specialCards(showTopCard());
            currentPlayer = currentPlayer.nextPlayer;

        }
    }

    
    private void specialCards(String playerCard) {
        // 2 Karten ziehen

        if (showTopCard().contains("7")) {
            if (playerCard.contains("7")) {
                currentPlayer.playerCards.remove(playerCard);
                drawCards += 2;
            }

            // 4 Ziehen
        } else if (playerCard.equals("PK")) {
            for (int i = 0; i <= 4; i++) {
                currentPlayer.nextPlayer.playerCards.add(getCardDeck().get(getCardDeck().size() - 1));
                getCardDeck().remove(getCardDeck().get(getCardDeck().size() - 1));
            }

            // Aussetzer
        } else if (playerCard.contains("A")) {
            currentPlayer = currentPlayer.nextPlayer;
        }

    }


    public static void createPlayer(String playerIP, String playerName, Server.Server.ClientHandler playerID) {
        reihenfolge.createPlayer(playerIP, playerName, playerID);
        System.out.println("Hallo " + playerName);
    }

    // Getter & Setter
    public ArrayList<Object> getAllKeyObjects() {
        return reihenfolge.getAllKeyObjects();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getPlayerObject(Server.Server.ClientHandler clientHandler) {
        return reihenfolge.getPlayerObject(clientHandler);
    }

}

// [Clienthandler , [ist-Drann , [karten] , [(als Map)Spielername , Anzahl
// Karten von Spieler] , Oberste Spielkarte]
