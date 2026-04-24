package org.polaris2023.caft.datagen;

import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.apache.commons.lang3.StringUtils;
import org.polaris2023.caft.datagen.provider.FutureEnergyCoreStructureRecipeProvider;
import org.polaris2023.caft.registry.ModBlocks;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.polaris2023.caft.CreateAeronauticsFuturisticTechnology.MODID;

@EventBusSubscriber(modid = MODID)
public class DataEvents {

    @SubscribeEvent
    public static void onEvent(GatherDataEvent event) {
        event.createProvider(FutureEnergyCoreStructureRecipeProvider::new);
        event.createProvider((out, future) -> new LanguageProvider(out, MODID, "en_us") {
            @Override
            protected void addTranslations() {
                add("key.categories.caft", "Create Aeronautics Futuristic Technology");
                add("key.caft.del", "Delete physics by baka4n");
                add("key.caft.adjust", "Adjust physics by baka4n");
                add("tooltip.caft.future_energy_core.status", "AF %1$s/%2$s | Integrity %3$s/%4$s | Efficiency %5$s%%");
                add("jei.caft.future_energy_core.title", "Future Recipe Structure");
                add("rei.caft.future_energy_core.title", "Future Recipe Structure");
                add("emi.category.caft.future_energy_core", "Future Recipe Structure");
                add("jei.caft.future_energy_core.preview", "3D Preview");
                add("emi.caft.future_energy_core.preview", "3D Preview");
                add("rei.caft.future_energy_core.preview", "3D Preview");
                add("jei.caft.future_energy_core.controls.left", "Left-click drag to rotate");
                add("emi.caft.future_energy_core.controls.left", "Left-click drag to rotate");
                add("rei.caft.future_energy_core.controls.left", "Left-click drag to rotate, right-click drag to pan, scroll to zoom");
                add("jei.caft.future_energy_core.controls.right", "Right-click drag to translation");
                add("emi.caft.future_energy_core.controls.right", "Right-click drag to translation");
                add("jei.caft.future_energy_core.zoom", "Scroll to zoom");
                add("emi.caft.future_energy_core.zoom", "Scroll to zoom");
                add("caft.ponder.fe.header", "Future Energy Multiblock Building");
                add("caft.ponder.fe.text_1", "Let's assemble the Future Energy Core.");
                add("caft.ponder.fe.text_2", "Layer 0");
                add("caft.ponder.fe.text_3", "Layer 1");
                add("caft.ponder.fe.text_4", "The block changes when the building is complete.");
                add("caft.ponder.fe.text_5", "Layer 2");
                add("caft.ponder.fe.text_6", "Destroying any block causes an explosion.");
                blockLang(this,
                        ModBlocks.ENERGY_CONDUIT, ModBlocks.FUTURE_ENERGY_CORE, ModBlocks.HEAT_SINK);
                //  "rei.caft.future_energy_core.title": "Future Recipe Structure",
            }
        });
    }

    @SafeVarargs
    public static void blockLang(LanguageProvider provider, DeferredBlock<? extends Block>... blocks) {
        for (var block : blocks) {
            provider.addBlock(block, defaultLang(block.get().getDescriptionId()));
        }
    }

    public static String defaultLang(String name) {
        String lowerCase = name.toLowerCase(Locale.ROOT);
        return Arrays.stream(lowerCase.substring(lowerCase.lastIndexOf(".") + 1).split("_"))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }
}
