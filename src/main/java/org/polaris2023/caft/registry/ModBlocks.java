package org.polaris2023.caft.registry;

import com.simibubi.create.api.stress.BlockStressValues;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import org.polaris2023.caft.Config;
import org.polaris2023.caft.CreateAeronauticsFuturisticTechnology;
import org.polaris2023.caft.block.PCABlock;
import org.polaris2023.caft.block.PCABlockEntity;

import static org.polaris2023.caft.CreateAeronauticsFuturisticTechnology.REGISTRATE;

public class ModBlocks {

    public static final BlockEntry<PCABlock> PCA;

    static {
        PCA = REGISTRATE
                .block("pca", PCABlock::new)
                .lang("Plasma Capacitor Array")
                .blockstate((ctx, prov) -> {
                    VariantBlockStateBuilder b = prov.getVariantBuilder(ctx.get());
                    BlockModelBuilder pca = prov
                            .models()
                            .getBuilder("pca")
                            .element()
                            .from(0, 0, 0)
                            .to(16, 16, 16)
                            .face(Direction.NORTH)
                            .texture("#front")
                            .cullface(Direction.NORTH)
                            .end()
                            .face(Direction.EAST)
                            .texture("#side")
                            .cullface(Direction.EAST)
                            .end()
                            .face(Direction.WEST)
                            .texture("#side")
                            .cullface(Direction.WEST)
                            .end()
                            .face(Direction.SOUTH)
                            .texture("#side")
                            .cullface(Direction.SOUTH)
                            .end()
                            .face(Direction.UP)
                            .texture("#top")
                            .cullface(Direction.UP)
                            .end()
                            .face(Direction.DOWN)
                            .texture("#bottom")
                            .cullface(Direction.DOWN)
                            .end()
                            .end()
                            .texture("front", ctx.getId().withPath("block/pca_front"))
                            .texture("side", ctx.getId().withPath("block/pca_side"))
                            .texture("top", ctx.getId().withPath("block/pca_top"))
                            .texture("bottom", ctx.getId().withPath("block/pca_bottom"))
                            .texture("particle", ctx.getId().withPath("block/pca_front"));
                    b
                            .addModels(b.partialState().with(PCABlock.FACING, Direction.NORTH),
                                    ConfiguredModel
                                            .builder()
                                            .modelFile(pca)
                                            .rotationX(270)
                                            .buildLast())
                            .addModels(b.partialState().with(PCABlock.FACING, Direction.SOUTH),
                                    ConfiguredModel
                                            .builder()
                                            .modelFile(pca)
                                            .rotationX(90)
                                            .buildLast())
                            .addModels(b.partialState().with(PCABlock.FACING, Direction.WEST),
                                    ConfiguredModel
                                            .builder()
                                            .modelFile(pca)
                                            .rotationX(90)
                                            .rotationY(90)
                                            .buildLast())
                            .addModels(b.partialState().with(PCABlock.FACING, Direction.EAST),
                                    ConfiguredModel
                                            .builder()
                                            .modelFile(pca)
                                            .rotationY(270)
                                            .rotationX(90)
                                            .buildLast())
                            .addModels(b.partialState().with(PCABlock.FACING, Direction.DOWN),
                                    ConfiguredModel
                                            .builder()
                                            .modelFile(pca)
                                            .buildLast())
                            .addModels(b.partialState().with(PCABlock.FACING, Direction.UP),
                                    ConfiguredModel
                                            .builder()
                                            .modelFile(pca)
                                            .rotationX(180)
                                            .buildLast())
                    ;
                    prov.simpleBlockItem(ctx.get(), pca);
                })
                .defaultLoot()

                .item()
                .model((ctx, prov) -> {})
                .build()
                .register();
    }

    public static void init() {}
}
