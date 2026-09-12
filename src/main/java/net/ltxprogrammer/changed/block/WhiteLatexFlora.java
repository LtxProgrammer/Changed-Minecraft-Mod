package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.ai.LatexAssimilationDecision;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.LevelUtil;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
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
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.function.Supplier;

import static net.ltxprogrammer.changed.block.AbstractDoubleTransfurCrystal.HALF;

public class WhiteLatexFlora extends BushBlock {
    public static final VoxelShape SHAPE_WHOLE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public WhiteLatexFlora(Properties properties) {
        super(properties.pushReaction(PushReaction.DESTROY));
    }

    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return SHAPE_WHOLE;
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE_WHOLE;
    }

    protected boolean mayPlaceOn(BlockState otherBlock, BlockGetter level, BlockPos blockPos) {
        return otherBlock.is(ChangedTags.Blocks.GROWS_WHITE_LATEX_FLORA) ||
                AbstractLatexBlock.isSurfaceOfType(LatexCoverGetter.extendDefault(level), blockPos, Direction.DOWN, SupportType.RIGID, ChangedLatexTypes.WHITE_LATEX.get());
    }

    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        if (!canSupportRigidBlock(level, blockPos.below()))
            return false;
        return blockState.is(ChangedTags.Blocks.GROWS_WHITE_LATEX_FLORA) ||
                AbstractLatexBlock.isSurfaceOfType(level, blockPos, Direction.DOWN, SupportType.RIGID, ChangedLatexTypes.WHITE_LATEX.get());
    }

    @Override
    public PlantType getPlantType(BlockGetter world, BlockPos pos) {
        return PlantType.get("white_latex_flora");
    }

    public boolean shouldDrop(BlockState blockState) {
        return !blockState.getProperties().contains(HALF) || blockState.getValue(HALF) != DoubleBlockHalf.UPPER;
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootParams.Builder lootBuilder) {
        if (!shouldDrop(blockState))
            return List.of();

        if (lootBuilder.getParameter(LootContextParams.TOOL).is(Tags.Items.SHEARS))
            return List.of(new ItemStack(this));

        return List.of();
    }
}
