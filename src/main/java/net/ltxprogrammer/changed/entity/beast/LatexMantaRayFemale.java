package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.EntityShape;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;

public class LatexMantaRayFemale extends AbstractLatexMantaRay {
    public LatexMantaRayFemale(EntityType<? extends LatexMantaRayFemale> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.34);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(5.58);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(28);
    }

    @Override
    public Gender getGender() {
        return Gender.FEMALE;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    public double getMyRidingOffset() {
        return 0.02;
    }

    @Override
    public boolean isVisuallySwimming() {
        if (this.getUnderlyingPlayer() != null && this.getUnderlyingPlayer().isEyeInFluid(FluidTags.WATER))
            return true;
        return super.isVisuallySwimming();
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#6f7696");
    }

    @Override
    public @NotNull EntityShape getEntityShape() {
        return EntityShape.MER;
    }
}
