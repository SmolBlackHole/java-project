package Client.Client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

public class Client {

    private static Socket socket;
    private static BufferedWriter bufferedWriter;
    private static BufferedReader bufferedReader;
    public static String username;
    public static String ip;
    public static int port;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;

        } catch (IOException e) {
            close(socket, bufferedWriter, bufferedReader);
        }
    }

    // Methode, die das Eingabefeld ausließt und den Inhalt absendet
    public static void sendMessage() {
        try {
            // hier wird geprüft, ob noch eine Verbindung besteht
            if (socket.isConnected()) {
                System.out.println("dieses statement wird erreicht");
                Scanner messagescanner = new Scanner(System.in);
                String messageToSend = messagescanner.nextLine();
                System.out.println("Echo: " + messageToSend);

                // hier wird der Nachricht die aktuelle Uhrzeit beigefügt
                String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
                bufferedWriter.write("|" + time + "|" + username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }

        } catch (IOException e) {
            close(socket, bufferedWriter, bufferedReader);
        }
    }

    // Methode, die auf Nachrichten wartet, und diese bearbeitet
    public void listenForMessage() {

        // hier wird ein Thread erstellt; diesen brauchen wir, um ununterbrochen auf eingehende Nachrichten zu warten
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                while (socket.isConnected()) {

                    // wenn eine Nachricht empfangen wird, wird diese in die Chatarea eingefügt
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);


                        // dient dazu, einen Zeilenumbruch nach jeder Nachricht zu machen
                    } catch (IOException e) {
                        close(socket, bufferedWriter, bufferedReader);
                    }
                }
            }
            // startet den Thread
        }).start();

    }

    // Methode, um bei Fehlern das Programm zu stoppen
    public static void close(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedReader.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Methode, die die Verbindung einleitet und das Programm startet
    public static void start() throws IOException {
        if (username != null) {

            // hier wird ein Socket erstellt; dieser dient dem Nachrichtenaustausch zwischen Server und Client
            Socket socket = new Socket(ip, port);
            Client client = new Client(socket, username);
            client.listenForMessage();
            client.sendMessage();
        }
    }




    // die Main Methode: sie startet die LoginGui
    public static void main(String[] args) throws IOException {

        Client.username = "Test2";
        Client.ip = "localhost";
        Client.port = 25565;
        Client.start();



    }
}
