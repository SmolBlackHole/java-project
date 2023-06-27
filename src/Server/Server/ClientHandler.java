package Server.Server;

import java.awt.List;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ObjectOutputStream objectOutputStream;
    private String username;
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    // hier werden die BufferedReader und BufferedWriter erstellt; diese benutzen
    // wir, um Nachrichten auslesen und abzuschicken zu können
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            OutputStream outputStream = socket.getOutputStream();
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = bufferedReader.readLine();
            this.objectOutputStream = new ObjectOutputStream(outputStream);
            clientHandlers.add(this);

            // hier wird die broadcast Methode aufgerufen, um den Chatbeitritt anzukündigen

            broadcast("|Server| " + username + " Ist dem Spiel beigetreten");

        } catch (IOException e) {
            close(socket, bufferedReader, bufferedWriter);
        }
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
                messageFromClient = bufferedReader.readLine();
                broadcast(messageFromClient);
            } catch (IOException e) {
                close(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    // Methode, die jedem Client die empfangene Nachricht zurücksendet: wird
    // benutzt, damit jeder Client die Nachricht des jeweils anderen lesen kann
    public void broadcast(String messageToSend) {

        for (ClientHandler clientHandler : clientHandlers) {
            try {
                clientHandler.bufferedWriter.write(messageToSend);
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();

            } catch (IOException e) {
                close(socket, bufferedReader, bufferedWriter);
            }
        }

    }

    // sobald ein Client seine Verbindung trennt, wird dies allen Clients mitgeteilt
    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcast("|Server| " + username + " hat das Spiel verlassen!");
    }

    /*
     * public void sendObjects(Objects) {
     * 
     * objectOutputStream.writeObject(Objects);
     * 
     * 
     * 
     * }
     */

    // Methode, um den Server zu schließen
    public void close(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
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

        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
