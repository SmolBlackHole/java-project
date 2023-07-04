package Client.Client;

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
    public static String choose;
    public static String newMessage;
    public static String gameData;
    public static int port;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
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
                        bufferedWriter.write("M<:!ds5" + "|" + time + "|" + username + ": " + messageToSend);
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
                    // wenn eine Nachricht empfangen wird, wird diese in die Chatarea eingefügt
                    try {
                        choose = "";
                        newMessage = "";
                        gameData = "";
                        msgFromGroupChat = bufferedReader.readLine();
                        for (int i = 0; i < msgFromGroupChat.length(); i++) {
                            if (choose.equals("M<:!ds5")) {
                                newMessage += msgFromGroupChat.charAt(i);
                            }

                            if (choose.equals("C8->7G#")) {
                                gameData += msgFromGroupChat.charAt(i);
                            }
                            if (i <= 6) {
                                choose += msgFromGroupChat.charAt(i);
                            }
                        }
                        if (choose.equals("M<:!ds5")) {
                            // Hier Hast du die Empfangene Nachricht aus dem Chat, auch deine Eigenen
                            System.out.println(newMessage);
                        }

                        if (choose.equals("C8->7G#")) {
                            // Hier Entsteht dann die Verknüpfung oder Methode die aus dem String die
                            // Variablen befüllt
                        }

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
                bufferedWriter.close();
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
            client.sendMessage();
        }
    }

    public void dataToSend(String data) {

        try {
            bufferedWriter.write(data);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e) {
            close(socket, bufferedWriter, bufferedReader);
        }

    }

    // die Main Methode: sie startet die LoginGui
    public static void main(String[] args) throws IOException {

        Client.username = "Patrik";
        Client.ip = "localhost";
        Client.port = 25565;
        Client.start();

    }

    public class start {
    }
}

// Benutz die Methode dataToSend(data) um deine Daten an den Server zu schicken.
// schicke einen String mit "Bh7.|+e" also: data = "Bh7.|+e".
// um dem Server zu Signalisieren das du eine Karte ziehen magst/musst bzws
// keine Karte legen kannst, sowohl bei einer +2 karte als auch wenn du keine
// Passenden Karten hast.
// schicke einen String mit "C8->7G#"+"Kartenname" also: data = "C8->7G#"+"H7"
// um dem Server die Gelegte Karte, in diesem fall die Herz 7, zu schicken. die
// Anfangszeichen dienen Als
// Identifikation zwischen Server und Client, damit beide wissen was für Daten
// gerade Gesendet wurden.
// (Das folgende ist noch nicht so Implementiert) benutze sendMessage(msg) um
// eine Chatnachricht zu schicken, diese wird dann mit dem Chat-Code, Username
// und der Uhrzeit versehen.
// in der Main Funktion siehst du die Variablen die ich befüllt habe. username,
// ip, und port, diese müssen in Zukunft als Allererstes abgefragt werden, bevor
// der Client gestartet wird

// die Informationen die der Client jede Runder bekommt wären folgende:
// C8->7G#ist-Drann|karten|ObersteSpielkarte|AnzahlSpieler|Spielername1|AnzahlKarten|Spielername2|AnzahlKarten|Spielername3|AnzahlKarten|
// daraus werde ich eine Funktion schreiben die euch diesen String in
// verschiedene Variablen aufteilt.
// istDrann = boolean --> ein bool um zu gucken ob der Spieler gerade drann ist
// karten = Arraylist --> die Karten die man am Anfang gezogen hat und die
// Karten die man Ziehen muss, ist immer eine Liste mit den Karten die der
// Spieler Aktuell haben sollte.
// ObersteSpielKarte = String --> der Name der Obersten Karte auf dem
// Wegwerfstapel
// AnzahlSpieler = Int
// mitspieler = [[Spielername,Anzahlkarten], [Spielername2,Anzahlkarten].....
// --> eine Liste aus Listen die alle Spieler und ihre Anzahl an Karten besitzt.
// hier wird auch der Eigene Name und die eigene Anzahl stehen, diese sollten am
// besten ausgefiltert werden.
// Diese Daten werden Jedesmal geschickt, auch wenn ihr karten ziehen müsst,
// oder nicht drann seit
// guck am besten das du die Liste mit den eigenen karten dann einfach entweder
// jedesmal ersetzt oder so filterst, dass du nur die einfügst die du noch nicht
// hast, damit sich keine Doppeln
