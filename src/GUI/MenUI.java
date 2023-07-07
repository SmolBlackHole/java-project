package GUI;

import Server.Server.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;

public class MenUI extends JFrame {

    private CardGameUI cardGameUI; // Referenz auf das CardGameUI-Objekt
    private Server server; // Referenz auf das Server-Objekt

    public MenUI() {
        // Fenstereinstellungen
        setTitle("Card Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLayout(new GridLayout(2, 1));

        // Button für "Host Server"
        JButton hostServerButton = new JButton("Host Server");
        hostServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHostServerOptions();
            }
        });

        // Button für "Spielen"
        JButton playButton = new JButton("Spielen");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginDialog();
            }
        });

        // Buttons zum Hauptfenster hinzufügen
        add(hostServerButton);
        add(playButton);

        setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MenUI();
            }
        });
    }

    private void showHostServerOptions() {
        // Fenster für Host-Server-Optionen
        JFrame hostServerFrame = new JFrame("Hosting Optionen");
        hostServerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        hostServerFrame.setSize(300, 200);
        hostServerFrame.setLayout(new GridLayout(3, 2));

        // Label und Textfeld für maximale Spieleranzahl
        JLabel maxPlayersLabel = new JLabel("Max Spieler:");
        JTextField maxPlayersField = new JTextField();
        maxPlayersField.setHorizontalAlignment(JTextField.CENTER);

        // Label und Textfeld für Port
        JLabel portLabel = new JLabel("Port:");
        JTextField portField = new JTextField("25565");
        portField.setHorizontalAlignment(JTextField.CENTER);

        // Button zum Starten des Servers
        JButton startHostingButton = new JButton("Start Server");
        startHostingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int maxPlayers = Integer.parseInt(maxPlayersField.getText());
                int port = Integer.parseInt(portField.getText());
                startServer(maxPlayers, port);
                hostServerFrame.dispose();
                showLoginDialog();
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

    private void showLoginDialog() {
        // Fenster für Login-Anmeldemaske
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.setSize(300, 200);
        loginFrame.setLayout(new GridLayout(2, 2));

        // Label und Textfeld für Benutzernamen
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        usernameField.setHorizontalAlignment(JTextField.CENTER);

        // Button zum Beitritt zum Server
        JButton joinServerButton = new JButton("Server Beitreten");
        joinServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                joinServer(username);
                loginFrame.dispose();
                showCardGameUI(); // Hier wird das CardGameUI-Fenster geöffnet
            }
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
            server = new Server(serverSocket);
            server.startServer(maxPlayers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void joinServer(String username) {
        // Code zum Beitritt zum Server mit dem angegebenen Benutzernamen
        System.out.println("Joining server with username: " + username);
    }

    private void showCardGameUI() {
        // Überprüfen, ob die Voraussetzungen erfüllt sind
        if (cardGameUI == null) {
            // Wenn das CardGameUI-Objekt noch nicht erstellt wurde, erstelle es
            cardGameUI = new CardGameUI();
        }
    }
}
