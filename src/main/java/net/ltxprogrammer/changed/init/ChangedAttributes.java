package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.GrabEntityAbilityInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
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
     * How Easily an entity will break free from a grab
     */
    public static final RegistryObject<Attribute> GRAB_STRUGGLE_STRENGTH = REGISTRY.register("grab_struggle_strength",
            () -> new RangedAttribute("attribute.name.changed.grab_struggle_strength", 0.005f, 0.0D, 2048.0D).setSyncable(true));
    /**
     * How Strong an entity's grab is
     */
    public static final RegistryObject<Attribute> GRAB_HOLD_STRENGTH = REGISTRY.register("grab_hold_strength",
            () -> new RangedAttribute("attribute.name.changed.grab_hold_strength", 1.0D, 0.0D, 2048.0D).setSyncable(true));
    /**
     * How difficult it is to escape suit
     */
    public static final RegistryObject<Attribute> SUIT_HOLD_STRENGTH = REGISTRY.register("suit_hold_strength",
            () -> new RangedAttribute("attribute.name.changed.suit_hold_strength", 1.0D, 0.0D, 2048.0D).setSyncable(true));

    /**
     * Multiplies the player's speed difference sprinting vs not. One is vanilla. Zero disables sprint. ONLY APPLIES WITH A TRANSFUR VARIANT.
     */
    public static final RegistryObject<Attribute> SPRINT_SPEED = REGISTRY.register("sprint_speed",
            () -> new RangedAttribute("attribute.name.changed.sprint_speed", 1.0D, 0.0D, 512.0D).setSyncable(true));
    /**
     * Multiplies the player's speed when sneaking. A higher value is faster. One is vanilla. ONLY APPLIES WITH A TRANSFUR VARIANT.
     */
    public static final RegistryObject<Attribute> SNEAK_SPEED = REGISTRY.register("sneak_speed",
            () -> new RangedAttribute("attribute.name.changed.sneak_speed", 1.0D, 0.0D, 512.0D).setSyncable(true));
    /**
     * Replaces player's base air capacity. In seconds. ONLY APPLIES WITH A TRANSFUR VARIANT.
     */
    public static final RegistryObject<Attribute> AIR_CAPACITY = REGISTRY.register("air_capacity",
            () -> new RangedAttribute("attribute.name.changed.air_capacity", 15.0D, 0.0D, 1024.0D).setSyncable(true));
    /**
     * Multiplies the entity's jump height.
     */
    public static final RegistryObject<Attribute> JUMP_STRENGTH = REGISTRY.register("jump_strength",
            () -> new RangedAttribute("attribute.name.changed.jump_strength", 1.0D, 0.0D, 256.0D).setSyncable(true));
    /**
     * Divides the entity's fall distance.
     */
    public static final RegistryObject<Attribute> FALL_RESISTANCE = REGISTRY.register("fall_resistance",
            () -> new RangedAttribute("attribute.name.changed.fall_resistance", 1.0D, 0.5D, 1024.0D).setSyncable(true));
    /**
     * Multiplies the entity's mining speed.
     */
    public static final RegistryObject<Attribute> MINING_SPEED = REGISTRY.register("mining_speed",
            () -> new RangedAttribute("attribute.name.changed.mining_speed", 1.0D, 0.0D, 256.0D).setSyncable(true));

    public static void modifyEntityAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(entityType -> {
            event.add(entityType, ChangedAttributes.TRANSFUR_TOLERANCE.get());
            event.add(entityType, ChangedAttributes.GRAB_STRUGGLE_STRENGTH.get(), GrabEntityAbilityInstance.GRAB_STRENGTH_DECAY);
        });

        event.add(EntityType.PLAYER, ChangedAttributes.TRANSFUR_TOLERANCE.get());
        event.add(EntityType.PLAYER, ChangedAttributes.TRANSFUR_DAMAGE.get(), 3.0D);
        event.add(EntityType.PLAYER, ChangedAttributes.GRAB_STRUGGLE_STRENGTH.get(), GrabEntityAbilityInstance.GRAB_STRENGTH_DECAY_PLAYER);
        event.add(EntityType.PLAYER, ChangedAttributes.GRAB_HOLD_STRENGTH.get(), 1.0D);
        event.add(EntityType.PLAYER, ChangedAttributes.SUIT_HOLD_STRENGTH.get(), 1.0D);
        event.add(EntityType.PLAYER, ChangedAttributes.SPRINT_SPEED.get(), 1.0D);
        event.add(EntityType.PLAYER, ChangedAttributes.SNEAK_SPEED.get(), 1.0D);
        event.add(EntityType.PLAYER, ChangedAttributes.AIR_CAPACITY.get(), 15.0D);
        event.add(EntityType.PLAYER, ChangedAttributes.JUMP_STRENGTH.get(), 1.0D);
        event.add(EntityType.PLAYER, ChangedAttributes.FALL_RESISTANCE.get(), 1.0D);
        event.add(EntityType.PLAYER, ChangedAttributes.MINING_SPEED.get(), 1.0D);
    }
}
