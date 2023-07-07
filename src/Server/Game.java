package Server;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;



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
    static int drawCards = 0;


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
        currentPlayer = currentPlayer.nextPlayer;
        if(!checked){ 
            specialCards(getTopCard());
            checked = true;  
        } 
     }
    public void play() {               
            // wenn Spieler keine Karte legen kann, so muss er ziehen
            System.out.println(currentPlayer.getPlayerName());
            System.out.println(currentPlayer.getPlayerCards());

            System.out.println("Lege eine deiner Karten");
            playerCard = null;
            while(playerCard == null){
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            };
            System.out.println("Grad dran: "+currentPlayer.getPlayerName() + " Dann: " + currentPlayer.nextPlayer.getPlayerName() + " Dann endlich: " + currentPlayer.nextPlayer.nextPlayer.getPlayerName());

            if(playerCard.isEmpty()) {
                    drawCards();
                    // wenn Spieler eine Karte legen kann, so wird sie aus seinen Karten gestrichen und auf den Stapel gepackt
            
            }
            else {
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
        do{
            currentPlayer.playerCards.add(getCardDeck().get(getCardDeck().size() - 1));
            getCardDeck().remove(getCardDeck().get(getCardDeck().size() - 1));
            i++;
        }while(i < drawCards);
        drawCards = 0;
        System.out.println("Ziehen ist abgeschlossen.");;

        if (getTopCard().contains("7")){
             play();
        }
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

//oder
        if (getTopCard().contains("7")) {
        // 7 legen       
         System.out.println("du musst 2 ziehen oder 7 legen " + currentPlayer.getPlayerName() + "\n");
            drawCards += 2;
            
            // 4 Ziehen
        } else if (getTopCard().equals("PK")) {
            System.out.println("du musst 4 ziehen" + currentPlayer.getPlayerName() + "\n");
            for (int i = 0; i <= 4; i++) {
                currentPlayer.playerCards.add(getCardDeck().get(getCardDeck().size() - 1));
                getCardDeck().remove(getCardDeck().get(getCardDeck().size() - 1));
            }
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
        return winner;
    }

    // aktuellen Spieler anzeigen lassen
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getPlayerObject(Object clientHandler) {
        return reihenfolge.getPlayerObject(clientHandler);
    }

    public ArrayList<String> getCardDeck(){
        return cardDeck;
    }

}

// [ist-Drann , [karten] , [Spielername , AnzahlKarten von Spieler] , Oberste
// Spielkarte]
