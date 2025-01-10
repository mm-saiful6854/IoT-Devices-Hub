package org.knowinforms;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class IoTClient{
    ExecutorService executorService;
    InetAddress address;
    int port;
    boolean running;

    public IoTClient(InetAddress address, int port) {
        executorService = Executors.newFixedThreadPool(1);
        this.address = address;
        this.port = port;
        running = true;


        executorService.execute(()->{
           while(running){
               try {
                   this.sendStatus(address, port, "alive|");
                   Thread.sleep(2000);
               } catch (IOException | InterruptedException e) {
                   e.printStackTrace();
               }
           }
        });
    }


    public void sendStatus(InetAddress inetAddress, int port, String status) throws IOException {
        try(Socket socket = new Socket(inetAddress, port)){

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();


            outputStream.write(status.getBytes());
            System.out.println("Client:: Data sent..");
            StringBuilder response = new StringBuilder();
            char ch ='|';
            while((ch = (char)inputStream.read()) != '|'){
                response.append(ch);
            }
            System.out.println("Client:: Response: "+ response.toString());
        } catch (Exception err){
            err.printStackTrace();
        }
    }

    public void shutdown() throws InterruptedException {
        this.running = false;
        executorService.shutdown();
        while(!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
            System.out.println("Time elapsed. But executorService is running: " + executorService.isTerminated());
            executorService.shutdownNow();
        }
    }

}
