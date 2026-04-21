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

    public PCABlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void initialize() {
        super.initialize();
        // 先注册
        RemoteStressManager.registerBaseStation(level, worldPosition);
        stationInfo = RemoteStressManager.getBaseStation(worldPosition);

        // 立即扫描一次，避免延迟导致的应力突变
        if (level != null && !level.isClientSide) {
            scanAndBindSubLevel();
        }
    }



    @Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide) return;

        if (stationInfo == null) {
            stationInfo = RemoteStressManager.getBaseStation(worldPosition);
            if (stationInfo == null) return;
        }

        // 先更新绑定状态
        if (scanCooldown-- <= 0) {
            scanCooldown = 20;
            scanAndBindSubLevel();
        }

        // 检查绑定的SubLevel是否有效
        if (stationInfo.boundSubLevelId != null &&
                (stationInfo.cachedSubLevel == null || stationInfo.cachedSubLevel.isRemoved())) {
            unbindSubLevel();
        }

        // 最后才更新基站状态（让calculateStressApplied使用最新状态）
        boolean isActive = !level.hasNeighborSignal(worldPosition);
        float currentStress = calculateStressApplied();
        RemoteStressManager.updateBaseStation(worldPosition, currentStress, getSpeed(), isActive);
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
        // 未初始化完成时不应施加应力
        if (stationInfo == null) {
            System.out.println("[PCA] StationInfo null at " + worldPosition);
            return 0f;
        }

        // 检查红石信号
        if (level != null && level.hasNeighborSignal(worldPosition)) {
            System.out.println("[PCA] Has redstone signal at " + worldPosition);
            return 0f;
        }

        // 检查是否有效绑定了 SubLevel
        if (stationInfo.boundSubLevelId == null) {
            System.out.println("[PCA] No bound SubLevel at " + worldPosition);
            return 0f;
        }

        // 检查绑定的 SubLevel 是否在有效距离内
        if (stationInfo.cachedSubLevel == null || stationInfo.cachedSubLevel.isRemoved()) {
            System.out.println("[PCA] Cached SubLevel invalid at " + worldPosition);
            return 0f;
        }

        // 检查距离
        double distance = calculateDistanceToBounds(stationInfo.cachedSubLevel.boundingBox());
        if (distance > SCAN_RADIUS) {
            System.out.println("[PCA] SubLevel too far: " + distance + " at " + worldPosition);
            return 0f;
        }

        System.out.println("[PCA] Applying stress: 0f at " + worldPosition);
        return 0f;
    }

    public UUID getBoundSubLevelId() {
        return stationInfo != null ? stationInfo.boundSubLevelId : null;
    }
}