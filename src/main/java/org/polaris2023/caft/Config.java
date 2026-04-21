package org.polaris2023.caft;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue PCA_STRESS_IMPACT = BUILDER
            .comment("Base stress impact of the PCA block at 1 RPM.")
            .defineInRange("kinetics.pcaStressImpact", 8.0D, 0.0D, Double.MAX_VALUE);


    static final ModConfigSpec SPEC = BUILDER.build();

}
