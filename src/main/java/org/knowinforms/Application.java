package org.knowinforms;

import sun.misc.Signal;

import java.io.IOException;
import java.net.InetAddress;

public class Application {
    private static Application app;
    IoTClient client;
    IoTServer server;

    public static void main(String[] args) throws IOException {
        System.out.println("IoT Devices Hub");
        app = new Application();

        new Thread(() -> {
            try {
                app.server = new IoTServer(45000);
                app.server.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        app.client = new IoTClient(InetAddress.getLocalHost(), 45000);

        Signal.handle(new Signal("TERM"), sig -> {
            try {
               shutdown();
            } catch (Exception e) {
                System.out.println("Exception stopping presence. Terminating!!!!"+ e);
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                shutdown();
            } catch (Exception e) {
                System.out.println("Exception stopping presence. Terminating!!!!"+e);
            }
        }));
    }

    public static void shutdown() throws InterruptedException, IOException {
        app.client.shutdown();
        app.server.stop();
        System.out.println("IoT Devices Hub shut down");
    }
}