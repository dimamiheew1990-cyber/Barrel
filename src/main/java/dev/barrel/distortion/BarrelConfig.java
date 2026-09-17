package dev.barrel.distortion;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Client preferences. Values are read on every frame so config reloads take effect immediately. */
public final class BarrelConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLED;
    public static final ModConfigSpec.DoubleValue STRENGTH;
    public static final ModConfigSpec.IntValue FOV_REFERENCE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("barrel_distortion");
        ENABLED = builder.comment("Enable barrel distortion.").define("enabled", true);
        STRENGTH = builder.comment("Radial distortion strength. 0.085 is tuned for 110 degree FOV.")
                .defineInRange("strength", 0.085D, 0.0D, 0.25D);
        FOV_REFERENCE = builder.comment("Informational FOV reference for the default strength.")
                .defineInRange("fovReference", 110, 30, 170);
        builder.pop();
        SPEC = builder.build();
    }

    private BarrelConfig() {}
}
