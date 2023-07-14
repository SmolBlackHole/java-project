package GUI;

import Client.Client.Client;
import Server.Server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Random;

import static Server.Server.Server.CheckMaxPlayer;

public class MenUI extends JFrame {
    private ArrayList<String> karten; // Kartenliste
    private CardGameUI cardGameUI; // Referenz auf das CardGameUI-Objekt
    private ChatUI chatUI; // Referenz auf das ChatUI-Objekt

    public MenUI() {
        // Fenstereinstellungen
        setTitle("Card Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLayout(new BorderLayout()); // Verwende BorderLayout
        setResizable(false); // Nicht skalierbares Fenster

        // Panel für Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 10));

        // Button für "Host Server"
        JButton hostServerButton = new JButton("Host Server");
        hostServerButton.setFont(new Font("Arial", Font.BOLD, 24));
        hostServerButton.addActionListener(e -> {
            showHostServerOptions();
            setVisible(false); // Minimiere das Hauptfenster
        });

        // Button für "Spielen"
        JButton playButton = new JButton("Spielen");
        playButton.setFont(new Font("Arial", Font.BOLD, 24));
        playButton.addActionListener(e -> {
            showLoginDialog();
            setVisible(false); // Minimiere das Hauptfenster
        });

        buttonPanel.add(hostServerButton);
        buttonPanel.add(playButton);

        // Buttons zum Hauptfenster hinzufügen
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
        setLocationRelativeTo(null); // Zentriert das Fenster auf dem Bildschirm

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Code zum Beenden des Programms
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(MenUI::new);
    }

    private void showHostServerOptions() {
        // Fenster für Host-Server-Optionen
        JFrame hostServerFrame = new JFrame("Hosting Optionen");
        hostServerFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); // Fenster minimieren statt schließen
        hostServerFrame.setSize(300, 200);
        hostServerFrame.setLayout(new GridLayout(3, 2));
        hostServerFrame.setResizable(false); // Nicht skalierbares Fenster
        hostServerFrame.setLocationRelativeTo(this); // Zentriert das Fenster relativ zum Hauptfenster

        // Label und Textfeld für maximale Spieleranzahl
        JLabel maxPlayersLabel = new JLabel("Max Spieler:");
        JTextField maxPlayersField = new JTextField("2");
        maxPlayersField.setHorizontalAlignment(JTextField.CENTER);

        /*

        // Label und Textfeld für Anzahl der Bots
        JLabel botCountLabel = new JLabel("Anzahl der Bots:");
        JTextField botCountField = new JTextField("0");
        botCountField.setHorizontalAlignment(JTextField.CENTER);
        botCountField.setEditable(false);
        botCountField.setBackground(UIManager.getColor("Panel.background"));

         */

        // Label und Textfeld für Port
        JLabel portLabel = new JLabel("Port:");
        JTextField portField = new JTextField("25565");
        portField.setHorizontalAlignment(JTextField.CENTER);
        portField.setEditable(false);
        portField.setBackground(UIManager.getColor("Panel.background"));

        // Button zum Starten des Servers
        JButton startHostingButton = new JButton("Start Server");

        startHostingButton.addActionListener(e -> {
            int maxPlayers = Integer.parseInt(maxPlayersField.getText());
            /*
            int botCount = Integer.parseInt(botCountField.getText());

            int totalPlayers = maxPlayers + botCount;


            if (totalPlayers <= Server.maxPlayer) {
 +               // Validierung der maximalen Spieleranzahl und Bots
                hostServerFrame.dispose();
                EventQueue.invokeLater(() -> startServer(maxPlayers, botCount, port));
            } else {
                JOptionPane.showMessageDialog(null, "Die maximale Spieleranzahl darf nicht größer als " + Server.maxPlayer + " sein.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
             */

            String maxPlayersText = maxPlayersField.getText();
            if (isValidNumber(maxPlayersText)) {
                //int maxPlayers = Integer.parseInt(maxPlayersText);
                int port = Integer.parseInt(portField.getText());
                hostServerFrame.setState(Frame.ICONIFIED); // Minimiere das Fenster statt es zu schließen
                EventQueue.invokeLater(() -> startServer(maxPlayers, port));
            } else {
                JOptionPane.showMessageDialog(hostServerFrame, "Ungültige Spielerzahl. Bitte geben Sie eine gültige Zahl ein.", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Komponenten zum Host-Server-Fenster hinzufügen
        hostServerFrame.add(maxPlayersLabel);
        hostServerFrame.add(maxPlayersField);
        hostServerFrame.add(portLabel);
        hostServerFrame.add(portField);
        hostServerFrame.add(new JLabel());
        hostServerFrame.add(startHostingButton);

        hostServerFrame.setVisible(true);
    }

    private boolean isValidNumber(String input) {
        try {
            int number = Integer.parseInt(input);
            return number >= 0; // Überprüfen, ob die Zahl nicht negativ ist
        } catch (NumberFormatException e) {
            return false; // Das Eingabeformat ist keine Zahl
        }
    }

    private void showLoginDialog() {
        // Fenster für Login-Anmeldemaske
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.setSize(300, 200);
        loginFrame.setLayout(new BorderLayout()); // Verwende BorderLayout
        loginFrame.setResizable(false); // Nicht skalierbares Fenster
        loginFrame.setLocationRelativeTo(this); // Zentriert das Fenster relativ zum Hauptfenster

        // Panel für Benutzernamen und Button
        JPanel contentPanel = new JPanel(new BorderLayout());

        // Label und Textfeld für Benutzernamen
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        usernameField.setHorizontalAlignment(JTextField.CENTER);

        // Button zum Beitritt zum Server
        JButton joinServerButton = new JButton("Server Beitreten");
        joinServerButton.addActionListener(e -> {
            String username = usernameField.getText();
            if (username.isEmpty()) {
                // Kein Benutzername eingegeben, einen zufälligen Namen aus der Liste wählen und anzeigen
                username = getRandomName(usernameField);
            }
            joinServer(username);
            loginFrame.dispose();
            setVisible(false); // Minimiere das Hauptfenster
        });

        // Füge Komponenten zum Content Panel hinzu
        contentPanel.add(usernameLabel, BorderLayout.NORTH);
        contentPanel.add(usernameField, BorderLayout.CENTER);
        contentPanel.add(joinServerButton, BorderLayout.SOUTH);

        // Füge Content Panel zum Login-Fenster hinzu
        loginFrame.add(contentPanel, BorderLayout.CENTER);

        // Zufälligen Namen auswählen und anzeigen
        getRandomName(usernameField);

        loginFrame.setVisible(true);
    }

    private String getRandomName(JTextField usernameField) {
        ArrayList<String> names = new ArrayList<>();
        names.add("SpongeBob Schwammkopf");
        names.add("SpongeGa (Urzeit Spongebob)");
        names.add("Patta (Urzeit Patrik)");
        names.add("KritzelBob");
        names.add("Patrik Star");
        names.add("Spitzkopf Larry");
        names.add("Dreckiger Dan");
        names.add("Thaddäus Tentakel");
        names.add("Siegbert Schnösel");
        names.add("Mr. Krabs");
        names.add("Sheldon J. Plankton");
        names.add("Meerjungfraumann");
        names.add("Gary");
        names.add("Sandy Cheeks");
        names.add("Drecksackblase");
        names.add("Homer Simpson");
        names.add("Fred Flintstone");
        names.add("Tom");
        names.add("Jerry");
        names.add("Rick Sanchez");
        names.add("Morty Smith");
        names.add("Summer Smith");
        names.add("Jerry Smith");
        names.add("Towly");
        names.add("Eric Cartmann");
        names.add("Kenny McCormick");
        names.add("Kyle Broflovski");
        names.add("Stan Marsh");
        names.add("Mr. Garrison");
        names.add("Dora The Explorer");
        names.add("Goofy");
        names.add("Batman");
        names.add("Shrek");
        names.add("Pikachu");
        names.add("Bob der Baumeister");
        names.add("Thomas die Lokomotive");
        names.add("Mr. Bean");

        Random random = new Random();
        int index = random.nextInt(names.size());
        String selectedName = names.get(index);
        usernameField.setText(selectedName); // Den ausgewählten Namen in das Textfeld einfügen
        return selectedName;
    }

    private void startServer(int maxPlayers, int port) {
        // Code zum Starten des Servers mit den angegebenen Parametern
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Server tempserver = new Server(serverSocket);
            Server.maxPlayer = maxPlayers;
            // Referenz auf das Server-Objekt
            CheckMaxPlayer();
            tempserver.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void joinServer(String username) {
        // Code zum Beitritt zum Server mit dem angegebenen Benutzernamen
        System.out.println("Joining server with username: " + username);
        try {
            Client.username = username; // Setze den Benutzernamen in der Client-Klasse
            Client.ip = "localhost"; // Setze die IP-Adresse des Servers
            Client.port = 25565; // Setze den Port des Servers
            Client.start(); // Dem Server beitreten

            showChatUI(); // Öffne das ChatUI-Fenster
            showCardGameUI(); // Öffne das CardGameUI-Fenster

            updateCardGameUI(); // Aktualisiere das CardGameUI
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateCardGameUI() {
        if (cardGameUI != null) {
            cardGameUI.renderHandCards(karten);
        }
    }

    private void showCardGameUI() {
        // Überprüfen, ob das CardGameUI-Objekt bereits erstellt wurde
        if (cardGameUI == null) {
            karten = new ArrayList<>();
            cardGameUI = new CardGameUI(Client.username, karten);
            cardGameUI.setTitel(Client.username);
        }
        updateCardGameUI(); // Aktualisiere das CardGameUI
    }

    private void showChatUI() {
        // Überprüfen, ob das ChatUI-Objekt bereits erstellt wurde
        if (chatUI == null) {
            // Erstelle ein neues ChatUI-Objekt
            chatUI = new ChatUI();
        } else {
            chatUI.setVisible(true); // Stelle sicher, dass das ChatUI-Fenster sichtbar ist
        }
    }
}
