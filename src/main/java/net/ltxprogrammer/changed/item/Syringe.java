package net.ltxprogrammer.changed.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.Pale;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.TagUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class Syringe extends Item implements SpecializedAnimations {
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

    public @NotNull ItemStack usedOnPlayer(@NotNull ItemStack stack, @NotNull Level level, @NotNull Player player, @NotNull Player sourcePlayer, boolean ignoreMovement) {
        if (!ignoreMovement && player.getDeltaMovement().lengthSqr() > 0.01f)
            return stack;
        if (!ProcessTransfur.isPlayerOrganic(player) && player != sourcePlayer)
            return stack;

        player.hurt(BLOODLOSS, 1.0f);
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        CompoundTag tag = new CompoundTag();
        tag.putUUID("owner", player.getUUID());

        ProcessTransfur.ifPlayerLatex(player, variant -> {
            ResourceLocation form = variant.getFormId();
            if (LatexVariant.SPECIAL_LATEX_FORMS.containsKey(form))
                form = LatexVariant.SPECIAL_LATEX;
            ItemStack nStack = new ItemStack(ChangedItems.LATEX_SYRINGE.get());
            tag.putBoolean("safe", Pale.isCured(player));
            tag.putString("form", form.toString());
            nStack.setTag(tag);

            player.getInventory().add(nStack);
        }, () -> {
            ItemStack nStack = new ItemStack(ChangedItems.BLOOD_SYRINGE.get());
            nStack.setTag(tag);

            player.getInventory().add(nStack);
        });
        return stack;
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        Player player = entity instanceof Player ? (Player)entity : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }
        ChangedSounds.broadcastSound(entity, ChangedSounds.SWORD1, 1, 1);
        if (player != null)
            return usedOnPlayer(stack, level, player, player, false);

        //entity.gameEvent(entity, GameEvent.DRINKING_FINISH, entity.eyeBlockPosition());
        return stack;
    }

    public int getUseDuration(@NotNull ItemStack p_43001_) {
        return 16;
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
            usedOnPlayer(itemStack, interactPlayer.level, interactPlayer, player, false);
            return InteractionResult.sidedSuccess(player.level.isClientSide);
        }

        return MinecraftForge.EVENT_BUS.post(
                new UsedOnEntity(livingEntity,
                        player.level,
                        player,
                        itemStack)) ?
                InteractionResult.sidedSuccess(player.level.isClientSide) :
                super.interactLivingEntity(itemStack, player, livingEntity, hand);
    }

    @Nullable
    @Override
    public AnimationHandler getAnimationHandler() {
        return new SyringeAnimation(this);
    }

    public static class SyringeAnimation extends AnimationHandler {
        public SyringeAnimation(Item item) {
            super(item);
        }

        @Override
        public void setupUsingAnimation(ItemStack itemStack, EntityStateContext entity, UpperModelContext model, HumanoidArm arm, float progress) {
            super.setupUsingAnimation(itemStack, entity, model, arm, progress);
            model.pointArmAt(arm, new Vec3(arm == HumanoidArm.RIGHT ? -1 : 1, -0.2, 0.35),
                    Mth.clamp(1.0F - (float)Math.pow(1.0 - progress, 27.0D), 0, 1));
        }

        @Override
        public void setupFirstPersonUseAnimation(ItemStack itemStack, EntityStateContext entity, HumanoidArm arm, PoseStack pose, float progress) {
            super.setupFirstPersonUseAnimation(itemStack, entity, arm, pose, progress);
            float relativeProgress = progress * itemStack.getUseDuration();
            if (progress > 0.2F)
                pose.translate(0.0D, Mth.abs(Mth.cos(relativeProgress / 4.0F * (float)Math.PI) * 0.025F), 0.0D);

            float f3 = 1.0F - (float)Math.pow(1.0 - progress, 27.0D);
            int i = arm == HumanoidArm.RIGHT ? 1 : -1;
            pose.translate((double)(f3 * 0.3F * (float)i), (double)(f3 * -0.5F), (double)(f3 * 0.0F));
            pose.mulPose(Vector3f.YP.rotationDegrees((float)i * f3 * 90.0F));
            pose.mulPose(Vector3f.XP.rotationDegrees(f3 * 10.0F));
            pose.mulPose(Vector3f.ZP.rotationDegrees((float)i * f3 * 30.0F));
        }
    }

    @Override
    public boolean triggerItemUseEffects(LivingEntity entity, ItemStack itemStack, int particleCount) {
        return true;
    }
}
