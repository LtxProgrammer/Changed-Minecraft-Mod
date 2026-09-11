package net.ltxprogrammer.changed.world;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.util.Cacheable;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;
import java.util.*;

public class LatexCoverState extends StateHolder<LatexType, LatexCoverState> {
    public static final Direction[] UPDATE_SHAPE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};

    public static final LootContextParam<LatexCoverState> LOOT_CONTEXT_PARAM = new LootContextParam<>(Changed.modResource("latex_cover_state"));

    public static final Cacheable<Codec<LatexCoverState>> CODEC = Cacheable.of(() -> codec(ChangedRegistry.LATEX_TYPE.get().getCodec(), LatexType::defaultCoverState).stable());

    private final int lightEmission;
    private final boolean isAir;
    private final boolean ignitedByLava;
    private final MapColor mapColor;
    private final float destroySpeed;
    private final boolean requiresCorrectToolForDrops;
    private final boolean canOcclude;
    private final boolean spawnParticlesOnBreak;
    private final boolean replaceable;
    private boolean isRandomlyTicking;

    public LatexCoverState(LatexType type, ImmutableMap<Property<?>, Comparable<?>> properties, MapCodec<LatexCoverState> codec) {
        super(type, properties, codec);
        LatexCoverProperties gameProperties = type.properties;
        this.lightEmission = gameProperties.lightEmission.applyAsInt(this.asState());
        this.isAir = gameProperties.isAir;
        this.ignitedByLava = gameProperties.ignitedByLava;
        this.mapColor = gameProperties.mapColor.apply(this.asState());
        this.destroySpeed = gameProperties.destroyTime;
        this.requiresCorrectToolForDrops = gameProperties.requiresCorrectToolForDrops;
        this.canOcclude = gameProperties.canOcclude;
        this.spawnParticlesOnBreak = gameProperties.spawnParticlesOnBreak;
        this.replaceable = gameProperties.replaceable;
    }

    public boolean canOcclude(BlockGetter level, BlockPos pos, LatexCoverState other, BlockPos otherPos) {
        return this.getType().canOcclude(this.asState(), level, pos, other, otherPos);
    }

    public int getLightEmission() {
        return this.lightEmission;
    }

    public boolean isPresent() {
        return !isAir;
    }

    public boolean isAir() {
        return isAir;
    }

    public boolean ignitedByLava() {
        return this.ignitedByLava;
    }

    public LatexType getType() {
        return this.owner;
    }

    public static LatexCoverState getAt(BlockGetter blockGetter, BlockPos blockPos) {
        if (blockGetter instanceof LevelAccessor levelAccessor)
            return getAt(levelAccessor, blockPos);
        return ChangedLatexTypes.NONE.get().defaultCoverState();
    }

    public static LatexCoverState getAt(LevelReader level, BlockPos blockPos) {
        if (level.isOutsideBuildHeight(blockPos)) {
            return ChangedLatexTypes.NONE.get().defaultCoverState();
        } else {
            return getAt(level.getChunk(blockPos).getSection(level.getSectionIndex(blockPos.getY())), blockPos);
        }
    }

    public static LatexCoverState getAt(ChunkAccess chunk, BlockPos blockPos) {
        if (chunk.isOutsideBuildHeight(blockPos)) {
            return ChangedLatexTypes.NONE.get().defaultCoverState();
        } else {
            return getAt(chunk.getSection(chunk.getSectionIndex(blockPos.getY())), blockPos);
        }
    }

    public static LatexCoverState getAt(LevelChunkSection section, BlockPos blockPos) {
        return getAt(section, blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static LatexCoverState getAt(LevelChunkSection section, int x, int y, int z) {
        return ((LevelChunkSectionExtension)section)
                .getLatexCoverState(x & 15, y & 15, z & 15);
    }

    public static void markAndNotifyAt(Level level, BlockPos blockPos, @Nullable LevelChunk levelchunk, LatexCoverState oldState, LatexCoverState newState, int flags, int timeToLive) {
        LatexType block = newState.getType();
        LatexCoverState recordedState = getAt(level, blockPos);
        {
            {
                if (recordedState == newState) {
                    LevelExtension levelExtension = UniversalDist.getLevelExtension(level);

                    if (oldState != recordedState) {
                        levelExtension.setCoversDirty(level, blockPos, oldState, recordedState);
                    }

                    if ((flags & 2) != 0 && (!level.isClientSide || (flags & 4) == 0) && (level.isClientSide || levelchunk.getFullStatus() != null && levelchunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING))) {
                        levelExtension.sendCoverUpdated(level, blockPos, oldState, newState, flags);
                    }

                    if ((flags & 1) != 0) {
                        levelExtension.coverUpdated(level, blockPos, oldState.getType());
                    }

                    if ((flags & 16) == 0 && timeToLive > 0) {
                        int nextFlags = flags & -34;
                        oldState.updateIndirectNeighbourShapes(level, blockPos, nextFlags, timeToLive - 1);
                        newState.updateNeighbourShapes(level, blockPos, nextFlags, timeToLive - 1);
                        newState.updateIndirectNeighbourShapes(level, blockPos, nextFlags, timeToLive - 1);
                    }

                    levelExtension.onLatexCoverStateChange(level, blockPos, oldState, recordedState);
                    newState.onLatexCoverStateChange(level, blockPos, oldState);
                }
            }
        }
    }

    public static boolean setAt(LevelWriter level, BlockPos blockPos, LatexCoverState state, int flags, int timeToLive) {
        if (level instanceof Level casted)
            return setAt(casted, blockPos, state, flags, timeToLive);
        if (level instanceof WorldGenRegion casted)
            return setAt(casted, blockPos, state, flags, timeToLive);
        return false;
    }

    public static boolean setAt(LevelWriter level, BlockPos blockPos, LatexCoverState state, int flags) {
        return setAt(level, blockPos, state, flags, 512);
    }

    public static boolean setAtAndUpdate(LevelWriter level, BlockPos blockPos, LatexCoverState state) {
        return setAt(level, blockPos, state, 3);
    }

    @Nullable
    public static LatexCoverState setAt(ChunkAccess chunk, BlockPos blockPos, LatexCoverState state, boolean unknown) {
        if (chunk instanceof EmptyLevelChunk)
            return null;
        if (chunk instanceof ImposterProtoChunk wrapper) // Mock vanilla behavior
            return wrapper.allowWrites ? setAt(wrapper.getWrapped(), blockPos, state, unknown) : null;
        if (chunk instanceof LevelChunk levelChunk)
            return setAt(levelChunk, blockPos, state, unknown);
        if (chunk instanceof ProtoChunk protoChunk)
            return setAt(protoChunk, blockPos, state, unknown);
        return null;
    }

    public static boolean setAt(WorldGenRegion level, BlockPos blockPos, LatexCoverState state, int flags, int timeToLive) {
        if (!level.ensureCanWrite(blockPos)) {
            return false;
        } else {
            ChunkAccess chunkaccess = level.getChunk(blockPos);
            LatexCoverState prevState = setAt(chunkaccess, blockPos, state, false);
            if (prevState != null) {
                UniversalDist.getLevelExtension(level.getLevel()).onLatexCoverStateChange(level.getLevel(), blockPos, prevState, state);
            }

            return true;
        }
    }

    public static boolean setAt(Level level, BlockPos blockPos, LatexCoverState state, int flags, int timeToLive) {
        if (level.isOutsideBuildHeight(blockPos)) {
            return false;
        } else if (!level.isClientSide && level.isDebug()) {
            return false;
        } else {
            LevelChunk levelchunk = level.getChunkAt(blockPos);

            blockPos = blockPos.immutable(); // Forge - prevent mutable BlockPos leaks
            net.minecraftforge.common.util.BlockSnapshot blockSnapshot = null;
            if (level.captureBlockSnapshots && !level.isClientSide) {
                blockSnapshot = net.minecraftforge.common.util.BlockSnapshot.create(level.dimension(), level, blockPos, flags);
                level.capturedBlockSnapshots.add(blockSnapshot);
            }

            LatexCoverState oldState = setAt(levelchunk, blockPos, state, (flags & 64) != 0);
            if (oldState == null) {
                if (blockSnapshot != null) level.capturedBlockSnapshots.remove(blockSnapshot);
                return false;
            } else {
                if (blockSnapshot == null) { // Don't notify clients or update physics while capturing blockstates
                    markAndNotifyAt(level, blockPos, levelchunk, oldState, state, flags, timeToLive);
                }

                return true;
            }
        }
    }

    @Nullable
    public static LatexCoverState setAt(LevelChunk chunk, BlockPos blockPos, LatexCoverState state, boolean unknown) {
        int i = blockPos.getY();
        LevelChunkSection section = chunk.getSection(chunk.getSectionIndex(i));
        boolean hasOnlyAir = section.hasOnlyAir();
        if (hasOnlyAir && state.isAir()) {
            return null;
        } else {
            int j = blockPos.getX() & 15;
            int k = i & 15;
            int l = blockPos.getZ() & 15;
            LatexCoverState oldState = setAt(section, j, k, l, state);
            if (oldState == state) {
                return null;
            } else {
                boolean flag1 = section.hasOnlyAir();
                if (hasOnlyAir != flag1) {
                    chunk.getLevel().getChunkSource().getLightEngine().updateSectionStatus(blockPos, flag1);
                }

                if (!chunk.getLevel().isClientSide) {
                    oldState.onRemove(chunk.getLevel(), blockPos, state, unknown);
                }

                if (!getAt(section, j, k, l).is(state.getType())) {
                    return null;
                } else {
                    if (!chunk.getLevel().isClientSide && !chunk.getLevel().captureBlockSnapshots) {
                        state.onPlace(chunk.getLevel(), blockPos, oldState, unknown);
                    }

                    chunk.setUnsaved(true);
                    return oldState;
                }
            }
        }
    }

    @Nullable
    public static LatexCoverState setAt(ProtoChunk chunk, BlockPos blockPos, LatexCoverState state, boolean unknown) {
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        if (j >= chunk.getMinBuildHeight() && j < chunk.getMaxBuildHeight()) {
            int l = chunk.getSectionIndex(j);
            LevelChunkSection levelchunksection = chunk.getSection(l);
            boolean flag = levelchunksection.hasOnlyAir();
            if (flag && state.is(ChangedLatexTypes.NONE.get())) {
                return state;
            } else {
                int i1 = SectionPos.sectionRelative(i);
                int j1 = SectionPos.sectionRelative(j);
                int k1 = SectionPos.sectionRelative(k);
                LatexCoverState prevState = setAt(levelchunksection, i1, j1, k1, state);

                return prevState;
            }
        } else {
            return ChangedLatexTypes.NONE.get().defaultCoverState();
        }
    }

    public static void setServerVerifiedAt(Level level, BlockPos blockPos, LatexCoverState latexCoverState, int flags) {
        if (level.isClientSide)
            setAt(level, blockPos, latexCoverState, flags);
    }

    public boolean is(LatexType type) {
        return getType() == type;
    }

    public boolean is(TagKey<LatexType> tag) {
        return getType().is(tag);
    }

    public LatexCoverState asState() {
        return this;
    }

    public boolean requiresCorrectToolForDrops() {
        return this.requiresCorrectToolForDrops;
    }

    public boolean shouldSpawnParticlesOnBreak() {
        return this.spawnParticlesOnBreak;
    }

    public final void updateNeighbourShapes(LevelAccessor level, BlockPos p_60703_, int flags) {
        this.updateNeighbourShapes(level, p_60703_, flags, 512);
    }

    static void updateOrDestroy(LatexCoverState prevState, LatexCoverState nextState, LevelAccessor level, BlockPos blockPos, int flags, int timeToLive) {
        if (nextState != prevState) {
            if (nextState.isAir()) {
                if (!level.isClientSide()) {
                    UniversalDist.getLevelExtension(level).destroyLatexCover(level, blockPos, (flags & 32) == 0, (Entity)null, timeToLive);
                }
            } else {
                setAt(level, blockPos, nextState, flags & -33, timeToLive);
            }
        }

    }

    public static void executeShapeUpdate(LevelAccessor level, Direction direction, BlockState neighborState, BlockPos blockPos, BlockPos neighborPos, int flags, int timeToLive) {
        LatexCoverState prevState = getAt(level, blockPos);
        LatexCoverState nextState = prevState.updateShape(direction, neighborState, level, blockPos, neighborPos);
        LatexCoverState.updateOrDestroy(prevState, nextState, level, blockPos, flags, timeToLive);
    }

    public static void executeShapeUpdate(LevelAccessor level, Direction direction, LatexCoverState neighborState, BlockPos blockPos, BlockPos neighborPos, int flags, int timeToLive) {
        LatexCoverState prevState = getAt(level, blockPos);
        LatexCoverState nextState = prevState.updateShape(direction, neighborState, level, blockPos, neighborPos);
        LatexCoverState.updateOrDestroy(prevState, nextState, level, blockPos, flags, timeToLive);
    }

    public static void executeInPlaceUpdate(LevelAccessor level, BlockState oldState, BlockState newState, BlockPos blockPos, int flags, int timeToLive) {
        LatexCoverState prevState = getAt(level, blockPos);
        LatexCoverState nextState = prevState.updateInPlace(oldState, newState, level, blockPos);
        LatexCoverState.updateOrDestroy(prevState, nextState, level, blockPos, flags, timeToLive);
    }

    public final void updateNeighbourShapes(LevelAccessor level, BlockPos blockPos, int flags, int timeToLive) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(Direction direction : UPDATE_SHAPE_ORDER) {
            blockpos$mutableblockpos.setWithOffset(blockPos, direction);
            LatexCoverState.executeShapeUpdate(level, direction.getOpposite(), this.asState(), blockpos$mutableblockpos, blockPos, flags, timeToLive - 1);
        }

    }

    public final void updateIndirectNeighbourShapes(LevelAccessor level, BlockPos blockPos, int flags) {
        this.updateIndirectNeighbourShapes(level, blockPos, flags, 512);
    }

    public void updateIndirectNeighbourShapes(LevelAccessor level, BlockPos blockPos, int flags, int timeToLive) {
        this.getType().updateIndirectNeighbourShapes(this.asState(), level, blockPos, flags, timeToLive);
    }

    public void onPlace(Level level, BlockPos blockPos, LatexCoverState oldState, boolean flag) {
        this.getType().onPlace(this.asState(), level, blockPos, oldState, flag);
    }

    public void onRemove(Level level, BlockPos blockPos, LatexCoverState oldState, boolean flag) {
        this.getType().onRemove(this.asState(), level, blockPos, oldState, flag);
    }

    public void onStruckByLighting(Level level, BlockPos blockPos, LightningBolt lightningBolt) {
        this.getType().onStruckByLighting(this.asState(), level, blockPos, lightningBolt);
    }

    public void onLatexCoverStateChange(Level level, BlockPos blockPos, LatexCoverState oldState) {
        this.getType().onLatexCoverStateChange(this.asState(), level, blockPos, oldState);
    }

    public static LatexCoverState setAt(LevelChunkSection section, BlockPos blockPos, LatexCoverState state) {
        return setAt(section, blockPos.getX(), blockPos.getY(), blockPos.getZ(), state);
    }

    public static LatexCoverState setAt(LevelChunkSection section, int x, int y, int z, LatexCoverState state) {
        return ((LevelChunkSectionExtension)section)
                .setLatexCoverState(x & 15, y & 15, z & 15, state);
    }

    public void randomTick(ServerLevel level, BlockPos position, RandomSource random) {
        this.getType().randomTick(this.asState(), level, position, random);
    }

    public void entityInside(Level level, BlockPos blockPos, Entity entity) {
        this.getType().entityInside(this.asState(), level, blockPos, entity);
    }

    public void spawnAfterBreak(ServerLevel level, BlockPos blockPos, ItemStack itemStack, boolean dropXp) {
        this.getType().spawnAfterBreak(this.asState(), level, blockPos, itemStack, dropXp);
    }

    public List<ItemStack> getDrops(LootParams.Builder builder) {
        return this.getType().getDrops(this.asState(), builder);
    }

    public void animateTick(Level level, BlockPos pos, RandomSource random) {
        this.getType().animateTick(this.asState(), level, pos, random);
    }

    public MapColor getMapColor(LatexCoverGetter level, BlockPos blockPos) {
        return this.getType().getMapColor(this.asState(), level, blockPos, this.mapColor);
    }

    public boolean isRandomlyTicking() {
        return this.isRandomlyTicking;
    }

    public long getSeed(BlockPos blockPos) {
        return this.getType().getSeed(this.asState(), blockPos);
    }

    public LatexCoverState updateShape(Direction direction, BlockState neighborState, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
        return this.getType().updateShape(this.asState(), direction, neighborState, level, blockPos, neighborPos);
    }

    public LatexCoverState updateShape(Direction direction, LatexCoverState neighborState, LevelAccessor level, BlockPos blockPos, BlockPos neighborPos) {
        return this.getType().updateShape(this.asState(), direction, neighborState, level, blockPos, neighborPos);
    }

    public LatexCoverState updateInPlace(BlockState oldState, BlockState newState, LevelAccessor level, BlockPos pos) {
        return this.getType().updateInPlace(this.asState(), oldState, newState, level, pos);
    }

    public boolean canBeReplaced() {
        return this.replaceable;
    }

    public boolean canOcclude() {
        return this.canOcclude;
    }

    public VoxelShape getShape(LatexCoverGetter level, BlockPos blockPos) {
        return this.getShape(level, blockPos, CollisionContext.empty());
    }

    public VoxelShape getShape(LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
        return this.getType().getShape(this.asState(), level, blockPos, context);
    }

    public VoxelShape getCollisionShape(LatexCoverGetter level, BlockPos blockPos) {
        return /*this.cache != null ? this.cache.collisionShape : */this.getCollisionShape(level, blockPos, CollisionContext.empty());
    }

    public VoxelShape getCollisionShape(LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
        return this.getType().getCollisionShape(this.asState(), level, blockPos, context);
    }

    public VoxelShape getSwimShape(LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
        return this.getType().getSwimShape(this.asState(), level, blockPos, context);
    }

    public VoxelShape getVisualShape(LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
        return this.getType().getVisualShape(this.asState(), level, blockPos, context);
    }

    public VoxelShape getInteractionShape(LatexCoverGetter level, BlockPos blockPos) {
        return this.getType().getInteractionShape(this.asState(), level, blockPos);
    }

    public boolean shouldResetFallDamage(LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
        return this.getType().shouldResetFallDamage(this.asState(), level, blockPos, context);
    }

    public final boolean entityCanStandOn(LatexCoverGetter level, BlockPos blockPos, Entity entity) {
        return this.entityCanStandOnFace(level, blockPos, entity, Direction.UP);
    }

    public final boolean entityCanStandOnFace(LatexCoverGetter level, BlockPos blockPos, Entity entity, Direction face) {
        return Block.isFaceFull(this.getCollisionShape(level, blockPos, CollisionContext.of(entity)), face);
    }

    public void initCache() {
        this.isRandomlyTicking = this.owner.isRandomlyTicking(this.asState());

        /*this.fluidState = this.owner.getFluidState(this.asState());
        this.isRandomlyTicking = this.owner.isRandomlyTicking(this.asState());
        if (!this.getBlock().hasDynamicShape()) {
            this.cache = new BlockBehaviour.BlockStateBase.Cache(this.asState());
        }

        this.legacySolid = this.calculateSolid();*/
    }

    public Vec3 findClosestSurface(Vec3 position, @Nullable Direction.Axis axis) {
        return getType().findClosestSurface(this.asState(), position, axis);
    }

    public static Set<Pair<BlockPos, LatexCoverState>> collectConnectedLatex(LatexCoverGetter level, BlockPos source, int maxDepth) {
        LatexCoverState sourceState = LatexCoverState.getAt(level, source);
        if (sourceState.isAir())
            return (Set<Pair<BlockPos, LatexCoverState>>) Collections.EMPTY_SET;

        Set<BlockPos> visited = new HashSet<>();
        Set<Pair<BlockPos, LatexCoverState>> result = new HashSet<>();

        Queue<Pair<BlockPos, Integer>> queue = new ArrayDeque<>();
        queue.add(Pair.of(source, 0));

        while (!queue.isEmpty()) {
            var entry = queue.poll();
            BlockPos pos = entry.getFirst();
            int depth = entry.getSecond();

            if (depth > maxDepth)
                continue;

            LatexCoverState state = LatexCoverState.getAt(level, pos);
            if (!state.is(sourceState.getType()))
                continue;

            result.add(Pair.of(pos, state));

            for (Direction dir : Direction.values()) {
                BlockPos next = pos.relative(dir);
                if (visited.add(next)) {
                    queue.add(Pair.of(next, depth + 1));
                }
            }
        }

        return result;
    }

    public LatexCoverState mirror(Mirror mirror) {
        return this.getType().mirror(this, mirror);
    }

    public LatexCoverState rotate(Rotation rotation) {
        return this.getType().rotate(this, rotation);
    }

    public record LatexNode(BlockPos pos, LatexCoverState state) {};

    public interface ShapeGetter {
        VoxelShape get(LatexCoverState state, LatexCoverGetter p_45741_, BlockPos p_45742_, CollisionContext p_45743_);
    }

    public static enum LatexCoverShapeGetter implements ShapeGetter {
        COLLIDER(LatexCoverState::getCollisionShape),
        OUTLINE(LatexCoverState::getShape),
        VISUAL(LatexCoverState::getVisualShape),
        SWIM(LatexCoverState::getSwimShape),
        FALLDAMAGE_RESETTING((state, level, blockPos, context) -> {
            return state.shouldResetFallDamage(level, blockPos, context) ? Shapes.block() : Shapes.empty();
        });

        private final ShapeGetter shapeGetter;

        private LatexCoverShapeGetter(ShapeGetter shapeGetter) {
            this.shapeGetter = shapeGetter;
        }

        public VoxelShape get(LatexCoverState state, LatexCoverGetter level, BlockPos blockPos, CollisionContext context) {
            return this.shapeGetter.get(state, level, blockPos, context);
        }

        public static LatexCoverShapeGetter wrap(ClipContext.Block block) {
            return switch (block) {
                case COLLIDER -> LatexCoverShapeGetter.COLLIDER;
                case OUTLINE -> LatexCoverShapeGetter.OUTLINE;
                case VISUAL -> LatexCoverShapeGetter.VISUAL;
                case FALLDAMAGE_RESETTING -> LatexCoverShapeGetter.FALLDAMAGE_RESETTING;
                default -> LatexCoverShapeGetter.COLLIDER; // In-case another mod mixins a type
            };
        }
    }

    public static InteractionResult handleInteractionEvent(PlayerInteractEvent.RightClickBlock event) {
        final var player = event.getEntity();
        final var hand = event.getHand();
        final var hitVec = event.getHitVec();
        final var itemStack = event.getItemStack();
        final var blockPos = event.getPos();

        if (player.isSpectator()) {
            return InteractionResult.SUCCESS;
        } else {
            UseOnContext useoncontext = new UseOnContext(player, hand, hitVec);
            if (event.getUseItem() != net.minecraftforge.eventbus.api.Event.Result.DENY) {
                InteractionResult result = itemStack.onItemUseFirst(useoncontext);
                if (result != InteractionResult.PASS) {
                    return result;
                }
            }
            boolean flag = !player.getMainHandItem().doesSneakBypassUse(player.level(), blockPos, player) || !player.getOffhandItem().doesSneakBypassUse(player.level(), blockPos, player);
            boolean flag1 = player.isSecondaryUseActive() && flag;
            LatexCoverState state = LatexCoverState.getAt(player.level(), blockPos);

            if (event.getUseBlock() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || (event.getUseBlock() != net.minecraftforge.eventbus.api.Event.Result.DENY && !flag1)) {
                InteractionResult interactionresult = state.use(player.level(), player, hand, hitVec);
                if (interactionresult.consumesAction()) {
                    return interactionresult;
                }
            }

            if (event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.DENY) {
                return InteractionResult.PASS;
            }
            if (event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || (!itemStack.isEmpty() && !player.getCooldowns().isOnCooldown(itemStack.getItem()))) {
                InteractionResult interactionresult1;
                if (player.isCreative()) {
                    int i = itemStack.getCount();
                    interactionresult1 = itemStack.useOn(useoncontext);
                    itemStack.setCount(i);
                } else {
                    interactionresult1 = itemStack.useOn(useoncontext);
                }

                return interactionresult1;
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    public InteractionResult use(Level level, Player player, InteractionHand hand, BlockHitResult hitVec) {
        return this.getType().use(this.asState(), level, player, hand, hitVec);
    }

    public void attack(Level level, BlockPos blockPos, Player player) {
        this.getType().attack(this.asState(), level, blockPos, player);
    }

    // TODO: hook into appropriate places
    @Nullable
    public SoundType getSoundType(LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return this.getType().getSoundType(this.asState(), level, pos, entity);
    }

    public float getDestroySpeed(LatexCoverGetter level, BlockPos blockPos) {
        return this.destroySpeed;
    }

    public float getDestroyProgress(Player player, LatexCoverGetter level, BlockPos blockPos) {
        return this.getType().getDestroyProgress(this.asState(), player, level, blockPos);
    }

    public boolean canHarvestLatexCover(LatexCoverGetter level, BlockPos pos, Player player) {
        return this.getType().canHarvestLatexCover(this.asState(), level, pos, player);
    }

    public boolean onDestroyedByPlayer(Level level, BlockPos pos, Player player, boolean willHarvest) {
        return this.getType().onDestroyedByPlayer(this.asState(), level, pos, player, willHarvest);
    }

    public BlockState getBlockStateForProperties() {
        Block block = getType().getBlock();
        return block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }
}
