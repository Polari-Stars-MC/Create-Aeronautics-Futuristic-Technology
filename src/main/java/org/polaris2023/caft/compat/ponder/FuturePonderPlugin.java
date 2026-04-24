package org.polaris2023.caft.compat.ponder;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.api.scene.SelectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.polaris2023.caft.block.FutureEnergyCoreBlock;
import org.polaris2023.caft.registry.ModBlocks;

import java.util.function.UnaryOperator;

import static net.createmod.ponder.api.PonderPalette.BLUE;
import static net.minecraft.core.Direction.DOWN;
import static org.polaris2023.caft.CreateAeronauticsFuturisticTechnology.MODID;

public class FuturePonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        FuturePonderPlugin.register(helper);
    }

    private static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper
                .forComponents(ModBlocks.FUTURE_ENERGY_CORE.getId())
                .addStoryBoard("multiblocks/future_energy_core", FuturePonderPlugin::fePage);
    }




    private static void fePage(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("fe", "Future Energy Multiblock Building");
        showTxtDecay(scene, 30, "Let's assemble the Future Energy Core.", BLUE, 2, false);

        showMultiblockBuilding(scene, auto(util, 0, 0, 0, 2, 0, 2), 30, "Layer 0", BLUE, DOWN,4);
        showMultiblockBuilding(scene, auto(util, 1), 30, "Layer 1", BLUE, DOWN,4);
        showModifyBlock(scene, auto(util, 1, 1, 1), 30, "The block changes when the building is complete.", BLUE, 2, state -> state.setValue(FutureEnergyCoreBlock.ACTIVE, true));
        showMultiblockBuilding(scene, auto(util, 2), 30, "Layer 2", BLUE, DOWN,4);
        showTxtDecay(scene, 30, "Destroying any block causes an explosion.", PonderPalette.RED, 0, true);
    }

    /**
     *
     * @param util
     * @param objects
     * @return
     */
    private static Selection auto(SceneBuildingUtil util, Object... objects) {
        final SelectionUtil select = util.select();
        return switch (objects.length) {
            case 0 -> select.everywhere();
            case 1 -> objects[0] instanceof BlockPos pos ? select.position(pos): select.layer((int) objects[0]);
            case 2 -> objects[0] instanceof BlockPos pos ?
                    objects[1] instanceof BlockPos pos1 ?
                            select.fromTo(pos, pos1):
                            select.cuboid(pos, (Vec3i) objects[1]):
                    objects[1] == null ?
                            select.layersFrom((int) objects[0]):
                            select.column((int) objects[0], (int) objects[1]);
            case 3 -> objects[2] == null ?
                    select.layers((int) objects[0], (int)objects[1]):
                    select.position((int) objects[0], (int)objects[1], (int)objects[2]);
            case 6 -> select.fromTo((int)objects[0], (int)objects[1], (int)objects[2], (int)objects[3], (int)objects[4], (int)objects[5]);
            default -> throw new IllegalArgumentException("Error ints");
        };
    }

    private static void showTxtDecay(CreateSceneBuilder scene,
                                     int showTxtTime,
                                     String txt,
                                     PonderPalette color,
                                     int decaySecond,
                                     boolean isEnd) {
        scene.overlay()
                .showText(showTxtTime)
                .text(txt)
                .colored(color);
        if (isEnd) {
            scene.markAsFinished();
        } else {
            scene.idleSeconds(decaySecond);
        }

    }
    private static void showMultiblockBuilding(CreateSceneBuilder scene, Selection sel, int showTxtTime, String txt, PonderPalette color, Direction face, int decaySecond /*2n*/) {

        scene.overlay()
                .showOutlineWithText(sel, showTxtTime)
                .text(txt)
                .colored(color);
        scene.idleSeconds(decaySecond);
        scene.world().showSection(sel, face);
        scene.idleSeconds(decaySecond / 2);
    }
    private static void showModifyBlock(CreateSceneBuilder scene, Selection sel, int showTxtTime, String txt, PonderPalette color, int decaySecond, UnaryOperator<BlockState> stateFunc) {

        scene.overlay()
                .showOutlineWithText(sel, showTxtTime)
                .text(txt)
                .colored(color);
        scene.idleSeconds(decaySecond);
        scene.world().modifyBlocks(sel, stateFunc, true);
        scene.idleSeconds(decaySecond);
    }
}
