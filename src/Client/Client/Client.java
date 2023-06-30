package Client.Client;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Client {

    private static Socket socket;
    private static BufferedWriter bufferedWriter;
    private static BufferedReader bufferedReader;
    public static String username;
    public static String ip;
    public static int port;
    private ObjectInputStream objectInputStream;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            InputStream inputStream = socket.getInputStream();
            this.objectInputStream = new ObjectInputStream(inputStream);

            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;

            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e) {
            close(socket, bufferedWriter, bufferedReader);
        }
    }

    // Methode, die das Eingabefeld ausließt und den Inhalt absendet
    public static void sendMessage() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()) {
                    try {
                        Scanner messagescanner = new Scanner(System.in);
                        String messageToSend = messagescanner.nextLine();
                        // hier wird der Nachricht die aktuelle Uhrzeit beigefügt
                        String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
                        bufferedWriter.write("|" + time + "|" + username + ": " + messageToSend);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();

                    } catch (IOException e) {
                        close(socket, bufferedWriter, bufferedReader);
                    }
                }
            }
            // startet den Thread
        }).start();
    }

    // Methode, die auf Nachrichten wartet, und diese bearbeitet
    public void listenForMessage() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                while (socket.isConnected()) {
                    System.out.println("Test2");

                    // wenn eine Nachricht empfangen wird, wird diese in die Chatarea eingefügt
                    try {
                        System.out.println("Test");
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);

                    } catch (IOException e) {
                        close(socket, bufferedWriter, bufferedReader);
                    }
                }
            }
            // startet den Thread
        }).start();

    }

    public void listenForObject() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Object> ObjectsFromServer = new ArrayList<Object>();
                while (socket.isConnected()) {

                    // wenn eine Nachricht empfangen wird, wird diese in die Chatarea eingefügt
                    try {
                        ObjectsFromServer = (ArrayList<Object>) objectInputStream.readObject();
                        System.out.println(ObjectsFromServer.get(0));

                    } catch (IOException | ClassNotFoundException e) {
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
            System.out.println("Der Server Ist Geschlossen");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Methode, die die Verbindung einleitet und das Programm startet
    public static void start() throws IOException {
        if (username != null) {

            // hier wird ein Socket erstellt; dieser dient dem Nachrichtenaustausch zwischen
            // Server und Client
            Socket socket = new Socket(ip, port);
            Client client = new Client(socket, username);
            client.listenForMessage();
            client.listenForObject();
            client.sendMessage();
        }
    }

    // die Main Methode: sie startet die LoginGui
    public static void main(String[] args) throws IOException {

        Client.username = "Lilly";
        Client.ip = "localhost";
        Client.port = 25565;
        Client.start();

    }

    public class start {
    }
}
