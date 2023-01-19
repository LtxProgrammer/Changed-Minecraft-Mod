package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.item.loot.SetVariantFunction;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedLootItemFunctions {
    public static final LootItemFunctionType SET_COUNT = register("set_variant", new SetVariantFunction.Serializer());

    private static LootItemFunctionType register(String name, Serializer<? extends LootItemFunction> serializer) {
        LootItemFunctionType ret = null;
        if (Registry.LOOT_FUNCTION_TYPE instanceof MappedRegistry<LootItemFunctionType> reg) {
            reg.unfreeze();
            ret = Registry.register(reg, Changed.modResource(name), new LootItemFunctionType(serializer));
            reg.freeze();
        }
        return ret;
    }
}
