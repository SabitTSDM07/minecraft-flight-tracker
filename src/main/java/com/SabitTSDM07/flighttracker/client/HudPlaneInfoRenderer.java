package com.SabitTSDM07.flighttracker.client;

import com.SabitTSDM07.flighttracker.PlaneData;
import com.SabitTSDM07.flighttracker.entity.PlaneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HudPlaneInfoRenderer {

    public static void renderOverlay(GuiGraphics graphics, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        HitResult hit = mc.hitResult;

        if (hit instanceof EntityHitResult ehr && ehr.getEntity() instanceof PlaneEntity planeEntity) {
            PlaneData data = planeEntity.getPlaneData();
            if (data == null) return;

            Font font = mc.font;
            PoseStack poseStack = graphics.pose();
            int x = 10;
            int y = 10;

            String[] lines = {
                    ChatFormatting.AQUA + "✈ " + data.icao24 + " [" + data.originCountry + "]",
                    ChatFormatting.GRAY + "From: " + (data.fromAirport == null ? "[Unknown]" : data.fromAirport),
                    ChatFormatting.GRAY + "To:   " + (data.toAirport == null ? "[Unknown]" : data.toAirport),
                    ChatFormatting.GOLD + "Speed: " + (int)(data.velocity * 3.6) + " km/h",
                    ChatFormatting.YELLOW + "Altitude: " + (int)(data.altitudeMeters * 3.28084) + " ft",
                    ChatFormatting.BLUE + "Heading: " + String.format("%.0f° (%s)", data.headingDeg, headingToCompass(data.headingDeg)),
                    ChatFormatting.DARK_GRAY + "Time: " + formatTimestamp(data.timestamp)
            };

            // Draw translucent background box
            int maxWidth = 0;
            for (String line : lines) {
                int lineWidth = font.width(line);
                if (lineWidth > maxWidth) maxWidth = lineWidth;
            }

            int padding = 5;
            int bgHeight = lines.length * 10 + padding * 2;

            graphics.fill(x - padding, y - padding, x + maxWidth + padding, y + bgHeight - padding, 0x88000000); // 0x88 = ~50% alpha

            // Draw text lines
            for (String line : lines) {
                graphics.drawString(font, line, x, y, 0xFFFFFF);
                y += 10;
            }
        }
    }

    private static String formatTimestamp(long timestamp) {
        Date date = new Date(timestamp * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    private static String headingToCompass(double heading) {
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        int index = (int)((heading + 22.5) % 360) / 45;
        return directions[index];
    }
}
