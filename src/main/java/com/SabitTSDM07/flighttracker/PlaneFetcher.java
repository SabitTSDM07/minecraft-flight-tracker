package com.SabitTSDM07.flighttracker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class PlaneFetcher {

    public static String username = null;
    public static String password = null;

    // Custom location override
    public static Double customLat = null;
    public static Double customLon = null;
    public static Double customRadius = null;

    // Fixed reference point in world coordinates (set by /ft location)
    public static Double customX = null;
    public static Double customY = null; // <-- Add this alongside customX and customZ
    public static Double customZ = null;

    // OpenSky bounding box is roughly 1.25° per 250km radius
    public static final double DEG_PER_KM = 1.0 / 111.0;

    public static String fetchPlanes(double playerLat, double playerLon, double radiusKm) {
        try {
            double radiusDeg = radiusKm * DEG_PER_KM;
            double lamin = playerLat - radiusDeg;
            double lamax = playerLat + radiusDeg;
            double lomin = playerLon - radiusDeg;
            double lomax = playerLon + radiusDeg;

            String urlStr = String.format(
                    "https://opensky-network.org/api/states/all?lamin=%.6f&lamax=%.6f&lomin=%.6f&lomax=%.6f",
                    lamin, lamax, lomin, lomax);

            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("GET");

            if (username != null && password != null && !username.isBlank() && !password.isBlank()) {
                String auth = username + ":" + password;
                String encoded = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
                conn.setRequestProperty("Authorization", "Basic " + encoded);
            } else {
                System.out.println("[FlightTracker] Proceeding with anonymous OpenSky API access.");
            }

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                return result.toString();

            } else {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ✅ Credential test method for login verification
    public static int testCredentials(String username, String password, String testUrl) {
        try {
            URL url = new URL(testUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder()
                    .encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            return conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
