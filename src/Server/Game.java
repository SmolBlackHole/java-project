package Server;

import java.util.ArrayList;
import java.util.Scanner;



public class Game {
    static int numberOfPlayers;
    static int cardsPerPlayer = 5;
    static Player firstPlayer;
    static Player currentPlayer;
    static Turn reihenfolge = new Turn();
    static Card card = new Card();
    static ArrayList<String> cardDeck = new ArrayList<String>();
    private String winner;


    // Methode startet das Spiel 
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
        play();
    }


    // je 5 Karten werden an jeden Spieler ausgeteilt
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


     // TEST FÜR CONSOLENSPIEL /////////////////
     static Scanner scanner = new Scanner(System.in);
     public static String playerCard;
     //////////////////////////////////////////
 
     
     // Methode regelt Spielablauf, solange es keinen Gewinner gibt
     // gibt es einen Gewinner, wird das Spiel beendet
       private void play() {
       while (winner == null) {
       System.out.println(currentPlayer.getPlayerName());
       System.out.println(currentPlayer.getPlayerCards());
       System.out.println("Lege einer deiner Karten");
       playerCard = scanner.nextLine();
       
       // wenn Spieler keine Karte legen kann, so muss er ziehen
       if (playerCard.isEmpty()) {
         drawCard();
         // wenn Spieler eine Karte legen kann, so wird sie aus seinen Karten gestrichen und auf den Stapel gepackt
       } else {
       putPlayerCardToCardDeck(playerCard);
       }
      System.out.println("\n");
       
       checkForWinner();
       // wird kontrolliert, ob spezialkarten gelegt wurden, ansonsten geht es normal weiter mit dem nächsten Spieler
      specialCards(showTopCard());
       currentPlayer = currentPlayer.nextPlayer;
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


     // @Chris: muss aufgerufen werden, wenn Spieler eine Karte ziehen muss (am besten extra button dafür, falls du es nicht schon hast)
     // Karten ziehen
    private void drawCard() {
        int i = 0;

        do{
            currentPlayer.playerCards.add(getCardDeck().get(getCardDeck().size() - 1));
            getCardDeck().remove(getCardDeck().get(getCardDeck().size() - 1));
            i++;
        }while(i < drawCards)

      drawCards = 0;
      System.out.println(currentPlayer.getPlayerCards());
      
      if (getTopCard().contains("7")) {
      play();
      }
      }


    // @Chris: muss aufgerufen werden, wenn Spieler eine Karte ablegt
    // Spieler legt eine Karte von seiner Hand auf den Stapel
    public void putPlayerCardToCardDeck(String playerCard) {
        getCardDeck().add(0, playerCard);
        currentPlayer.getPlayerCards().remove(playerCard);

        System.out.println(currentPlayer.getPlayerCards());
    }

     

    // Methode beschreibt was bei Sonderkarten passiert
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


    // Methode erzeugt Spieler
     public static void createPlayer(String playerIP, String playerName, Server.Server.ClientHandler playerID) {
        reihenfolge.createPlayer(playerIP, playerName, playerID);
        System.out.println("Hallo " + playerName);
    }



//      Getter

    // Oberste Karte anzeigen lassen
    public String getTopCard() {
        return getCardDeck().get(0);
    }

    // Gewinner anzeigen lassen
    public String getWinner(){
        System.out.println("Gewinner ist: " + winner);
        return winner;
    }

    // aktuellen Spieler anzeigen lassen
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

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

    public Player getPlayerObject(Server.Server.ClientHandler clientHandler) {
        return reihenfolge.getPlayerObject(clientHandler);
    }

}

// [ist-Drann , [karten] , [Spielername , AnzahlKarten von Spieler] , Oberste
// Spielkarte]
