package Server.Server;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import Server.Bot;
import Server.Player;

public class ClientHandler implements Runnable {

    Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    String sk;
    private String username;
    private String newMessage;
    private String choose;
    private String spielerKarte;
    public static ArrayList < ClientHandler > clientHandlers = new ArrayList < > ();
    public boolean gameRunning = false;

    // hier werden die BufferedReader und BufferedWriter erstellt; diese benutzen
    // wir, um Nachrichten auslesen und abzuschicken zu können
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = bufferedReader.readLine();
            clientHandlers.add(this);

            // hier wird die broadcast Methode aufgerufen, um den Chatbeitritt anzukündigen

            broadcast("M<:!ds5" + "|Server| " + username + " Ist dem Spiel beigetreten");

        } catch (IOException e) {
            close(socket, bufferedWriter, bufferedReader);
        }
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public String getIP() {
        String ip = (((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress()).toString().replace("/", "");
        return ip;
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public void run() {

        String messageFromClient;

        while (socket.isConnected()) {
            // soballd ein Client eine Nachricht sendet, wird diese hier empfangen und mit
            // der Methode broadcast an alle Clients versendet
            try {
                newMessage = "";
                choose = "";
                sk = "";
                messageFromClient = bufferedReader.readLine();
                for (int i = 0; i < messageFromClient.length(); i++) {
                    if (choose.equals("M<:!ds5")) {
                        newMessage += messageFromClient.charAt(i);
                    }

                    if (choose.equals("F4->3GA")) {
                        sk += messageFromClient.charAt(i);
                    }
                    if (i <= 6) {
                        choose += messageFromClient.charAt(i);
                    }
                }

                if (choose.equals("M<:!ds5")) {
                    String temp = choose + newMessage;
                    broadcast(temp);
                }

                if (choose.equals("Bh7.|+e")) {
                    System.out.println("Spieler Zieht");
                    Server.game.playerCard = "";
                }

                if (choose.equals("F4->3GA")) {
                    spielerKarte = sk;
                    Server.game.playerCard = spielerKarte;
                    System.out.println("Gelegte Karte " + Server.game.playerCard);

                }

                spielerKarte = null;

            } catch (IOException e) {
                close(socket, bufferedWriter, bufferedReader);
                break;
            }
        }
    }

    // Methode, die jedem Client die empfangene Nachricht zurücksendet: wird
    // benutzt, damit jeder Client die Nachricht des jeweils anderen lesen kann
    public void broadcast(String messageToSend) {

        for (ClientHandler clientHandler: clientHandlers) {
            try {
                clientHandler.bufferedWriter.write(messageToSend);
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();

            } catch (IOException e) {
                close(socket, bufferedWriter, bufferedReader);
            }
        }
    }

    public void sendObject(String messageToSend) {
        ClientHandler clientHandler = this;
        try {
            clientHandler.bufferedWriter.write(messageToSend);
            clientHandler.bufferedWriter.newLine();
            clientHandler.bufferedWriter.flush();

        } catch (IOException e) {
            close(socket, bufferedWriter, bufferedReader);
        }
    }

    // sobald ein Client seine Verbindung trennt, wird dies allen Clients mitgeteilt
    public void removeClientHandler() {
        if (gameRunning) {
            Bot temp = new Bot(this);
            Server.game.getPlayerObject(this).setPlayerID(temp);
            Server.bots.add(temp);
        }
        clientHandlers.remove(this);
        broadcast("M<:!ds5" + "|Server| " + username + " hat das Spiel verlassen!");
        Server.clientHandlers.remove(this);
    }

    // Methode, um den Server zu schließen
    public void close(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();

            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {}
        removeClientHandler();
    }

    public String requestCard() {
        while (spielerKarte == null) {
            //  delay?
        }
        return spielerKarte;
    }

    public void startGameRunning() {
        gameRunning = true;
    }
}