package net.ltxprogrammer.changed.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class FluidCanister extends BlockItem {
    private final @Nullable Supplier<? extends Fluid> fluid;

    public FluidCanister(Block block, Item.Properties properties, @Nullable Supplier<? extends Fluid> fluid) {
        super(block, properties);
        this.fluid = fluid;
    }

    public @Nullable Fluid getFluid() {
        return fluid == null ? null : fluid.get();
    }
}
