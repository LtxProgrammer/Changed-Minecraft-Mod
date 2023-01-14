package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.network.packet.SyncVariantAbilityPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public abstract class AbstractAbility {
    public TranslatableComponent getDisplayName() {
        return new TranslatableComponent("ability." + getId().toString().replace(':', '.'));
    }

    public abstract ResourceLocation getId();

    public abstract boolean canUse(Player player, LatexVariant<?> variant);
    public abstract boolean canKeepUsing(Player player, LatexVariant<?> variant);

    public abstract void startUsing(Player player, LatexVariant<?> variant);
    public abstract void tick(Player player, LatexVariant<?> variant);
    public abstract void stopUsing(Player player, LatexVariant<?> variant);

    // Called when the player loses the variant (death or untransfur)
    public void onRemove(Player player, LatexVariant<?> variant) {}

    // A unique tag for the ability is provided when saving/reading data. If no data is saved to the tag, then readData does not run
    public void saveData(CompoundTag tag, Player player, LatexVariant<?> variant) {}
    public void readData(CompoundTag tag, Player player, LatexVariant<?> variant) {}

    // Broadcast changes to clients
    protected void setDirty(Player player, LatexVariant<?> variant) {
        CompoundTag data = new CompoundTag();
        saveData(data, player, variant);

        if (player.level.isClientSide)
            Changed.PACKET_HANDLER.sendToServer(new SyncVariantAbilityPacket(getId(), data));
        else
            Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SyncVariantAbilityPacket(getId(), data, player.getUUID()));
    }
}
