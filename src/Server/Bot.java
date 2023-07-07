package Server;

// Muss Handkkarten auswerten können -> darf legen oder nicht

import java.util.ArrayList;
import Server.Server.*;

import Server.Server.ClientHandler;

public class Bot {

    private String username;
    private String newMessage;
    private String choose;
    private String spielerKarte;
    public ArrayList<String> playerCards = new ArrayList<>();
    public String topCard;
    public boolean isTurn;

    public Bot(ClientHandler clientHandler){
        Player player = Server.game.getPlayerObject(clientHandler);
        this.username = player.getPlayerName();
        this.playerCards = player.getPlayerCards();
    }

    public void createList(boolean isTurn, ArrayList<String> playerCards, String topCard){
        this.isTurn = isTurn;
        this.playerCards = playerCards;
        this.topCard = topCard;


    }

    // Checkt ob der Bot dran ist
    public void checkTurn(boolean isTurn, ArrayList<String> playerCards, String topCard) {
        if(isTurn == true) {
            System.out.println("Der Bot ist dran");
            canPlay(playerCards, topCard);
        }
    }

    // Vergleicht ob der Bot eine Karte legen kann
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

    // Checkt ob die topCard eine 7 oder 8 ist und ruft dann den Vergleich mit allen Handkarten auf
    public void canPlay(ArrayList<String> playerCards, String topCard) {
        if (topCard.contains("7") || topCard.contains("8")) {
            if (topCard.contains("7")) {
                for (String card : playerCards) {
                    if (isValidMoveSpecial(card, topCard)) {
                        spielerKarte = card;
                    System.out.println("Karte "+spielerKarte+" kann gelegt werden");
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
                    }
                    spielerKarte = "";
                    System.out.println("keine Karte kann gelegt werden");
                    // Bot muss aussetzen
                }
            }
        } 
        // Normaler Zug
        else {
            for (String card : playerCards) {
                if (isValidMove(card, topCard)) {
                    spielerKarte = card;
                    System.out.println("Karte "+spielerKarte+" kann gelegt werden");
                }
            }
            spielerKarte = "";
            System.out.println("keine Karte kann gelegt werden");
            // Bot muss eine Karte ziehen
        }
        Server.game.playerCard = spielerKarte; 
    }

    // Setter und Getter

}
