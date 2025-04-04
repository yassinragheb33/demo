package com.example.weather;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class WeatherHttpClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter city name: ");
        String city = scanner.nextLine().trim();
        scanner.close();

        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);

        String requestUrl = "http://localhost:8080/weather/" + encodedCity; // han8ayar el IP fel LAN

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Weather data for " + city + ": " + response.body());
            } else {
                System.out.println("No data available for " + city + ".");
            }

        } catch (Exception e) {
            System.err.println("Error getting weather data: " + e.getMessage());
        }
    }
}
