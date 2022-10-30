package net.ltxprogrammer.changed.block;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChangedBlock extends Block {
    public ChangedBlock(Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder p_60538_) {
        return new ArrayList<>(Collections.singleton(this.asItem().getDefaultInstance()));
    }
}
