package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.fml.DistExecutor;

import java.util.List;
import java.util.function.Supplier;

import static net.ltxprogrammer.changed.block.AbstractDoubleLatexCrystal.HALF;
import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

public abstract class AbstractLatexCrystal extends BushBlock implements NonLatexCoverableBlock {
    private final LatexVariant<?> variant;
    private final Supplier<Item> crystal;

    public AbstractLatexCrystal(LatexVariant<?> variant, Supplier<Item> crystal, Properties p_53514_) {
        super(p_53514_);
        this.variant = variant;
        this.crystal = crystal;
    }

    public AbstractLatexCrystal(Supplier<Item> crystal, Properties properties) {
        super(properties);
        this.variant = null;
        this.crystal = crystal;
    }

    public static void cutoutRenderer(Block block) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                ItemBlockRenderTypes.setRenderLayer(block, renderType -> renderType == RenderType.cutout()));
    }

    public static void translucentRenderer(Block block) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                ItemBlockRenderTypes.setRenderLayer(block, renderType -> renderType == RenderType.translucent()));
    }

    @Override
    protected boolean mayPlaceOn(BlockState p_51042_, BlockGetter p_51043_, BlockPos p_51044_) {
        return p_51042_.is(ChangedTags.Blocks.GROWS_LATEX_CRYSTALS) || p_51042_.getBlock() instanceof DarkLatexBlock || getLatexed(p_51042_) == LatexType.DARK_LATEX;
    }

    public boolean canSurvive(BlockState blockState, LevelReader p_52888_, BlockPos p_52889_) {
        BlockState blockStateOn = p_52888_.getBlockState(p_52889_.below());
        return blockState.is(ChangedTags.Blocks.GROWS_LATEX_CRYSTALS) || blockStateOn.getBlock() instanceof DarkLatexBlock || getLatexed(blockStateOn) == LatexType.DARK_LATEX;
    }

    private LatexType getLatexed(BlockState p_51042_) {
        return p_51042_.getProperties().contains(COVERED) ? p_51042_.getValue(COVERED) : LatexType.NEUTRAL;
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
                ProcessTransfur.progressTransfur(le, 8300, variant.getFormId());
            }

        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder p_60538_) {
        if (p_60537_.getProperties().contains(HALF) && p_60537_.getValue(HALF) == DoubleBlockHalf.UPPER)
            return List.of();
        if (this instanceof AbstractDoubleLatexCrystal)
            return List.of(new ItemStack(crystal.get(), 2));
        else
            return List.of(new ItemStack(crystal.get(), 1));
    }
}
