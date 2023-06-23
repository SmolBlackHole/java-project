package Server.Server;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    public static ArrayList<String> chat = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    // hier werden die BufferedReader und BufferedWriter erstellt; diese benutzen wir, um Nachrichten auslesen und abzuschicken zu können
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = bufferedReader.readLine();
            String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
            clientHandlers.add(this);


            // hier wird die broadcast Methode aufgerufen, um den Chatbeitritt anzukündigen
            broadcast("|Server" + username + " Ist dem Spiel beigetreten");


        } catch (IOException e) {
            close(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {

        String messageFromClient;


        while (socket.isConnected()) {
            // soballd ein Client eine Nachricht sendet, wird diese hier empfangen und mit der Methode broadcast an alle Clients versendet
            try {
                messageFromClient = bufferedReader.readLine();
                broadcast(messageFromClient);
            } catch (IOException e) {
                close(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    // Methode, die jedem Client die empfangene Nachricht zurücksendet: wird benutzt, damit jeder Client die Nachricht des jeweils anderen lesen kann
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
        String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        broadcast("|Server" + username + " hat den Chat verlassen!");
    }


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

