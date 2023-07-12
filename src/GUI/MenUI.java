package GUI;

import Client.Client.Client;
import Server.Server.Server;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import static Server.Server.Server.CheckMaxPlayer;

public class MenUI extends JFrame {
    private ArrayList < String > karten; // Kartenliste
    private CardGameUI cardGameUI; // Referenz auf das CardGameUI-Objekt
    private ChatUI chatUI; // Referenz auf das ChatUI-Objekt

    public MenUI() {
        // Fenstereinstellungen
        setTitle("Card Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLayout(new GridLayout(2, 1));
        setResizable(false); // Nicht skalierbares Fenster

        // Panel für Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 10));

        // Button für "Host Server"
        JButton hostServerButton = new JButton("Host Server");
        hostServerButton.setFont(new Font("Arial", Font.BOLD, 24));
        hostServerButton.setPreferredSize(new Dimension(200, 100)); // Vergrößere die Höhe des Buttons
        hostServerButton.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Füge eine Border hinzu
        hostServerButton.addActionListener(e -> {
            showHostServerOptions();
            setVisible(false); // Minimiere das Hauptfenster
        });

        // Button für "Spielen"
        JButton playButton = new JButton("Spielen");
        playButton.setFont(new Font("Arial", Font.BOLD, 24));
        playButton.setPreferredSize(new Dimension(200, 100)); // Vergrößere die Höhe des Buttons
        playButton.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Füge eine Border hinzu
        playButton.addActionListener(e -> {
            showLoginDialog();
            setVisible(false); // Minimiere das Hauptfenster
        });

        buttonPanel.add(hostServerButton);
        buttonPanel.add(playButton);

        // Buttons zum Hauptfenster hinzufügen
        add(buttonPanel);

        setVisible(true);
        setLocationRelativeTo(null); // Zentriert das Fenster auf dem Bildschirm
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(MenUI::new);
    }

    private void showHostServerOptions() {
        // Fenster für Host-Server-Optionen
        JFrame hostServerFrame = new JFrame("Hosting Optionen");
        hostServerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        hostServerFrame.setSize(300, 200);
        hostServerFrame.setLayout(new GridLayout(3, 2));
        hostServerFrame.setResizable(false); // Nicht skalierbares Fenster
        hostServerFrame.setLocationRelativeTo(this); // Zentriert das Fenster relativ zum Hauptfenster

        // Label und Textfeld für maximale Spieleranzahl
        JLabel maxPlayersLabel = new JLabel("Max Spieler:");
        JTextField maxPlayersField = new JTextField("2");
        maxPlayersField.setHorizontalAlignment(JTextField.CENTER);

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
            int port = Integer.parseInt(portField.getText());
            hostServerFrame.dispose();

            EventQueue.invokeLater(() -> startServer(maxPlayers, port));
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

    private void showLoginDialog() {
        // Fenster für Login-Anmeldemaske
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.setSize(300, 200);
        loginFrame.setLayout(new GridLayout(3, 2));
        loginFrame.setResizable(false); // Nicht skalierbares Fenster
        loginFrame.setLocationRelativeTo(this); // Zentriert das Fenster relativ zum Hauptfenster

        // Label und Textfeld für Benutzernamen
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        usernameField.setHorizontalAlignment(JTextField.CENTER);

        // Button zum Beitritt zum Server
        JButton joinServerButton = new JButton("Server Beitreten");
        joinServerButton.addActionListener(e -> {
            String username = usernameField.getText();
            joinServer(username);
            loginFrame.dispose();
            setVisible(false); // Minimiere das Hauptfenster
        });

        // Komponenten zum Login-Fenster hinzufügen
        loginFrame.add(usernameLabel);
        loginFrame.add(usernameField);
        loginFrame.add(new JLabel());
        loginFrame.add(joinServerButton);

        loginFrame.setVisible(true);
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
            karten = new ArrayList < > ();
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
        }
    }
}