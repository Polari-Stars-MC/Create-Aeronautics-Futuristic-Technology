package org.polaris2023.caft.registry;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import org.polaris2023.caft.block.PCABlockEntity;

import static org.polaris2023.caft.CreateAeronauticsFuturisticTechnology.REGISTRATE;

public class ModBlockEntities {

    public static final BlockEntityEntry<PCABlockEntity> PCA = REGISTRATE
            .blockEntity("pca", PCABlockEntity::new)
            .validBlock(ModBlocks.PCA)
            .register();

    public static void init() {}
}
