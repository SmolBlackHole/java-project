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
    static ArrayList < String > cardDeck = card.getCardDeck();
    private String winner;
    public boolean checked = true;
    static int drawCards = 0;
    public boolean stackSeven;
    public ArrayList<String> playedCards = new ArrayList<String>();

    // Methode startet das Spiel -> wird in Server aufgerufen
    // Spieleranzahl muss mit übergeben werden
    public void startGame(int size) {
        numberOfPlayers = size;

        // Methode aus "Card" wird aufgerufen, um Karten zu mischen
        Card.mixCards();
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

    // TEST FÜR CONSOLENSPIEL /////////////////
    static Scanner scanner = new Scanner(System.in);
    public static String playerCard;
    //////////////////////////////////////////

    // Methode regelt Spielablauf, solange es keinen Gewinner gibt
    // gibt es einen Gewinner, wird das Spiel beendet
    public void special() {
        System.out.println("checked " + checked);
        if (!checked) {
            specialCards(getTopCard());
        }
    }
    public void play() {
        // wenn Spieler keine Karte legen kann, so muss er ziehen
        System.out.println(currentPlayer.getPlayerName());
        System.out.println(currentPlayer.getPlayerCards());

        System.out.println("Lege eine deiner Karten");
        while (playerCard == null) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        };
               

        if (playerCard.isEmpty()) {
            System.out.println("Der Spieler " + currentPlayer.getPlayerName() + " zieht.");
            stackSeven = false;
            drawCards();
            // wenn Spieler eine Karte legen kann, so wird sie aus seinen Karten gestrichen und auf den Stapel gepackt

        } else if (stackSeven && !playerCard.contains("7")) {
            stackSeven = false;
            System.out.println("Der Spieler " + currentPlayer.getPlayerName() + " hat die Karte " + playerCard + " gelegt."); 
            putPlayerCardToCardDeck(playerCard);
            drawCards();
        } else {
            System.out.println("Der Spieler " + currentPlayer.getPlayerName() + " hat die Karte " + playerCard + " gelegt."); 
            putPlayerCardToCardDeck(playerCard);
        }
        System.out.println("\n");

        // currentPlayer = currentPlayer.nextPlayer;
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

    // @Chris: muss aufgerufen werden, wenn Spieler eine Karte ziehen muss (am besten extra button dafür, falls du es nicht schon hast)
    // Karten ziehen

    private void drawCards() {
        int i = 0;
        System.out.println("Spieler " + currentPlayer.getPlayerName() + " muss " + drawCards + " Karten ziehen.");
        do {
            currentPlayer.playerCards.add(getCardDeck().get(getCardDeck().size() - 1));
            getCardDeck().remove(getCardDeck().get(getCardDeck().size() - 1));
            i++;
        } while (i < drawCards);
        drawCards = 0;
    }

    // @Chris: muss aufgerufen werden, wenn Spieler eine Karte ablegt
    // Spieler legt eine Karte von seiner Hand auf den Stapel
    public void putPlayerCardToCardDeck(String playerCard) {

        if(cardDeck.size() <= 8){
            cardDeck.addAll(getPlayedCards());
            playedCards.clear();
            Collections.shuffle(cardDeck);
            System.out.println("Carddeck" + cardDeck);
        }
        System.out.println("Before "+getPlayedCards());

        checked = false;
        getPlayedCards().add(0, playerCard);
        currentPlayer.getPlayerCards().remove(playerCard);
        System.out.println("after "+getPlayedCards());


        System.out.println(currentPlayer.getPlayerCards());



    }

    // Methode beschreibt was bei Sonderkarten passiert
    private void specialCards(String playerCard) {
        // 2 Karten ziehen
        checked = true;

        //oder
        if (getTopCard().contains("7")) {
            // 7 legen
            System.out.println("du musst 2 ziehen oder 7 legen " + currentPlayer.getPlayerName() + "\n");
            drawCards += 2;
            stackSeven = true;

            // 4 Ziehen
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

    // Methode erzeugt Spieler
    public static void createPlayer(String playerName, Server.Server.ClientHandler playerID) {
        reihenfolge.createPlayer(playerName, playerID);
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

    public Player getPlayerObject(Object clientHandler) {
        return reihenfolge.getPlayerObject(clientHandler);
    }

    public ArrayList < String > getCardDeck() {
        return cardDeck;
    }

    public ArrayList < String > getPlayedCards() {
        return playedCards;
    }
}

// [ist-Drann , [karten] , [Spielername , AnzahlKarten von Spieler] , Oberste
// Spielkarte]