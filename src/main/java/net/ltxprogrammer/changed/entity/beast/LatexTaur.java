package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.network.packet.MountTransfurPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public interface LatexTaur<T extends ChangedEntity> extends Saddleable {
    @Override default boolean isSaddleable() { return false; }

    String SADDLE_LOCATION = Changed.modResourceStr("saddle");

    default void equipSaddle(T self, @Nullable SoundSource p_21748_) {
        self.getPersistentData().put(SADDLE_LOCATION, (new ItemStack(Items.SADDLE)).serializeNBT());
        if (p_21748_ != null) {
            self.level.playSound(null, self, SoundEvents.HORSE_SADDLE, p_21748_, 0.5F, 1.0F);
        }

    }

    default boolean isSaddled(T self) {
        return ProcessTransfur.ifPlayerTransfurred(self.getUnderlyingPlayer(), variant -> {
            var ability = variant.getAbilityInstance(ChangedAbilities.ACCESS_SADDLE.get());
            if (ability != null)
                return ability.saddle != null && !ability.saddle.isEmpty();
            else
                return false;
        }, () -> self.getPersistentData().contains(SADDLE_LOCATION));
    }

    default void doPlayerRide(T self, Player player) {
        if (!self.level.isClientSide) {
            player.setYRot(self.getYRot());
            player.setXRot(self.getXRot());
            Player underlying = self.getUnderlyingPlayer();
            if (underlying == null)
                player.startRiding(self);
            else {
                player.startRiding(underlying);
                Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new MountTransfurPacket(player.getUUID(), underlying.getUUID()));
            }
        }
    }

    default double getTorsoYOffset(T self) {
        final float ageAdjusted = (self.tickCount) * 0.33333334F * 0.25F * 0.15f;
        float ageSin = Mth.sin(ageAdjusted * Mth.PI * 0.5f);
        float ageCos = Mth.cos(ageAdjusted * Mth.PI * 0.5f);
        float bpiSize = (self.getBasicPlayerInfo().getSize() - 1.0f) * 2.0f;
        return Mth.lerp(Mth.lerp(1.0f - Mth.abs(Mth.positiveModulo(ageAdjusted, 2.0f) - 1.0f),
                ageSin * ageSin * ageSin * ageSin, 1.0f - (ageCos * ageCos * ageCos * ageCos)),
                0.95f, 0.87f) + bpiSize;
    }
}
