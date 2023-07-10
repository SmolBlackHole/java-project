package GUI;

import Client.Client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CardGameUI {

    // Breite und Höhe einer Karte
    private static final int CARD_WIDTH = (int) (73 * 1.5);
    private static final int CARD_HEIGHT = (int) (97 * 1.5);
    //  Offset beim Karten verschieben (beim Hovern)
    private static final int HOVER_OFFSET = -20 * 2;
    // Anfangsposition der Karten
    private static final int INITIAL_X = 1800;
    private static final int INITIAL_Y = 650;
    //  Faktor, um die Karte beim Hovern zu vergrößern
    private static final double HOVER_SCALE_FACTOR = 1.5;
    // Pfad zum Hintergrundbild
    private static final String BACKGROUND_IMAGE_PATH = "src/GUI/Assets/Table.jpg";
    // Breite und Höhe des Fensters
    private static final int WindowWidth = 1440;
    private static final int WindowHeight = 900;
    // Liste zur Speicherung der Karten-Labels
    private List<JLabel> cardLabels = new ArrayList<>();
    // Array zur Speicherung der ursprünglichen Positionen der Karten
    private Point[] originalCardLocations;
    // Label zur Darstellung der vergrößerten Karte in der Mitte
    private JLabel enlargedCardLabel;
    private JFrame frame = new JFrame();
    private static ArrayList<String> karten;
    private String obersteSpielkarte;
    private int anzahlSpieler;
    private ArrayList<ArrayList<Object>> spieler;
    private boolean isInizialized = false;
    private boolean istDrann;

    public CardGameUI(String username, ArrayList<String> karten) {
        CardGameUI.karten = karten;
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                         UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                // Erzeugen des Kartendecks
                List<String> cardDeck = Card.getCardDeck();

                // JFrame-Objekt initialisieren
                frame = new JFrame();

                // Erstelle das Hauptfenster
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(WindowWidth, WindowHeight);

                frame.setTitle("Card Game - " + username);

                // Haupt-Panel mit Hintergrundbild
                JPanel mainPanel = new JPanel(null) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        ImageIcon backgroundIcon = new ImageIcon(BACKGROUND_IMAGE_PATH);
                        Image background = backgroundIcon.getImage();
                        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                    }
                };
                frame.setContentPane(mainPanel);

                // Erstellen der Karten-Labels und Hinzufügen zum Hauptpanel
                cardLabels = new ArrayList<>();
                enlargedCardLabel = createEnlargedCardLabel();

                int x = INITIAL_X;
                int y = INITIAL_Y;

                for (int i = cardDeck.size() - 1; i >= 0; i--) {
                    String card = cardDeck.get(i);
                    JLabel cardLabel = createCardLabel(card);
                    cardLabel.setBounds(x, y, CARD_WIDTH, CARD_HEIGHT);
                    mainPanel.add(cardLabel);
                    cardLabels.add(cardLabel);

                    x -= 30;
                }


                // Speichern der ursprünglichen Kartenpositionen
                originalCardLocations = new Point[cardLabels.size()];
                for (int i = 0; i < cardLabels.size(); i++) {
                    originalCardLocations[i] = cardLabels.get(i).getLocation();
                }

                // Hinzufügen des Labels für die vergrößerte Karte
                mainPanel.add(enlargedCardLabel);

                // Hinzufügen der MouseListener zu den Karten-Labels
                addMouseListeners(frame);

                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }



    // Methode zur Erstellung eines Karten-Labels
    private JLabel createCardLabel(String cardName) {
        JLabel cardLabel = new JLabel();
        ImageIcon imageIcon = new ImageIcon("src/GUI/Assets/" + cardName + ".png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
        ImageIcon scaledImageIcon = new ImageIcon(scaledImage);
        cardLabel.setIcon(scaledImageIcon);
        return cardLabel;
    }

    // Methode zur Erstellung des Labels für die vergrößerte Karte
    private JLabel createEnlargedCardLabel() {
        JLabel enlargedCardLabel = new JLabel();
        enlargedCardLabel.setVisible(false);
        return enlargedCardLabel;
    }

    // Methode zum Hinzufügen der MouseListener zu den Karten-Labels
    private void addMouseListeners(JFrame frame) {
        for (int i = 0; i < cardLabels.size(); i++) {
            int index = i;
            JLabel cardLabel = cardLabels.get(i);
            cardLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    moveCard(cardLabel, HOVER_OFFSET);
                    setComponentZOrder(frame, cardLabel, index + cardLabels.size());
                    showEnlargedCard(cardLabel.getIcon());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    moveCard(cardLabel, 0);
                    setComponentZOrder(frame, cardLabel, index);
                    hideEnlargedCard();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (enlargedCardLabel.isVisible()) {
                        hideEnlargedCard();
                    } else {
                        showEnlargedCard(cardLabel.getIcon());
                    }
                }
            });
            // Füge MouseMotionListener hinzu, um die Z-Reihenfolge der Karten zu aktualisieren
            cardLabel.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (index == cardLabels.size() - 1) {
                        // Setze die Z-Reihenfolge der letzten Karte auf die Vorderseite
                        setComponentZOrder(frame, cardLabel, index + cardLabels.size());
                    } else if (index == 0) {
                        // Setze die Z-Reihenfolge der ersten Karte auf die Rückseite
                        setComponentZOrder(frame, cardLabel, index);
                    }
                }
            });
        }
    }

    // Methode zum Verschieben einer Karte
    private void moveCard(JLabel cardLabel, int yOffset) {
        int cardIndex = cardLabels.indexOf(cardLabel);
        Point originalLocation = originalCardLocations[cardIndex];
        Point newLocation = new Point(originalLocation.x, originalLocation.y + yOffset);
        cardLabel.setLocation(newLocation);
    }

    // Methode zum Ändern der Z-Reihenfolge einer Komponente
    private void setComponentZOrder(JFrame frame, Component component, int position) {
        Container contentPane = frame.getContentPane();
        if (position < 0 || position >= contentPane.getComponentCount()) {
            return;
        }

        Component[] components = contentPane.getComponents();
        if (components[position] == component) {
            return;
        }

        contentPane.setComponentZOrder(component, position);
        contentPane.validate();
    }

    // Methode zum Anzeigen der vergrößerten Karte
    private void showEnlargedCard(Icon cardIcon) {
        enlargedCardLabel.setIcon(cardIcon);
        int enlargedWidth = (int) (CARD_WIDTH * HOVER_SCALE_FACTOR);
        int enlargedHeight = (int) (CARD_HEIGHT * HOVER_SCALE_FACTOR);
        enlargedCardLabel.setBounds(
                (WindowWidth - enlargedWidth) / 2,
                (WindowHeight - enlargedHeight) / 2,
                enlargedWidth,
                enlargedHeight
        );
        enlargedCardLabel.setVisible(true);
    }

    // Methode zum Ausblenden der vergrößerten Karte
    private void hideEnlargedCard() {
        enlargedCardLabel.setVisible(false);
    }


    public void setTitel(String s) {
        if (frame != null) {
            frame.setTitle(s);
        } else {
            System.out.println("WAS IST DEN HIER LOS?!?!");
        }
    }

    // Professionell von Vanessa geklauter Code (Testzwecke)
    public static class Card {
        private static final ArrayList<String> cardDeck = new ArrayList<>();

        public static void mixCards() {


            Collections.shuffle(cardDeck); // Mischen der Karten
        }

        public static ArrayList<String> getCardDeck() {
            mixCards();
            return cardDeck;
        }
    }

    public void renderHandCards(ArrayList<String> karten) {
        // Leeren Sie die Liste der Karten-Labels
        cardLabels.clear();

        // Holen Sie sich das Hauptpanel
        JPanel mainPanel = (JPanel) frame.getContentPane();

        // Berechnen Sie die Anfangsposition der Karten basierend auf der Anzahl der Karten
        int startX = INITIAL_X - (karten.size() - 1) * 30;
        int startY = INITIAL_Y - (karten.size() - 1) * 30;

        // Erstellen und Hinzufügen der Karten-Labels zum Hauptpanel
        for (int i = 0; i < karten.size(); i++) {
            String card = karten.get(i);
            JLabel cardLabel = createCardLabel(card);
            int x = startX + i * 30;
            int y = startY + i * 30;
            cardLabel.setBounds(x, y, CARD_WIDTH, CARD_HEIGHT);
            mainPanel.add(cardLabel);
            cardLabels.add(cardLabel);
        }

        frame.validate();
        frame.repaint();
    }

    private void handleGameData(boolean istDrann, ArrayList<String> karten, String obersteSpielkarte,
                                int anzahlSpieler, ArrayList<ArrayList<Object>> spieler) {
        // Aktualisieren Sie das UI mit den empfangenen Daten
        updateGameUI(istDrann, karten, obersteSpielkarte, anzahlSpieler, spieler);
    }

    private void updateGameUI(boolean istDrann, ArrayList<String> karten, String obersteSpielkarte,
                              int anzahlSpieler, ArrayList<ArrayList<Object>> spieler) {

        // Beispielaktualisierungen:
        System.out.println("Ist dran: " + istDrann);
        System.out.println("Karten: " + karten);
        System.out.println("Oberste Spielkarte: " + obersteSpielkarte);
        System.out.println("Anzahl Spieler: " + anzahlSpieler);
        System.out.println("Spieler: " + spieler);


        karten = Client.receiveKarten();


        // Rendern Sie die Handkarten
        renderHandCards(karten);
    }
}