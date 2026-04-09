package com.supheria.solar_apocalypse_core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "solar_apocalypse_core", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class Weather2PrecipitationCompat {
    private static Field vanillaPrecipitationField;
    private static boolean fieldLookupAttempted;
    private static boolean overrideActive;
    private static boolean originalVanillaPrecipitation;
    private static Method setRainingMethod;
    private static Method setRainLevelMethod;
    private static Method setThunderLevelMethod;
    private static boolean methodLookupAttempted;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Field field = getVanillaPrecipitationField();
        if (field == null) {
            return;
        }

        boolean collapse = CollapsePhaseState.isInCollapseOverworld();
        try {
            if (collapse && !overrideActive) {
                originalVanillaPrecipitation = field.getBoolean(null);
                field.setBoolean(null, true);
                overrideActive = true;
            } else if (!collapse && overrideActive) {
                field.setBoolean(null, originalVanillaPrecipitation);
                overrideActive = false;
            }
        } catch (IllegalAccessException ignored) {
        }

        if (collapse) {
            forceClientRainState();
        }
    }

    private static Field getVanillaPrecipitationField() {
        if (fieldLookupAttempted) {
            return vanillaPrecipitationField;
        }

        fieldLookupAttempted = true;
        try {
            Class<?> configParticleClass = Class.forName("weather2.config.ConfigParticle");
            Field field = configParticleClass.getDeclaredField("Particle_vanilla_precipitation");
            field.setAccessible(true);
            vanillaPrecipitationField = field;
        } catch (ReflectiveOperationException ignored) {
            vanillaPrecipitationField = null;
        }
        return vanillaPrecipitationField;
    }

    private static void forceClientRainState() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }

        try {
            resolveRainStateMethods(level);
            if (setRainingMethod != null) {
                setRainingMethod.invoke(level.getLevelData(), true);
            }
            if (setRainLevelMethod != null) {
                setRainLevelMethod.invoke(level, 1.0F);
            }
            if (setThunderLevelMethod != null) {
                setThunderLevelMethod.invoke(level, 0.0F);
            }
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private static void resolveRainStateMethods(ClientLevel level) {
        if (methodLookupAttempted) {
            return;
        }

        methodLookupAttempted = true;
        setRainingMethod = resolveMethod(level.getLevelData().getClass(), boolean.class, "setRaining");
        setRainLevelMethod = resolveMethod(ClientLevel.class, float.class, "setRainLevel");
        setThunderLevelMethod = resolveMethod(ClientLevel.class, float.class, "setThunderLevel");
    }

    private static Method resolveMethod(Class<?> owner, Class<?> argumentType, String... candidateNames) {
        for (String candidateName : candidateNames) {
            try {
                return owner.getMethod(candidateName, argumentType);
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return null;
    }

    private Weather2PrecipitationCompat() {}
}
