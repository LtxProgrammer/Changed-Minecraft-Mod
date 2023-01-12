package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.network.packet.MountLatexPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import static net.ltxprogrammer.changed.world.inventory.CentaurSaddleMenu.SADDLE_LOCATION;

public class LightLatexCentaur extends LightLatexKnight implements Saddleable {
    public LightLatexCentaur(EntityType<? extends LightLatexCentaur> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.WHITE;
    }

    @Override
    public boolean isSaddleable() {
        return false;
    }

    @Override
    public void equipSaddle(@Nullable SoundSource p_21748_) {
        getPersistentData().put(SADDLE_LOCATION, (new ItemStack(Items.SADDLE)).serializeNBT());
        if (p_21748_ != null) {
            this.level.playSound((Player)null, this, SoundEvents.HORSE_SADDLE, p_21748_, 0.5F, 1.0F);
        }

    }

    @Override
    public boolean isSaddled() {
        return getPersistentData().contains(SADDLE_LOCATION);
    }

    protected void doPlayerRide(Player p_30634_) {
        if (!this.level.isClientSide) {
            p_30634_.setYRot(this.getYRot());
            p_30634_.setXRot(this.getXRot());
            Player underlying = getUnderlyingPlayer();
            if (underlying == null)
                p_30634_.startRiding(this);
            else {
                p_30634_.startRiding(underlying);
                Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new MountLatexPacket(p_30634_.getUUID(), underlying.getUUID()));
            }
        }
    }

    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() + 0.8;
    }

    public InteractionResult mobInteract(Player p_30713_, InteractionHand p_30714_) {
        if (isSaddled()) {
            this.doPlayerRide(p_30713_);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }

        return InteractionResult.PASS;
    }
}
