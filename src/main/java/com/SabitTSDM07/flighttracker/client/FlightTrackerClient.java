package com.SabitTSDM07.flighttracker.client;

import com.SabitTSDM07.flighttracker.client.model.Modelplane;
import com.SabitTSDM07.flighttracker.client.renderer.PlaneRenderer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "flighttracker", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FlightTrackerClient {

    public static final ModelLayerLocation PLANE_LAYER = new ModelLayerLocation(
            new ResourceLocation("flighttracker", "plane"), "main");

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(PLANE_LAYER, Modelplane::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(com.SabitTSDM07.flighttracker.FlightTrackerEntities.PLANE.get(), PlaneRenderer::new);
    }

    // NEW: Register the HUD overlay renderer
    @Mod.EventBusSubscriber(modid = "flighttracker", value = Dist.CLIENT)
    public static class ClientOverlayHandler {
        @SubscribeEvent
        public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
            if (event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
                // Only render after the hotbar layer to avoid GUI overlap
                HudPlaneInfoRenderer.renderOverlay(event.getGuiGraphics(), event.getPartialTick());
            }
        }
    }
}
