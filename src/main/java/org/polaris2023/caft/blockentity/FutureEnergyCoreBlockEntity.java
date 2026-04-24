package org.polaris2023.caft.blockentity;

import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import org.polaris2023.caft.Config;
import org.polaris2023.caft.block.multi.FutureEnergyCoreMultiblock;
import org.polaris2023.caft.content.energy.FutureForceStorage;
import org.polaris2023.caft.block.FutureEnergyCoreBlock;
import org.polaris2023.caft.network.FutureEnergyCoreSyncPacket;
import org.polaris2023.caft.registry.ModBlockEntities;

public class FutureEnergyCoreBlockEntity extends BlockEntity {
    private final FutureForceStorage energyStorage = new FutureForceStorage(Config.ENERGY_CAPACITY.get());

    private int rotationalInputBuffer;
    private int heat;
    private int activeTicks;
    private int idleTicks;
    private int scanCooldown;
    private int animationTick;
    private int requiredIntegrity = FutureEnergyCoreMultiblock.MAX_INTEGRITY;
    private boolean assembledEffectPlayed;

    public FutureEnergyCoreBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FUTURE_ENERGY_CORE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FutureEnergyCoreBlockEntity blockEntity) {
        blockEntity.animationTick++;
        if (level.isClientSide) {
            blockEntity.clientTick();
            return;
        }
        blockEntity.serverTick((ServerLevel) level);
    }

    private void clientTick() {
        if (!this.isActive() || this.level == null) {
            return;
        }

        if (this.level.random.nextInt(3) == 0) {
            float glow = 0.45F + 0.035F * this.getIntegrity();
            this.level.addParticle(
                    new DustParticleOptions(new Vector3f(0.25F, glow, 1.0F), 1.0F),
                    this.worldPosition.getX() + 0.5D,
                    this.worldPosition.getY() + 0.7D,
                    this.worldPosition.getZ() + 0.5D,
                    0.0D,
                    0.01D,
                    0.0D
            );
        }
    }

    private void serverTick(ServerLevel level) {
        this.energyStorage.setCapacity(Config.ENERGY_CAPACITY.get());
        this.scanCooldown--;
        if (this.scanCooldown <= 0) {
            this.scanCooldown = Config.STRUCTURE_SCAN_INTERVAL.get();
            this.refreshStructure();
        }

        boolean complete = this.hasCompleteStructure();
        if (complete && this.rotationalInputBuffer > 0) {
            this.activeTicks++;
            this.idleTicks = 0;
            this.heat += Config.HEAT_GAIN_PER_TICK.get();
        } else {
            this.idleTicks++;
            this.activeTicks = 0;
            this.heat = Math.max(0, this.heat - Config.PASSIVE_COOLDOWN_PER_TICK.get());
        }

        double efficiency = this.getCurrentEfficiency();
        int inputBudget = Math.min(this.rotationalInputBuffer, Config.ROTATION_INPUT_PER_TICK.get());
        int converted = this.energyStorage.receive((int) Math.floor(inputBudget * efficiency), false);
        this.rotationalInputBuffer = Math.max(0, this.rotationalInputBuffer - inputBudget);

        boolean active = complete;
        this.updateBlockState(active, this.getIntegrity());

        if (complete && !this.assembledEffectPlayed) {
            this.playAssemblyEffects(level);
            this.assembledEffectPlayed = true;
        } else if (!complete) {
            this.assembledEffectPlayed = false;
        }

        this.syncToTrackingPlayers();
        this.setChanged();
    }

    public void requestImmediateScan() {
        this.scanCooldown = 0;
    }

    public int acceptRotationalInput(Direction side, int amount) {
        if (!FutureEnergyCoreMultiblock.isBottomInputFace(side)) {
            return 0;
        }

        int accepted = Math.clamp(amount, 0, Config.ROTATION_INPUT_PER_TICK.get());
        this.rotationalInputBuffer += accepted;
        this.setChanged();
        return accepted;
    }

    public int extractAeronauticalForce(Direction side, int amount, boolean simulate) {
        if (!FutureEnergyCoreMultiblock.isTopOutputFace(side)) {
            return 0;
        }
        return this.energyStorage.extract(Math.min(amount, Config.AF_OUTPUT_PER_TICK.get()), simulate);
    }

    public double getCurrentEfficiency() {
        double integrityRatio = this.requiredIntegrity <= 0 ? 0.0D : (double) this.getIntegrity() / this.requiredIntegrity;
        double structureEfficiency = Mth.lerp(integrityRatio, Config.BASE_EFFICIENCY.get(), Config.COMPLETE_EFFICIENCY.get());
        int heatOverflow = Math.max(0, this.heat - Config.OVERHEAT_THRESHOLD_TICKS.get());
        double heatRatio = heatOverflow <= 0 ? 0.0D : Math.min(1.0D, (double) heatOverflow / Config.OVERHEAT_THRESHOLD_TICKS.get());
        double heatPenalty = heatRatio * Config.OVERHEAT_MAX_PENALTY.get();
        return Math.max(0.0D, structureEfficiency - heatPenalty);
    }

    public int getRequiredIntegrity() {
        return this.requiredIntegrity;
    }

    public int getIntegrity() {
        return this.getBlockState().getValue(FutureEnergyCoreBlock.INTEGRITY);
    }

    public boolean isActive() {
        return this.getBlockState().getValue(FutureEnergyCoreBlock.ACTIVE);
    }

    public int getEnergyStored() {
        return this.energyStorage.getAmount();
    }

    public int getCapacity() {
        return this.energyStorage.getCapacity();
    }

    public int getHeat() {
        return this.heat;
    }

    public int getAnimationTick() {
        return this.animationTick;
    }

    public Component describeStatus() {
        return Component.translatable(
                "tooltip.caft.future_energy_core.status",
                this.getEnergyStored(),
                this.getCapacity(),
                this.getIntegrity(),
                this.getRequiredIntegrity(),
                Math.round(this.getCurrentEfficiency() * 100.0D)
        );
    }

    public void applyClientSync(int energy, int heat, int integrity, int requiredIntegrity, boolean active) {
        this.energyStorage.setAmount(energy);
        this.heat = heat;
        this.requiredIntegrity = requiredIntegrity;
        this.updateBlockState(active, integrity);
    }

    public void onControllerRemoved() {
        if (this.level instanceof ServerLevel serverLevel) {
            this.clearInternalState();
            this.detonateCore(serverLevel);
            this.setChanged();
        }
    }

    private void refreshStructure() {
        if (this.level == null) {
            return;
        }

        FutureEnergyCoreMultiblock.ValidationResult result = FutureEnergyCoreMultiblock.validate(this.level, this.worldPosition);
        this.requiredIntegrity = result.requiredIntegrity();
        if (!result.complete() && this.isActive() && this.level instanceof ServerLevel serverLevel) {
            this.playBreakEffects(serverLevel);
        }
        this.updateBlockState(false, result.integrity());
    }

    private void updateBlockState(boolean active, int integrity) {
        if (this.level == null) {
            return;
        }

        if (!this.level.getBlockState(this.worldPosition).is(this.getBlockState().getBlock())) {
            return;
        }

        BlockState currentState = this.getBlockState();
        BlockState updatedState = currentState
                .setValue(FutureEnergyCoreBlock.ACTIVE, active)
                .setValue(FutureEnergyCoreBlock.INTEGRITY, integrity);
        if (updatedState != currentState) {
            this.level.setBlock(this.worldPosition, updatedState, Block.UPDATE_CLIENTS);
        }
    }

    private void invalidateStructure(ServerLevel level, boolean playBreakEffects) {
        this.clearInternalState();
        this.updateBlockState(false, 0);
        if (playBreakEffects) {
            this.playBreakEffects(level);
        }
        this.syncToTrackingPlayers();
    }

    private void clearInternalState() {
        this.energyStorage.setAmount(0);
        this.rotationalInputBuffer = 0;
        this.heat = 0;
        this.activeTicks = 0;
        this.idleTicks = 0;
        this.requiredIntegrity = FutureEnergyCoreMultiblock.MAX_INTEGRITY;
        this.assembledEffectPlayed = false;
    }

    private boolean hasCompleteStructure() {
        return this.requiredIntegrity > 0 && this.getIntegrity() >= this.requiredIntegrity;
    }

    private void playAssemblyEffects(ServerLevel level) {
        for (int i = 0; i < 20; i++) {
            level.sendParticles(
                    ParticleTypes.END_ROD,
                    this.worldPosition.getX() + 0.5D,
                    this.worldPosition.getY() + 0.6D,
                    this.worldPosition.getZ() + 0.5D,
                    1,
                    0.35D,
                    0.35D,
                    0.35D,
                    0.02D
            );
        }
        level.playSound(null, this.worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.8F, 1.2F);
    }

    private void playBreakEffects(ServerLevel level) {
        level.sendParticles(
                ParticleTypes.EXPLOSION,
                this.worldPosition.getX() + 0.5D,
                this.worldPosition.getY() + 0.5D,
                this.worldPosition.getZ() + 0.5D,
                6,
                0.25D,
                0.25D,
                0.25D,
                0.02D
        );
        level.playSound(null, this.worldPosition, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 0.5F, 1.4F);
    }

    private void detonateCore(ServerLevel level) {
        level.explode(
                null,
                this.worldPosition.getX() + 0.5D,
                this.worldPosition.getY() + 0.5D,
                this.worldPosition.getZ() + 0.5D,
                Config.CORE_BREAK_EXPLOSION_POWER.get().floatValue(),
                ExplosionInteraction.BLOCK
        );
    }

    private void syncToTrackingPlayers() {
        if (!(this.level instanceof ServerLevel)) {
            return;
        }
        VeilPacketManager.tracking(this).sendPacket(new FutureEnergyCoreSyncPacket(
                this.worldPosition,
                this.energyStorage.getAmount(),
                this.heat,
                this.getIntegrity(),
                this.requiredIntegrity,
                this.isActive()
        ));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("EnergyStorage", this.energyStorage.serializeNBT());
        tag.putInt("RotationalInputBuffer", this.rotationalInputBuffer);
        tag.putInt("Heat", this.heat);
        tag.putInt("ActiveTicks", this.activeTicks);
        tag.putInt("IdleTicks", this.idleTicks);
        tag.putInt("RequiredIntegrity", this.requiredIntegrity);
        tag.putBoolean("AssembledEffectPlayed", this.assembledEffectPlayed);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.energyStorage.deserializeNBT(tag.getCompound("EnergyStorage"));
        this.rotationalInputBuffer = tag.getInt("RotationalInputBuffer");
        this.heat = tag.getInt("Heat");
        this.activeTicks = tag.getInt("ActiveTicks");
        this.idleTicks = tag.getInt("IdleTicks");
        this.requiredIntegrity = tag.contains("RequiredIntegrity") ? tag.getInt("RequiredIntegrity") : FutureEnergyCoreMultiblock.MAX_INTEGRITY;
        this.assembledEffectPlayed = tag.getBoolean("AssembledEffectPlayed");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        this.saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        this.loadAdditional(tag, registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
