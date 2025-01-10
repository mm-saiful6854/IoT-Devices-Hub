package org.knowinforms;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IoTServer {
    ServerSocket serverSocket;
    ExecutorService executorService;

    public IoTServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        executorService = Executors.newFixedThreadPool(5);
    }

    public void start() {
        System.out.println("Server started...");
        while (!serverSocket.isClosed()) {
            try {
                Socket clientConnection = serverSocket.accept();
                executorService.execute(new Processor(clientConnection));
            } catch (IOException e) {
                if (serverSocket.isClosed()) {
                    System.out.println("Server stopped.");
                } else {
                    e.printStackTrace();
                }
            }
        }
    }
    public void stop() throws IOException, InterruptedException {
        serverSocket.close();
        executorService.shutdown();
        if (!executorService.awaitTermination(2, TimeUnit.MINUTES)) {
            executorService.shutdownNow();
        }
        System.out.println("Server shut down.");
    }

    private static class Processor implements Runnable {
        private Socket clientConnection;

        public Processor(Socket clientConnection) {
            this.clientConnection = clientConnection;
        }

        @Override
        public void run() {
            try (Socket client = clientConnection) {
                InputStream inputStream = client.getInputStream();
                OutputStream outputStream = client.getOutputStream();

                StringBuilder status = new StringBuilder();
                char ch = '|';
                while ((ch = (char)inputStream.read()) != '|') {
                    status.append(ch);
                }
                System.out.println(status);
                if (status.toString().equalsIgnoreCase("alive")) {
                    outputStream.write("OK|".getBytes());
                }
                System.out.println("processing done");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
