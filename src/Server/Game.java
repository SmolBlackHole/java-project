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
    static ArrayList<String> cardDeck = card.getCardDeck();
    private String winner;
    public boolean checked;


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
    }


    // je 5 Karten werden an jeden Spieler ausgeteilt
    private void dealCards() {
        currentPlayer = firstPlayer;

        for (int i = 0; i < numberOfPlayers; i++) {

            for(int cards = 0; cards < cardsPerPlayer; cards++) {
                currentPlayer.getPlayerCards().add(getCardDeck().get(0));
                getCardDeck().remove(0);
            }

            System.out.println(currentPlayer.getPlayerName());
            System.out.println(currentPlayer.getPlayerCards());

            currentPlayer = currentPlayer.getNextPlayer();
        }
    }


     // TEST FÜR CONSOLENSPIEL /////////////////
     static Scanner scanner = new Scanner(System.in);
     public static String playerCard;
     //////////////////////////////////////////
 
     
     // Methode regelt Spielablauf, solange es keinen Gewinner gibt
     // gibt es einen Gewinner, wird das Spiel beendet
     public void special(){
        if(!checked){ 
            specialCards(getTopCard());
            checked = true;  
        } 
     }
    public void play() {               
            // wenn Spieler keine Karte legen kann, so muss er ziehen
            System.out.println(currentPlayer.getPlayerName());
            System.out.println(currentPlayer.getPlayerCards());

            System.out.println("Lege einer deiner Karten");
            //playerCard = currentPlayer.getPlayerID().requestCard();
            playerCard = scanner.nextLine();

            if(playerCard.isEmpty()) {
                    drawCards(1);
                    // wenn Spieler eine Karte legen kann, so wird sie aus seinen Karten gestrichen und auf den Stapel gepackt
            
            }
            else {
                putPlayerCardToCardDeck(playerCard);
            }
            System.out.println("\n");
        
            
            currentPlayer = currentPlayer.nextPlayer;
        
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
    private void drawCards(int drawCards) {
        int i = 0;
        do{
            currentPlayer.playerCards.add(getCardDeck().get(getCardDeck().size() - 1));
            getCardDeck().remove(getCardDeck().get(getCardDeck().size() - 1));
            i++;
        }while(i < drawCards);
        System.out.println("Ziehen ist abgeschlossen.");
        }


    // @Chris: muss aufgerufen werden, wenn Spieler eine Karte ablegt
    // Spieler legt eine Karte von seiner Hand auf den Stapel
    public void putPlayerCardToCardDeck(String playerCard) {
        checked = false;
        getCardDeck().add(0, playerCard);
        currentPlayer.getPlayerCards().remove(playerCard);

        System.out.println(currentPlayer.getPlayerCards());
    }

     

    // Methode beschreibt was bei Sonderkarten passiert
    private void specialCards(String playerCard) {
        // 2 Karten ziehen

        if (getTopCard().contains("7")) {
            System.out.println("du musst 2 ziehen " + currentPlayer.getPlayerName() + "\n");
            drawCards(2);
            System.out.println("Du bist immernoch an der Reihe.\n");
            
            // 4 Ziehen
        } else if (getTopCard().equals("PK")) {
            System.out.println("du musst 4 ziehen" + currentPlayer.getPlayerName() + "\n");
            drawCards(4);
            currentPlayer = currentPlayer.nextPlayer;
            System.out.println("Jetzt ist " + currentPlayer.getPlayerName() + " an der Reihe.\n");
            // Aussetzer
        } else if (playerCard.contains("A")) {
            System.out.println("du musst aussetzen" + currentPlayer.getPlayerName() + "\n");
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

    public Player getPlayerObject(Server.Server.ClientHandler clientHandler) {
        return reihenfolge.getPlayerObject(clientHandler);
    }

    public ArrayList<String> getCardDeck(){
        return cardDeck;
    }

}

// [ist-Drann , [karten] , [Spielername , AnzahlKarten von Spieler] , Oberste
// Spielkarte]
