package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import Client.Client.Client;

import static Client.Client.Client.wilderChatString;

public class ChatUI extends JFrame {

    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private Client client;

    public ChatUI() {

        // Fenstereinstellungen
        setTitle("Chat");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());

        // Chatbereich
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // Eingabebereich für Nachrichten
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Senden");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);

        // Nachrichten vom Client empfangen
        listenForMessages();
    }

    private void sendMessage() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            Client.sendMessage(message);
            messageField.setText("");
        }
    }

    public void addMessage(String message) {
        chatArea.append(message + "\n");
    }

    private void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String Platzhalter = "";
                while (true) {
                    String receivedMessage = Client.receiveMessage();

                    if (!Objects.equals(receivedMessage, Platzhalter)) {
                        addMessage(receivedMessage); // Empfangene Nachricht zum Chat
                        Platzhalter = receivedMessage;
                    }
                }
            }
        }).start();
    }
}
