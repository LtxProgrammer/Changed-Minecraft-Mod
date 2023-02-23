package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.network.packet.SyncVariantAbilityPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import org.apache.commons.lang3.function.TriFunction;

public abstract class AbstractAbility<Instance extends AbstractAbilityInstance> {
    private final TriFunction<AbstractAbility<Instance>, Player, LatexVariantInstance<?>, Instance> ctor;

    public AbstractAbility(TriFunction<AbstractAbility<Instance>, Player, LatexVariantInstance<?>, Instance> ctor) {
        this.ctor = ctor;
    }

    public Instance makeInstance(Player player, LatexVariantInstance<?> variant) {
        return ctor.apply(this, player, variant);
    }

    public TranslatableComponent getDisplayName(Player player, LatexVariantInstance<?> variant) {
        return new TranslatableComponent("ability." + getId().toString().replace(':', '.'));
    }

    public abstract ResourceLocation getId();

    public boolean canUse(Player player, LatexVariantInstance<?> variant) { return false; }
    public boolean canKeepUsing(Player player, LatexVariantInstance<?> variant) { return false; }

    public void startUsing(Player player, LatexVariantInstance<?> variant) {}
    public void tick(Player player, LatexVariantInstance<?> variant) {}
    public void stopUsing(Player player, LatexVariantInstance<?> variant) {}

    // Called when the player loses the variant (death or untransfur)
    public void onRemove(Player player, LatexVariantInstance<?> variant) {}

    // A unique tag for the ability is provided when saving/reading data. If no data is saved to the tag, then readData does not run
    public void saveData(CompoundTag tag, Player player, LatexVariantInstance<?> variant) {}
    public void readData(CompoundTag tag, Player player, LatexVariantInstance<?> variant) {}

    public ResourceLocation getTexture(Player player, LatexVariantInstance<?> variant) {
        return new ResourceLocation(getId().getNamespace(), "textures/abilities/" + getId().getPath() + ".png");
    }

    // Broadcast changes to clients
    public final void setDirty(Player player, LatexVariantInstance<?> variant) {
        CompoundTag data = new CompoundTag();
        saveData(data, player, variant);

        if (player.level.isClientSide)
            Changed.PACKET_HANDLER.sendToServer(new SyncVariantAbilityPacket(getId(), data));
        else
            Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SyncVariantAbilityPacket(getId(), data, player.getUUID()));
    }

    public final void setDirty(AccessSaddleAbilityInstance instance) {
        CompoundTag data = new CompoundTag();
        instance.saveData(data);

        if (instance.player.level.isClientSide)
            Changed.PACKET_HANDLER.sendToServer(new SyncVariantAbilityPacket(getId(), data));
        else
            Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SyncVariantAbilityPacket(getId(), data, instance.player.getUUID()));
    }
}
