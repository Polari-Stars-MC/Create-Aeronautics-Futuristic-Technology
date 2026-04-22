package org.polaris2023.caft.quantum;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.polaris2023.caft.Config;

public final class QuantumJumpCalculator {
    private QuantumJumpCalculator() {
    }

    public static Vector3d computeJumpPosition(Player player, double distance, double precisionError) {
        Level level = player.level();
        double clampedDistance = Mth.clamp(distance, 0.0D, Config.BLINK_MAX_DISTANCE.get());
        Vec3 origin = player.position();
        Vec3 direction = getSafeDirection(player);
        if (direction == Vec3.ZERO) {
            return new Vector3d(origin.x, origin.y, origin.z);
        }

        Vec3 intendedTarget = origin.add(direction.scale(clampedDistance));

        BlockHitResult hitResult = level.clip(new ClipContext(
                player.getEyePosition(),
                intendedTarget.add(0.0D, player.getEyeHeight(), 0.0D),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));

        Vec3 safeTarget = intendedTarget;
        if (hitResult.getType() != HitResult.Type.MISS) {
            safeTarget = hitResult.getLocation().subtract(direction.scale(0.75D));
        }

        safeTarget = applyPrecisionError(player, safeTarget, precisionError);
        safeTarget = resolveSafeLanding(level, player.getBoundingBox(), safeTarget, direction);
        safeTarget = sanitizeTarget(level, player.getBoundingBox(), origin, safeTarget);
        return new Vector3d(safeTarget.x, safeTarget.y, safeTarget.z);
    }

    public static void applyConfiguredMomentum(Player player, Vec3 originalVelocity) {
        if (Config.PRESERVE_MOMENTUM_ON_BLINK.get()) {
            player.setDeltaMovement(originalVelocity);
        } else {
            player.setDeltaMovement(Vec3.ZERO);
        }
        player.hurtMarked = true;
    }

    public static Vec3 sanitizeTarget(Level level, AABB entityBounds, Vec3 fallback, Vec3 candidate) {
        if (!isFinite(candidate)) {
            return fallback;
        }

        WorldBorder border = level.getWorldBorder();
        double horizontalMargin = Math.max(entityBounds.getXsize(), entityBounds.getZsize()) * 0.5D + 1.0D;
        double minX = border.getMinX() + horizontalMargin;
        double maxX = border.getMaxX() - horizontalMargin;
        double minZ = border.getMinZ() + horizontalMargin;
        double maxZ = border.getMaxZ() - horizontalMargin;

        if (minX > maxX || minZ > maxZ) {
            return fallback;
        }

        double clampedX = Mth.clamp(candidate.x, minX, maxX);
        double clampedZ = Mth.clamp(candidate.z, minZ, maxZ);
        double minY = level.getMinBuildHeight();
        double maxY = level.getMaxBuildHeight() - entityBounds.getYsize();
        double clampedY = Mth.clamp(candidate.y, minY, maxY);
        Vec3 sanitized = new Vec3(clampedX, clampedY, clampedZ);

        if (!isFinite(sanitized)) {
            return fallback;
        }

        AABB movedBounds = entityBounds.move(sanitized.subtract(entityBounds.getCenter()));
        return level.noCollision(null, movedBounds) ? sanitized : fallback;
    }

    private static Vec3 applyPrecisionError(Player player, Vec3 target, double precisionError) {
        double clampedError = Mth.clamp(precisionError, Config.PRECISION_ERROR_MIN.get(), Config.PRECISION_ERROR_MAX.get());
        if (clampedError <= 0.0D) {
            return target;
        }

        double yawRadians = Math.toRadians(player.getYRot());
        Vec3 lateral = new Vec3(-Math.sin(yawRadians), 0.0D, Math.cos(yawRadians));
        double lateralOffset = (player.getRandom().nextDouble() * 2.0D - 1.0D) * clampedError;
        double forwardOffset = (player.getRandom().nextDouble() * 2.0D - 1.0D) * (clampedError * 0.25D);
        return target.add(lateral.scale(lateralOffset)).add(player.getLookAngle().scale(forwardOffset));
    }

    private static Vec3 resolveSafeLanding(Level level, AABB playerBounds, Vec3 candidate, Vec3 direction) {
        AABB movedBounds = playerBounds.move(candidate.subtract(playerBounds.getCenter()));
        if (level.noCollision(null, movedBounds)) {
            return findGroundedPosition(level, movedBounds, candidate);
        }

        for (double retreat = 0.5D; retreat <= 3.0D; retreat += 0.5D) {
            Vec3 fallback = candidate.subtract(direction.scale(retreat));
            AABB fallbackBounds = playerBounds.move(fallback.subtract(playerBounds.getCenter()));
            if (level.noCollision(null, fallbackBounds)) {
                return findGroundedPosition(level, fallbackBounds, fallback);
            }
        }

        return playerBounds.getCenter();
    }

    private static Vec3 findGroundedPosition(Level level, AABB bounds, Vec3 candidate) {
        BlockPos below = BlockPos.containing(candidate.x, bounds.minY - 0.1D, candidate.z);
        if (!level.getBlockState(below).isAir()) {
            return candidate;
        }

        for (int drop = 1; drop <= 3; drop++) {
            BlockPos checkPos = below.below(drop);
            if (!level.getBlockState(checkPos).isAir()) {
                double y = checkPos.getY() + 1.0D;
                return new Vec3(candidate.x, y, candidate.z);
            }
        }

        return candidate;
    }

    private static Vec3 getSafeDirection(Player player) {
        Vec3 lookAngle = player.getLookAngle();
        if (!isFinite(lookAngle)) {
            return Vec3.ZERO;
        }

        double lengthSquared = lookAngle.lengthSqr();
        if (lengthSquared < 1.0E-8D) {
            Direction direction = player.getDirection();
            return new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ());
        }

        return lookAngle.normalize();
    }

    private static boolean isFinite(Vec3 vector) {
        return Double.isFinite(vector.x) && Double.isFinite(vector.y) && Double.isFinite(vector.z);
    }
}
