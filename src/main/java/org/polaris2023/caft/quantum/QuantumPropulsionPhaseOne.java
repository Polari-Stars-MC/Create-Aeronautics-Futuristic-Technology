package org.polaris2023.caft.quantum;

import org.polaris2023.caft.CreateAeronauticsFuturisticTechnology;

import java.util.List;

public final class QuantumPropulsionPhaseOne {

    public static final QuantumPropulsionBlueprint MECHANICAL_DYNAMICS_QPS = new QuantumPropulsionBlueprint(
            "Quantum Propulsion System",
            List.of(
                    new QuantumPropulsionBlueprint.QuantumDriveProfile(
                            QuantumDriveType.BLINK_DRIVE,
                            "Blink Drive",
                            "Short-range spatial translation for precise repositioning, hazard avoidance, and line-breaking movement.",
                            "Compresses traversable space into a brief, deterministic jump while preserving collision legality and landing validation."
                    ),
                    new QuantumPropulsionBlueprint.QuantumDriveProfile(
                            QuantumDriveType.SURGE_DRIVE,
                            "Surge Drive",
                            "Burst propulsion for rapid entry, inertia correction, and short-term acceleration spikes.",
                            "Dumps buffered energy into a directional thrust pulse that resets heading and amplifies momentum for a fraction of a second."
                    )
            ),
            List.of(
                    "The Quantum Propulsion System is not treated as magic teleportation. It is an industrial engine that temporarily compresses local distance, letting the frame cross a route that has been engineered into a survivable shortcut.",
                    "Because the engine has to reconcile mass distribution, landing topology, and inertia vectors in real time, low charge, heat saturation, or frame damage can destabilize the transfer. Engineers value it for mobility, but only when the machine is maintained like any other precision drivetrain."
            ),
            new QuantumPropulsionBlueprint.QuantumBalanceFramework(
                    new QuantumPropulsionBlueprint.ValueRange(200, 400, "FE/RF", "Configurable base cost before distance, thrust, and load modifiers."),
                    new QuantumPropulsionBlueprint.ValueRange(16, 32, "blocks", "Maximum blink distance drops when chassis load or instability increases."),
                    new QuantumPropulsionBlueprint.ValueRange(3, 8, "seconds", "Upgrade modules can shave off cooldown at the cost of idle efficiency."),
                    new QuantumPropulsionBlueprint.ValueRange(0, 25, "percent", "Instability rises under low charge, damage, and heat stress."),
                    new QuantumPropulsionBlueprint.ValueRange(0.5, 2.0, "blocks", "Engine tier and stabilization quality define the landing error radius."),
                    List.of(
                            "Blink cost = base energy + distance coefficient + load modifier",
                            "Surge cost = base energy + thrust coefficient + momentum modifier",
                            "Failure chance = baseline fault + low charge penalty + damage penalty - stability bonuses",
                            "Precision error = base radius + over-range penalty - stabilizer bonuses"
                    ),
                    List.of(
                            "Blink Mk1: 280 FE, 20 block cap, 6 second cooldown, +/-1.5 block error",
                            "Blink Mk2: 340 FE, 28 block cap, 5 second cooldown, +/-0.8 block error",
                            "Surge Mk1: 220 FE, +180 percent speed for 0.8 seconds, 4 second cooldown",
                            "Surge Mk2: 320 FE, +250 percent speed for 1.2 seconds, 5 second cooldown"
                    )
            ),
            new QuantumPropulsionBlueprint.QuantumGameplayFramework(
                    List.of(QuantumControlMode.CHARGE, QuantumControlMode.FIXED_DISTANCE, QuantumControlMode.UPGRADE_LOCKED),
                    "Direction follows the player's current look vector by default.",
                    "Auto-navigation keeps the current heading and executes only a forward blink, shortening or denying the move if collision risk is too high.",
                    List.of(
                            "Blink must validate that the destination volume is not embedded in solid terrain.",
                            "Blink may nudge the landing point toward the nearest safe space when the target edge is hazardous.",
                            "Surge preserves momentum, then layers on a directional thrust pulse instead of replacing all velocity.",
                            "Walls, slopes, fluids, and adhesive surfaces can dampen or redirect Surge behavior."
                    ),
                    List.of(
                            QuantumFailureConsequence.OFFSET_BLINK,
                            QuantumFailureConsequence.ENERGY_BACKLASH,
                            QuantumFailureConsequence.TEMPORARY_STUN,
                            QuantumFailureConsequence.TRAJECTORY_DRIFT,
                            QuantumFailureConsequence.EXTENDED_COOLDOWN,
                            QuantumFailureConsequence.EXTRA_MODULE_WEAR
                    ),
                    List.of(
                            "The system is intended to outperform ordinary movement only in burst windows, emergency escapes, and precision route changes.",
                            "High-end builds buy lower cooldowns and tighter precision, but the manufacturing cost and maintenance burden rise with each tier."
                    )
            ),
            new QuantumPropulsionBlueprint.QuantumAudioVisualDirection(
                    List.of(
                            "Back-mounted particle vortex that tightens inward during charge-up.",
                            "Compressed halo and screen-edge refraction to imply local space distortion.",
                            "Charge state instability should make the vortex flicker rather than simply brighten."
                    ),
                    List.of(
                            "Short afterimages and filament-like trail particles during translation.",
                            "Arrival shock ring, dust kick-up, or spark burst depending on the receiving surface.",
                            "Surge should read as thrust wake and impulse ripples rather than teleport smoke."
                    ),
                    List.of(
                            "Charge: high-frequency modulated hum that climbs with selected distance or thrust.",
                            "Execution: sharp crack layered with a vacuum-tear transient.",
                            "Cooldown: low-frequency heartbeat echo that decays as the engine settles.",
                            "Fault state: clipped warning buzz, electrical distortion, and short burst discharge."
                    ),
                    List.of(
                            "Low-energy operation should sound thin and visually unstable.",
                            "High-grade stabilization should make effects feel more focused and engineered, not louder.",
                            "Failure states can briefly desync HUD overlays, double the afterimage, or introduce jitter."
                    )
            ),
            new QuantumPropulsionBlueprint.QuantumModularFramework(
                    "Base Engine Mk1 -> Reinforced Engine Mk2 -> Precision Engine Mk3",
                    List.of(
                            "Engine core selects Blink-only, Surge-only, or dual-mode behavior.",
                            "Energy bus feeds FE/RF power and supports a local buffer for burst discharge.",
                            "Stability frame manages mass distribution, precision, and fault tolerance.",
                            "Control logic defines charge behavior, lock-on rules, and auto-navigation support."
                    ),
                    List.of(
                            QuantumUpgradeModule.COOLING_ACCELERATOR,
                            QuantumUpgradeModule.JUMP_AMPLIFIER,
                            QuantumUpgradeModule.STABILITY_CORE,
                            QuantumUpgradeModule.ENERGY_EFFICIENCY_LOGIC_BOARD,
                            QuantumUpgradeModule.INERTIA_RECTIFIER,
                            QuantumUpgradeModule.COLLISION_PREDICTION_UNIT
                    ),
                    List.of(
                            "Cooling Accelerator: cuts cooldown but increases passive drain or thermal overhead.",
                            "Jump Amplifier: extends blink distance while magnifying precision penalties near the cap.",
                            "Stability Core: reduces error and failure chance, especially on heavily loaded frames.",
                            "Energy Efficiency Logic Board: lowers cost per use and rewards repeated operation.",
                            "Inertia Rectifier: improves Surge heading correction and landing posture.",
                            "Collision Prediction Unit: pre-validates dangerous landing spaces and lowers crash risk."
                    )
            )
    );

    private QuantumPropulsionPhaseOne() {
    }

    public static void bootstrap() {
        CreateAeronauticsFuturisticTechnology.LOGGER.info(
                "Loaded phase 1 design for {} with {} drive profiles and {} upgrade modules.",
                MECHANICAL_DYNAMICS_QPS.systemName(),
                MECHANICAL_DYNAMICS_QPS.driveProfiles().size(),
                MECHANICAL_DYNAMICS_QPS.modularFramework().upgradeModules().size()
        );
    }
}
