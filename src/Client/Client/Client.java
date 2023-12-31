package Client.Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import Server.Player;

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
    public static boolean istDrann;
    public static ArrayList < String > karten = new ArrayList < String > ();
    public static String ObersteSpielkarte;
    public static ArrayList < ArrayList > Spieler = new ArrayList < ArrayList > ();
    public static int AnzahlSpieler;
    public static String Gewinner;

    public static String wilderChatString;
    public static ArrayList < String > wilderKartenString;
    public static boolean wilderBool;
    public static String wildeObersteSpielkarte;
    public static int wildeSpielerAnzahl;
    public static String winnerWinnerChickenDinner;
    private static ArrayList < ArrayList > wildeSpielerListe;

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
    public static void sendMessage(String msg) {
        try {
            // hier wird der Nachricht die aktuelle Uhrzeit beigefügt
            String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
            bufferedWriter.write("M<:!ds5" + "| " + time + " | " + username + ": " + msg);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            close(socket, bufferedWriter, bufferedReader);
        }
    }

    public static String receiveGameData() {
        return gameData;
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
                        String winner = "";
                        gameData = "";
                        msgFromGroupChat = bufferedReader.readLine();
                        for (int i = 0; i < msgFromGroupChat.length(); i++) {
                            if (choose.equals("M<:!ds5")) {
                                newMessage += msgFromGroupChat.charAt(i);
                            }
                            if (choose.equals("C8->7G#")) {
                                gameData += msgFromGroupChat.charAt(i);
                            }
                            if (choose.equals("K<;?dHs")) {
                                winner += msgFromGroupChat.charAt(i);
                            }
                            if (i <= 6) {
                                choose += msgFromGroupChat.charAt(i);
                            }
                        }
                        if (choose.equals("M<:!ds5")) {
                            // Hier Hast du die Empfangene Nachricht aus dem Chat, auch deine Eigenen
                            wilderChatString = newMessage;
                            System.out.println(newMessage);
                        }
                        if (choose.equals("K<;?dHs")) {
                            // Wenn diese String befüllt ist, bekommst du den Namen des Gewinners und das Spiel soll zuende sein.
                            Gewinner = winner;
                            System.out.println("Der Gewinnder ist: " + Gewinner);
                        }

                        if (choose.equals("C8->7G#")) {
                            int a = 0;
                            String b;
                            String turn = "";
                            String kar = "";
                            String top = "";
                            String count = "";
                            String Pname = "";
                            String Pcount = "";
                            String Pturn = "";
                            Spieler.clear();

                            for (int i = 0; i < gameData.length(); i++) {
                                ArrayList < Object > Spielerdaten = new ArrayList < Object > ();
                                b = "" + gameData.charAt(i);
                                if (a == 7) {
                                    a = 4;
                                    boolean pt = false;
                                    String pn = Pname;
                                    Spielerdaten.add(pn);
                                    Pname = "";
                                    try {
                                        int nc = Integer.parseInt(Pcount);
                                        Spielerdaten.add(nc);
                                        Pcount = "";

                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }

                                    if (Pturn.equals("true")) {
                                        pt = true;
                                    }
                                    Spielerdaten.add(pt);
                                    Spieler.add(Spielerdaten);
                                    Pturn = "";
                                }
                                if (a == 0 && !b.equals("|")) {
                                    turn = turn + b;
                                }

                                if (a == 1 && !b.equals("|")) {
                                    if (!b.equals("[") && !b.equals("]")) {
                                        if (!b.equals(" ")) {
                                            kar = kar + b;
                                        }
                                    }
                                }
                                if (a == 2 && !b.equals("|")) {
                                    top = top + b;
                                }
                                if (a == 3 && !b.equals("|")) {
                                    count = count + b;
                                }

                                if (a == 4 && !b.equals("|")) {
                                    Pname = Pname + b;
                                }
                                if (a == 5 && !b.equals("|")) {
                                    Pcount = Pcount + b;

                                }
                                if (a == 6 && !b.equals("|")) {
                                    Pturn = Pturn + b;

                                }

                                if (b.equals("|")) {
                                    a++;
                                }
                            }
                            if (turn.equals("true")) {
                                istDrann = true;
                            }
                            if (turn.equals("false")) {
                                istDrann = false;
                            }
                            wilderBool = istDrann;

                            ArrayList < String > strList = new ArrayList < String > (Arrays.asList(kar.split(",")));
                            karten = strList;
                            wilderKartenString = karten;

                            ObersteSpielkarte = top;
                            wildeObersteSpielkarte = ObersteSpielkarte;
                            try {
                                AnzahlSpieler = Integer.parseInt(count);
                                wildeSpielerAnzahl = AnzahlSpieler;
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }


                            //Prints

                            if(istDrann){
                                System.out.println("Sie sind dran!");
                            }
                            System.out.println(Spieler);
                            System.out.println("\n");
                            if(istDrann){
                                System.out.println("Deine Karten sind: " + karten);
                            }
                            System.out.println("Gelegte Karte ist: " + ObersteSpielkarte);
                            System.out.println("\n");


                            wildeSpielerListe = Spieler;
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
            client.Play();
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

    public static BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public static BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public static Socket getSocket() {
        return socket;
    }

    public void Play() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected() || Gewinner == null) {

                    Scanner myObj = new Scanner(System.in);
                    String play = myObj.nextLine();
                    boolean chat = false;


                    if(play != "" && !istDrann){
                        if(play.charAt(0) == ('/')){
                            String newstr = play.substring(0, 0) + play.substring(0 + 1);
                            sendMessage(newstr);
                        }else if(!istDrann){
                            System.out.println("Falsche Eingabe: Benutze / um eine Nachricht in den Chat zu schicken, oder warte bis du Dran bist!");
                        }

                    } else if (istDrann) {
                        boolean check = false;
                        if (play.isEmpty()) {
                            //Befehl zum senden das mann ziehen muss
                            dataToSend("Bh7.|+e");
                            check = true;
                        }
                        else if(play.charAt(0) == ('/')){
                            String newstr = play.substring(0, 0) + play.substring(0 + 1);
                            sendMessage(newstr);
                            chat = true;
                        }
                        else if(play.length() == 2 && (ObersteSpielkarte.charAt(0) == play.charAt(0) || ObersteSpielkarte.charAt(1) == play.charAt(1))){
                            check = true;

                        }

                        if (check && karten.contains(play)) {
                            //befehlt zum senden der gelegten karte
                            dataToSend("F4->3GA" + play);
                        }
                        else if (!chat && !play.isEmpty()) {
                            System.out.println("Du kannst diese Karte nicht Legen, wähle eine andere Karte oder benutze / um eine Nachricht in den Chat zu schicken");
                        }
                    }
                    else{
                        System.out.println("Falsche Eingabe: Benutze / um eine Nachricht in den Chat zu schicken, oder warte bis du Dran bist!");
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                winnerWinnerChickenDinner = Gewinner;
            }
        }).start();
    }

    // die Main Methode: sie startet die LoginGui
    public static void main(String[] args) throws IOException {

        Scanner Abfrage = new Scanner(System.in);
        System.out.println("Enter Username");
        Client.username = Abfrage.nextLine();
        System.out.println("Enter Ip");
        Client.ip = Abfrage.nextLine();
        System.out.println("Enter Port");
        Client.port = Abfrage.nextInt();
        Client.start();

    }

    public static String receiveMessage() {
        return wilderChatString;
    }
    public static ArrayList < String > receiveKarten() {
        return karten;
    }
    public static boolean receiveTurn() {
        return wilderBool;
    }
    public static String receiveObersteSpielkarte() {
        return wildeObersteSpielkarte;
    }
    public static int receiveAnzahlSpieler() {
        return wildeSpielerAnzahl;
    }
    public static String receiveGewinner() {
        return Gewinner;
    }
    public static ArrayList < ArrayList > receiveSpielerListe() {
        return wildeSpielerListe;
    }
}

// ToDo:
// Benutz die Methode dataToSend(data) um deine Daten an den Server zu schicken.
// schicke einen String mit "Bh7.|+e" also: data = "Bh7.|+e".
// um dem Server zu signalisieren, dass du eine Karte ziehen musst bzw.
// keine Karte legen kannst, sowohl bei einer +2 karte als, auch wenn du keine
// passenden Karten hast.

// schicke einen String mit "F4->3GA"+"Kartenname" also: data = "F4->3GA"+"H7"
// um dem Server die gelegte Karte, in diesem fall die Herz 7, zu schicken. Die
// Anfangszeichen dienen als
// Identifikation zwischen Server und Client, damit beide wissen was für Daten
// gerade Gesendet wurden.

// in der Main Funktion siehst du die Variablen die ich befüllt habe. username,
// ip, und port, diese müssen in Zukunft als Allererstes abgefragt werden, bevor
// der Client gestartet wird

// die Informationen, die der Client jede Runder bekommt, wären folgende:
// C8->7G#istDrann|karten|ObersteSpielkarte|AnzahlSpieler|Spielername1|AnzahlKarten|istDrann|Spielername2|Istdrann2|AnzahlKarten|Spielername3|AnzahlKarten|IstDrann3|
// istDrann = boolean --> ein bool um zu gucken ob der Spieler gerade Drann ist
// karten = Arraylist --> die Karten, die man am Anfang gezogen hat und die
// Karten, die man Ziehen muss, ist immer eine Liste mit den Karten, die der
// Spieler aktuell haben sollte.
// ObersteSpielKarte = String --> der Name der Obersten Karte auf dem
// Wegwerfstapel
// AnzahlSpieler = Int
// mitspieler = [[Spielername,Anzahlkarten], [Spielername2,Anzahlkarten].....
// --> eine Liste aus Listen die alle Spieler und ihre Anzahl an Karten besitzt.
// hier wird auch der Eigene Name und die eigene Anzahl stehen, diese sollten am
// besten ausgefiltert werden.
// Diese Daten werden Jedes Mal geschickt, auch wenn ihr karten ziehen müsst,
// oder nicht Drann seit

// guck am besten das du die Liste mit den eigenen karten dann einfach entweder
// jedesmal ersetzt oder so filterst, dass du nur die einfügst die du noch nicht
// hast, damit sich keine Doppeln
// wenn die Variable Gewinner mit dem Namen des Gewinners gefüllt ist, ist soll das Spiel vorbei sein

// Done

// Benutze sendMessage(msg) um
// eine Chatnachricht zu schicken, diese wird dann mit dem Chat-Code, Username
// und der Uhrzeit versehen.