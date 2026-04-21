package org.polaris2023.caft.manager;

import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 远程应力管理器
 */
public class RemoteStressManager {

    // 基站信息
    private static final Map<BlockPos, BaseStationInfo> BASE_STATIONS = new ConcurrentHashMap<>();

    // SubLevel -> 提供应力的基站列表
    private static final Map<UUID, List<BaseStationInfo>> SUBLEVEL_TO_BASES = new ConcurrentHashMap<>();

    // 被远程供能的机械 -> 提供应力的基站
    private static final Map<BlockPos, BaseStationInfo> POWERED_MACHINES = new ConcurrentHashMap<>();

    /**
     * 基站信息类
     */
    public static class BaseStationInfo {
        public final Level level;
        public final BlockPos pos;
        public UUID boundSubLevelId;
        public SubLevel cachedSubLevel;
        public boolean isActive;
        public float stress;
        public float speed;

        public BaseStationInfo(Level level, BlockPos pos) {
            this.level = level;
            this.pos = pos.immutable();
            this.isActive = true;
            this.stress = 16384f;
            this.speed = 64f;
        }

        public void updateFromBlockEntity(float stress, float speed, boolean active) {
            this.stress = stress;
            this.speed = speed;
            this.isActive = active;
        }
    }

    /**
     * 注册基站
     */
    public static void registerBaseStation(Level level, BlockPos pos) {
        BlockPos immutablePos = pos.immutable();
        if (!BASE_STATIONS.containsKey(immutablePos)) {
            BASE_STATIONS.put(immutablePos, new BaseStationInfo(level, immutablePos));
        }
    }

    /**
     * 移除基站
     */
    public static void removeBaseStation(BlockPos pos) {
        BlockPos immutablePos = pos.immutable();
        BaseStationInfo info = BASE_STATIONS.remove(immutablePos);
        if (info != null && info.boundSubLevelId != null) {
            // 移除该基站对SubLevel的供能
            List<BaseStationInfo> bases = SUBLEVEL_TO_BASES.get(info.boundSubLevelId);
            if (bases != null) {
                bases.remove(info);
                if (bases.isEmpty()) {
                    SUBLEVEL_TO_BASES.remove(info.boundSubLevelId);
                }
            }
        }
    }

    /**
     * 更新基站绑定的SubLevel
     */
    public static void updateBaseStationBinding(BlockPos pos, UUID subLevelId, SubLevel subLevel) {
        BaseStationInfo info = BASE_STATIONS.get(pos.immutable());
        if (info == null) return;

        // 移除旧绑定
        if (info.boundSubLevelId != null) {
            List<BaseStationInfo> bases = SUBLEVEL_TO_BASES.get(info.boundSubLevelId);
            if (bases != null) {
                bases.remove(info);
                if (bases.isEmpty()) {
                    SUBLEVEL_TO_BASES.remove(info.boundSubLevelId);
                }
            }
            // 清除该基站供能的所有机械
            clearPoweredMachinesForBase(info);
        }

        // 添加新绑定
        info.boundSubLevelId = subLevelId;
        info.cachedSubLevel = subLevel;

        if (subLevelId != null && subLevel != null) {
            SUBLEVEL_TO_BASES.computeIfAbsent(subLevelId, k -> new ArrayList<>()).add(info);
            // 注册新SubLevel内的所有机械
            registerMachinesInSubLevel(info, subLevel);
        }
    }

    /**
     * 清除指定基站供能的所有机械
     */
    private static void clearPoweredMachinesForBase(BaseStationInfo info) {
        Iterator<Map.Entry<BlockPos, BaseStationInfo>> iterator = POWERED_MACHINES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, BaseStationInfo> entry = iterator.next();
            if (entry.getValue() == info) {
                iterator.remove();
            }
        }
    }

    /**
     * 注册SubLevel内的所有机械
     */
    private static void registerMachinesInSubLevel(BaseStationInfo info, SubLevel subLevel) {
        if (!info.isActive) return;

        LevelPlot plot = subLevel.getPlot();
        if (plot == null) return;

        Collection<PlotChunkHolder> loadedChunks = plot.getLoadedChunks();

        for (PlotChunkHolder chunkHolder : loadedChunks) {
            LevelChunk chunk = chunkHolder.getChunk();
            if (chunk == null) continue;

            for (BlockPos machinePos : chunk.getBlockEntities().keySet()) {
                // 注册这个机械由当前基站供能
                if (!POWERED_MACHINES.containsKey(machinePos)) {
                    POWERED_MACHINES.put(machinePos, info);
                }
            }
        }
    }

    /**
     * 刷新SubLevel内的机械注册（当SubLevel有新chunk加载时调用）
     */
    public static void refreshSubLevelMachines(UUID subLevelId) {
        List<BaseStationInfo> bases = SUBLEVEL_TO_BASES.get(subLevelId);
        if (bases == null) return;

        for (BaseStationInfo info : bases) {
            if (info.cachedSubLevel != null && !info.cachedSubLevel.isRemoved()) {
                registerMachinesInSubLevel(info, info.cachedSubLevel);
            }
        }
    }

    /**
     * 查询某个机械方块是否应该被远程供能
     */
    public static BaseStationInfo getRemotePowerSource(Level level, BlockPos machinePos) {
        return POWERED_MACHINES.get(machinePos.immutable());
    }

    /**
     * 更新基站状态
     */
    public static void updateBaseStation(BlockPos pos, float stress, float speed, boolean active) {
        BaseStationInfo info = BASE_STATIONS.get(pos.immutable());
        if (info != null) {
            boolean wasActive = info.isActive;
            info.updateFromBlockEntity(stress, speed, active);

            // 如果激活状态变化，需要重新注册机械
            if (wasActive != active && info.boundSubLevelId != null && info.cachedSubLevel != null) {
                if (active) {
                    registerMachinesInSubLevel(info, info.cachedSubLevel);
                } else {
                    clearPoweredMachinesForBase(info);
                }
            }
        }
    }

    /**
     * 获取基站信息
     */
    public static BaseStationInfo getBaseStation(BlockPos pos) {
        return BASE_STATIONS.get(pos.immutable());
    }

    /**
     * 获取所有基站
     */
    public static Collection<BaseStationInfo> getAllBaseStations() {
        return BASE_STATIONS.values();
    }

    /**
     * 清除所有数据（用于世界卸载时）
     */
    public static void clear() {
        BASE_STATIONS.clear();
        SUBLEVEL_TO_BASES.clear();
        POWERED_MACHINES.clear();
    }
}