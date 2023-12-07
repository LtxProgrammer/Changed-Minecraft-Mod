package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class FurnaceFuel {
    private static final Map<Supplier<? extends Item>, Function<ItemStack, Integer>> BURN_TIMES = new HashMap<>();

    public static void addItemBurnTime(Supplier<? extends Item> item, Function<ItemStack, Integer> fn) {
        BURN_TIMES.put(item, fn);
    }

    public static void addBlockBurnTime(Supplier<? extends Block> block, Function<ItemStack, Integer> fn) {
        BURN_TIMES.put(() -> ChangedItems.getBlockItem(block.get()), fn);
    }

    public static void addItemBurnTime(Supplier<? extends Item> item, int fixedTime) {
        addItemBurnTime(item, stack -> fixedTime);
    }

    public static void addBlockBurnTime(Supplier<? extends Block> block, int fixedTime) {
        addBlockBurnTime(block, stack -> fixedTime);
    }

    static {
        addBlockBurnTime(ChangedBlocks.BEEHIVE_CORNER, 200);
        addBlockBurnTime(ChangedBlocks.BEEHIVE_FLOOR, 200);
        addBlockBurnTime(ChangedBlocks.BEEHIVE_ROOF, 200);
        addBlockBurnTime(ChangedBlocks.BEEHIVE_WALL, 200);
        addBlockBurnTime(ChangedBlocks.BOOK_STACK, 150);
        addBlockBurnTime(ChangedBlocks.BOX_PILE, 300);
        addBlockBurnTime(ChangedBlocks.CARDBOARD_BOX_TALL, 200);
        addBlockBurnTime(ChangedBlocks.CARDBOARD_BOX, 100);
        addBlockBurnTime(ChangedBlocks.ORANGE_LAB_CARPETING, 100);
        addBlockBurnTime(ChangedBlocks.ORANGE_TREE_SAPLING, 100);
        addBlockBurnTime(ChangedBlocks.TILES_GREENHOUSE, 300);
        addBlockBurnTime(ChangedBlocks.TILES_GREENHOUSE_CONNECTED, 300);
        addBlockBurnTime(ChangedBlocks.WALL_GREENHOUSE, 300);
    }

    @SubscribeEvent
    public static void onBurnItem(FurnaceFuelBurnTimeEvent event) {
        BURN_TIMES.forEach((item, fn) -> {
            if (event.getItemStack().is(item.get()))
                event.setBurnTime(fn.apply(event.getItemStack()));
        });
    }
}
