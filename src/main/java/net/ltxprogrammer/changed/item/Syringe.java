package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedEffects;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Syringe extends Item {
    public static final DamageSource BLOODLOSS = (new DamageSource("changed:bloodloss")).bypassArmor();

    public Syringe(Properties p_41383_) {
        super(p_41383_.tab(ChangedTabs.TAB_CHANGED_ITEMS));
    }

    @OnlyIn(Dist.CLIENT)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientInitializer {
        @SubscribeEvent
        public static void onItemColorsInit(ColorHandlerEvent.Item event) {
            event.getItemColors().register((stack, layer) ->
                            switch (layer) {
                                case 0 -> Syringe.getVariant(stack) != null ? ChangedEntities.getEntityColorBack(Syringe.getVariant(stack).getEntityType().getRegistryName())
                                        : 0xF0F0F0;
                                case 1 -> Syringe.getVariant(stack) != null ? ChangedEntities.getEntityColorFront(Syringe.getVariant(stack).getEntityType().getRegistryName())
                                        : 0xF0F0F0;
                                default -> -1;
                            },
                    ChangedItems.LATEX_SYRINGE.get());

            event.getItemColors().register((stack, layer) ->
                            switch (layer) {
                                case 0 -> Syringe.getVariant(stack) != null ? ChangedEntities.getEntityColorBack(Syringe.getVariant(stack).getEntityType().getRegistryName())
                                        : 0xF0F0F0;
                                case 1 -> Syringe.getVariant(stack) != null ? ChangedEntities.getEntityColorFront(Syringe.getVariant(stack).getEntityType().getRegistryName())
                                        : 0xF0F0F0;
                                default -> -1;
                            },
                    ChangedItems.LATEX_TIPPED_ARROW.get());
        }
    }

    public static ItemStack setUnpureVariant(ItemStack itemStack, ResourceLocation variant) {
        CompoundTag tag = itemStack.getOrCreateTag();
        TagUtil.putResourceLocation(tag, "form", variant);
        tag.putBoolean("safe", false);
        return itemStack;
    }

    public static void addVariantTooltip(ItemStack p_43556_, List<Component> p_43557_) {
        if (p_43556_.getOrCreateTag().contains("form")) {
            p_43557_.add(new TranslatableComponent(getVariantDescriptionId(p_43556_)));
        }
    }

    public static String getVariantDescriptionId(ItemStack stack) {
        return "entity." + TagUtil.getResourceLocation(stack.getTag(), "form").toString().replace("form_", "")
                .replace(':', '.').replace('/', '_');
    }

    public static LatexVariant<?> getVariant(ItemStack p_43364_) {
        if (p_43364_.getTag() != null) {
            if (p_43364_.getTag().contains("form")) {
                return LatexVariant.PUBLIC_LATEX_FORMS.get(TagUtil.getResourceLocation(p_43364_.getTag(), "form"));
            }
        }

        return LatexVariant.LIGHT_LATEX_WOLF.male();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        Player player = entity instanceof Player ? (Player)entity : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }

        if (player != null) {
            player.hurt(BLOODLOSS, 1.0f);
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            CompoundTag tag = new CompoundTag();
            tag.putUUID("owner", player.getUUID());
            if (ProcessTransfur.isPlayerLatex(player)) {
                ResourceLocation form = ProcessTransfur.getPlayerLatexVariant(player).getFormId();
                if (LatexVariant.SPECIAL_LATEX_FORMS.containsKey(form))
                    form = LatexVariant.SPECIAL_LATEX;
                ItemStack nStack = new ItemStack(ChangedItems.LATEX_SYRINGE.get());
                tag.putBoolean("safe", true);
                tag.putString("form", form.toString());
                nStack.setTag(tag);

                player.getInventory().add(nStack);
            }

            else {
                ItemStack nStack = new ItemStack(ChangedItems.BLOOD_SYRINGE.get());
                nStack.setTag(tag);

                player.getInventory().add(nStack);
            }
        }

        //entity.gameEvent(entity, GameEvent.DRINKING_FINISH, entity.eyeBlockPosition());
        return stack;
    }

    public int getUseDuration(@NotNull ItemStack p_43001_) {
        return 16;
    }

    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack p_42997_) {
        return UseAnim.DRINK;
    }

    public static class BloodSyringe extends Syringe {
        public BloodSyringe(Properties p_41383_) {
            super(p_41383_);
        }

        public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
            Player player = entity instanceof Player ? (Player)entity : null;
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
            }

            if (player != null) {
                if (stack.getTag() != null && stack.getTag().getUUID("owner").equals(player.getUUID())) {
                    player.heal(1.0f);
                }

                else {
                    player.addEffect(new MobEffectInstance(ChangedEffects.HYPERCOAGULATION, 800));
                }

                player.awardStat(Stats.ITEM_USED.get(this));
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                stack = new ItemStack(ChangedItems.SYRINGE.get());
            }

            //entity.gameEvent(entity, GameEvent.DRINKING_FINISH, entity.eyeBlockPosition());
            return stack;
        }

        @Override
        public boolean isFoil(ItemStack stack) {
            if (stack.getTag() == null)
                return false;
            return stack.getTag().contains("owner");
        }
    }

    public static class LatexSyringe extends Syringe {
        public LatexSyringe(Properties p_41383_) {
            super(p_41383_);
        }

        public void fillItemCategory(CreativeModeTab p_43356_, NonNullList<ItemStack> p_43357_) {
            if (this.allowdedIn(p_43356_)) {
                for(ResourceLocation variant : LatexVariant.PUBLIC_LATEX_FORMS.keySet()) {
                    p_43357_.add(Syringe.setUnpureVariant(new ItemStack(this), variant));
                }
            }
        }

        public void appendHoverText(ItemStack p_43359_, @Nullable Level p_43360_, List<Component> p_43361_, TooltipFlag p_43362_) {
            Syringe.addVariantTooltip(p_43359_, p_43361_);
        }

        public int getUseDuration(@NotNull ItemStack p_43001_) {
            return 48;
        }

        public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
            Player player = entity instanceof Player ? (Player)entity : null;
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
            }

            if (player != null) {
                CompoundTag tag = stack.getTag();

                if (tag != null && tag.contains("form")) {
                    ResourceLocation formLocation = new ResourceLocation(tag.getString("form"));
                    if (formLocation.equals(LatexVariant.SPECIAL_LATEX))
                        formLocation = Changed.modResource("special/form_" + entity.getUUID());
                    ProcessTransfur.transfur(entity, level, LatexVariant.ALL_LATEX_FORMS.getOrDefault(formLocation, LatexVariant.LIGHT_LATEX_WOLF.male()),
                            false);
                }

                else {
                    ProcessTransfur.transfur(entity, level, LatexVariant.LIGHT_LATEX_WOLF.male(), player.isCreative());
                }

                player.awardStat(Stats.ITEM_USED.get(this));
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                stack = new ItemStack(ChangedItems.SYRINGE.get());
            }

            //entity.gameEvent(entity, GameEvent.DRINKING_FINISH, entity.eyeBlockPosition());
            return stack;
        }

        @Override
        public boolean isFoil(ItemStack stack) {
            if (stack.getTag() == null)
                return false;
            return stack.getTag().contains("form") && stack.getTag().contains("owner");
        }

        @Override
        public @NotNull Rarity getRarity(ItemStack stack) {
            if (stack.getTag() == null)
                return Rarity.COMMON;
            return stack.getTag().contains("safe") ? (stack.getTag().getBoolean("safe") ? Rarity.RARE : Rarity.UNCOMMON) : Rarity.UNCOMMON;
        }
    }
}
