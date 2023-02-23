package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.process.Pale;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
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

public class LatexSyringe extends Item implements SpecializedAnimations {
    public LatexSyringe(Properties p_41383_) {
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

    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
        if (this.allowdedIn(tab)) {
            for(ResourceLocation variant : LatexVariant.PUBLIC_LATEX_FORMS) {
                list.add(Syringe.setOwner(Syringe.setPureVariant(new ItemStack(this), variant), UniversalDist.getLocalPlayer()));
            }
        }
    }

    public void appendHoverText(ItemStack p_43359_, @Nullable Level p_43360_, List<Component> p_43361_, TooltipFlag p_43362_) {
        Syringe.addOwnerTooltip(p_43359_, p_43361_);
        Syringe.addVariantTooltip(p_43359_, p_43361_);
    }

    public int getUseDuration(@NotNull ItemStack p_43001_) {
        return 48;
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
        ChangedSounds.broadcastSound(entity, ChangedSounds.SWORD1, 1, 1);
        if (player != null) {
            CompoundTag tag = stack.getTag();

            if (tag != null && tag.contains("safe") && ProcessTransfur.isPlayerLatex(player)) {
                if (tag.getBoolean("safe"))
                    Pale.tryCure(player);
            }

            else if (tag != null && tag.contains("form")) {
                ResourceLocation formLocation = new ResourceLocation(tag.getString("form"));
                if (formLocation.equals(LatexVariant.SPECIAL_LATEX))
                    formLocation = Changed.modResource("special/form_" + entity.getUUID());
                ProcessTransfur.transfur(entity, level, ChangedRegistry.LATEX_VARIANT.get().getValue(formLocation),
                        false);
            }

            else {
                ProcessTransfur.transfur(entity, level, LatexVariant.FALLBACK_VARIANT, player.isCreative());
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

    @Nullable
    @Override
    public SpecializedAnimations.AnimationHandler getAnimationHandler() {
        return new Syringe.SyringeAnimation(this);
    }

    @Override
    public boolean triggerItemUseEffects(LivingEntity entity, ItemStack itemStack, int particleCount) {
        return true;
    }

    // Cancel this event if your implementation consumes the action upon a block
    public static class UsedOnBlock extends Event {
        public final BlockPos blockPos;
        public final BlockState blockState;
        public final Level level;
        public final Player player;

        public final ItemStack syringe;
        public final LatexVariant<?> syringeVariant;

        public UsedOnBlock(BlockPos blockPos, BlockState blockState, Level level, Player player, ItemStack syringe, LatexVariant<?> syringeVariant) {
            this.blockPos = blockPos;
            this.blockState = blockState;
            this.level = level;
            this.player = player;
            this.syringe = syringe;
            this.syringeVariant = syringeVariant;
        }

        @Override public boolean isCancelable() { return true; }
    }

    // Cancel this event if your implementation consumes the action upon a block
    public static class UsedOnEntity extends Event {
        public final LivingEntity entity;
        public final Level level;
        public final Player player;

        public final ItemStack syringe;
        public final LatexVariant<?> syringeVariant;

        public UsedOnEntity(LivingEntity entity, Level level, Player player, ItemStack syringe, LatexVariant<?> syringeVariant) {
            this.entity = entity;
            this.level = level;
            this.player = player;
            this.syringe = syringe;
            this.syringeVariant = syringeVariant;
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
                        context.getItemInHand(),
                        Syringe.getVariant(context.getItemInHand()))) ?
                InteractionResult.sidedSuccess(context.getLevel().isClientSide) :
                super.useOn(context);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand hand) {
        return MinecraftForge.EVENT_BUS.post(
                new UsedOnEntity(livingEntity,
                        player.level,
                        player,
                        itemStack,
                        Syringe.getVariant(itemStack))) ?
                InteractionResult.sidedSuccess(player.level.isClientSide) :
                super.interactLivingEntity(itemStack, player, livingEntity, hand);
    }
}
