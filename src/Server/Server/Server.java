package Server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // Methode, die, solange der Socket nicht geschlossen ist, ständig auf Verbindungsanfragen wartet
    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {

                // hier werden die Verbindungsanfragen von Clients akzeptiert
                Socket socket = serverSocket.accept();
                System.out.println(ClientHandler.clientHandlers.toArray());
                ClientHandler clientHandler = new ClientHandler(socket);

                System.out.println("Ein wilder Chatbesucher ist erschienen");
                // um diesen Prozess durchgehend im Hintergrund laufen zu lassen, benutzen wir hier einen Thread
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch (IOException ignored){

        }
    }




    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {

        // hier wird vor dem Starten um eine Porteingabe gebeten
        Scanner portscanner = new Scanner(System.in);
        System.out.println("Port Eingeben");
        int port = Integer.parseInt(portscanner.nextLine());

        // hier wird ein neuer Socket erstellt
        ServerSocket serverSocket = new ServerSocket(port);
        Server server = new Server(serverSocket);

        server.startServer();

    }
}
