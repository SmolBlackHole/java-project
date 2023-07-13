package Server;

// Muss Handkarten auswerten können -> darf legen oder nicht

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import Server.Server.*;

public class Bot {

    private String username;
    private String newMessage;
    private String choose;
    private String spielerKarte;
    public ArrayList<String> playerCards = new ArrayList<>();
    public String topCard;
    public boolean isTurn = false;
    public Player player;

    public Bot(ClientHandler clientHandler){
        this.player = Server.game.getPlayerObject(clientHandler);
        this.username = player.getPlayerName();
        this.playerCards = player.getPlayerCards();
        if(Server.game.currentPlayer == player){
            Server.game.playerCard = "";
        }
    }

    public void createList(boolean isTurn, ArrayList<String> playerCards, String topCard){
        this.isTurn = isTurn;
        this.playerCards = playerCards;
        this.topCard = topCard;
        checkTurn(isTurn, playerCards, topCard);

    }

    // Checkt ob der Bot dran ist
    public void checkTurn(boolean isTurn, ArrayList<String> playerCards, String topCard) {
        if(isTurn) {
            int sleep = 3 + (int)(Math.random() * ((7 - 5) + 1));
            System.out.println("delay "+ sleep);
            try {
                TimeUnit.SECONDS.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Der Bot ist dran");
            canPlay(playerCards, topCard);
            Server.game.playerCard = spielerKarte;
        }
    }

    // Vergleicht, ob der Bot eine Karte legen kann
    public boolean isValidMove(String playerCard, String topCard) {
        if (topCard.charAt(0) == playerCard.charAt(0)) {
            return true;
        }
        if (topCard.charAt(1) == playerCard.charAt(1)) {
            return true;
        }
        return false;
    }
    
    // Vergleicht den zweiten Char für die specialCards
    public boolean isValidMoveSpecial(String playerCard, String topCard) {
        if (topCard.charAt(1) == playerCard.charAt(1)) {
            return true;
        }
        return false;
    }

    // Checkt, ob die topCard eine 7 oder 8 ist und ruft dann den Vergleich mit allen Handkarten auf
    public void canPlay(ArrayList<String> playerCards, String topCard) {
        /*
        if (topCard.contains("7") || topCard.contains("8")) {
            if (topCard.contains("7")) {
                for (String card : playerCards) {
                    if (isValidMoveSpecial(card, topCard)) {
                        spielerKarte = card;
                        System.out.println("Karte "+spielerKarte+" kann gelegt werden");
                        break;
                    }
                    spielerKarte = "";
                    System.out.println("keine Karte kann gelegt werden");
                    // Bot muss x Karten ziehen
                }
            }
            if (topCard.contains("8")) {
                for (String card : playerCards) {
                    if (isValidMoveSpecial(card, topCard)) {
                        spielerKarte = card;
                        System.out.println("Karte "+spielerKarte+" kann gelegt werden");
                        break;
                    }
                    spielerKarte = "";
                    System.out.println("keine Karte kann gelegt werden");
                    // Bot muss aussetzen
                }
            }
        } 
        */
        // Normaler Zug
        if (topCard.contains("7")) {
            for (String card : playerCards) {
                if (isValidMoveSpecial(card, topCard)) {
                    spielerKarte = card;
                    System.out.println("Karte "+spielerKarte+" kann gelegt werden");
                    break;
                }
            }
            Server.game.playerCard = spielerKarte; 
        }
        for (String cardelse : playerCards) {
            if (isValidMove(cardelse, topCard)) {
                spielerKarte = cardelse;
                System.out.println("Karte "+spielerKarte+" kann gelegt werden");
                break;
            }
            spielerKarte = "";
        }
        Server.game.playerCard = spielerKarte; 
    }

    public String getUsername (){
        return username;
    }
}
