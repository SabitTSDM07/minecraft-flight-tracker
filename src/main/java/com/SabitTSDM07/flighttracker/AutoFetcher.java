package com.SabitTSDM07.flighttracker;

import com.SabitTSDM07.flighttracker.PlaneData;
import com.google.gson.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;
import java.util.concurrent.*;

public class AutoFetcher {

    private static ScheduledExecutorService executor;
    private static ScheduledFuture<?> task;

    public static int toggle(CommandSourceStack source) {
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
            source.sendSuccess(() -> Component.literal("⏹ Auto-fetching stopped."), false);
            return 1;
        }

        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("⚠️ Must be run by a player."));
            return 0;
        }

        executor = Executors.newSingleThreadScheduledExecutor();
        task = executor.scheduleAtFixedRate(() -> fetchAndRender(player), 0, 30, TimeUnit.SECONDS);
        source.sendSuccess(() -> Component.literal("▶ Auto-fetching started."), false);
        return 1;
    }

    private static void fetchAndRender(ServerPlayer player) {
        if (PlaneFetcher.customLat == null || PlaneFetcher.customLon == null || PlaneFetcher.customRadius == null) {
            return;
        }

        String json = PlaneFetcher.fetchPlanes(PlaneFetcher.customLat, PlaneFetcher.customLon, PlaneFetcher.customRadius);
        if (json == null) return;

        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray states = root.getAsJsonArray("states");

            if (states == null || states.isEmpty()) return;

            Map<String, PlaneData> uniquePlanes = new HashMap<>();

            for (JsonElement el : states) {
                JsonArray state = el.getAsJsonArray();

                if (state.size() < 11) continue;

                String icao24 = state.get(0).getAsString();
                if (icao24 == null || icao24.isEmpty()) continue;

                String callsign = state.get(1).isJsonNull() ? "[N/A]" : state.get(1).getAsString().trim();
                double lon = state.get(5).isJsonNull() ? 0.0 : state.get(5).getAsDouble();
                double lat = state.get(6).isJsonNull() ? 0.0 : state.get(6).getAsDouble();
                double altMeters = state.get(7).isJsonNull() ? 0.0 : state.get(7).getAsDouble();
                double heading = state.get(10).isJsonNull() ? 0.0 : state.get(10).getAsDouble();
                double altFt = altMeters * 3.28084;
                String originCountry = state.get(2).isJsonNull() ? "[Unknown]" : state.get(2).getAsString();
                double velocity = state.get(9).isJsonNull() ? 0 : state.get(9).getAsDouble();
                long timestamp = System.currentTimeMillis() / 1000L;
                String fromAirport = "[Unknown]"; // placeholder — you can later use OpenSky arrival board API
                String toAirport = "[Unknown]";


                if (lat == 0.0 && lon == 0.0) continue;

                uniquePlanes.put(icao24, new PlaneData(icao24, callsign, originCountry, lat, lon, altFt, heading, velocity, timestamp, fromAirport, toAirport));
            }

            List<PlaneData> deduplicated = new ArrayList<>(uniquePlanes.values());
            PlaneEntityManager.renderPlanes(player, deduplicated);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
