package net.ltxprogrammer.changed.block;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class LabStairBlock extends StairBlock {
    public LabStairBlock(Supplier<BlockState> state, Properties properties) {
        super(state, properties.requiresCorrectToolForDrops());
    }

    @Override
    public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder p_60538_) {
        return new ArrayList<>(Collections.singleton(this.asItem().getDefaultInstance()));
    }
}
