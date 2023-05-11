package net.ltxprogrammer.changed.block;

import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.block.entity.GasCanisterBlockEntity;
import net.ltxprogrammer.changed.entity.projectile.GasParticle;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.item.GasCanister;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static net.ltxprogrammer.changed.item.GasCanister.CAPACITY;

public class GasCanisterBlock extends AbstractCustomShapeTallEntityBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final VoxelShape SHAPE_WHOLE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 28.0D, 12.0D);
    private final List<LatexVariant<?>> variants;
    private final ChangedParticles.Color3 color;

    public GasCanisterBlock(List<LatexVariant<?>> variants, ChangedParticles.Color3 color) {
        super(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).sound(SoundType.METAL).color(MaterialColor.WOOL).strength(0.7F));
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, false));
        this.variants = variants;
        this.color = color;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        if (!(stack.getItem() instanceof GasCanister))
            return;
        if (!(level.getBlockEntity(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below()) instanceof GasCanisterBlockEntity lBlockEntity))
            return;
        if (!(level.getBlockEntity(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos) instanceof GasCanisterBlockEntity uBlockEntity))
            return;

        lBlockEntity.setUsage(stack.getDamageValue());
        uBlockEntity.setUsage(stack.getDamageValue());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        var blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        var canisterEntity = blockEntity instanceof GasCanisterBlockEntity ? (GasCanisterBlockEntity)blockEntity : null;
        return super.getDrops(state, builder).stream().peek(itemStack -> {
            if (canisterEntity != null && itemStack.getItem() instanceof GasCanister)
                itemStack.setDamageValue(canisterEntity.getUsage());
        }).collect(Collectors.toList());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER)
            return SHAPE_WHOLE.move(0, -1, 0);
        else
            return SHAPE_WHOLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GasCanisterBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var beBottom = level.getBlockEntity(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below(), ChangedBlockEntities.GAS_CANISTER.get());
        var beTop = level.getBlockEntity(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos, ChangedBlockEntities.GAS_CANISTER.get());
        if (beBottom.isEmpty() || beTop.isEmpty())
            return super.use(state, level, pos, player, hand, hitResult);

        if (beTop.get().getUsage() < CAPACITY && !state.getValue(OPEN)) {
            level.setBlockAndUpdate(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below(), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, true));
            level.setBlockAndUpdate(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos, state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(OPEN, true));
            level.scheduleTick(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos, this, 1);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        else if (state.getValue(OPEN)) {
            level.setBlockAndUpdate(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below(), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(OPEN, false));
            level.setBlockAndUpdate(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos, state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(OPEN, false));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        super.tick(state, level, pos, random);
        var beBottom = level.getBlockEntity(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below(), ChangedBlockEntities.GAS_CANISTER.get());
        var beTop = level.getBlockEntity(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos, ChangedBlockEntities.GAS_CANISTER.get());
        if (beBottom.isEmpty() || beTop.isEmpty())
            return;

        if (beTop.get().getUsage() >= CAPACITY || variants.isEmpty() || !state.getValue(OPEN))
            return;

        GasParticle nParticle = new GasParticle(ChangedEntities.GAS_PARTICLE.get(), level).setVariant(
                variants.get(random.nextInt(variants.size()))
        );

        Vector3f frontVector = state.getValue(FACING).getOpposite().step();
        frontVector.mul(0.35f, 0.35f, 0.35f);

        nParticle.setPos(Vec3.atCenterOf(beTop.get().getBlockPos())
                .add(new Vec3(frontVector))
        );

        nParticle.setColor(color);
        nParticle.shoot(frontVector.x(), frontVector.y(), frontVector.z(), 0.5F, 1.0F);
        level.addFreshEntity(nParticle);

        int usage = beTop.get().getUsage() + 1;
        beTop.get().setUsage(usage);
        beBottom.get().setUsage(usage);

        level.scheduleTick(state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos, this, 1);
    }
}
