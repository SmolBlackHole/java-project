package GUI;

import Client.Client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static Client.Client.Client.*;

public class CardGameUI {
    // Breite und Höhe einer Karte
    private static final int CARD_WIDTH = (int) (73 * 1.5);
    private static final int CARD_HEIGHT = (int) (97 * 1.5);
    //  Offset beim Karten verschieben (beim Hover)
    private static final int HOVER_OFFSET = -20 * 2;
    // Anfangsposition der Karten
    private static final int INITIAL_X = 1000;
    private static final int INITIAL_Y = 800;
    //  Faktor, um die Karte beim Hover zu vergrößern
    private static final double HOVER_SCALE_FACTOR = 1.5;
    // Pfad zum Hintergrundbild
    private static final String BACKGROUND_IMAGE_PATH = "src/GUI/Assets/Table.jpg";
    // Breite und Höhe des Fensters
    private static final int WindowWidth = 1440;
    private static final int WindowHeight = 900;
    private static ArrayList<String> karten;
    private static ArrayList<ArrayList> spieler;
    private ArrayList<String> obersteKarten; // ArrayList für die obersten Karten
    // Liste zur Speicherung der Karten-Labels
    private List<JLabel> cardLabels = new ArrayList<>();
    // Array zur Speicherung der ursprünglichen Positionen der Karten
    private Point[] originalCardLocations;
    // Label zur Darstellung der vergrößerten Karte in der Mitte
    private JLabel enlargedCardLabel;
    // Label zur Darstellung der obersten Karte
    private JLabel obersteKartenLabel;
    private static JFrame frame = new JFrame();
    private static final Object lock = new Object();

    public CardGameUI(String username, ArrayList<String> karten) {
        CardGameUI.karten = karten;
        obersteKarten = new ArrayList<>(); // Initialisierung der ArrayList für die obersten Karten
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                         UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

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

                // Erstellen der Karten-Labels und Hinzufügen zum Haupt panel
                cardLabels = new ArrayList<>();
                enlargedCardLabel = createEnlargedCardLabel();
                // Erstellen des Labels für die oberste Spielkarte
                obersteKartenLabel = new JLabel();
                obersteKartenLabel.setVisible(false);
                mainPanel.add(obersteKartenLabel);

                // Erstellen der Karten-Labels und Hinzufügen zum Haupt panel
                int x = INITIAL_X;

                for (int i = karten.size() - 1; i >= 0; i--) {
                    String card = karten.get(i);
                    JLabel cardLabel = createCardLabel(card);
                    cardLabel.setBounds(x, INITIAL_Y, CARD_WIDTH, CARD_HEIGHT);
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
                // Hinzufügen des Labels für die oberste Karte
                mainPanel.add(obersteKartenLabel);

                // Hinzufügen der MouseListener zu den Karten-Labels
                addMouseListeners();

                // Karten rendern
                renderHandCards(karten);

                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                listenForGameInfo();
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
    // Methode zum Hinzufügen der MouseListener zu einem Karten-Label
    private void addMouseListenerToCardLabel(JLabel cardLabel, int index) {
        cardLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                moveCard(cardLabel, HOVER_OFFSET);
                setComponentZOrder(frame, cardLabel, index + cardLabels.size());
                //showEnlargedCard(cardLabel.getIcon());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                moveCard(cardLabel, 0);
                setComponentZOrder(frame, cardLabel, index);
                //hideEnlargedCard();
            }


            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    String selectedCard = karten.get(index);
                    if (istDrann){
                        dataToSend("F4->3GA" + selectedCard);
                        removeCardLabel(cardLabel);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Du bist nicht dran!");
                    }
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

    private void removeCardLabel(JLabel cardLabel) {
        cardLabels.remove(cardLabel);
        frame.getContentPane().remove(cardLabel);
        frame.validate();
        frame.repaint();
    }

    // Methode zum Hinzufügen der MouseListener zu den Karten-Labels
    private void addMouseListeners() {
        for (int i = 0; i < cardLabels.size(); i++) {
            JLabel cardLabel = cardLabels.get(i);
            addMouseListenerToCardLabel(cardLabel, i);
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

        // Aktualisiere die Z-Reihenfolge aller Karten-Labels
        for (int i = 0; i < cardLabels.size(); i++) {
            JLabel cardLabel = cardLabels.get(i);
            int zIndex = i < position ? i : i + 1; // Erhöhe die Z-Reihenfolge um 1 für Karten vor der Zielposition
            contentPane.setComponentZOrder(cardLabel, zIndex);
        }

        contentPane.validate();
    }

    // Methode zum Aktualisieren der Z-Reihenfolge der Karten
    private void setCardZOrder() {
        Container contentPane = frame.getContentPane();
        for (int i = 0; i < cardLabels.size(); i++) {
            JLabel cardLabel = cardLabels.get(i);
            contentPane.setComponentZOrder(cardLabel, i);
        }
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

    // Methode zum Rendern der Handkarten
    public void renderHandCards(ArrayList<String> karten) {
        synchronized (lock) {
            // Leere die Liste der Karten-Labels
            cardLabels.clear();

            // Holt sich das Haupt panel
            JPanel mainPanel = (JPanel) frame.getContentPane();

            // Anzahl der Karten
            int cardCount = karten.size();

            // Breite des sichtbaren Bereichs abzüglich der Kartenbreite
            int visibleWidth = WindowWidth - CARD_WIDTH;

            // Berechnung des horizontalen Abstands zwischen den Karten basierend auf der Anzahl der Karten
            int horizontalSpacing = Math.min(visibleWidth / (cardCount - 1), 30);

            // Anfangsposition der Karten
            int startX = (WindowWidth - (CARD_WIDTH + (cardCount - 1) * horizontalSpacing)) / 2;
            int y = istDrann ? INITIAL_Y - 150 : INITIAL_Y; // Anpassung der Y-Position

            // Erstellen und Hinzufügen der Karten-Labels zum Haupt panel
            for (int i = 0; i < cardCount; i++) {
                String card = karten.get(i);
                JLabel cardLabel = createCardLabel(card);
                int x = startX + i * horizontalSpacing;
                cardLabel.setBounds(x, y, CARD_WIDTH, CARD_HEIGHT);
                mainPanel.add(cardLabel);
                cardLabels.add(0, cardLabel); // Füge die Karte am Anfang der Liste hinzu
            }

            // Speichern der ursprünglichen Kartenpositionen
            originalCardLocations = new Point[cardLabels.size()];
            for (int i = 0; i < cardLabels.size(); i++) {
                originalCardLocations[i] = cardLabels.get(i).getLocation();
            }

            // Füge MouseListener hinzu
            frame.validate();
            frame.repaint();

            setCardZOrder();
            addMouseListeners();

            frame.validate();
            frame.repaint();
        }
    }

    // Methode zum Rendern der obersten Karte
    private void renderObersteKarte() {
        if (!obersteKarten.isEmpty()) {
            String obersteKarte = obersteKarten.get(obersteKarten.size() - 1);
            obersteKartenLabel.setIcon(createCardIcon(obersteKarte));
            obersteKartenLabel.setBounds(
                    (WindowWidth - CARD_WIDTH) / 2,
                    (WindowHeight - CARD_HEIGHT) / 2,
                    CARD_WIDTH,
                    CARD_HEIGHT
            );
            obersteKartenLabel.setVisible(true);
        } else {
            obersteKartenLabel.setVisible(false);
        }
    }

    // Methode zum Erstellen eines ImageIcon für eine Karte
    private ImageIcon createCardIcon(String cardName) {
        ImageIcon imageIcon = new ImageIcon("src/GUI/Assets/" + cardName + ".png");
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    public void setTitel(String s) {
        if (frame != null) {
            frame.setTitle(s);
        } else {
            System.out.println("WAS IST DEN HIER LOS?!?!");
        }
    }

    public static void dataToSend(String data) {
        synchronized (lock) {
            try {
                BufferedWriter writer = Client.getBufferedWriter();
                writer.write(data);
                writer.newLine();
                writer.flush();
                frame.repaint();
            } catch (IOException e) {
                // Handle IOException
            }
        }
    }

    private void listenForGameInfo() {
        new Thread(() -> {
            ArrayList<String> PlatzhalterString = new ArrayList<>();
            boolean PlatzhalterBoolean = false;
            String obersteSpielKarte = "";
            int spielerAnazhl = 0;
            String gewinner = "";
            boolean vorherIstDrann = false; // Vorheriger Wert von istDrann speichern
            ArrayList<String> PlatzhalterSpieler = new ArrayList<>();

            while (true) {
                synchronized (lock){
                karten = Client.receiveKarten();
                istDrann = Client.receiveTurn();
                obersteSpielKarte = Client.receiveObersteSpielkarte();
                spielerAnazhl = Client.receiveAnzahlSpieler();
                gewinner = Client.receiveGewinner();
                spieler = Client.receiveSpielerListe();

                if (!Objects.equals(karten, PlatzhalterString)) {
                    renderHandCards(karten);
                    PlatzhalterString = karten;
                    System.out.println("Karten empfangen " + karten);
                    addMouseListeners();
                }

                if (istDrann != vorherIstDrann) { // Prüfen, ob sich der Wert von istDrann geändert hat
                    if (istDrann) {
                        System.out.println("Du bist dran");
                    } else {
                        System.out.println("Du bist nicht dran");
                    }
                    vorherIstDrann = istDrann; // Aktuellen Wert von istDrann als vorherigen Wert speichern
                }

                if (!Objects.equals(obersteSpielKarte, Client.receiveObersteSpielkarte())) {
                    obersteSpielKarte = Client.receiveObersteSpielkarte();
                    obersteKarten.add(obersteSpielKarte); // Füge die neue oberste Karte zur ArrayList hinzu
                    renderObersteKarte(); // Rendere die oberste Karte

                    //System.out.println("Oberste Karte empfangen " + obersteSpielKarte);
                }

                if (spielerAnazhl != Client.receiveAnzahlSpieler()) {
                    spielerAnazhl = Client.receiveAnzahlSpieler();

                    //System.out.println("Anzahl Spieler empfangen " + spielerAnazhl);
                }

                if (!Objects.equals(gewinner, Client.receiveGewinner())) {
                    gewinner = Client.receiveGewinner();
                    // System.out.println("Gewinner empfangen " + gewinner);
                }

                if (!Objects.equals(spieler, Client.receiveSpielerListe())) {
                    spieler = Client.receiveSpielerListe();
                    System.out.println("Spieler empfangen " + spieler);
                }
            }}
        }).start();
    }
}
