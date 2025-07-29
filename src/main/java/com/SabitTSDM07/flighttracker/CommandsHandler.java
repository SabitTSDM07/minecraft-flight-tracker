package com.SabitTSDM07.flighttracker;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import com.google.gson.*;
import com.SabitTSDM07.flighttracker.PlaneData;

import java.util.*;

public class CommandsHandler {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("ft")

                        // ✅ Login to OpenSky
                        .then(Commands.literal("login")
                                .then(Commands.argument("username", StringArgumentType.string())
                                        .then(Commands.argument("password", StringArgumentType.string())
                                                .executes(ctx -> {
                                                    String username = StringArgumentType.getString(ctx, "username");
                                                    String password = StringArgumentType.getString(ctx, "password");

                                                    OpenSkySession.setCredentials(username, password);
                                                    String testUrl = "https://opensky-network.org/api/states/own";
                                                    int responseCode = PlaneFetcher.testCredentials(username, password, testUrl);

                                                    if (responseCode == 200) {
                                                        ctx.getSource().sendSuccess(() -> Component.literal("✅ OpenSky login verified and set."), false);
                                                        return 1;
                                                    } else {
                                                        OpenSkySession.setCredentials(null, null);
                                                        String msg = (responseCode == 401) ?
                                                                "❌ Invalid OpenSky credentials. Please try again." :
                                                                "❌ Failed to verify credentials (HTTP " + responseCode + ").";
                                                        ctx.getSource().sendFailure(Component.literal(msg));
                                                        return 0;
                                                    }
                                                })))
                        )

                        // ✅ Set custom tracking location and radius
                        .then(Commands.literal("location")
                                .then(Commands.argument("lat", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("lon", DoubleArgumentType.doubleArg())
                                                .then(Commands.argument("radius", DoubleArgumentType.doubleArg(1.0))
                                                        .executes(context -> {
                                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                                            double lat = DoubleArgumentType.getDouble(context, "lat");
                                                            double lon = DoubleArgumentType.getDouble(context, "lon");
                                                            double radius = DoubleArgumentType.getDouble(context, "radius");

                                                            PlaneFetcher.customLat = lat;
                                                            PlaneFetcher.customLon = lon;
                                                            PlaneFetcher.customRadius = radius;
                                                            PlaneFetcher.customX = player.getX();
                                                            PlaneFetcher.customY = player.getY();
                                                            PlaneFetcher.customZ = player.getZ();

                                                            context.getSource().sendSuccess(
                                                                    () -> Component.literal(String.format("✅ Flight center set to %.6f, %.6f at radius %.0f km.", lat, lon, radius)),
                                                                    false
                                                            );
                                                            return 1;
                                                        }))))
                        )

                        // ✅ Erase all planes and clear tracking config
                        .then(Commands.literal("erase")
                                .executes(ctx -> {
                                    PlaneFetcher.customLat = null;
                                    PlaneFetcher.customLon = null;
                                    PlaneFetcher.customRadius = null;

                                    PlaneEntityManager.clearAllPlanes();

                                    ctx.getSource().sendSuccess(() -> Component.literal("❌ All tracked aircraft erased."), false);
                                    return 1;
                                })
                        )

                        // ✅ Start/Stop auto-fetch
                        .then(Commands.literal("toggle")
                                .executes(ctx -> {
                                    MinecraftServer server = ctx.getSource().getServer();
                                    return AutoFetcher.toggle(ctx.getSource());
                                })
                        )

                        // ✅ Manual plane fetch & render
                        .then(Commands.literal("fetchplanes")
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayer();
                                    if (player == null) {
                                        ctx.getSource().sendFailure(Component.literal("⚠️ This command must be run by a player."));
                                        return 0;
                                    }

                                    double lat = PlaneFetcher.customLat != null ? PlaneFetcher.customLat : 24.4 - player.getZ();
                                    double lon = PlaneFetcher.customLon != null ? PlaneFetcher.customLon : 88.6 + player.getX();
                                    double radius = PlaneFetcher.customRadius != null ? PlaneFetcher.customRadius : 250.0;

                                    String json = PlaneFetcher.fetchPlanes(lat, lon, radius);
                                    if (json == null) {
                                        player.sendSystemMessage(Component.literal("❌ Failed to fetch planes. Are you logged in?"));
                                        return 0;
                                    }

                                    try {
                                        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
                                        JsonArray states = root.getAsJsonArray("states");
                                        if (states == null || states.isEmpty()) {
                                            player.sendSystemMessage(Component.literal("✈ No planes found."));
                                            return 1;
                                        }

                                        player.sendSystemMessage(Component.literal("✈ " + states.size() + " aircraft found in the area:"));

                                        Set<String> seenIcao24 = new HashSet<>();
                                        List<PlaneData> planeDataList = new ArrayList<>();

                                        for (JsonElement elem : states) {
                                            JsonArray plane = elem.getAsJsonArray();
                                            if (plane.size() < 11 || plane.get(0).isJsonNull()) continue;

                                            String icao24 = plane.get(0).getAsString();
                                            if (!seenIcao24.add(icao24)) continue;

                                            String callsign = plane.get(1).isJsonNull() ? "[N/A]" : plane.get(1).getAsString().trim();
                                            double planeLat = plane.get(6).isJsonNull() ? 0.0 : plane.get(6).getAsDouble();
                                            double planeLon = plane.get(5).isJsonNull() ? 0.0 : plane.get(5).getAsDouble();
                                            double altMeters = plane.get(7).isJsonNull() ? 0.0 : plane.get(7).getAsDouble();
                                            double heading = plane.get(10).isJsonNull() ? 0.0 : plane.get(10).getAsDouble();
                                            double altFt = altMeters * 3.28084;
                                            String originCountry = plane.get(2).isJsonNull() ? "[Unknown]" : plane.get(2).getAsString();
                                            double velocity = plane.get(9).isJsonNull() ? 0 : plane.get(9).getAsDouble();
                                            long timestamp = System.currentTimeMillis() / 1000L;
                                            String fromAirport = "[Unknown]"; // placeholder — you can later use OpenSky arrival board API
                                            String toAirport = "[Unknown]";

                                            String color = altitudeToColor(altFt);
                                            String arrow = headingToArrow(heading);

                                            player.sendSystemMessage(Component.literal(color + (planeDataList.size() + 1) + ". ✈ " + callsign + " " + arrow +
                                                    " @ " + String.format("%.4f", planeLat) + ", " + String.format("%.4f", planeLon) +
                                                    " [" + String.format("%.0f", altFt) + " ft]"));

                                            planeDataList.add(new PlaneData(icao24, callsign, originCountry, planeLat, planeLon, altFt, heading, velocity, timestamp, fromAirport, toAirport));
                                        }

                                        PlaneEntityManager.renderPlanes(player, planeDataList);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        player.sendSystemMessage(Component.literal("❌ Error parsing API response."));
                                    }

                                    return 1;
                                }))
        );
    }

    // Altitude-based color coding
    private static String altitudeToColor(double altitude) {
        if (altitude < 3000) return "§c"; // Red
        if (altitude < 6000) return "§6"; // Orange
        if (altitude < 12000) return "§e"; // Yellow
        if (altitude < 20000) return "§a"; // Green
        if (altitude < 30000) return "§b"; // Blue
        if (altitude < 40000) return "§9"; // Indigo
        return "§d"; // Violet
    }

    // Direction arrows from heading angle
    private static String headingToArrow(double deg) {
        int dir = ((int) ((deg + 22.5) % 360) / 45);
        return switch (dir) {
            case 0 -> "↑";
            case 1 -> "↗";
            case 2 -> "→";
            case 3 -> "↘";
            case 4 -> "↓";
            case 5 -> "↙";
            case 6 -> "←";
            case 7 -> "↖";
            default -> "?";
        };
    }
}
