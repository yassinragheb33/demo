package com.example.weather;

import org.springframework.web.bind.annotation.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class WeatherServer {
    public static void main(String[] args) {
        SpringApplication.run(WeatherServer.class, args);
        new Thread(WeatherTCPServer::startServer).start();
    }
}

@RestController
@RequestMapping("/weather")
class WeatherController {
    @GetMapping("/{city}")
    public WeatherData getWeather(@PathVariable String city) {
        System.out.println("HTTP Request received for city: " + city);
        return WeatherTCPServer.getWeatherData(city);
    }
}

class WeatherTCPServer {
    private static final int PORT = 5000;
    private static final ConcurrentHashMap<String, WeatherData> weatherDataMap = new ConcurrentHashMap<>();

    public static void startServer() {
        System.out.println("Starting TCP Server on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("✅ TCP Server is running on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            System.err.println(" TCP Server failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            String line = reader.readLine();
            System.out.println("Received from client: " + line);

            if (line != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String city = parts[0].trim().replace("\"", "").toLowerCase(); // fix quote + normalize
                    double temperature = Double.parseDouble(parts[1].trim());
                    double humidity = Double.parseDouble(parts[2].trim());

                    weatherDataMap.put(city, new WeatherData(city, temperature, humidity));
                    writer.println("Data received");
                    System.out.println(" Stored weather data for: " + city);
                } else {
                    writer.println("Invalid data format");
                }
            }
        } catch (IOException e) {
            System.err.println("Client connection error: " + e.getMessage());
        }
    }

    public static WeatherData getWeatherData(String city) {
        return weatherDataMap.getOrDefault(city.toLowerCase(), new WeatherData(city, -1, -1));
    }
}

class WeatherData {
    private String city;
    private double temperature;
    private double humidity;

    public WeatherData(String city, double temperature, double humidity) {
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public String getCity() { return city; }
    public double getTemperature() { return temperature; }
    public double getHumidity() { return humidity; }
}
