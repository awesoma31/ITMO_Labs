package org.awesoma.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

class Server extends Thread {

    private final ServerSocket serverSocket;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(0);
    }

    public void run() {
        System.out.println("[INFO]: Server started");
        while(true) {
            try {
                System.out.println("Waiting for the client on port " +
                        serverSocket.getLocalPort() + "...");
                Socket server = serverSocket.accept();

                DataInputStream in = new DataInputStream(server.getInputStream());
                DataOutputStream out = new DataOutputStream(server.getOutputStream());

                System.out.println("User connected to " + server.getRemoteSocketAddress());

                System.out.println("User disconnected");
                server.close();

            } catch (SocketTimeoutException s) {
                System.out.println("Время сокета истекло!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String [] args) {
        int port = 1234;
        try {
            Thread t = new Server(port);
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

