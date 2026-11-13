package net.ltxprogrammer.changed.entity.latex;

import com.google.common.collect.ImmutableMap;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.item.AbstractLatexBucket;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverProperties;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class LatexType {
    protected final StateDefinition<LatexType, LatexCoverState> coverStateDefinition;
    private LatexCoverState defaultCoverState;

    protected final boolean hasCollision;
    protected final float explosionResistance;
    protected final boolean isRandomlyTicking;
    protected final SoundType soundType;
    protected final float friction;
    protected final float speedFactor;
    protected final float jumpFactor;
    public final LatexCoverProperties properties;
    @Nullable
    protected ResourceLocation drops;
    private final Supplier<ResourceLocation> lootTableSupplier;

    private Object renderProperties;

    protected LatexType(LatexCoverProperties properties) {
        this.hasCollision = properties.hasCollision;
        this.drops = properties.drops;
        this.explosionResistance = properties.explosionResistance;
        this.isRandomlyTicking = properties.isRandomlyTicking;
        this.soundType = properties.soundType;
        this.friction = properties.friction;
        this.speedFactor = properties.speedFactor;
        this.jumpFactor = properties.jumpFactor;
        this.properties = properties;
        ResourceLocation lootTableCache = properties.drops;
        if (lootTableCache != null) {
            this.lootTableSupplier = () -> lootTableCache;
        } else if (properties.lootTableSupplier != null) {
            this.lootTableSupplier = properties.lootTableSupplier;
        } else {
            this.lootTableSupplier = () -> {
                ResourceLocation registryName = ChangedRegistry.LATEX_TYPE.getKey(this);
                return ResourceLocation.fromNamespaceAndPath(registryName.getNamespace(), "latex_cover/" + registryName.getPath());
            };
        }

        this.coverStateDefinition = createStateDefinition();
        this.defaultCoverState = coverStateDefinition.any();

        this.initClient();
    }

    public boolean isRandomlyTicking(LatexCoverState coverState) {
        return this.isRandomlyTicking;
    }

    public String toString() {
        return "LatexType{" + ChangedRegistry.LATEX_TYPE.getKey(this) + "}";
    }

    protected void registerDefaultCoverState(LatexCoverState state) {
        this.defaultCoverState = state;
    }

    private StateDefinition<LatexType, LatexCoverState> createStateDefinition() {
        return Util.make(new StateDefinition.Builder<>(this), this::buildStateDefinition).create(LatexType::defaultCoverState, LatexCoverState::new);
    }

    protected void buildStateDefinition(StateDefinition.Builder<LatexType, LatexCoverState> builder) {

    }

    public StateDefinition<LatexType, LatexCoverState> getStateDefinition() {
        return this.coverStateDefinition;
    }

    public final LatexCoverState defaultCoverState() {
        return defaultCoverState;
    }

    public LatexCoverState sourceCoverState() {
        return defaultCoverState();
    }

    public LatexCoverState mirror(LatexCoverState state, Mirror mirror) {
        return state;
    }

    public LatexCoverState rotate(LatexCoverState state, Rotation rotation) {
        return state;
    }

    public void updateIndirectNeighbourShapes(LatexCoverState state, LevelAccessor level, BlockPos blockPos, int flags, int timeToLive) {}

    public void onPlace(LatexCoverState state, Level level, BlockPos blockPos, LatexCoverState oldState, boolean flag) {}

    public void onRemove(LatexCoverState state, Level level, BlockPos blockPos, LatexCoverState oldState, boolean flag) {}

    public void onStruckByLighting(LatexCoverState state, Level level, BlockPos strikePosition, LightningBolt lightningBolt) {}

    public void animateTick(LatexCoverState state, Level level, BlockPos pos, RandomSource random) {}

    public MapColor getMapColor(LatexCoverState state, LatexCoverGetter level, BlockPos pos, MapColor defaultColor) {
        return defaultColor;
    }

    public final ResourceLocation getLootTable() {
        if (this.drops == null) {
            this.drops = this.lootTableSupplier.get();
        }

        return this.drops;
    }

    public long getSeed(LatexCoverState state, BlockPos blockPos) {
        return Mth.getSeed(blockPos);
    }

    public static List<ItemStack> getDrops(LatexCoverState state, ServerLevel level, BlockPos blockPos) {
        LootParams.Builder builder = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, null);
        return state.getDrops(builder);
    }

    public static List<ItemStack> getDrops(LatexCoverState state, ServerLevel level, BlockPos blockPos, @Nullable Entity source, ItemStack tool) {
        LootParams.Builder builder = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos)).withParameter(LootContextParams.TOOL, tool).withOptionalParameter(LootContextParams.THIS_ENTITY, source).withOptionalParameter(LootContextParams.BLOCK_ENTITY, null);
        return state.getDrops(builder);
    }

    public static void dropResources(LatexCoverState state, Level level, BlockPos blockPos) {
        if (level instanceof ServerLevel) {
            getDrops(state, (ServerLevel)level, blockPos).forEach((p_152406_) -> {
                Block.popResource(level, blockPos, p_152406_);
            });
            state.spawnAfterBreak((ServerLevel)level, blockPos, ItemStack.EMPTY, true);
        }

    }

    public static void dropResources(LatexCoverState state, LevelAccessor level, BlockPos blockPos) {
        if (level instanceof ServerLevel) {
            getDrops(state, (ServerLevel)level, blockPos).forEach((p_49859_) -> {
                Block.popResource((ServerLevel)level, blockPos, p_49859_);
            });
            state.spawnAfterBreak((ServerLevel)level, blockPos, ItemStack.EMPTY, true);
        }

    }

    public static void dropResources(LatexCoverState state, Level level, BlockPos blockPos, @Nullable Entity source, ItemStack tool) {
        dropResources(state, level, blockPos, source, tool, true);
    }

    public static void dropResources(LatexCoverState state, Level level, BlockPos blockPos, @Nullable Entity source, ItemStack tool, boolean dropXp) {
        if (level instanceof ServerLevel) {
            getDrops(state, (ServerLevel)level, blockPos, source, tool).forEach((p_49944_) -> {
                Block.popResource(level, blockPos, p_49944_);
            });
            state.spawnAfterBreak((ServerLevel)level, blockPos, tool, dropXp);
        }

    }

    private void initClient() {
        // Minecraft instance isn't available in datagen, so don't call initializeClient if in datagen
        if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT && !net.minecraftforge.fml.loading.FMLLoader.getLaunchHandler().isData()) {
            initializeClient(properties -> {
                if (properties == this)
                    throw new IllegalStateException("Don't extend IClientLatexTypeProperties in your latex type, use an anonymous class instead.");
                this.renderProperties = properties;
            });
        }
    }

    public LatexCoverState updateShape(LatexCoverState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
        return state;
    }

    public LatexCoverState updateShape(LatexCoverState state, Direction direction, LatexCoverState neighborState, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
        return state;
    }

    public LatexCoverState updateInPlace(LatexCoverState state, BlockState oldState, BlockState newState, LevelAccessor level, BlockPos pos) {
        return state;
    }

    public VoxelShape getShape(LatexCoverState state, LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
        return Shapes.block();
    }

    public VoxelShape getCollisionShape(LatexCoverState state, LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
        return this.hasCollision ? this.getShape(state, level, blockPos, context) : Shapes.empty();
    }

    public VoxelShape getSwimShape(LatexCoverState state, LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
        return this.getShape(state, level, blockPos, context);
    }

    public VoxelShape getVisualShape(LatexCoverState state, LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
        return this.getCollisionShape(state, level, blockPos, context);
    }

    public VoxelShape getInteractionShape(LatexCoverState state, LatexCoverGetter level, BlockPos blockPos) {
        return Shapes.empty();
    }

    public boolean shouldResetFallDamage(LatexCoverState state, LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
        return false;
    }

    public Object getRenderPropertiesInternal() {
        return renderProperties;
    }

    public void initializeClient(Consumer<IClientLatexTypeExtensions> consumer) {}

    public void randomTick(LatexCoverState state, ServerLevel level, BlockPos blockPos, RandomSource random) {}

    public void entityInside(LatexCoverState state, Level level, BlockPos blockPos, Entity entity) {}

    public void spawnAfterBreak(LatexCoverState state, ServerLevel level, BlockPos blockPos, ItemStack itemStack, boolean dropXp) {}

    public List<ItemStack> getDrops(LatexCoverState coverState, LootParams.Builder builder) {
        ResourceLocation resourcelocation = this.getLootTable();
        if (resourcelocation == BuiltInLootTables.EMPTY) {
            return Collections.emptyList();
        } else {
            LootParams lootparams = builder.withParameter(LatexCoverState.LOOT_CONTEXT_PARAM, coverState).create(ChangedLootContextParamSets.LATEX_COVER);
            ServerLevel serverlevel = lootparams.getLevel();
            LootTable loottable = serverlevel.getServer().getLootData().getLootTable(resourcelocation);
            return loottable.getRandomItems(lootparams);
        }
    }

    public InteractionResult use(LatexCoverState state, Level level, Player player, InteractionHand hand, BlockHitResult hitVec) {
        return InteractionResult.PASS;
    }

    public void attack(LatexCoverState state, Level level, BlockPos blockPos, Player player) {}

    public boolean isAir() {
        return this.properties.isAir;
    }

    public boolean is(TagKey<LatexType> tag) {
        final var tagManager = ChangedRegistry.LATEX_TYPE.get().tags();
        if (tagManager == null)
            return false;
        return tagManager.getTag(tag).contains(this);
    }

    @Nullable
    public Item getGooItem() {
        return null;
    }

    @Nullable
    public AbstractLatexBucket getBucketItem() {
        return null;
    }

    @Nullable
    public Block getBlock() {
        return null;
    }

    @Nullable
    public EntityType<?> getPupEntityType(RandomSource random) {
        return null;
    }

    @Nullable
    public TransfurVariant<?> getTransfurVariant(TransfurCause cause, RandomSource random) {
        return null;
    }

    @Nullable
    public static LatexType getEntityLatexType(@Nullable Entity entity) {
        if (entity instanceof LivingEntity livingEntity &&
                EntityUtil.maybeGetOverlaying(livingEntity) instanceof ChangedEntity changedEntity)
            return changedEntity.getLatexType();
        return null;
    }

    public boolean isHostileTo(@Nullable LatexType otherType) {
        return otherType == null;
    }

    public boolean isFriendlyTo(@Nullable LatexType otherType) {
        return false;
    }

    public Vec3 findClosestSurface(LatexCoverState state, Vec3 position, @Nullable Direction.Axis axis) {
        return position;
    }

    // Return true to cancel the call to Block.fallOn
    public boolean fallOn(Level level, BlockState originalState, BlockPos originalPos, LatexCoverState coverState, BlockPos coverPos, Entity entity, float distance) {
        return false;
    }

    // Return true to cancel the call to Block.updateEntityAfterFallOn
    public boolean updateEntityAfterFallOn(LatexCoverGetter level, Block originalBlock, LatexCoverState state, Entity entity) {
        return false;
    }

    public void popExperience(ServerLevel level, BlockPos blockPos, int exp) {
        if (level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !level.restoringBlockSnapshots) {
            ExperienceOrb.award(level, Vec3.atCenterOf(blockPos), exp);
        }

    }

    // Return true to cancel the call to Block.stepOn
    public boolean stepOn(Level level, BlockPos coverPos, LatexCoverState coverState, BlockPos originalPos, BlockState originalState, Entity entity) {
        return false;
    }

    public void playerDestroy(Level level, Player player, BlockPos blockPos, LatexCoverState coverState, ItemStack heldItem) {
        //player.awardStat(Stats.BLOCK_MINED.get(this));
        player.causeFoodExhaustion(0.005F);
        dropResources(coverState, level, blockPos, player, heldItem, false);
    }

    @Nullable
    public SoundType getSoundType(LatexCoverState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return this.soundType;
    }

    public float getFriction() {
        return this.friction;
    }

    public float getSpeedFactor() {
        return this.speedFactor;
    }

    public float getJumpFactor() {
        return this.jumpFactor;
    }

    public float getDestroyProgress(LatexCoverState state, Player player, LatexCoverGetter level, BlockPos blockPos) {
        BlockState propertyState = state.getBlockStateForProperties();

        float f = state.getDestroySpeed(level, blockPos);
        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i = ForgeHooks.isCorrectToolForDrops(propertyState, player) ? 30 : 100;
            return player.getDigSpeed(propertyState, blockPos) / f / (float)i;
        }
    }

    public boolean canHarvestLatexCover(LatexCoverState coverState, LatexCoverGetter level, BlockPos pos, Player player) {
        BlockState propertyState = coverState.getBlockStateForProperties();

        return ForgeHooks.isCorrectToolForDrops(propertyState, player);
    }

    protected void spawnDestroyParticles(Level level, Player player, BlockPos blockPos, LatexCoverState coverState) {
        UniversalDist.getLevelExtension(level).customLevelEvent(level, player, 2001, blockPos, ChangedLatexTypes.getLatexCoverStateIDMap().getId(coverState));
    }

    public void playerWillDestroy(Level level, BlockPos blockPos, LatexCoverState coverState, Player player) {
        this.spawnDestroyParticles(level, player, blockPos, coverState);
        //level.gameEvent(GameEvent.BLOCK_DESTROY, blockPos, GameEvent.Context.of(player, coverState));
    }

    public boolean onDestroyedByPlayer(LatexCoverState coverState, Level level, BlockPos pos, Player player, boolean willHarvest) {
        this.playerWillDestroy(level, pos, coverState, player);
        return LatexCoverState.setAt(level, pos, ChangedLatexTypes.NONE.get().defaultCoverState(), level.isClientSide ? 11 : 3);
    }

    public void destroy(LevelAccessor level, BlockPos blockPos, LatexCoverState coverState) {
    }

    public boolean canOcclude(LatexCoverState latexCoverState, BlockGetter level, BlockPos pos, LatexCoverState other, BlockPos otherPos) {
        return false;
    }

    public void onLatexCoverStateChange(LatexCoverState state, Level level, BlockPos blockPos, LatexCoverState oldState) {

    }

    public static class None extends LatexType {
        public None() {
            super(LatexCoverProperties.of().air().replaceable().noLootTable());
        }

        @Override
        public VoxelShape getShape(LatexCoverState state, LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
            return Shapes.empty();
        }
    }

    public static CompoundTag writeLatexCoverState(LatexCoverState latexCoverState) {
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putString("Name", ChangedRegistry.LATEX_TYPE.get().getKey(latexCoverState.getType()).toString());
        ImmutableMap<Property<?>, Comparable<?>> immutablemap = latexCoverState.getValues();
        if (!immutablemap.isEmpty()) {
            CompoundTag properties = new CompoundTag();

            for(Map.Entry<Property<?>, Comparable<?>> entry : immutablemap.entrySet()) {
                Property<?> property = entry.getKey();
                properties.putString(property.getName(), ((Property)property).getName(entry.getValue()));
            }

            compoundtag.put("Properties", properties);
        }

        return compoundtag;
    }

    public static LatexCoverState readLatexCoverState(HolderGetter<LatexType> registry, CompoundTag tag) {
        if (!tag.contains("Name", 8)) {
            return ChangedLatexTypes.NONE.get().defaultCoverState();
        } else {
            ResourceLocation resourcelocation = ResourceLocation.parse(tag.getString("Name"));
            Optional<? extends Holder<LatexType>> optional = registry.get(ChangedRegistry.LATEX_TYPE.createResourceKey(resourcelocation));
            if (optional.isEmpty()) {
                return ChangedLatexTypes.NONE.get().defaultCoverState();
            } else {
                LatexType cover = optional.get().value();
                LatexCoverState coverState = cover.defaultCoverState();
                if (tag.contains("Properties", 10)) {
                    CompoundTag compoundtag = tag.getCompound("Properties");
                    StateDefinition<LatexType, LatexCoverState> statedefinition = cover.getStateDefinition();

                    for(String s : compoundtag.getAllKeys()) {
                        Property<?> property = statedefinition.getProperty(s);
                        if (property != null) {
                            coverState = setValueHelper(coverState, property, s, compoundtag, tag);
                        }
                    }
                }

                return coverState;
            }
        }
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(S state, Property<T> property, String propertyName, CompoundTag properties, CompoundTag fullTag) {
        Optional<T> optional = property.getValue(properties.getString(propertyName));
        if (optional.isPresent()) {
            return state.setValue(property, optional.get());
        } else {
            Changed.LOGGER.warn("Unable to read property: {} with value: {} for coverstate: {}", propertyName, properties.getString(propertyName), fullTag.toString());
            return state;
        }
    }

    public static LatexCoverState updateFromNeighbourShapes(LatexCoverState coverState, LevelAccessor level, BlockPos blockPos) {
        LatexCoverState nextCoverState = coverState;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(Direction direction : LatexCoverState.UPDATE_SHAPE_ORDER) {
            blockpos$mutableblockpos.setWithOffset(blockPos, direction);
            nextCoverState = nextCoverState.updateShape(direction, level.getBlockState(blockpos$mutableblockpos), level, blockPos, blockpos$mutableblockpos);
        }

        return nextCoverState;
    }
}