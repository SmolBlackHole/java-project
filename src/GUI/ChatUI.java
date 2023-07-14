package GUI;

import Client.Client.Client;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ChatUI extends JFrame {

    private JTextArea chatArea;
    private JTextField messageField;

    public ChatUI() {
        initializeUI();
        listenForMessages();
    }

    private void initializeUI() {
        setTitle("Chat");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        JButton sendButton = new JButton("Senden");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.addActionListener(e -> sendMessage());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);
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
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private void listenForMessages() {
        new Thread(() -> {
            String placeholderMessage = "";
            while (true) {
                String receivedMessage = Client.receiveMessage();

                if (!Objects.equals(receivedMessage, placeholderMessage)) {
                    addMessage(receivedMessage);
                    placeholderMessage = receivedMessage;
                }
            }
        }).start();
    }
}
