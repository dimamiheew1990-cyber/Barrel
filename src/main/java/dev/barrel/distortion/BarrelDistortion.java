package dev.barrel.distortion;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(BarrelDistortion.MOD_ID)
public final class BarrelDistortion {
    public static final String MOD_ID = "barrel_distortion";

    public BarrelDistortion(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, BarrelConfig.SPEC);
        if (net.neoforged.fml.loading.FMLEnvironment.dist == Dist.CLIENT) {
            ClientBarrelRenderer.register(modBus);
        }
    }
}
