package org.polaris2023.caft.manager;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.polaris2023.caft.Config;
import org.polaris2023.caft.quantum.MountedQuantumPropulsionController;
import org.polaris2023.caft.quantum.QuantumEngineData;
import org.polaris2023.caft.quantum.QuantumEngineState;
import org.polaris2023.caft.quantum.QuantumMountType;

public class AircraftQuantumController extends MountedQuantumPropulsionController {
    private Player pilot;
    private Vec3 aircraftMotion = Vec3.ZERO;

    public AircraftQuantumController() {
        this(createEngineData());
    }

    private AircraftQuantumController(QuantumEngineData engineData) {
        super(
                QuantumMountType.CUSTOM_AIRCRAFT,
                Config.BLINK_MAX_DISTANCE.get() + 8.0D,
                engineData,
                new QuantumEngineState(engineData, 1.0D, 0, 0)
        );
    }

    private static QuantumEngineData createEngineData() {
        return new QuantumEngineData(24000, Config.SURGE_BASE_ENERGY_COST.get());
    }

    public void setPilot(Player pilot) {
        this.pilot = pilot;
        bindPlayer(pilot);
    }

    public void setAircraftMotion(Vec3 aircraftMotion) {
        this.aircraftMotion = aircraftMotion;
    }

    public void synchronizeFromDamageState(double durabilityRatio, int damageLevel, int overheatTicks) {
        setEngineState(new QuantumEngineState(getEngineData(), durabilityRatio, damageLevel, overheatTicks));
    }

    public double getSurgeStrength() {
        return 1.0D + aircraftMotion.length() * 0.5D;
    }

    @Override
    protected double getJumpDistance() {
        return super.getJumpDistance() + aircraftMotion.length() * 4.0D;
    }

    @Override
    protected Player getFeedbackPlayer() {
        return pilot;
    }
}
