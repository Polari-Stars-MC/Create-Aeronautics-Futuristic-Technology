package org.polaris2023.caft.content.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.polaris2023.caft.registry.ModBlocks;

import java.util.LinkedHashMap;
import java.util.Map;

public final class FutureEnergyCoreMultiblock {
    public static final int REQUIRED_CONDUITS = 4;
    public static final int REQUIRED_HEAT_SINKS = 12;
    public static final int MAX_INTEGRITY = 1 + REQUIRED_CONDUITS + REQUIRED_HEAT_SINKS;

    private static final BlockPos[] CONDUIT_OFFSETS = new BlockPos[]{
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1)
    };

    private static final BlockPos[] HEAT_SINK_OFFSETS = new BlockPos[]{
            new BlockPos(1, 0, 1),
            new BlockPos(1, 0, -1),
            new BlockPos(-1, 0, 1),
            new BlockPos(-1, 0, -1),
            new BlockPos(1, 1, 0),
            new BlockPos(-1, 1, 0),
            new BlockPos(0, 1, 1),
            new BlockPos(0, 1, -1),
            new BlockPos(1, -1, 0),
            new BlockPos(-1, -1, 0),
            new BlockPos(0, -1, 1),
            new BlockPos(0, -1, -1)
    };

    private FutureEnergyCoreMultiblock() {
    }

    public static ValidationResult validate(Level level, BlockPos controllerPos) {
        int matched = level.getBlockState(controllerPos).is(ModBlocks.FUTURE_ENERGY_CORE.get()) ? 1 : 0;
        Map<BlockPos, Block> missing = new LinkedHashMap<>();

        for (BlockPos offset : CONDUIT_OFFSETS) {
            BlockPos target = controllerPos.offset(offset);
            if (level.getBlockState(target).is(ModBlocks.ENERGY_CONDUIT.get())) {
                matched++;
            } else {
                missing.put(target, ModBlocks.ENERGY_CONDUIT.get());
            }
        }

        for (BlockPos offset : HEAT_SINK_OFFSETS) {
            BlockPos target = controllerPos.offset(offset);
            if (level.getBlockState(target).is(ModBlocks.HEAT_SINK.get())) {
                matched++;
            } else {
                missing.put(target, ModBlocks.HEAT_SINK.get());
            }
        }

        return new ValidationResult(matched, matched >= MAX_INTEGRITY, missing);
    }

    public static boolean isTopOutputFace(Direction direction) {
        return direction == Direction.UP;
    }

    public static boolean isBottomInputFace(Direction direction) {
        return direction == Direction.DOWN;
    }

    public record ValidationResult(int integrity, boolean complete, Map<BlockPos, Block> missingBlocks) {
    }
}
