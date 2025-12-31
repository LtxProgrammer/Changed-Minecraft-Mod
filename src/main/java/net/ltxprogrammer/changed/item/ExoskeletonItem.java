package net.ltxprogrammer.changed.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.ltxprogrammer.changed.block.IRobotCharger;
import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.robot.AbstractRobot;
import net.ltxprogrammer.changed.entity.robot.ChargerType;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Cacheable;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class ExoskeletonItem<T extends AbstractRobot> extends PlaceableEntity<T> implements AccessoryItem, ExtendedItemProperties {
    protected static final UUID MECH_ATTACK_DAMAGE_UUID = UUID.fromString("bfed474a-d281-4102-9b5f-cd785026b1d5");
    protected static final UUID MECH_ATTACK_SPEED_UUID = UUID.fromString("8c461d33-f151-4c32-a2d7-e76593ce5a35");
    protected static final UUID MECH_MOVEMENT_SPEED_UUID = UUID.fromString("97790787-d3fe-47bd-90eb-86c63164f131");
    protected static final UUID MECH_ARMOR_UUID = UUID.fromString("40845805-4dde-4c45-8eb7-defe001f9035");
    protected static final UUID MECH_KNOCKBACK_UUID = UUID.fromString("494836c5-32c2-4b38-9ae3-261d295389e3");

    private static final Cacheable<Multimap<Attribute, AttributeModifier>> DEFAULT_MODIFIERS = Cacheable.of(() -> {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(MECH_ATTACK_DAMAGE_UUID, "Weapon modifier", 2, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(MECH_ATTACK_SPEED_UUID, "Weapon modifier", -1, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(MECH_MOVEMENT_SPEED_UUID, "Movement modifier", 0.2, AttributeModifier.Operation.MULTIPLY_TOTAL));
        builder.put(Attributes.ARMOR, new AttributeModifier(MECH_ARMOR_UUID, "Armor modifier", 20, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(MECH_KNOCKBACK_UUID, "Armor modifier", 2, AttributeModifier.Operation.MULTIPLY_BASE));
        return builder.build();
    });

    public static final int CHARGE_IN_SECONDS = 12 * 60; // 12 minutes
    public static final int CHARGE_LOW_WARNING = CHARGE_IN_SECONDS - (3 * 60); // Warn player when lower than 3 minutes left
    public static final int CHARGE_CRITICAL_WARNING = CHARGE_IN_SECONDS - 60; // Warn player when lower than 1 minute left

    public static final int EXOSKELETON_EQUIP_DELAY = 30;

    public ExoskeletonItem(Properties builder, Supplier<EntityType<T>> entityType) {
        super(builder.durability(CHARGE_IN_SECONDS), entityType);
        DispenserBlock.registerBehavior(this, AccessoryItem.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment == Enchantments.THORNS) return true;

        return enchantment != Enchantments.MENDING && super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean isConsideredByEnchantment(Enchantment enchantment, ItemStack itemStack, AccessorySlotType slotType, LivingEntity pEntity) {
        if (enchantment == Enchantments.THORNS) return true;

        return AccessoryItem.super.isConsideredByEnchantment(enchantment, itemStack, slotType, pEntity);
    }

    @Override
    public boolean isConsideredIntoPostHurtEffects(ItemStack itemStack, AccessorySlotType slotType, LivingEntity livingEntity) {
        return true;
    }

    @Override
    public boolean allowedInSlot(ItemStack itemStack, LivingEntity wearer, AccessorySlotType slot) {
        if (!canUse(itemStack)) {
            return false;
        }

        boolean isTransfurring = ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(wearer)).map(variant -> variant.transfurProgression)
                .map(progress -> progress < 1f).orElse(false);

        return !isTransfurring && EntityUtil.maybeGetOverlaying(wearer).getType().is(ChangedTags.EntityTypes.CAN_WEAR_EXOSKELETON);
    }

    @Override
    public boolean shouldDisableSlot(AccessorySlotContext<?> slotContext, AccessorySlotType otherSlot) {
        return AccessoryItem.super.shouldDisableSlot(slotContext, otherSlot) || otherSlot == ChangedAccessorySlots.HANDS.get();
    }

    // TODO: extend functionality to allow custom values
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(ItemStack stack) {
        return DEFAULT_MODIFIERS.get();
    }

    public float getJumpStrengthMultiplier(ItemStack stack) {
        return 1.25f;
    }

    public float getFallDamageMultiplier(ItemStack stack) {
        return 0.4f;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.CHEST) {
            return getAttributeModifiers(stack);
        }

        return ImmutableMultimap.of();
    }

    @Override
    protected void finalizeEntity(T entity, ItemStack itemStack) {
        super.finalizeEntity(entity, itemStack);
        entity.loadFromItemStack(itemStack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPlaceContext placeContext = new BlockPlaceContext(context);
        var blockState = level.getBlockState(context.getClickedPos());

        if (blockState.getBlock() instanceof IRobotCharger charger && charger.getChargerType() == ChargerType.EXOSKELETON) {
            var robot = this.placeAndShrink(placeContext);
            if (robot != null) {
                charger.acceptRobot(blockState, level, context.getClickedPos(), robot);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return super.useOn(context);
    }

    protected boolean canUse(ItemStack stack) {
        return !(stack.getDamageValue() >= stack.getMaxDamage() - 1);
    }

    private static Component makePrompt(ItemStack itemStack, Component text) {
        MutableComponent hoverName = (Component.literal(""))
                .append(itemStack.getHoverName())
                .withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(itemStack))));

        return Component.translatable("changed.exoskeleton.prompt", hoverName, text)
                .withStyle(ChatFormatting.AQUA);
    }

    protected static final Component EXOSKELETON_BATTERY_LOW = Component.translatable("changed.exoskeleton.battery_low");
    protected static final Component EXOSKELETON_BATTERY_CRITICAL = Component.translatable("changed.exoskeleton.battery_critical");

    protected void tellWearer(LivingEntity entity, ItemStack itemStack, Component message) {
        if (entity instanceof ServerPlayer wearer) {
            wearer.displayClientMessage(makePrompt(itemStack, message), false);
            ChangedSounds.sendLocalSound(wearer, ChangedSounds.EXOSKELETON_CHIME, 0.6f, 1f);
        }
    }

    private static final float UNBREAKABLE_NERF = 1.0f - 0.6F; // 60% chance to ignore unbreakable enchantment

    // Copied from ItemStack.hurt(), with custom unbreakable nerf
    protected boolean hurtItem(ItemStack stack, int damage, RandomSource random, @Nullable ServerPlayer wearer) {
        if (!stack.isDamageableItem()) {
            return false;
        } else {
            if (damage > 0) {
                int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);
                int j = 0;

                for(int k = 0; i > 0 && k < damage; ++k) {
                    if (random.nextFloat() < UNBREAKABLE_NERF && DigDurabilityEnchantment.shouldIgnoreDurabilityDrop(stack, i, random)) {
                        ++j;
                    }
                }

                damage -= j;
                if (damage <= 0) {
                    return false;
                }
            }

            if (wearer != null && damage != 0) {
                CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(wearer, stack, stack.getDamageValue() + damage);
            }

            int l = stack.getDamageValue() + damage;
            stack.setDamageValue(l);
            return l >= stack.getMaxDamage();
        }
    }

    protected void degradeCharge(AccessorySlotContext<?> slotContext, int amount) {
        if (slotContext.wearer().level().isClientSide)
            return;

        ServerPlayer player = null;
        if (slotContext.wearer() instanceof ServerPlayer serverPlayer)
            player = serverPlayer;

        int before = slotContext.stack().getDamageValue();
        this.hurtItem(slotContext.stack(), amount, slotContext.wearer().getRandom(), player);
        int after = slotContext.stack().getDamageValue();

        if (after >= CHARGE_LOW_WARNING && before < CHARGE_LOW_WARNING)
            tellWearer(slotContext.wearer(), slotContext.stack(), EXOSKELETON_BATTERY_LOW);
        else if (after >= CHARGE_CRITICAL_WARNING && before < CHARGE_CRITICAL_WARNING)
            tellWearer(slotContext.wearer(), slotContext.stack(), EXOSKELETON_BATTERY_CRITICAL);

        if (after >= CHARGE_IN_SECONDS) {
            slotContext.stack().setDamageValue(CHARGE_IN_SECONDS - 1);
            AccessorySlots.tryReplaceSlot(slotContext.wearer(), slotContext.slotType(), ItemStack.EMPTY);
        }
    }

    @Override
    public void accessoryTick(AccessorySlotContext<?> slotContext) {
        boolean ignoreDamage = slotContext.wearer() instanceof Player player && player.getAbilities().invulnerable;

        if (!canUse(slotContext.stack())) {
            if (!slotContext.wearer().level().isClientSide && !ignoreDamage)
                AccessorySlots.tryReplaceSlot(slotContext.wearer(), slotContext.slotType(), ItemStack.EMPTY);
        }

        else if (slotContext.wearer().tickCount % 20 == 0) {
            if (!ignoreDamage)
                degradeCharge(slotContext, 1);
        }

        if (slotContext.wearer().isInWaterOrRain() && !ignoreDamage) {
            int rate = slotContext.wearer().isInWater() ? 20 : 40;

            if (slotContext.wearer().tickCount % rate == 0) {
                slotContext.wearer().hurt(ChangedDamageSources.ELECTROCUTION.source(slotContext.wearer().level().registryAccess()), 3);
                TscWeapon.applyShock(slotContext.wearer(), 3);
                degradeCharge(slotContext, 30);
            }
        }
    }

    private static final int ATTACK_STUN = 5;
    private static final double ATTACK_RANGE = 1.0;

    @Override
    public void accessorySwing(AccessorySlotContext<?> slotContext, InteractionHand hand) {
        if (!AccessoryItem.isEmptyHanded(slotContext, hand))
            return;

        TscWeapon.sweepWeapon(slotContext.wearer(), ATTACK_RANGE);
    }

    @Override
    public void accessoryAttack(AccessorySlotContext<?> slotContext, InteractionHand hand, Entity target) {
        if (!AccessoryItem.isEmptyHanded(slotContext, hand))
            return;

        TscWeapon.sweepWeapon(slotContext.wearer(), ATTACK_RANGE);
        if (target instanceof LivingEntity livingTarget) {
            TscWeapon.applyShock(livingTarget, ATTACK_STUN);

            degradeCharge(slotContext, 5);
        }
    }

    @Override
    public float accessoryHurt(AccessorySlotContext<?> slotContext, DamageSource source, float amount) {
        if (!source.is(DamageTypeTags.BYPASSES_ARMOR))
            degradeCharge(slotContext, (int)(4 * amount));
        return amount;
    }

    @Override
    public void accessoryEquipped(AccessorySlotContext<?> slotContext) {
        if (slotContext.wearer() instanceof Player wearer) {
            ChangedSounds.sendLocalSound(wearer, ChangedSounds.EXOSKELETON_LOCK, 0.7f, 1.5f);
        }

        slotContext.wearer().addEffect(
                new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2 * 20, 5, false, false, false));
    }
}
