package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;

public class CardGameUI {

    public CardGameUI() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                         UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                Turn.createPlayer("127.0.0.1", "Player 1", null);
                Turn.createPlayer("127.0.0.1", "Player 2", null);
                Turn.createPlayer("127.0.0.1", "Player 3", null);
                Turn.connectFirstAndLast();

                List<Turn.Hand> players = new ArrayList<>(3);
                Turn.Player temp = Turn.firstPlayer;
                do {
                    players.add(new Turn.Hand(temp));
                    temp = temp.nextPlayer;
                } while (temp != Turn.firstPlayer);

                JFrame frame = new JFrame("Card Game");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new GamePane(players));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        new CardGameUI();
    }

    public static class GamePane extends JPanel {

        private final List<Turn.Hand> players;

        private final Map<Turn.Card, Rectangle> mapCards;

        private Turn.Card selected;

        public GamePane(List<Turn.Hand> players) {
            this.players = players;
            mapCards = new HashMap<>(players.size() * 5);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (selected != null) {
                        Rectangle bounds = mapCards.get(selected);
                        bounds.y += 20;
                        repaint();
                    }
                    selected = null;
                    for (Turn.Card card : players.get(0).reveresed()) {
                        Rectangle bounds = mapCards.get(card);
                        if (bounds.contains(e.getPoint())) {
                            selected = card;
                            bounds.y -= 20;
                            repaint();
                            break;
                        }
                    }
                }
            });

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    invalidate();
                }
            });
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, 400);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            mapCards.clear();
            Turn.Hand hand = players.get(0);
            int cardHeight = (getHeight() - 20) / 3;
            int cardWidth = (int) (cardHeight * 0.6);
            int xDelta = cardWidth / 2;
            int xPos = (int) ((getWidth() / 2) - (cardWidth * (hand.size() / 4.0)));
            int yPos = (getHeight() - 20) - cardHeight;
            for (Turn.Card card : hand.cards()) {
                Rectangle bounds = new Rectangle(xPos, yPos, cardWidth, cardHeight);
                mapCards.put(card, bounds);
                xPos += xDelta;
            }
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            Turn.Hand hand = players.get(0);
            for (Turn.Card card : hand.cards) {
                Rectangle bounds = mapCards.get(card);
                System.out.println(bounds);
                if (bounds != null) {
                    g2d.setColor(Color.WHITE);
                    g2d.fill(bounds);
                    g2d.setColor(Color.BLACK);
                    g2d.draw(bounds);
                    Graphics2D copy = (Graphics2D) g2d.create();
                    paintCard(copy, card, bounds);
                    copy.dispose();
                }
            }
            g2d.dispose();
        }

        protected void paintCard(Graphics2D g2d, Turn.Card card, Rectangle bounds) {
            g2d.translate(bounds.x + 5, bounds.y + 5);
            g2d.setClip(0, 0, bounds.width - 5, bounds.height - 5);

            String text = card.getFace().getValue() + card.getSuit().getValue();
            FontMetrics fm = g2d.getFontMetrics();

            g2d.drawString(text, 0, fm.getAscent());
        }

        @Override
        public void addNotify() {
            super.addNotify();
            revalidate();
        }

        @Override
        public void removeNotify() {
            super.removeNotify();
            mapCards.clear();
        }
    }

    public static class Turn {

        static Player firstPlayer;
        static Player prevPlayer;

        public static void createPlayer(String playerIP, String playerName, Server.Server.ClientHandler playerID) {
            Player player = new Player(playerIP, playerName, playerID);
            if (firstPlayer == null) {
                firstPlayer = player;
            }
        }

        public static void connectFirstAndLast() {
            Player tempPlayer = firstPlayer;
            while (tempPlayer.getNextPlayer() != null) {
                tempPlayer = tempPlayer.getNextPlayer();
            }
            tempPlayer.setNextPlayer(firstPlayer);
            prevPlayer = tempPlayer;
        }

        public enum Suit {
            CLUBS("C"),
            DIAMONDS("D"),
            HEARTS("H"),
            SPADES("S");

            private final String value;

            Suit(String value) {
                this.value = value;
            }

            public String getValue() {
                return value;
            }
        }

        public enum Face {
            ACE("A"),
            TWO("2"),
            THREE("3"),
            FOUR("4"),
            FIVE("5"),
            SIX("6"),
            SEVEN("7"),
            EIGHT("8"),
            NINE("9"),
            TEN("10"),
            JACK("J"),
            QUEEN("Q"),
            KING("K");

            private final String value;

            Face(String value) {
                this.value = value;
            }

            public String getValue() {
                return value;
            }
        }

        public static class Player {
            private final String playerIP;
            private final String playerName;
            private final Server.Server.ClientHandler playerID;
            private Player nextPlayer;

            public Player(String playerIP, String playerName, Server.Server.ClientHandler playerID) {
                this.playerIP = playerIP;
                this.playerName = playerName;
                this.playerID = playerID;
            }

            public Player getNextPlayer() {
                return nextPlayer;
            }

            public void setNextPlayer(Player nextPlayer) {
                this.nextPlayer = nextPlayer;
            }
        }

        public static class Hand {

            private final List<Card> cards;

            public Hand(Player player) {
                cards = new ArrayList<>(25);
                // Add logic to get the player's cards based on the provided player object
                // Example: cards = player.getPlayerCards();
                cards.add(new Card(Suit.CLUBS, Face.TWO));
                cards.add(new Card(Suit.HEARTS, Face.KING));
                // ...
            }

            public void add(Card card) {
                cards.add(card);
            }

            public int size() {
                return cards.size();
            }

            public Iterable<Card> cards() {
                return cards;
            }

            public Iterable<Card> reveresed() {
                List<Card> reversed = new ArrayList<>(cards);
                Collections.reverse(reversed);
                return reversed;
            }
        }

        public static class Card {

            private final Suit suit;
            private final Face face;

            public Card(Suit suit, Face face) {
                this.suit = suit;
                this.face = face;
            }

            public Suit getSuit() {
                return suit;
            }

            public Face getFace() {
                return face;
            }
        }
    }
}
