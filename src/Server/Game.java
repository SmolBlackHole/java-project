package Server;

import java.util.ArrayList;
import java.util.Scanner;

import static Server.Card.getCardDeck;
import static Server.Card.mixCards;

public class Game {
    static int numberOfPlayers;
    static int cardsPerPlayer = 5;
    static Player firstPlayer;
    static Player currentPlayer;
    static Turn reihenfolge = new Turn();
    static Card card = new Card();
    static ArrayList<String> cardDeck = new ArrayList<String>();
    private String winner;
    private int drawCards = 0;

    public void startGame(int size) {
        numberOfPlayers = size;
        mixCards();
        System.out.println("Gemischtes cardDeck: \n" + getCardDeck() + "\n");
        reihenfolge.connectFirstAndLast();
        firstPlayer = reihenfolge.getFirstPlayer();
        currentPlayer = firstPlayer;
        dealCards();
        play();
    }

    public void playerMove() {
        // Move
    }

    // 5 Karten an jeden Spieler austeilen
    private void dealCards() {
        currentPlayer = firstPlayer;
        int cards = 0;

        for (int i = 0; i < numberOfPlayers; i++) {

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
        int i = 0;

        do {
            currentPlayer.playerCards.add(getCardDeck().get(getCardDeck().size() - 1));
            getCardDeck().remove(getCardDeck().get(getCardDeck().size() - 1));
            i++;
        } while (i < drawCards);

        drawCards = 0;

        if (getTopCard().contains("7")) {
            play();
        }
    }

    public boolean checkForWinner() {
        if (currentPlayer.getPlayerCards().isEmpty()) {
            winner = currentPlayer.getPlayerName();
            System.out.println("Gewinner ist: " + winner);
            return true;
        }
        return false;
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
            specialCards(getTopCard());
            currentPlayer = currentPlayer.nextPlayer;

        }
    }

    private void specialCards(String playerCard) {
        // 2 Karten ziehen

        if (getTopCard().contains("7")) {
            if (playerCard.contains("7")) {
                currentPlayer.playerCards.remove(playerCard);
                drawCards += 2;
                System.out.println("du musst 2 ziehen " + currentPlayer.nextPlayer.getPlayerName() + "\n");
            }

            // 4 Ziehen
        } else if (playerCard.equals("PK")) {
            for (int i = 0; i <= 4; i++) {
                currentPlayer.nextPlayer.playerCards.add(getCardDeck().get(getCardDeck().size() - 1));
                getCardDeck().remove(getCardDeck().get(getCardDeck().size() - 1));
                System.out.println("du musst 4 ziehen" + currentPlayer.nextPlayer.getPlayerName() + "\n");
            }

            // Aussetzer
        } else if (playerCard.contains("A")) {
            System.out.println("du musst aussetzen" + currentPlayer.nextPlayer.getPlayerName() + "\n");
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

    public void printAllKeyObjects() {
        ArrayList<Object> keyObjects = getAllKeyObjects();
        for (int i = 0; i < keyObjects.size(); i += 2) {
            String playerName = (String) keyObjects.get(i);
            int cardDeckSize = (int) keyObjects.get(i + 1);
            System.out.println("Player Name: " + playerName + ", Card Deck Size: " + cardDeckSize);
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getPlayerObject(Server.Server.ClientHandler clientHandler) {
        return reihenfolge.getPlayerObject(clientHandler);
    }

    // Oberste Karte anzeigen lassen
    public String getTopCard() {
        return getCardDeck().get(0);
    }

}
