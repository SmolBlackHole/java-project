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

import static Client.Client.Client.username;

public class CardGameUI {
    // Breite und Höhe einer Karte
    private static final int CARD_WIDTH = (int) (73 * 1.5);
    private static final int CARD_HEIGHT = (int) (97 * 1.5);
    //  Offset beim Karten verschieben (beim Hover)
    private static final int HOVER_OFFSET = -20 * 2;
    // Anfangsposition der Karten
    private static final int INITIAL_X = 1000;
    private static final int INITIAL_Y = 800;
    private static final int OTHERPLAYER_INITIAL_X = 1000;
    private static final int OTHERPLAYER_INITIAL_Y = 50;
    // Pfad zum Hintergrundbild
    private static final String BACKGROUND_IMAGE_PATH = "src/GUI/Assets/Table.jpg";
    // Breite und Höhe des Fensters
    private static final int WindowWidth = 1440;
    private static final int WindowHeight = 900;
    private static final Object lock = new Object();
    static ArrayList<ArrayList> spieler = new ArrayList<>();
    private static ArrayList<String> karten;
    private static JFrame frame = new JFrame();
    private final ArrayList<String> obersteKarten; // ArrayList für die obersten Karten
    // Liste zur Speicherung der Karten-Labels
    private List<JLabel> cardLabels = new ArrayList<>();
    // Array zur Speicherung der ursprünglichen Positionen der Karten
    private Point[] originalCardLocations;
    // Label zur Darstellung der vergrößerten Karte in der Mitte
    private JLabel enlargedCardLabel;
    // Label zur Darstellung der obersten Karte
    private JLabel obersteKartenLabel;
    private boolean istDrann;
    private JLabel ziehStapelLabel;
    private String ziehStapelBildPath;


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
                // Erstellen des Labels für die oberste Spielkarte
                obersteKartenLabel = new JLabel();
                obersteKartenLabel.setVisible(false);
                mainPanel.add(obersteKartenLabel);
                // Erstellen des Labels für den Zieh stapel
                ziehStapelLabel = createZiehStapelLabel();
                mainPanel.add(ziehStapelLabel);

                if (ziehStapelBildPath == null) {
                    ziehStapelBildPath = getZiehStapelBildPath();
                }

                // ImageIcon für den Zieh stapel erstellen
                ImageIcon ziehStapelIcon = new ImageIcon(ziehStapelBildPath);
                Image ziehStapelImage = ziehStapelIcon.getImage();
                Image scaledZiehStapelImage = ziehStapelImage.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
                ImageIcon scaledZiehStapelIcon = new ImageIcon(scaledZiehStapelImage);

                ziehStapelLabel.setIcon(scaledZiehStapelIcon);
                ziehStapelLabel.setBounds(
                        (WindowWidth - CARD_WIDTH) / 2 - 200, // Anpassen der Position des Zieh stapels
                        (WindowHeight - CARD_HEIGHT) / 2,
                        CARD_WIDTH,
                        CARD_HEIGHT
                );
                ziehStapelLabel.setVisible(true);

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

                // Hinzufügen des Labels für die oberste Karte
                mainPanel.add(obersteKartenLabel);

                // Hinzufügen der MouseListener zu den Karten-Labels
                addMouseListeners();

                // Karten rendern
                renderHandCards(karten);
                renderObersteKarte();
                renderOtherPlayers(spieler);

                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                listenForGameInfo();
            }
        });
    }

    public static void dataToSend(String data) {
        synchronized (lock) {
            try {
                BufferedWriter writer = Client.getBufferedWriter();
                writer.write(data);
                writer.newLine();
                writer.flush();
                frame.validate();
                frame.repaint();
            } catch (IOException e) {
                // Handle IOException
            }
        }
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

    // Methode zum Hinzufügen des MouseListeners zu den Karten-Labels
    private void addMouseListenerToCardLabel(JLabel cardLabel, int index) {
        cardLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                moveCard(cardLabel, HOVER_OFFSET);
                setComponentZOrder(frame, cardLabel, index + 1); // Ändere die Z-Reihenfolge auf den höchsten Wert
                //showEnlargedCard(cardLabel.getIcon());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                moveCard(cardLabel, 0);
                setComponentZOrder(frame, cardLabel, index); // Ändere die Z-Reihenfolge auf den ursprünglichen Index
                //hideEnlargedCard();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int selectedCardIndex = cardLabels.size() - 1 - index; // Index basierend auf der tatsächlichen Reihenfolge
                    if (istDrann) {
                        String selectedCard = karten.get(selectedCardIndex);
                        String obersteSpielKarte = obersteKarten.get(obersteKarten.size() - 1);
                        if (isCardPlayable(selectedCard, obersteSpielKarte)) {
                            dataToSend("F4->3GA" + selectedCard);
                            System.out.println("Gelegte Karte: " + selectedCard);
                            removeCardLabel(cardLabel);
                            frame.validate();
                            frame.repaint();
                            renderHandCards(karten); // Karten erneut rendern
                            renderObersteKarte();
                            renderOtherPlayers(spieler);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Diese Karte darf nicht gelegt werden!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Du bist nicht dran!");
                    }
                }
            }

            private boolean isCardPlayable(String selectedCard, String obersteSpielKarte) {
                String selectedSymbol = selectedCard.substring(0, 1);
                String selectedValue = selectedCard.substring(1);
                String obersteSymbol = obersteSpielKarte.substring(0, 1);
                String obersteValue = obersteSpielKarte.substring(1);

                return selectedSymbol.equals(obersteSymbol) || selectedValue.equals(obersteValue);
            }
        });

        // Füge MouseMotionListener hinzu, um die Z-Reihenfolge der Karten zu aktualisieren
        cardLabel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int reversedIndex = cardLabels.size() - 1 - index; // Umkehren des Index
                if (reversedIndex == cardLabels.size() - 1) {
                    // Setze die Z-Reihenfolge der letzten Karte auf die Vorderseite
                    setComponentZOrder(frame, cardLabel, reversedIndex + cardLabels.size());
                } else if (reversedIndex == 0) {
                    // Setze die Z-Reihenfolge der ersten Karte auf die Rückseite
                    setComponentZOrder(frame, cardLabel, reversedIndex);
                }
            }
        });
    }

    // Methode zum Hinzufügen des MouseListeners zum Zieh stapel-Label
    private void addMouseListenerToZiehStapelLabel(JLabel ziehStapelLabel) {
        ziehStapelLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (istDrann) {
                        // Datenversand, nur wenn der Spieler dran ist
                        dataToSend("Bh7.|+e");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Du bist nicht dran!");
                    }
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
        for (int i = cardLabels.size() - 1; i >= 0; i--) {
            JLabel cardLabel = cardLabels.get(i);
            addMouseListenerToCardLabel(cardLabel, i);
        }
    }

    // Methode zum Verschieben einer Karte
    private void moveCard(JLabel cardLabel, int yOffset) {
        int cardIndex = cardLabels.indexOf(cardLabel);

        if (cardIndex < 0 || cardIndex >= originalCardLocations.length) {
            return;
        }

        Point originalLocation = originalCardLocations[cardIndex];
        Point newLocation = new Point(originalLocation.x, originalLocation.y + yOffset);

        // Prüfe, ob der Spieler an der Reihe ist und die Karte nach oben bewegt werden soll
        if (istDrann && yOffset < 0) {
            newLocation = new Point(originalLocation.x, originalLocation.y + yOffset);
        }

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

        contentPane.remove(component); // Entferne die Komponente aus dem Container
        contentPane.add(component, position); // Füge die Komponente an der gewünschten Position hinzu

        // Aktualisiere die Z-Reihenfolge aller Karten-Labels
        for (int i = 0; i < cardLabels.size(); i++) {
            JLabel cardLabel = cardLabels.get(i);
            int zIndex = i < position ? i : i + 1; // Erhöhe die Z-Reihenfolge um 1 für Karten vor der Zielposition
            contentPane.setComponentZOrder(cardLabel, zIndex);
        }

        contentPane.validate();
        contentPane.repaint();
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

    // Methode zum Rendern der Handkarten
    public void renderHandCards(ArrayList<String> karten) {
        synchronized (lock) {
            // Holt sich das Haupt panel
            JPanel mainPanel = (JPanel) frame.getContentPane();

            // Entferne die alten Karten-Labels aus dem Haupt panel und dem cardLabels-Array
            for (JLabel cardLabel : cardLabels) {
                mainPanel.remove(cardLabel);
            }
            cardLabels.clear();

            // Anzahl der Karten
            int cardCount = karten.size();

            // Breite des sichtbaren Bereichs abzüglich der Kartenbreite
            int visibleWidth = WindowWidth - CARD_WIDTH;

            // Berechnung des horizontalen Abstands zwischen den Karten basierend auf der Anzahl der Karten
            int horizontalSpacing;
            if (cardCount > 1) {
                horizontalSpacing = Math.min(visibleWidth / (cardCount - 1), 30);
            } else {
                horizontalSpacing = visibleWidth; // Falls nur eine Karte vorhanden ist, nimm die gesamte verfügbare Breite
            }

            // Anfangsposition der Karten
            int startX = (WindowWidth - (CARD_WIDTH + (cardCount - 1) * horizontalSpacing)) / 2;

            int y = istDrann ? INITIAL_Y - 150 : INITIAL_Y; // Y-Position basierend auf der Spielerposition

            // Erstellen und Hinzufügen der Karten-Labels zum Haupt panel
            for (int i = cardCount - 1; i >= 0; i--) {
                String card = karten.get(cardCount - 1 - i);
                JLabel cardLabel = createCardLabel(card);
                int x = startX + (cardCount - 1 - i) * horizontalSpacing;
                cardLabel.setBounds(x, y, CARD_WIDTH, CARD_HEIGHT);
                mainPanel.add(cardLabel);
                cardLabels.add(0, cardLabel); // Füge die Karte am Anfang der Liste hinzu
            }

            setCardZOrder();
            addMouseListeners();

            frame.validate();
            frame.repaint();

            // Speichern der ursprünglichen Kartenpositionen
            originalCardLocations = new Point[cardLabels.size()];
            for (int i = 0; i < cardLabels.size(); i++) {
                originalCardLocations[i] = cardLabels.get(i).getLocation();
            }

            // Benutzername anzeigen
            JLabel usernameLabel = new JLabel(username);
            usernameLabel.setBounds(INITIAL_X - 100, INITIAL_Y, 200, 20);
            usernameLabel.setForeground(Color.WHITE);
            mainPanel.add(usernameLabel);
        }
    }

    private void renderOtherPlayers(ArrayList<ArrayList> spieler) {
        synchronized (lock) {
            Container contentPane = frame.getContentPane();
            int numPlayers = spieler.size();

            // Entferne vorhandene Karten der anderen Spieler
            Component[] components = contentPane.getComponents();
            for (Component component : components) {
                if (component instanceof JPanel && component.getName() != null && component.getName().startsWith("otherPlayer")) {
                    contentPane.remove(component);
                }
            }

            // Berechne die Position der Karten der anderen Spieler basierend auf der Fensterbreite
            int cardSpacing = (WindowWidth - OTHERPLAYER_INITIAL_X * 2 - CARD_WIDTH) / (numPlayers - 1);
            int x = OTHERPLAYER_INITIAL_X;

            ArrayList<ArrayList> otherPlayers = filterOtherPlayers(spieler);

            for (ArrayList playerInfo : otherPlayers) {
                String playerName = (String) playerInfo.get(0);
                int numCards = (int) playerInfo.get(1);
                boolean isCurrentPlayer = (boolean) playerInfo.get(2);

                int visibleHeight = WindowHeight - CARD_HEIGHT;

                int horizontalSpacing;
                if (numCards > 1) {
                    horizontalSpacing = Math.min(visibleHeight / (numCards - 1), 30);
                } else {
                    horizontalSpacing = visibleHeight; // Falls nur eine Karte vorhanden ist, nimm die gesamte verfügbare Höhe
                }

                JPanel playerPanel = new JPanel();
                playerPanel.setName("otherPlayer_" + playerName); // Setze einen Namen für das Panel, um es später zu identifizieren
                playerPanel.setOpaque(false);
                playerPanel.setLayout(null);

                // Wähle das passende Asset-Bild basierend auf dem Index
                int assetIndex = otherPlayers.indexOf(playerInfo) % 4 + 1; // Modulo-Operator, um den Index im Bereich von 1 bis 4 zu halten
                String assetPath = "src/GUI/Assets/Back" + assetIndex + ".png";
                ImageIcon assetIcon = new ImageIcon(assetPath);
                Image assetImage = assetIcon.getImage();
                Image scaledAssetImage = assetImage.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
                ImageIcon scaledAssetIcon = new ImageIcon(scaledAssetImage);

                for (int i = 0; i < numCards; i++) {
                    JLabel cardLabel = new JLabel(scaledAssetIcon);
                    cardLabel.setBounds(i * horizontalSpacing, 0, CARD_WIDTH, CARD_HEIGHT);
                    playerPanel.add(cardLabel);
                }

                // Füge den Text für den Benutzer hinzu
                JLabel playerNameLabel = new JLabel(playerName + " (" + numCards + ")");
                playerNameLabel.setBounds(0, CARD_HEIGHT, CARD_WIDTH + (numCards - 1) * horizontalSpacing, 20);
                playerNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
                playerNameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                playerNameLabel.setForeground(Color.WHITE); // Textfarbe auf Weiß setzen
                playerPanel.add(playerNameLabel);

                playerPanel.setBounds(x, OTHERPLAYER_INITIAL_Y, CARD_WIDTH + (numCards - 1) * horizontalSpacing, CARD_HEIGHT + 20); // Höhe um 20 erhöhen, um Platz für den Text zu schaffen
                contentPane.add(playerPanel);

                x += cardSpacing;
            }

            setCardZOrder();

            frame.validate();
            frame.repaint();
        }
    }

    private ArrayList<ArrayList> filterOtherPlayers(ArrayList<ArrayList> spieler) {
        ArrayList<ArrayList> otherPlayers = new ArrayList<>();

        for (ArrayList playerInfo : spieler) {
            String playerName = (String) playerInfo.get(0);
            if (!playerName.equals(username)) {
                otherPlayers.add(playerInfo);
            }
        }

        return otherPlayers;
    }

    // Methode zum Rendern der obersten Karte und des Zieh stapels
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
            frame.getContentPane().add(obersteKartenLabel);
        } else {
            obersteKartenLabel.setVisible(false);
        }

        // Pfad zum Zieh stapel-Bild erhalten
        String ziehStapelBildPath = getZiehStapelBildPath();

        // ImageIcon für den Zieh stapel erstellen
        ImageIcon ziehStapelIcon = new ImageIcon(ziehStapelBildPath);
        Image ziehStapelImage = ziehStapelIcon.getImage();
        Image scaledZiehStapelImage = ziehStapelImage.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
        ImageIcon scaledZiehStapelIcon = new ImageIcon(scaledZiehStapelImage);

        ziehStapelLabel.setIcon(scaledZiehStapelIcon);
        ziehStapelLabel.setBounds(
                (WindowWidth - CARD_WIDTH) / 2 - 200, // Anpassen der Position des Zieh stapels
                (WindowHeight - CARD_HEIGHT) / 2,
                CARD_WIDTH,
                CARD_HEIGHT
        );
        ziehStapelLabel.setVisible(true);
    }

    // Methode zum Abrufen des Pfads zum Zieh stapel-Bild
    private String getZiehStapelBildPath() {
        if (ziehStapelBildPath != null) {
            return ziehStapelBildPath; // Rückgabe des zuvor ausgewählten Bildpfads
        }

        String[] ziehStapelBilder = {
                "Back1.png",
                "Back2.png",
                "Back3.png",
                "Back4.png"
        };
        String ziehStapelBild = ziehStapelBilder[(int) (Math.random() * ziehStapelBilder.length)];
        ziehStapelBildPath = "src/GUI/Assets/" + ziehStapelBild;
        return ziehStapelBildPath;
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

    // Methode zum Erstellen des Labels für den Zieh stapel
    private JLabel createZiehStapelLabel() {
        JLabel ziehStapelLabel = new JLabel();
        ziehStapelLabel.setVisible(false);
        addMouseListenerToZiehStapelLabel(ziehStapelLabel); // Füge den MouseListener hinzu
        return ziehStapelLabel;
    }

    private void listenForGameInfo() {
        new Thread(() -> {
            ArrayList<String> placeholderString = new ArrayList<>();
            String obersteSpielKarte = "";
            int spielerAnzahl = 0;
            String gewinner = "";
            boolean vorherIstDrann = false; // Vorheriger Wert von istDrann speichern

            while (true) {
                synchronized (lock) {
                    karten = Client.receiveKarten();
                    boolean aktuellerIstDrann = Client.receiveTurn();
                    obersteSpielKarte = Client.receiveObersteSpielkarte();
                    spielerAnzahl = Client.receiveAnzahlSpieler();
                    gewinner = Client.receiveGewinner();
                    spieler = Client.receiveSpielerListe();

                    if (!Objects.equals(karten, placeholderString)) {
                        renderHandCards(karten);
                        placeholderString = karten;
                        //System.out.println("Karten empfangen " + karten);
                        addMouseListeners();
                    }

                    if (aktuellerIstDrann != vorherIstDrann) { // Prüfen, ob sich der Wert von istDrann geändert hat
                        istDrann = aktuellerIstDrann; // Aktuellen Wert von istDrann zuweisen
                        vorherIstDrann = aktuellerIstDrann; // Aktuellen Wert von istDrann als vorherigen Wert speichern
                        renderObersteKarte();
                        renderHandCards(karten);
                        renderOtherPlayers(spieler);
                    }

                    frame.validate();
                    frame.repaint();

                    if (!Objects.equals(obersteSpielKarte, Client.receiveObersteSpielkarte())) {
                        obersteSpielKarte = Client.receiveObersteSpielkarte();
                        obersteKarten.add(obersteSpielKarte); // Füge die neue oberste Karte zur ArrayList hinzu
                        System.out.println("Oberste Karte erhalten: " + obersteSpielKarte);
                        if (obersteKarten.size() > 5) {
                            obersteKarten.remove(0);
                            System.out.println("ObersteKarten zwischenspeicher: "+obersteKarten);
                            renderObersteKarte();
                        }
                        renderObersteKarte(); // Rendere die oberste Karte
                    }

                    if (spielerAnzahl != Client.receiveAnzahlSpieler()) {
                        spielerAnzahl = Client.receiveAnzahlSpieler();
                    }

                    if (!Objects.equals(gewinner, Client.receiveGewinner())) {
                        gewinner = Client.receiveGewinner();
                        System.out.println("Gewinner empfangen " + gewinner);
                    }

                    if (!Objects.equals(spieler, Client.receiveSpielerListe())) {
                        spieler = Client.receiveSpielerListe();
                        System.out.println("Spieler empfangen " + spieler);

                        List<Object> ownPlayerInfo = getOwnPlayerInfo(spieler, username);
                        if (ownPlayerInfo != null) {
                            istDrann = (boolean) ownPlayerInfo.get(2);
                        }

                        renderHandCards(karten);
                        renderOtherPlayers(spieler);
                        renderObersteKarte();
                    }

                    frame.validate();
                    frame.repaint();
                }
            }
        }).start();
    }

    private List<Object> getOwnPlayerInfo(ArrayList<ArrayList> spieler, String username) {
        for (ArrayList playerInfo : spieler) {
            String playerName = (String) playerInfo.get(0);
            boolean isCurrentPlayer = (boolean) playerInfo.get(2);
            if (playerName.equals(username)) {
                return isCurrentPlayer ? playerInfo : null;
            }
        }
        return null;
    }
}