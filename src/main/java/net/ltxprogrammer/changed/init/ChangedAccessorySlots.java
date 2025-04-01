package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ChangedAccessorySlots {
    public static final DeferredRegister<AccessorySlotType> REGISTRY = ChangedRegistry.ACCESSORY_SLOTS.createDeferred(Changed.MODID);
    public static final RegistryObject<AccessorySlotType> BODY = REGISTRY.register("body", () -> new AccessorySlotType(EquipmentSlot.CHEST));
    public static final RegistryObject<AccessorySlotType> LEGS = REGISTRY.register("legs", () -> new AccessorySlotType(EquipmentSlot.LEGS));
    public static final RegistryObject<AccessorySlotType> LOWER_BODY = REGISTRY.register("lower_body", () -> new AccessorySlotType(EquipmentSlot.CHEST));
    public static final RegistryObject<AccessorySlotType> LOWER_BODY_SIDE = REGISTRY.register("lower_body_side", () -> new AccessorySlotType(EquipmentSlot.CHEST));
}
