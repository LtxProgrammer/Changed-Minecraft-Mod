package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.variant.EntityShape;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LatexGnollTaur extends WhiteLatexKnight implements LatexTaur<LatexGnollTaur> {
    public LatexGnollTaur(EntityType<? extends LatexGnollTaur> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.catLike(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.2);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.9);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(30);
        attributes.getInstance(ForgeMod.STEP_HEIGHT_ADDITION.get()).setBaseValue(computeStepHeightOffset(1.1));
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public TransfurVariant<?> getTransfurVariant() {
        return ChangedTransfurVariants.LATEX_GNOLL_TAUR.get();
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.fromInt(0xffbf75);
    }

    @Override
    public boolean isSaddleable() {
        return false;
    }

    @Override
    public void equipSaddle(@Nullable SoundSource p_21748_) {
        this.equipSaddle(this, p_21748_);
    }

    @Override
    public boolean isSaddled() {
        return this.isSaddled(this);
    }

    protected void doPlayerRide(Player player) {
        this.doPlayerRide(this, player);
    }

    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() + getTorsoYOffset(this) - (2.0 / 16.0);
    }

    public InteractionResult mobInteract(Player p_30713_, InteractionHand p_30714_) {
        if (isSaddled()) {
            this.doPlayerRide(p_30713_);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean isAllowedToSwim() {
        return true;
    }

    @Override
    public @NotNull EntityShape getEntityShape() {
        return EntityShape.TAUR;
    }
}
