package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DarkLatexPup extends AbstractDarkLatexEntity {
    protected static final int MAX_AGE = 72000;
    protected int age = 0;

    public DarkLatexPup(EntityType<? extends DarkLatexPup> type, Level level) {
        super(type, level);
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.DARK;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.NONE;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.BALD.get();
    }

    @Override
    public @Nullable List<HairStyle> getValidHairStyles() {
        return List.of();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        age = tag.getInt("age");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("age", age);
    }

    @Override
    public float getEyeHeightMul() {
        if (this.isCrouching())
            return 0.55F;
        else
            return 0.8F;
    }

    @Override
    public void visualTick(Level level) {
        super.visualTick(level);

        age++;

        var underlyingPlayer = getUnderlyingPlayer();
        if (ProcessTransfur.ifPlayerLatex(underlyingPlayer, variant -> {
            if (variant.ageAsVariant > MAX_AGE || age > MAX_AGE) {
                var newVariant = LatexVariant.DARK_LATEX_WOLF.randomGender(level.random);
                ProcessTransfur.setPlayerLatexVariant(underlyingPlayer, newVariant);
                ChangedSounds.broadcastSound(this, newVariant.sound, 1.0f, 1.0f);
                underlyingPlayer.heal(12.0f);
            }
        })) return;

        if (age > MAX_AGE) {
            var wolf = LatexVariant.DARK_LATEX_WOLF.randomGender(level.random).getEntityType().create(level);
            if (wolf != null) {
                wolf.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                level.addFreshEntity(wolf);
            }
            this.discard();
        }
    }
}
