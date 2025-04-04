package com.example.weather;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class SensorClient {
    private static final String HOST = "localhost"; // han8ayaro ma3a el LAN
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to Weather Server.");

            System.out.print("Enter city name: ");
            String city = scanner.nextLine().trim();

            System.out.print("Enter temperature (C): ");
            double temp = scanner.nextDouble();

            System.out.print("Enter humidity (%): ");
            double humidity = scanner.nextDouble();

            String message = "\"" + city + "\"," + temp + "," + humidity;
            writer.println(message);

            String response = reader.readLine();
            System.out.println("Server Response: " + response);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
