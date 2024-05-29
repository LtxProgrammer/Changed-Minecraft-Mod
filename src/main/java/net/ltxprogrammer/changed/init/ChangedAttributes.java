package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ChangedAttributes {
    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(Registry.ATTRIBUTE_REGISTRY, Changed.MODID);

    public static final RegistryObject<Attribute> TRANSFUR_TOLERANCE = REGISTRY.register("transfur_tolerance",
            () -> new RangedAttribute("attribute.name.changed.transfur_tolerance", 20.0D, 1.0D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> TRANSFUR_DAMAGE = REGISTRY.register("transfur_damage",
            () -> new RangedAttribute("attribute.name.changed.transfur_damage", 3.0D, 0.0D, 2048.0D));
}
