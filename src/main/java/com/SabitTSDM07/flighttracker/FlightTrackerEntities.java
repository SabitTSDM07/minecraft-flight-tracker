package com.SabitTSDM07.flighttracker;

import com.SabitTSDM07.flighttracker.entity.PlaneEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.LivingEntity;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class FlightTrackerEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "flighttracker");

    public static final RegistryObject<EntityType<PlaneEntity>> PLANE =
            ENTITIES.register("plane", () -> EntityType.Builder.<PlaneEntity>of(PlaneEntity::new, MobCategory.MISC)
                    .sized(1.0f, 0.5f) // adjust hitbox size to match your model
                    .clientTrackingRange(256)
                    .build(new ResourceLocation("flighttracker", "plane").toString()));

    public static void register() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    // Register entity attributes to avoid NPE when creating the entity
    @SubscribeEvent
    public static void onRegisterAttributes(EntityAttributeCreationEvent event) {
        AttributeSupplier attributes = AttributeSupplier.builder()
                .add(Attributes.MAX_HEALTH, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .build();

        event.put((EntityType<? extends LivingEntity>) (EntityType<?>) PLANE.get(), attributes);
    }
}
