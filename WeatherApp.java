

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WeatherApp {

    public static JSONObject getWeatherData(String locationName) {

        // Retrieve location coordinates
        JSONArray locationData = getLocationData(locationName);
        if (locationData == null || locationData.isEmpty()) {
            System.out.println("Error: No location data found.");
            return null;
        }

        // Extract latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // Build API request with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude +
                "&longitude=" + longitude + "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";

        try {
            // Call API and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // Check for response status; 200 means that the connection was a success
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            }

            // Store resulting JSON data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                // Read and store into the string builder
                resultJson.append(scanner.nextLine());
            }

            // Close scanner
            scanner.close();

            // Close URL connection
            conn.disconnect();

            // Parse through our data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(resultJson.toString());

            // Retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            // Index of current hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            // Get weather code
            JSONArray weathercode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            // Get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // Get wind speed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            // Build weather JSON data object that we are going to access in frontend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Function for retrieving geographic coordinates
    public static JSONArray getLocationData(String locationName) {

        locationName = locationName.replaceAll(" ", "+");

        // Build API URL with location parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            // Call API and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // Check response status; 200 means successful connection
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            }

            // Store API results
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());

            // Read and store the result
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }

            // Close scanner
            scanner.close();

            // Close URL connection
            conn.disconnect();

            // Parse JSON string into a JSON object
            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(resultJson.toString());

            // Get the list of location data the API generated from location name
            JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
            return locationData;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            // Creating a connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set request method to GET
            conn.setRequestMethod("GET");

            // Connect to our API
            conn.connect();
            return conn;

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Connection not established
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();

        // Iterate through the time list and see which one matches the current time
        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                // Return index
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime() {
        // Get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // Format and print the current date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    private static String convertWeatherCode(long weatherCode) {
        String weatherCondition = "";
        if (weatherCode == 0L) {
            weatherCondition = "Clear";
        } else if ((weatherCode >= 51L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode <= 99L)) {
            weatherCondition = "Rain";
        } else if (weatherCode >= 71L && weatherCode <= 77L) {
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }
}
