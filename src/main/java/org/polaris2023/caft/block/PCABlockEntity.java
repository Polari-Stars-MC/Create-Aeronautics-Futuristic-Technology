package org.polaris2023.caft.block;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.polaris2023.caft.manager.RemoteStressManager;

import java.util.List;
import java.util.UUID;

public class PCABlockEntity extends SimpleKineticBlockEntity {

    private static final double SCAN_RADIUS = 128.0;
    private int scanCooldown = 0;
    private RemoteStressManager.BaseStationInfo stationInfo;
    private boolean isNetworkReady = false;
    private int initDelay = 20;

    public PCABlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void initialize() {
        super.initialize();
        RemoteStressManager.registerBaseStation(level, worldPosition);
        stationInfo = RemoteStressManager.getBaseStation(worldPosition);

        if (stationInfo != null) {
            stationInfo.boundSubLevelId = null;
            stationInfo.cachedSubLevel = null;
        }
    }



    @Override
    public void tick() {
        if (initDelay > 0) {
            initDelay--;
            if (initDelay == 0) {
                isNetworkReady = true;
                getOrCreateNetwork().updateNetwork();
            }
            super.tick();
            return;
        }

        super.tick();

        if (level == null || level.isClientSide) return;

        if (!isNetworkReady) return;

        if (stationInfo == null) {
            stationInfo = RemoteStressManager.getBaseStation(worldPosition);
            if (stationInfo == null) return;
        }

        if (scanCooldown-- <= 0) {
            scanCooldown = 20;
            scanAndBindSubLevel();
        }

        boolean isActive = !level.hasNeighborSignal(worldPosition);
        float currentStress = calculateStressApplied();
        RemoteStressManager.updateBaseStation(worldPosition, currentStress, getSpeed(), isActive);

        if (stationInfo.boundSubLevelId != null &&
                (stationInfo.cachedSubLevel == null || stationInfo.cachedSubLevel.isRemoved())) {
            unbindSubLevel();
        }
    }

    private void scanAndBindSubLevel() {
        if (stationInfo == null) return;

        SubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) return;

        SubLevel nearestSubLevel = null;
        double nearestDistance = Double.MAX_VALUE;

        for (SubLevel subLevel : container.getAllSubLevels()) {
            if (subLevel.isRemoved()) continue;

            BoundingBox3dc bounds = subLevel.boundingBox();
            double distance = calculateDistanceToBounds(bounds);

            if (distance < SCAN_RADIUS && distance < nearestDistance) {
                nearestDistance = distance;
                nearestSubLevel = subLevel;
            }
        }

        if (nearestSubLevel != null) {
            UUID newId = nearestSubLevel.getUniqueId();
            if (!newId.equals(stationInfo.boundSubLevelId)) {
                RemoteStressManager.updateBaseStationBinding(worldPosition, newId, nearestSubLevel);
            }
        } else if (stationInfo.boundSubLevelId != null) {
            unbindSubLevel();
        }
    }

    private double calculateDistanceToBounds(BoundingBox3dc bounds) {
        double baseX = worldPosition.getX();
        double baseY = worldPosition.getY();
        double baseZ = worldPosition.getZ();

        double dx = 0, dy = 0, dz = 0;
        if (baseX < bounds.minX()) dx = bounds.minX() - baseX;
        if (baseX > bounds.maxX()) dx = baseX - bounds.maxX();
        if (baseY < bounds.minY()) dy = bounds.minY() - baseY;
        if (baseY > bounds.maxY()) dy = baseY - bounds.maxY();
        if (baseZ < bounds.minZ()) dz = bounds.minZ() - baseZ;
        if (baseZ > bounds.maxZ()) dz = baseZ - bounds.maxZ();

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private void unbindSubLevel() {
        if (stationInfo == null) return;
        RemoteStressManager.updateBaseStationBinding(worldPosition, null, null);
    }

    @Override
    public void remove() {
        if (stationInfo != null) {
            RemoteStressManager.removeBaseStation(worldPosition);
        }
        super.remove();
    }

    @Override
    public void onChunkUnloaded() {
        if (stationInfo != null) {
            RemoteStressManager.removeBaseStation(worldPosition);
        }
        super.onChunkUnloaded();
    }

    @Override
    public float calculateStressApplied() {
        if (!isNetworkReady) return 0f;
        if (initDelay > 0) return 0f;

        if (stationInfo == null) return 0f;

        if (level != null && level.hasNeighborSignal(worldPosition)) return 0f;

        if (stationInfo.boundSubLevelId == null) return 0f;

        if (stationInfo.cachedSubLevel == null || stationInfo.cachedSubLevel.isRemoved()) return 0f;

        double distance = calculateDistanceToBounds(stationInfo.cachedSubLevel.boundingBox());
        if (distance > SCAN_RADIUS) return 0f;

        return 16384f;
    }

    public UUID getBoundSubLevelId() {
        return stationInfo != null ? stationInfo.boundSubLevelId : null;
    }
}