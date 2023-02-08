package net.ltxprogrammer.changed.item;

import com.google.common.collect.Multimap;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedEffects;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class TscStaff extends TscWeapon implements SpecializedItemRendering {
    public TscStaff() {
        super(new Properties().durability(500));
    }

    private static final ModelResourceLocation STAFF_INVENTORY =
            new ModelResourceLocation(Changed.modResource("tsc_staff"), "inventory");
    private static final ModelResourceLocation STAFF_IN_HAND =
            new ModelResourceLocation(Changed.modResource("tsc_staff_in_hand"), "inventory");
    private static final ModelResourceLocation STAFF_IN_HAND_EMISSIVE =
            new ModelResourceLocation(Changed.modResource("tsc_staff_in_hand_emissive"), "inventory");

    @Nullable
    @Override
    public ModelResourceLocation getEmissiveModelLocation(ItemStack itemStack, ItemTransforms.TransformType type) {
        return SpecializedItemRendering.isGUI(type) ? null : STAFF_IN_HAND_EMISSIVE;
    }

    @Override
    public ModelResourceLocation getModelLocation(ItemStack itemStack, ItemTransforms.TransformType type) {
        return SpecializedItemRendering.isGUI(type) ? STAFF_INVENTORY : STAFF_IN_HAND;
    }

    @Override
    public void loadSpecialModels(Consumer<ResourceLocation> loader) {
        loader.accept(STAFF_IN_HAND);
        loader.accept(STAFF_IN_HAND_EMISSIVE);
    }

    public boolean hurtEnemy(ItemStack itemStack, LivingEntity enemy, LivingEntity source) {
        enemy.addEffect(new MobEffectInstance(ChangedEffects.SHOCK, 10, 0, false, false, true));
        itemStack.hurtAndBreak(1, source, (entity) -> {
            entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    @Override
    public double attackDamage() {
        return Tiers.IRON.getAttackDamageBonus();
    }

    @Override
    public double attackSpeed() {
        return Tiers.WOOD.getSpeed();
    }

    @Override
    public int getEnchantmentValue() {
        return Tiers.IRON.getEnchantmentValue();
    }
}
