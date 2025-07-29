package com.SabitTSDM07.flighttracker;

import com.SabitTSDM07.flighttracker.entity.PlaneEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import com.SabitTSDM07.flighttracker.PlaneData;

import java.util.*;

public class PlaneEntityManager {

    private static final Map<UUID, Map<String, PlaneEntity>> activePlanes = new HashMap<>();

    public static void renderPlanes(ServerPlayer player, List<PlaneData> planes) {
        ServerLevel level = player.serverLevel();

        if (PlaneFetcher.customLat == null || PlaneFetcher.customLon == null ||
                PlaneFetcher.customX == null || PlaneFetcher.customZ == null || PlaneFetcher.customY == null) {
            System.out.println("[FlightTracker] Skipping renderPlanes: location not set.");
            return;
        }

        double originLat = PlaneFetcher.customLat;
        double originLon = PlaneFetcher.customLon;
        double originX = PlaneFetcher.customX;
        double originZ = PlaneFetcher.customZ;
        double originY = PlaneFetcher.customY;

        Map<String, PlaneEntity> currentPlanes = activePlanes.computeIfAbsent(player.getUUID(), k -> new HashMap<>());
        Set<String> updatedIcao24s = new HashSet<>();

        for (PlaneData plane : planes) {
            double dx = (plane.longitude - originLon);
            double dz = (originLat - plane.latitude);
            double x = originX + dx / PlaneFetcher.DEG_PER_KM;
            double z = originZ + dz / PlaneFetcher.DEG_PER_KM;

            double altitudeFeet = plane.altitudeMeters * 1;
            double y = originY + (altitudeFeet / 1000.0); // Corrected altitude

            PlaneEntity entity = currentPlanes.get(plane.icao24);
            if (entity == null || !entity.isAlive()) {
                entity = new PlaneEntity(FlightTrackerEntities.PLANE.get(), level);
                level.addFreshEntity(entity);
                currentPlanes.put(plane.icao24, entity);
            }
            entity.setPlaneData(plane);

            entity.setPos(x, y, z);
            float heading = (float) plane.headingDeg;
            float mcYaw = (float) ((-heading + 180 + 360) % 360); // Ensure range [0, 360)
            if (mcYaw > 180) mcYaw -= 360; // Wrap to [-180, 180]
            entity.setYRot(mcYaw);
            entity.setNoGravity(true);
            entity.setInvulnerable(true);
            entity.setCustomName(Component.literal(getColoredTag(plane, altitudeFeet)));
            entity.setCustomNameVisible(true);

            updatedIcao24s.add(plane.icao24);
        }

        // Remove any planes not present in the current fetch
        currentPlanes.entrySet().removeIf(entry -> {
            if (!updatedIcao24s.contains(entry.getKey())) {
                PlaneEntity entity = entry.getValue();
                if (entity.isAlive()) entity.discard();
                return true;
            }
            return false;
        });
    }

    private static String getColoredTag(PlaneData plane, double altitudeFeet) {
        String color = altitudeToColor(altitudeFeet);
        String direction = headingToArrow(plane.headingDeg);
        String callsign = (plane.callsign == null || plane.callsign.isEmpty()) ? "[N/A]" : plane.callsign;
        return color + "✈ " + callsign + " " + direction + " [" + (int) altitudeFeet + " ft]";
    }

    private static String altitudeToColor(double altitude) {
        if (altitude < 3000) return "§c"; // Red
        if (altitude < 6000) return "§6"; // Orange
        if (altitude < 12000) return "§e"; // Yellow
        if (altitude < 20000) return "§a"; // Green
        if (altitude < 30000) return "§b"; // Blue
        if (altitude < 40000) return "§9"; // Indigo
        return "§d"; // Violet
    }

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

    public static void clearPlanesForPlayer(ServerPlayer player) {
        Map<String, PlaneEntity> entities = activePlanes.remove(player.getUUID());
        if (entities != null) {
            for (PlaneEntity e : entities.values()) {
                if (e.isAlive()) e.discard();
            }
        }
    }

    public static void clearAllPlanes() {
        for (Map<String, PlaneEntity> map : activePlanes.values()) {
            for (PlaneEntity e : map.values()) {
                if (e.isAlive()) e.discard();
            }
        }
        activePlanes.clear();
    }
}
