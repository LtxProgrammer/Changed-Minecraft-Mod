package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.fml.DistExecutor;

import java.util.List;
import java.util.function.Supplier;

import static net.ltxprogrammer.changed.block.AbstractDoubleLatexCrystal.HALF;
import static net.ltxprogrammer.changed.block.AbstractLatexBlock.getLatexed;

public abstract class AbstractLatexCrystal extends BushBlock implements NonLatexCoverableBlock {
    public static final VoxelShape SHAPE_WHOLE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    private final LatexVariant<?> variant;
    private final Supplier<? extends Item> crystal;

    public AbstractLatexCrystal(LatexVariant<?> variant, Supplier<? extends Item> crystal, Properties p_53514_) {
        super(p_53514_);
        this.variant = variant;
        this.crystal = crystal;
    }

    public AbstractLatexCrystal(Supplier<? extends Item> crystal, Properties properties) {
        super(properties);
        this.variant = null;
        this.crystal = crystal;
    }

    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return SHAPE_WHOLE;
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE_WHOLE;
    }

        @Override
    protected boolean mayPlaceOn(BlockState p_51042_, BlockGetter p_51043_, BlockPos p_51044_) {
        return p_51042_.is(ChangedTags.Blocks.GROWS_LATEX_CRYSTALS) || p_51042_.getBlock() instanceof DarkLatexBlock || getLatexed(p_51042_) == LatexType.DARK_LATEX;
    }

    public boolean canSurvive(BlockState blockState, LevelReader p_52888_, BlockPos p_52889_) {
        BlockState blockStateOn = p_52888_.getBlockState(p_52889_.below());
        return blockState.is(ChangedTags.Blocks.GROWS_LATEX_CRYSTALS) || blockStateOn.getBlock() instanceof DarkLatexBlock || getLatexed(blockStateOn) == LatexType.DARK_LATEX;
    }

    @Override
    public PlantType getPlantType(BlockGetter world, BlockPos pos) {
        return PlantType.get("latex_crystal");
    }

    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.makeStuckInBlock(state, new Vec3((double)0.8F, 0.75D, (double)0.8F));

        if (variant == null) return;

        if (entity instanceof LivingEntity le && !(entity instanceof LatexEntity)) {
            if (entity instanceof Player player && ProcessTransfur.isPlayerLatex(player))
                return;
            if (!level.isClientSide) {
                ProcessTransfur.progressTransfur(le, 8.3f, variant.getFormId());
            }

        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootContext.Builder lootBuilder) {
        if (blockState.getProperties().contains(HALF) && blockState.getValue(HALF) == DoubleBlockHalf.UPPER)
            return List.of();

        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, lootBuilder.getParameter(LootContextParams.TOOL)) > 0)
            return List.of(new ItemStack(ChangedItems.getBlockItem(this)));

        if (this instanceof AbstractDoubleLatexCrystal)
            return List.of(new ItemStack(crystal.get(), 2));
        else
            return List.of(new ItemStack(crystal.get(), 1));
    }
}
