package GUI;

import Client.Client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CardGameUI {
    // Breite und Höhe einer Karte
    private static final int CARD_WIDTH = (int) (73 * 1.5);
    private static final int CARD_HEIGHT = (int) (97 * 1.5);
    //  Offset beim Karten verschieben (beim Hover)
    private static final int HOVER_OFFSET = -20 * 2;
    // Anfangsposition der Karten
    private static final int INITIAL_X = 1000;
    private static final int INITIAL_Y = 650;
    //  Faktor, um die Karte beim Hover zu vergrößern
    private static final double HOVER_SCALE_FACTOR = 1.5;
    // Pfad zum Hintergrundbild
    private static final String BACKGROUND_IMAGE_PATH = "src/GUI/Assets/Table.jpg";
    // Breite und Höhe des Fensters
    private static final int WindowWidth = 1440;
    private static final int WindowHeight = 900;
    private static ArrayList<String> karten;
    // Liste zur Speicherung der Karten-Labels
    private List<JLabel> cardLabels = new ArrayList<>();
    // Array zur Speicherung der ursprünglichen Positionen der Karten
    private Point[] originalCardLocations;
    // Label zur Darstellung der vergrößerten Karte in der Mitte
    private JLabel enlargedCardLabel;
    private JFrame frame = new JFrame();

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

                // Erstellen der Karten-Labels und Hinzufügen zum Hauptpanel
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


    public void setTitel(String s) {
        if (frame != null) {
            frame.setTitle(s);
        } else {
            System.out.println("WAS IST DEN HIER LOS?!?!");
        }
    }

    // Methode zum Rendern der Handkarten
    // Methode zum Rendern der Handkarten
    public void renderHandCards(ArrayList<String> karten) {
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
        int y = INITIAL_Y;

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


    private void listenForGameInfo() {
        new Thread(() -> {
            ArrayList<String> PlatzhalterString = new ArrayList<>();
            while (true) {
                karten = Client.receiveKarten();

                if (!Objects.equals(karten, PlatzhalterString)) {
                    renderHandCards(karten); // Empfangene Karten zur Render Queue hinzufügen
                    PlatzhalterString = karten;
                    System.out.println("Karten empfangen " + karten);
                    addMouseListeners(); // Füge MouseListener hinzu
                }
            }
        }).start();
    }
}