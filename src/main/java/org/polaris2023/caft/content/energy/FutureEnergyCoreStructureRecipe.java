package org.polaris2023.caft.content.energy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.polaris2023.caft.registry.ModBlocks;
import org.polaris2023.caft.registry.ModRecipes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class FutureEnergyCoreStructureRecipe implements Recipe<RecipeInput> {
    public static final int MAX_WIDTH = 9;
    public static final int MAX_HEIGHT = 9;
    public static final int MAX_DEPTH = 9;
    public static final int MAX_TOTAL_BLOCKS = MAX_WIDTH * MAX_HEIGHT * MAX_DEPTH;
    public static final MapCodec<FutureEnergyCoreStructureRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.intRange(1, MAX_WIDTH).fieldOf("width").forGetter(FutureEnergyCoreStructureRecipe::getWidth),
            ExtraCodecs.intRange(1, MAX_HEIGHT).fieldOf("height").forGetter(FutureEnergyCoreStructureRecipe::getHeight),
            ExtraCodecs.intRange(1, MAX_DEPTH).fieldOf("depth").forGetter(FutureEnergyCoreStructureRecipe::getDepth),
            Codec.STRING.fieldOf("controller").forGetter(FutureEnergyCoreStructureRecipe::getController),
            Codec.unboundedMap(Codec.STRING, SymbolDefinition.CODEC).fieldOf("key").forGetter(FutureEnergyCoreStructureRecipe::getKey),
            Codec.STRING.listOf().listOf().fieldOf("layers").forGetter(FutureEnergyCoreStructureRecipe::getLayers)
    ).apply(instance, FutureEnergyCoreStructureRecipe::new));

    private final int width;
    private final int height;
    private final int depth;
    private final char controllerSymbol;
    private final Map<Character, StructureRequirement> key;
    private final List<List<String>> layers;
    private final List<StructureCell> requiredCells;
    private final int requiredBlockCount;

    public FutureEnergyCoreStructureRecipe(int width, int height, int depth, String controller, Map<String, SymbolDefinition> key, List<List<String>> layers) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.controllerSymbol = requireSingleCharacter(controller, "controller");
        this.key = normalizeKey(key);
        this.layers = copyLayers(layers);
        validateDimensions();
        validateController();

        List<StructureCell> cells = new ArrayList<>();
        BlockPos controllerOffset = null;

        for (int y = 0; y < this.layers.size(); y++) {
            List<String> layer = this.layers.get(y);
            if (layer.size() != this.depth) {
                throw new IllegalArgumentException("Layer " + y + " must contain exactly " + this.depth + " rows");
            }

            for (int z = 0; z < layer.size(); z++) {
                String row = layer.get(z);
                if (row.length() != this.width) {
                    throw new IllegalArgumentException("Layer " + y + ", row " + z + " must be exactly " + this.width + " characters wide");
                }

                for (int x = 0; x < row.length(); x++) {
                    char symbol = row.charAt(x);
                    if (symbol == ' ') {
                        continue;
                    }

                    StructureRequirement requirement = this.key.get(symbol);
                    if (requirement == null) {
                        throw new IllegalArgumentException("Undefined symbol '" + symbol + "' in structure layers");
                    }

                    if (symbol == this.controllerSymbol) {
                        if (controllerOffset != null) {
                            throw new IllegalArgumentException("Controller symbol must appear exactly once");
                        }
                        controllerOffset = new BlockPos(x, y, z);
                    }

                    cells.add(new StructureCell(new BlockPos(x, y, z), requirement));
                }
            }
        }

        if (controllerOffset == null) {
            throw new IllegalArgumentException("Controller symbol must appear exactly once");
        }

        BlockPos finalControllerOffset = controllerOffset;
        this.requiredCells = cells.stream()
                .map(cell -> new StructureCell(
                        new BlockPos(
                                cell.patternOffset().getX() - finalControllerOffset.getX(),
                                cell.patternOffset().getY() - finalControllerOffset.getY(),
                                cell.patternOffset().getZ() - finalControllerOffset.getZ()
                        ),
                        cell.requirement()
                ))
                .toList();
        this.requiredBlockCount = this.requiredCells.size();
    }

    private void validateDimensions() {
        if (this.width < 1 || this.width > MAX_WIDTH) {
            throw new IllegalArgumentException("Structure width must be between 1 and " + MAX_WIDTH);
        }
        if (this.height < 1 || this.height > MAX_HEIGHT) {
            throw new IllegalArgumentException("Structure height must be between 1 and " + MAX_HEIGHT);
        }
        if (this.depth < 1 || this.depth > MAX_DEPTH) {
            throw new IllegalArgumentException("Structure depth must be between 1 and " + MAX_DEPTH);
        }
        if (this.layers.size() != this.height) {
            throw new IllegalArgumentException("Structure must define exactly " + this.height + " layers");
        }
    }

    private void validateController() {
        StructureRequirement controllerRequirement = this.key.get(this.controllerSymbol);
        if (controllerRequirement == null) {
            throw new IllegalArgumentException("Controller symbol '" + this.controllerSymbol + "' is missing from the key");
        }

        ResourceLocation expectedController = BuiltInRegistries.BLOCK.getKey(ModBlocks.FUTURE_ENERGY_CORE.get());
        if (!expectedController.equals(controllerRequirement.asBlockId())) {
            throw new IllegalArgumentException("Controller symbol must point to " + expectedController);
        }
    }

    private static Map<Character, StructureRequirement> normalizeKey(Map<String, SymbolDefinition> rawKey) {
        Map<Character, StructureRequirement> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, SymbolDefinition> entry : rawKey.entrySet()) {
            char symbol = requireSingleCharacter(entry.getKey(), "key symbol");
            normalized.put(symbol, entry.getValue().toRequirement());
        }
        return Map.copyOf(normalized);
    }

    private static List<List<String>> copyLayers(List<List<String>> layers) {
        return layers.stream()
                .map(List::copyOf)
                .toList();
    }

    private static char requireSingleCharacter(String value, String fieldName) {
        if (value == null || value.length() != 1) {
            throw new IllegalArgumentException(fieldName + " must be exactly one character");
        }
        return value.charAt(0);
    }

    public ValidationResult validate(Level level, BlockPos controllerPos) {
        int matched = 0;
        Map<BlockPos, StructureRequirement> missing = new LinkedHashMap<>();

        for (StructureCell cell : this.requiredCells) {
            BlockPos target = controllerPos.offset(cell.patternOffset());
            if (cell.requirement().matches(level.getBlockState(target))) {
                matched++;
            } else {
                missing.put(target, cell.requirement());
            }
        }

        return new ValidationResult(matched, this.requiredBlockCount, matched >= this.requiredBlockCount, missing);
    }

    public int getRequiredBlockCount() {
        return this.requiredBlockCount;
    }

    public List<DisplayCell> getDisplayCells() {
        return this.requiredCells.stream()
                .map(cell -> new DisplayCell(cell.patternOffset(), cell.requirement()))
                .toList();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getDepth() {
        return this.depth;
    }

    public List<List<String>> getLayers() {
        return this.layers;
    }

    public String getController() {
        return String.valueOf(this.controllerSymbol);
    }

    public Map<String, SymbolDefinition> getKey() {
        Map<String, SymbolDefinition> serializedKey = new LinkedHashMap<>();
        this.key.forEach((symbol, requirement) -> serializedKey.put(String.valueOf(symbol), SymbolDefinition.fromRequirement(requirement)));
        return serializedKey;
    }

    @Override
    public boolean matches(RecipeInput input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.FUTURE_ENERGY_CORE_STRUCTURE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return ModRecipes.FUTURE_ENERGY_CORE_STRUCTURE_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public record ValidationResult(int integrity, int requiredIntegrity, boolean complete, Map<BlockPos, StructureRequirement> missingBlocks) {
    }

    public record DisplayCell(BlockPos offset, StructureRequirement requirement) {
    }

    private record StructureCell(BlockPos patternOffset, StructureRequirement requirement) {
    }

    public record StructureRequirement(@NotNull Optional<Block> block, @NotNull Optional<TagKey<Block>> tag) {
        public StructureRequirement {
            Objects.requireNonNull(block, "block");
            Objects.requireNonNull(tag, "tag");
            if (block.isPresent() == tag.isPresent()) {
                throw new IllegalArgumentException("Structure requirement must define exactly one of block or tag");
            }
        }

        public static StructureRequirement ofBlock(ResourceLocation blockId) {
            Block resolvedBlock = BuiltInRegistries.BLOCK.getOptional(blockId)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown block id in structure recipe: " + blockId));
            return new StructureRequirement(Optional.of(resolvedBlock), Optional.empty());
        }

        public static StructureRequirement ofTag(ResourceLocation tagId) {
            return new StructureRequirement(Optional.empty(), Optional.of(TagKey.create(Registries.BLOCK, tagId)));
        }

        public boolean matches(BlockState state) {
            if (this.block.isPresent()) {
                return state.is(this.block.get());
            }
            return state.is(this.tag.orElseThrow());
        }

        public ResourceLocation asBlockId() {
            Block resolvedBlock = this.block.orElseThrow(() -> new IllegalStateException("Structure requirement is tag-based"));
            return BuiltInRegistries.BLOCK.getKey(resolvedBlock);
        }
    }

    public record SymbolDefinition(Optional<ResourceLocation> block, Optional<ResourceLocation> tag) {
        public static final Codec<SymbolDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.optionalFieldOf("block").forGetter(SymbolDefinition::block),
                ResourceLocation.CODEC.optionalFieldOf("tag").forGetter(SymbolDefinition::tag)
        ).apply(instance, SymbolDefinition::new));

        public SymbolDefinition {
            Objects.requireNonNull(block, "block");
            Objects.requireNonNull(tag, "tag");
            if (block.isPresent() == tag.isPresent()) {
                throw new IllegalArgumentException("Structure symbol must define exactly one of block or tag");
            }
        }

        public SymbolDefinition(ResourceLocation block) {
            this(Optional.of(block), Optional.empty());
        }

        public static SymbolDefinition fromRequirement(StructureRequirement requirement) {
            if (requirement.block().isPresent()) {
                return new SymbolDefinition(BuiltInRegistries.BLOCK.getKey(requirement.block().get()));
            }
            return new SymbolDefinition(Optional.empty(), Optional.of(requirement.tag().orElseThrow().location()));
        }

        public StructureRequirement toRequirement() {
            if (this.block.isPresent()) {
                return StructureRequirement.ofBlock(this.block.get());
            }
            return StructureRequirement.ofTag(this.tag.orElseThrow());
        }
    }

    public static class Serializer implements RecipeSerializer<FutureEnergyCoreStructureRecipe> {
        private static final StreamCodec<RegistryFriendlyByteBuf, FutureEnergyCoreStructureRecipe> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public void encode(RegistryFriendlyByteBuf buffer, FutureEnergyCoreStructureRecipe recipe) {
                buffer.writeVarInt(recipe.width);
                buffer.writeVarInt(recipe.height);
                buffer.writeVarInt(recipe.depth);
                buffer.writeUtf(recipe.getController());

                buffer.writeVarInt(recipe.key.size());
                recipe.key.forEach((symbol, requirement) -> {
                    buffer.writeUtf(String.valueOf(symbol));
                    buffer.writeBoolean(requirement.block().isPresent());
                    if (requirement.block().isPresent()) {
                        ResourceLocation.STREAM_CODEC.encode(buffer, BuiltInRegistries.BLOCK.getKey(requirement.block().get()));
                    } else {
                        ResourceLocation.STREAM_CODEC.encode(buffer, requirement.tag().orElseThrow().location());
                    }
                });

                buffer.writeVarInt(recipe.layers.size());
                for (List<String> layer : recipe.layers) {
                    buffer.writeVarInt(layer.size());
                    for (String row : layer) {
                        buffer.writeUtf(row);
                    }
                }
            }

            @Override
            public FutureEnergyCoreStructureRecipe decode(RegistryFriendlyByteBuf buffer) {
                int width = buffer.readVarInt();
                int height = buffer.readVarInt();
                int depth = buffer.readVarInt();
                String controller = buffer.readUtf();

                int keySize = buffer.readVarInt();
                Map<String, SymbolDefinition> key = new LinkedHashMap<>();
                for (int i = 0; i < keySize; i++) {
                    String symbol = buffer.readUtf();
                    boolean isBlock = buffer.readBoolean();
                    ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buffer);
                    key.put(symbol, isBlock ? new SymbolDefinition(id) : new SymbolDefinition(Optional.empty(), Optional.of(id)));
                }

                int layerCount = buffer.readVarInt();
                List<List<String>> layers = new ArrayList<>(layerCount);
                for (int y = 0; y < layerCount; y++) {
                    int rowCount = buffer.readVarInt();
                    List<String> layer = new ArrayList<>(rowCount);
                    for (int z = 0; z < rowCount; z++) {
                        layer.add(buffer.readUtf());
                    }
                    layers.add(layer);
                }

                return new FutureEnergyCoreStructureRecipe(width, height, depth, controller, key, layers);
            }
        };

        @Override
        public @NotNull MapCodec<FutureEnergyCoreStructureRecipe> codec() {
            return FutureEnergyCoreStructureRecipe.CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, FutureEnergyCoreStructureRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
