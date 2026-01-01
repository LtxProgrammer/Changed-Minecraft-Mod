package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ChangedAttributes {
    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Changed.MODID);

    public static final RegistryObject<Attribute> TRANSFUR_TOLERANCE = REGISTRY.register("transfur_tolerance",
            () -> new RangedAttribute("attribute.name.changed.transfur_tolerance", 20.0D, 1.0D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> TRANSFUR_DAMAGE = REGISTRY.register("transfur_damage",
            () -> new RangedAttribute("attribute.name.changed.transfur_damage", 3.0D, 0.0D, 2048.0D));

    /**
     *  Multiplies the player's speed difference sprinting vs not. One is vanilla. Zero disables sprint. ONLY APPLIES WITH A TRANSFUR VARIANT.
     */
    public static final RegistryObject<Attribute> SPRINT_SPEED = REGISTRY.register("sprint_speed",
            () -> new RangedAttribute("attribute.name.changed.sprint_speed", 1.0D, 0.0D, 512.0D).setSyncable(true));
    /**
     *  Multiplies the player's speed when sneaking. A higher value is faster. One is vanilla. ONLY APPLIES WITH A TRANSFUR VARIANT.
     */
    public static final RegistryObject<Attribute> SNEAK_SPEED = REGISTRY.register("sneak_speed",
            () -> new RangedAttribute("attribute.name.changed.sneak_speed", 1.0D, 0.0D, 512.0D).setSyncable(true));
    /**
     *  Replaces player's base air capacity. Rounds to nearest tick. ONLY APPLIES WITH A TRANSFUR VARIANT.
     */
    public static final RegistryObject<Attribute> AIR_CAPACITY = REGISTRY.register("air_capacity",
            () -> new RangedAttribute("attribute.name.changed.air_capacity", 300.0D, 0.0D, 16384.0D).setSyncable(true));
}
