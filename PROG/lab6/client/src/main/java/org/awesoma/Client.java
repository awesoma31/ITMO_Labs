package org.awesoma;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

class Client {
    private final String host;
    private final int port;

    private SocketChannel clientChannel;


    Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws IOException {
        try {
            clientChannel = SocketChannel.open();
            clientChannel.connect(new InetSocketAddress(host, port));

            interactive();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void interactive() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;
        String response;
        while (true) {
            ByteBuffer buf = ByteBuffer.allocate(1024);
            input = reader.readLine();
            buf.put(input.getBytes());
            buf.flip();
            clientChannel.write(buf);
            buf.clear();

            clientChannel.read(buf);
            buf.flip();

            byte[] responseData = new byte[buf.limit()];
            buf.get(responseData);
            response = new String(responseData);
            System.out.println("Server responded: " + response);
        }
    }
}