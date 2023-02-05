package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class PinkLatex implements ArmorMaterial {
    public static PinkLatex INSTANCE = new PinkLatex();

    @Override
    public int getDurabilityForSlot(EquipmentSlot slot) {
        return 5;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot slot) {
        return 2;
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public SoundEvent getEquipSound() {
        return ChangedSounds.EQUIP3;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of();
    }

    @Override
    public String getName() {
        return Changed.modResourceStr("pink");
    }

    @Override
    public float getToughness() {
        return 0;
    }

    @Override
    public float getKnockbackResistance() {
        return 0;
    }

    public static class Pants extends ArmorItem implements LatexFusingItem {
        public Pants() {
            super(INSTANCE, EquipmentSlot.LEGS, new Properties().tab(ChangedTabs.TAB_CHANGED_ITEMS));
        }

        @Nullable
        @Override
        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return Changed.modResourceStr("textures/models/pink_pants.png");
        }

        @Override
        public LatexVariant<?> getFusionVariant(LatexVariant<?> currentVariant, LivingEntity livingEntity, ItemStack itemStack) {
            if (currentVariant.is(LatexVariant.LATEX_DEER))
                return LatexVariant.LATEX_PINK_DEER;
            else if (currentVariant.is(LatexVariant.LATEX_YUIN))
                return LatexVariant.LATEX_PINK_YUIN_DRAGON;
            return null;
        }


        @Override
        public void nonLatexWearTick(LivingEntity entity, ItemStack itemStack) {
            var tag = itemStack.getOrCreateTag();
            var age = (tag.contains("age") ? tag.getInt("age") : 0) + 1;
            tag.putInt("age", age);
            if (age < 12000) // Half a minecraft day
                return;
            if (ProcessTransfur.progressTransfur(entity, 3000, LatexVariant.LATEX_PINK_WYVERN.getFormId()))
                itemStack.shrink(1);
        }

        @Override
        public void nonFusionWearTick(LivingEntity entity, ItemStack itemStack) {
            if (entity instanceof Player player && ProcessTransfur.isPlayerLatex(player)) {
                if (ProcessTransfur.isPlayerOrganic(player))
                    return;
                if (player.getRandom().nextBoolean()) {
                    var newEntity = ProcessTransfur.getPlayerLatexVariant(player).getEntityType().create(entity.level);
                    newEntity.moveTo(entity.position());
                    entity.level.addFreshEntity(newEntity);
                    ProcessTransfur.setPlayerLatexVariant(player, LatexVariant.LATEX_PINK_WYVERN);
                } else {
                    var wyvern = ChangedEntities.LATEX_PINK_WYVERN.get().create(entity.level);
                    wyvern.moveTo(entity.position());
                    entity.level.addFreshEntity(wyvern);
                }
            }

            else {
                if (entity.getType().is(ChangedTags.EntityTypes.ORGANIC_LATEX))
                    return;
                var wyvern = ChangedEntities.LATEX_PINK_WYVERN.get().create(entity.level);
                wyvern.moveTo(entity.position());
                entity.level.addFreshEntity(wyvern);
            }

            itemStack.shrink(1);
            ChangedSounds.broadcastSound(entity, LatexVariant.LATEX_PINK_WYVERN.sound, 1, 1);
        }

        @Override
        public boolean customWearRenderer() {
            return false;
        }

        @Override
        public boolean allowedToKeepWearing(LivingEntity entity) {
            return true;
        }
    }
}
