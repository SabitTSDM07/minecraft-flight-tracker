package com.SabitTSDM07.flighttracker;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext; // ✅ Import this
import com.SabitTSDM07.flighttracker.FlightTrackerEntities; // ✅ Adjust path if needed

@Mod(FlightTrackerMod.MODID)
public class FlightTrackerMod {
    public static final String MODID = "flighttracker";

    public FlightTrackerMod() {
        // ✅ Register entities to mod event bus
        FlightTrackerEntities.register();

        // ✅ Register for Forge event bus
        MinecraftForge.EVENT_BUS.register(this);

        System.out.println("✅ Flight Tracker Mod Loaded!");
    }

    // ✅ Register commands when server starts
    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        CommandsHandler.register(event.getDispatcher());
    }
}
