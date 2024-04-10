package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.process.Pale;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class LatexFlask extends PotionItem implements VariantHoldingBase {
    public LatexFlask(Properties properties) {
        super(properties.tab(ChangedTabs.TAB_CHANGED_ITEMS));
    }

    @Override
    public Item getOriginalItem() {
        return ChangedItems.getBlockItem(ChangedBlocks.ERLENMEYER_FLASK.get());
    }

    @Override
    public Rarity getRarity(ItemStack p_41461_) {
        return Rarity.UNCOMMON;
    }

    public void appendHoverText(ItemStack p_43359_, @Nullable Level p_43360_, List<Component> p_43361_, TooltipFlag p_43362_) {
        Syringe.addOwnerTooltip(p_43359_, p_43361_);
        Syringe.addVariantTooltip(p_43359_, p_43361_);
    }

    public String getDescriptionId(ItemStack p_43364_) {
        return getOrCreateDescriptionId();
    }

    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
        if (this.allowdedIn(tab)) {
            for(ResourceLocation variant : TransfurVariant.PUBLIC_LATEX_FORMS) {
                list.add(Syringe.setOwner(Syringe.setPureVariant(new ItemStack(this), variant), UniversalDist.getLocalPlayer()));
            }
        }
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        Player player = entity instanceof Player ? (Player)entity : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, stack);
        }
        //ChangedSounds.broadcastSound(entity, ChangedSounds.SWORD1, 1, 1);
        if (player != null) {
            CompoundTag tag = stack.getTag();

            if (tag != null && tag.contains("safe") && ProcessTransfur.isPlayerLatex(player)) {
                if (tag.getBoolean("safe"))
                    Pale.tryCure(player);
            }

            else if (tag != null && tag.contains("form")) {
                ResourceLocation formLocation = new ResourceLocation(tag.getString("form"));
                if (formLocation.equals(TransfurVariant.SPECIAL_LATEX))
                    formLocation = Changed.modResource("special/form_" + entity.getUUID());
                ProcessTransfur.transfur(entity, level, ChangedRegistry.TRANSFUR_VARIANT.get().getValue(formLocation), false,
                        TransfurContext.hazard(TransfurCause.FACE_HAZARD));
            }

            else {
                ProcessTransfur.transfur(entity, level, TransfurVariant.FALLBACK_VARIANT, player.isCreative(),
                        TransfurContext.hazard(TransfurCause.FACE_HAZARD));
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            stack = new ItemStack(ChangedBlocks.ERLENMEYER_FLASK.get());
        }

        //entity.gameEvent(entity, GameEvent.DRINKING_FINISH, entity.eyeBlockPosition());
        return stack;
    }
}
