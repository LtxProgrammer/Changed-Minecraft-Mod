package net.ltxprogrammer.changed.init;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.tree.*;
import net.ltxprogrammer.changed.ability.tree.condition.*;
import net.ltxprogrammer.changed.ability.tree.effects.*;
import net.ltxprogrammer.changed.ability.tree.requirements.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ChangedAbilityTreeCodecs {
    public static final DeferredRegister<Codec<? extends NodeEffect>> NODE_EFFECT_REGISTRY = ChangedRegistry.ABILITY_NODE_EFFECTS.createDeferred(Changed.MODID);

    /**
     * A group of node effects. This is useful for controlling multiple effects under one condition.
     */
    public static final RegistryObject<Codec<GroupNodeEffect>> GROUP_EFFECT = NODE_EFFECT_REGISTRY.register("group", () -> GroupNodeEffect.CODEC);
    /**
     * Adds a scalar to the specified attribute in the entity. Can be positive or negative.
     */
    public static final RegistryObject<Codec<AttributeModifierNodeEffect>> ATTRIBUTE_MODIFIER_EFFECT = NODE_EFFECT_REGISTRY.register("attribute_modifier", () -> AttributeModifierNodeEffect.CODEC);
    /**
     * Applies a MobEffect to the entity.
     */
    public static final RegistryObject<Codec<MobEffectNodeEffect>> MOB_EFFECT_EFFECT = NODE_EFFECT_REGISTRY.register("mob_effect", () -> MobEffectNodeEffect.CODEC);
    /**
     * Grants the ability to create an item, with a condition, exhaustion, and hunger required.
     * If the effect is active: the entity has access to an ability that allows them to create the defined item.
     */
    public static final RegistryObject<Codec<UnlockActiveAbilityNodeEffect>> UNLOCK_ACTIVE_ABILITY_EFFECT = NODE_EFFECT_REGISTRY.register("unlock_active_ability", () -> UnlockActiveAbilityNodeEffect.CODEC);
    /**
     * Marks a registered feature as enabled with a factor.
     */
    public static final RegistryObject<Codec<EnableFeatureNodeEffect>> ENABLE_FEATURE_EFFECT = NODE_EFFECT_REGISTRY.register("enable_feature", () -> EnableFeatureNodeEffect.CODEC);
    /**
     * Gives the player an enchantment that is intrinsic to themselves, such as Aqua Affinity
     */
    public static final RegistryObject<Codec<IntrinsicEnchantmentNodeEffect>> INTRINSIC_ENCHANTMENT_EFFECT = NODE_EFFECT_REGISTRY.register("intrinsic_enchantment", () -> IntrinsicEnchantmentNodeEffect.CODEC);
    /**
     * Gives the player an enchantment that is intrinsic to themselves, such as Aqua Affinity
     */
    public static final RegistryObject<Codec<PostChainNodeEffect>> POST_CHAIN_NODE_EFFECT = NODE_EFFECT_REGISTRY.register("post_chain", () -> PostChainNodeEffect.CODEC);

    public static final DeferredRegister<Codec<? extends AbstractCondition>> EFFECT_CONDITION_REGISTRY = ChangedRegistry.ABILITY_EFFECT_CONDITIONS.createDeferred(Changed.MODID);

    /**
     * Always resolves to `true`. This is the default condition used.
     */
    public static final RegistryObject<Codec<TrueCondition>> TRUE_CONDITION = EFFECT_CONDITION_REGISTRY.register("true", () -> TrueCondition.CODEC);
    /**
     * Only resolves to `true` if all sub conditions resolve to `true`, or if this has no sub conditions.
     */
    public static final RegistryObject<Codec<AllCondition>> ALL_CONDITION = EFFECT_CONDITION_REGISTRY.register("all", () -> AllCondition.CODEC);
    /**
     * Resolves to `true` if any of the sub conditions resolve to `true`. Resolves to `false` if this has no sub conditions.
     */
    public static final RegistryObject<Codec<AnyCondition>> ANY_CONDITION = EFFECT_CONDITION_REGISTRY.register("any", () -> AnyCondition.CODEC);
    /**
     * Resolves to `true` if none of the sub conditions resolve to `true`, or if this has no sub conditions.
     * Can be used to negate a condition.
     */
    public static final RegistryObject<Codec<NoneCondition>> NONE_CONDITION = EFFECT_CONDITION_REGISTRY.register("none", () -> NoneCondition.CODEC);
    /**
     * Resolves to `true` if and only if the entities tickCount modulo this tickRate is zero (entity.tickCount % tickRate == 0).
     * Can be used to have an effect run only every set of ticks (i.e. once every 20 ticks)
     */
    public static final RegistryObject<Codec<NthTickCondition>> NTH_TICK_CONDITION = EFFECT_CONDITION_REGISTRY.register("nth_tick", () -> NthTickCondition.CODEC);
    /**
     * Resolves to `true` if the entity is standing on a block that matches any of the given predicates (exact ID, block tag)
     */
    public static final RegistryObject<Codec<StandingOnCondition>> STANDING_ON_CONDITION = EFFECT_CONDITION_REGISTRY.register("standing_on", () -> StandingOnCondition.CODEC);
    /**
     * Resolves to `true` if the entity is either `touching` the fluid predicate, or is `submerged` in the fluid predicate.
     * <p>`touching` tests if the entity's hitbox intersects with the fluid</p>
     * <p>`submerged` tests if the fluid on the entity's "eye" is the specified fluid</p>
     */
    public static final RegistryObject<Codec<InFluidCondition>> IN_FLUID_CONDITION = EFFECT_CONDITION_REGISTRY.register("in_fluid", () -> InFluidCondition.CODEC);
    /**
     * Resolves to `true` if the entity is exposed to the level of light specified.
     * <p>`blockScale` scales the contribution of block light to the test</p>
     * <p>`sunScale` scales the contribution of skylight to the test (depends on the angle of the sun)</p>
     * <p>`moonScale` scales the contribution of moon light to the test (depends on the angle and phase of the moon)</p>
     * The light contribution is computed as such: (blockLight [0-15] * blockScale) + (sunLight [0-15] * cos(sunAngle) * sunScale) + (moonLight [0-4] * cos(moonAngle) * moonScale).
     * The resolved value is done through an integer comparison of the computed light contribution to the threshold.
     * `greater_than` will test if the computed light contribution is greater than the threshold, and is the default value.
     * This can be used to test if the entity is exposed varying facets of light, such as sun exposure, moon exposure, absence of light, etc.
     */
    public static final RegistryObject<Codec<LightExposureCondition>> LIGHT_EXPOSURE_CONDITION = EFFECT_CONDITION_REGISTRY.register("light_exposure", () -> LightExposureCondition.CODEC);

    public static final DeferredRegister<Codec<? extends AbstractRequirement>> PURCHASE_REQUIREMENT_REGISTRY = ChangedRegistry.PURCHASE_REQUIREMENTS.createDeferred(Changed.MODID);

    public static final RegistryObject<Codec<HasAdvancementsRequirement>> HAS_ADVANCEMENTS_REQUIREMENT = PURCHASE_REQUIREMENT_REGISTRY.register("has_advancements", () -> HasAdvancementsRequirement.CODEC);

    public static final RegistryObject<Codec<HasAllOtherNodesInTreeRequirement>> HAS_ALL_OTHER_NODES = PURCHASE_REQUIREMENT_REGISTRY.register("has_all_other_nodes", () -> HasAllOtherNodesInTreeRequirement.CODEC);
}
