package com.SabitTSDM07.flighttracker;

public class OpenSkySession {
    private static String username = "";
    private static String password = "";

    public static void setCredentials(String user, String pass) {
        username = user;
        password = pass;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static boolean isLoggedIn() {
        return !username.isEmpty() && !password.isEmpty();
    }
}
