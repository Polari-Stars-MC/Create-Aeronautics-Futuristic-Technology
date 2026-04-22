package org.polaris2023.caft.registry;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import org.polaris2023.caft.block.QuantumEngineBlockEntity;

import static org.polaris2023.caft.CreateAeronauticsFuturisticTechnology.REGISTRATE;

public class ModBlockEntities {
    public static final BlockEntityEntry<QuantumEngineBlockEntity> QUANTUM_ENGINE = REGISTRATE
            .<QuantumEngineBlockEntity>blockEntity("quantum_engine", QuantumEngineBlockEntity::new)
            .validBlocks(ModBlocks.QUANTUM_ENGINE)
            .register();

    public static void init() {}
}
