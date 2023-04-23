package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.network.packet.MountLatexPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
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

import java.util.List;

public class HeadlessKnight extends LightLatexKnight implements LatexTaur<HeadlessKnight> {
    public HeadlessKnight(EntityType<? extends HeadlessKnight> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.WHITE;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.BALD.get();
    }

    @Override
    public boolean isSaddleable() {
        return false;
    }

    public final static String SADDLE_LOCATION = Changed.modResourceStr("saddle");

    @Override
    public LatexVariant<?> getSelfVariant() {
        return null;
    }

    @Override
    public LatexVariant<?> getTransfurVariant() {
        return LatexVariant.LIGHT_LATEX_CENTAUR;
    }

    @Override
    public void equipSaddle(@Nullable SoundSource p_21748_) {
        equipSaddle(this, p_21748_);
    }

    @Override
    public boolean isSaddled() {
        return isSaddled(this);
    }

    protected void doPlayerRide(Player player) {
        doPlayerRide(this, player);
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

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.EMPTY;
    }
}
