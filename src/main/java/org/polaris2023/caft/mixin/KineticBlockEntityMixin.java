package org.polaris2023.caft.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.polaris2023.caft.manager.RemoteStressManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KineticBlockEntity.class)
public abstract class KineticBlockEntityMixin extends BlockEntity {

    @Shadow
    protected float speed;

    @Shadow
    public abstract void setSpeed(float speed);

    @Shadow
    public abstract float getSpeed();

    @Unique
    private float caft$remoteSpeed = 0;
    @Unique
    private boolean caft$isRemotelyPowered = false;

    public KineticBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (!(((KineticBlockEntity) (Object) this) instanceof SimpleKineticBlockEntity)) return;
        if (level == null || level.isClientSide) return;
        RemoteStressManager.BaseStationInfo remoteSource = RemoteStressManager.getRemotePowerSource(level, worldPosition);
        if (remoteSource != null && remoteSource.isActive) {
            float targetSpeed = remoteSource.speed;
            if (!caft$isRemotelyPowered) {
                caft$isRemotelyPowered = true;
            }
            caft$remoteSpeed = targetSpeed;
            if (Math.abs(speed - targetSpeed) > 0.001F) {
                setSpeed(targetSpeed);
            }
        } else if (caft$isRemotelyPowered) {
            caft$isRemotelyPowered = false;
            caft$remoteSpeed = 0;
            if (Math.abs(speed) > 0.001F) {
                setSpeed(0);
            }
        }
    }

    @Inject(method = "calculateStressApplied", at = @At("HEAD"), cancellable = true)
    private void calculateStressApplied(CallbackInfoReturnable<Float> cir) {
        if (level != null && !level.isClientSide && caft$isRemotelyPowered) {
            // 被远程供能时，不消耗本地网络的应力
            cir.setReturnValue(0f);
        }
    }

    /**
     * 修改应力容量计算 - 被远程供能时不提供应力容量
     */
    @Inject(method = "calculateAddedStressCapacity", at = @At("HEAD"), cancellable = true)
    private void caft$calculateAddedStressCapacity(CallbackInfoReturnable<Float> cir) {
        if (level != null && !level.isClientSide && caft$isRemotelyPowered) {
            cir.setReturnValue(0f);
        }
    }
}
