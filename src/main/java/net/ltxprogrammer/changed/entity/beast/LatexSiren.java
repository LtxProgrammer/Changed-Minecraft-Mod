package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.SirenSingAbilityInstance;
import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.EntityShape;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;

public class LatexSiren extends AbstractAquaticGenderedEntity {
    protected final SirenSingAbilityInstance sing;

    public LatexSiren(EntityType<? extends LatexSiren> type, Level level) {
        super(type, level);
        sing = registerAbility(ability -> this.wantToSing(), new SirenSingAbilityInstance(ChangedAbilities.SIREN_SING.get(), IAbstractChangedEntity.forEntity(this)));
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

    public boolean wantToSing() {
        return getTarget() != null;
    }

    @Override
    public boolean isVisuallySwimming() {
        if (this.getUnderlyingPlayer() != null && this.getUnderlyingPlayer().isEyeInFluid(FluidTags.WATER))
            return true;
        return super.isVisuallySwimming();
    }

    @Override
    public double getMyRidingOffset() {
        return 0.02;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#969696");
    }

    @Override
    public @NotNull EntityShape getEntityShape() {
        return EntityShape.MER;
    }
}
