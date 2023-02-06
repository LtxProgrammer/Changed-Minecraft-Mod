package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedEffects;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.Pale;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.TagUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
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

    public static ItemStack setPureVariant(ItemStack itemStack, ResourceLocation variant) {
        CompoundTag tag = itemStack.getOrCreateTag();
        setVariant(itemStack, variant);
        tag.putBoolean("safe", true);
        return itemStack;
    }

    public static ItemStack setUnpureVariant(ItemStack itemStack, ResourceLocation variant) {
        CompoundTag tag = itemStack.getOrCreateTag();
        setVariant(itemStack, variant);
        tag.putBoolean("safe", false);
        return itemStack;
    }

    public static ItemStack setVariant(ItemStack itemStack, ResourceLocation variant) {
        CompoundTag tag = itemStack.getOrCreateTag();
        TagUtil.putResourceLocation(tag, "form", variant);
        return itemStack;
    }

    public static ItemStack setOwner(ItemStack itemStack, @Nullable Player player) {
        if (player == null)
            return itemStack;
        itemStack.getOrCreateTag().putUUID("owner", player.getUUID());
        return itemStack;
    }

    public static void addOwnerTooltip(ItemStack stack, List<Component> builder) {
        if (stack.getOrCreateTag().contains("owner")) {
            Player player = UniversalDist.getLevel().getPlayerByUUID(stack.getOrCreateTag().getUUID("owner"));
            if (player != null)
                builder.add(new TranslatableComponent("text.changed.syringe.owner", player.getName()));
            else
                builder.add(new TranslatableComponent("text.changed.syringe.no_owner"));
        }
    }

    public static void addVariantTooltip(ItemStack stack, List<Component> builder) {
        if (stack.getOrCreateTag().contains("form")) {
            builder.add(new TranslatableComponent(getVariantDescriptionId(stack)));
        }
    }

    public static String getVariantDescriptionId(ItemStack stack) {
        LatexVariant<?> variant = LatexVariant.ALL_LATEX_FORMS.get(TagUtil.getResourceLocation(stack.getTag(), "form"));
        if (variant == null)
            return "entity." + TagUtil.getResourceLocation(stack.getTag(), "form").toString().replace("form_", "")
                    .replace(':', '.').replace('/', '_');
        return variant.getEntityType().getDescriptionId();
    }

    public static LatexVariant<?> getVariant(ItemStack p_43364_) {
        if (p_43364_.getTag() != null) {
            if (p_43364_.getTag().contains("form")) {
                return LatexVariant.PUBLIC_LATEX_FORMS.get(TagUtil.getResourceLocation(p_43364_.getTag(), "form"));
            }
        }

        return LatexVariant.FALLBACK_VARIANT;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    public @NotNull ItemStack usedOnPlayer(@NotNull ItemStack stack, @NotNull Level level, @NotNull Player player, boolean ignoreMovement) {
        if (!ignoreMovement && player.getDeltaMovement().lengthSqr() > 0.01f)
            return stack;

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
            tag.putBoolean("safe", Pale.isCured(player));
            tag.putString("form", form.toString());
            nStack.setTag(tag);

            player.getInventory().add(nStack);
        }

        else {
            ItemStack nStack = new ItemStack(ChangedItems.BLOOD_SYRINGE.get());
            nStack.setTag(tag);

            player.getInventory().add(nStack);
        }

        return stack;
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        Player player = entity instanceof Player ? (Player)entity : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }

        if (player != null)
            return usedOnPlayer(stack, level, player, false);

        //entity.gameEvent(entity, GameEvent.DRINKING_FINISH, entity.eyeBlockPosition());
        return stack;
    }

    public int getUseDuration(@NotNull ItemStack p_43001_) {
        return 16;
    }

    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack p_42997_) {
        return UseAnim.DRINK;
    }

    // Cancel this event if your implementation consumes the action upon a block
    public static class UsedOnBlock extends Event {
        public final BlockPos blockPos;
        public final BlockState blockState;
        public final Level level;
        public final Player player;

        public final ItemStack syringe;

        public UsedOnBlock(BlockPos blockPos, BlockState blockState, Level level, Player player, ItemStack syringe) {
            this.blockPos = blockPos;
            this.blockState = blockState;
            this.level = level;
            this.player = player;
            this.syringe = syringe;
        }

        @Override public boolean isCancelable() { return true; }
    }

    // Cancel this event if your implementation consumes the action upon a block
    public static class UsedOnEntity extends Event {
        public final LivingEntity entity;
        public final Level level;
        public final Player player;

        public final ItemStack syringe;

        public UsedOnEntity(LivingEntity entity, Level level, Player player, ItemStack syringe) {
            this.entity = entity;
            this.level = level;
            this.player = player;
            this.syringe = syringe;
        }

        @Override public boolean isCancelable() { return true; }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
        return MinecraftForge.EVENT_BUS.post(
                new UsedOnBlock(context.getClickedPos(),
                        clickedState,
                        context.getLevel(),
                        context.getPlayer(),
                        context.getItemInHand())) ?
                InteractionResult.sidedSuccess(context.getLevel().isClientSide) :
                super.useOn(context);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand hand) {
        if (livingEntity instanceof Player interactPlayer) {
            usedOnPlayer(itemStack, interactPlayer.level, interactPlayer, false);
            return InteractionResult.sidedSuccess(player.level.isClientSide);
        }

        else if (livingEntity instanceof LatexEntity latex) {
            var variant = latex.getSelfVariant() != null ? latex.getSelfVariant() : latex.getTransfurVariant();
            if (variant != null) {
                CompoundTag tag = new CompoundTag();
                ItemStack nStack = new ItemStack(ChangedItems.LATEX_SYRINGE.get());
                tag.putBoolean("safe", true);
                tag.putString("form", variant.toString());
                nStack.setTag(tag);
                if (!player.getAbilities().instabuild)
                    itemStack.shrink(1);

                player.getInventory().add(nStack);
                return InteractionResult.sidedSuccess(player.level.isClientSide);
            }
        }
        return MinecraftForge.EVENT_BUS.post(
                new UsedOnEntity(livingEntity,
                        player.level,
                        player,
                        itemStack)) ?
                InteractionResult.sidedSuccess(player.level.isClientSide) :
                super.interactLivingEntity(itemStack, player, livingEntity, hand);
    }
}
