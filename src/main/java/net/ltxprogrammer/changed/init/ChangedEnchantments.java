package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.world.enchantments.TransfurResistanceEnchantment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ChangedEnchantments {
    private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Changed.MODID);

    public static final RegistryObject<Enchantment> TRANSFUR_RESISTANCE =
            REGISTRY.register("transfur_resistance", () -> new TransfurResistanceEnchantment(Enchantment.Rarity.RARE, ARMOR_SLOTS));
}
