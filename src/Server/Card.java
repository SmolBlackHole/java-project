package Server;

import java.util.ArrayList;
import java.util.Collections;

public class Card {
    private static ArrayList < String > cardDeck = new ArrayList < String > ();
    private static ArrayList < String > newDeck = new ArrayList < String > ();


    // Methode erzeugt alle Karten im Spiel und mischt diese
    public static void mixCards() {
        cardDeck.add("H7");
        cardDeck.add("H8");
        cardDeck.add("H9");
        cardDeck.add("HX");
        cardDeck.add("HB");
        cardDeck.add("HD");
        cardDeck.add("HK");
        cardDeck.add("HA");

        cardDeck.add("C7");
        cardDeck.add("C8");
        cardDeck.add("C9");
        cardDeck.add("CX");
        cardDeck.add("CB");
        cardDeck.add("CD");
        cardDeck.add("CK");
        cardDeck.add("CA");

        cardDeck.add("P7");
        cardDeck.add("P8");
        cardDeck.add("P9");
        cardDeck.add("PX");
        cardDeck.add("PB");
        cardDeck.add("PD");
        cardDeck.add("PK");
        cardDeck.add("PA");

        cardDeck.add("K7");
        cardDeck.add("K8");
        cardDeck.add("K9");
        cardDeck.add("KX");
        cardDeck.add("KB");
        cardDeck.add("KD");
        cardDeck.add("KK");
        cardDeck.add("KA");

        Collections.shuffle(cardDeck);
    }

    // Getter & Setter
    public static ArrayList < String > getCardDeck() {
        return cardDeck;
    }
}