package Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Game {
    static int numberOfPlayers;
    static int cardsPerPlayer = 5;
    static Player firstPlayer;
    public static Player currentPlayer;
    static Turn reihenfolge = new Turn();
    static Card card = new Card();
    static ArrayList<String> cardDeck = card.getCardDeck();
    private String winner;
    public boolean checked = true;
    static int drawCards = 0;
    public boolean stackSeven;
    public ArrayList<String> playedCards = new ArrayList<String>();


    // Methode erzeugt Spieler
    public static void createPlayer(String playerName, Server.Server.ClientHandler playerID) {
        reihenfolge.createPlayer(playerName, playerID);
    }


    // Methode startet das Spiel -> wird in Server aufgerufen
    // Spieleranzahl muss mit übergeben werden
    public void startGame(int size) {
        numberOfPlayers = size;

        // je nach Spieleranzahl werden mehr Kartendecks hinzugefügt
        int multp = (int) Math.ceil((double) numberOfPlayers / 4);
        System.out.println(multp);
        for (int i = 1; i <= multp; i++) {
            // Methode aus "Card" wird aufgerufen, um Karten zu mischen
            Card.mixCards();
        }
        System.out.println("Gemischtes cardDeck: \n" + getCardDeck() + "\n");

        // Methode aus "Turn" wird aufgerufen, um Spielerreihenfolge festzulegen
        reihenfolge.connectFirstAndLast();
        // erster Spieler wird festgelegt
        firstPlayer = reihenfolge.getFirstPlayer();
        // aktueller Spieler wird festgelegt
        currentPlayer = firstPlayer;
        dealCards();
    }


    // je 5 Karten werden an jeden Spieler ausgeteilt
    private void dealCards() {
        currentPlayer = firstPlayer;

        for (int i = 0; i < numberOfPlayers; i++) {

            for (int cards = 0; cards < cardsPerPlayer; cards++) {
                currentPlayer.getPlayerCards().add(getCardDeck().get(0));
                getCardDeck().remove(0);
            }

            System.out.println(currentPlayer.getPlayerName());
            System.out.println(currentPlayer.getPlayerCards());

            currentPlayer = currentPlayer.getNextPlayer();
        }
        playedCards.add(getCardDeck().get(0));
        getCardDeck().remove(0);
    }


    // Scanner für Konsolenspiel
    static Scanner scanner = new Scanner(System.in);
    public static String playerCard;


    // Methode regelt Spielablauf
    public void play() {
        System.out.println(currentPlayer.getPlayerName());
        System.out.println(currentPlayer.getPlayerCards());

        System.out.println("Lege eine deiner Karten");
        while (playerCard == null) {
            // um Programm daran zu hindern, zu schnell über den Code zu gehen
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Kann der Spieler keine legen, muss er eine ziehen
        if (playerCard.isEmpty()) {
            System.out.println("Der Spieler " + currentPlayer.getPlayerName() + " zieht.");
            stackSeven = false;
            drawCards();

            // ist dafür da, um die 7 stacken zu können
            // liegt eine 7 oben und man hat selber keine, so muss man Karten ziehen
        } else if (stackSeven && !playerCard.contains("7")) {
            stackSeven = false;
            System.out.println("Der Spieler " + currentPlayer.getPlayerName() + " hat die Karte " + playerCard + " gelegt.");
            putPlayerCardToCardDeck(playerCard);
            drawCards();

            // hat Spieler eine passende Karte, kann er diese legen
        } else {
            System.out.println("Der Spieler " + currentPlayer.getPlayerName() + " hat die Karte " + playerCard + " gelegt.");
            putPlayerCardToCardDeck(playerCard);
        }
        System.out.println("\n");
    }



    // Karten ziehen
    private void drawCards() {

        // wenn Kartenstapel leer ist, wird ein neues Kartendeck hinzugefügt
        if (cardDeck.size() <= drawCards || cardDeck.size() < 2) {
            Server.Server.Server.addToCardDeck();
        }


        int i = 0;
        System.out.println("Spieler " + currentPlayer.getPlayerName() + " muss " + drawCards + " Karten ziehen.");
        // lässt Spieler Karte(n) ziehen
        do {
            currentPlayer.playerCards.add(getCardDeck().get(getCardDeck().size() - 1));
            getCardDeck().remove(getCardDeck().get(getCardDeck().size() - 1));
            i++;
        } while (i < drawCards);
        drawCards = 0;
    }


    // Spieler legt eine Karte von seiner Hand auf den Stapel
    public void putPlayerCardToCardDeck(String playerCard) {
        checked = false;
        getPlayedCards().add(0, playerCard);
        currentPlayer.getPlayerCards().remove(playerCard);
    }


    // neues Kartendeck wird zum Ablagestapel hinzugefügt und durchgemischt, wenn keine Karten mehr im Stapel sind
    public void addToCardDeck() {
        if (cardDeck.size() <= drawCards) {
            String save = playedCards.get(0);
            playedCards.remove(0);

            cardDeck.addAll(getPlayedCards());
            playedCards.clear();

            playedCards.add(0, save);

            Collections.shuffle(cardDeck);
            System.out.println("Carddeck" + cardDeck);
        }
        if (cardDeck.size() <= drawCards || cardDeck.size() < 2) {
            Card.mixCards();
        }
    }

    
    // Methode beschreibt was bei Sonderkarten passiert
    private void specialCards(String playerCard) {
        checked = true;

    // 2 Karten ziehen oder auch 7 legen
        if (getTopCard().contains("7")) {
            System.out.println("du musst 2 ziehen oder 7 legen " + currentPlayer.getPlayerName() + "\n");
            drawCards += 2;
            stackSeven = true;

    // 4 Ziehen & Aussetzen
        } else if (getTopCard().equals("PK")) {
            System.out.println("du musst 4 ziehen " + currentPlayer.getPlayerName() + "\n");
            for (int i = 0; i <= 4; i++) {
                currentPlayer.playerCards.add(getCardDeck().get(getCardDeck().size() - 1));
                getCardDeck().remove(getCardDeck().get(getCardDeck().size() - 1));
            }
            currentPlayer = currentPlayer.nextPlayer;
            System.out.println("Jetzt ist " + currentPlayer.getPlayerName() + " an der Reihe.\n");

    // Aussetzer
        } else if (playerCard.contains("A")) {
            System.out.println("du musst aussetzen " + currentPlayer.getPlayerName() + "\n");
            currentPlayer = currentPlayer.nextPlayer;
            System.out.println("Jetzt ist " + currentPlayer.getPlayerName() + " an der Reihe.\n");
        }
    }


    // checkt, ob die oberste Karte eine "special card" ist und ob diese schon ausgeführt wurde
    public void special() {
        System.out.println("checked " + checked);
        if (!checked) {
            specialCards(getTopCard());
        }
    }


    // Methode um zu kontrollieren, ob es bereits einen Gewinner gibt
    public boolean checkForWinner() {
        if (currentPlayer.getPlayerCards().isEmpty()) {
            winner = currentPlayer.getPlayerName();
            getWinner();
            return true;
        }
        return false;
    }



//      Getter

    // Oberste Karte anzeigen lassen
    public String getTopCard() {
        return getPlayedCards().get(0);
    }

    // Gewinner anzeigen lassen
    public String getWinner() {
        return winner;
    }

    // aktuellen Spieler anzeigen lassen
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public Player getPlayerObject(Object clientHandler) {
        return reihenfolge.getPlayerObject(clientHandler);
    }

    public ArrayList<String> getCardDeck() {
        return cardDeck;
    }

    public ArrayList<String> getPlayedCards() {
        return playedCards;
    }
}

// [ist-Drann , [karten] , [Spielername , AnzahlKarten von Spieler] , Oberste
// Spielkarte]