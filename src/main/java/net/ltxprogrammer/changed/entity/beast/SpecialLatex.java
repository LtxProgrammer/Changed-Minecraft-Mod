package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class SpecialLatex extends LatexEntity {
    private UUID assignedUUID = null;
    public PatreonBenefits.SpecialLatexForm specialLatexForm = null;

    public SpecialLatex(EntityType<? extends LatexEntity> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public void setSpecialLatexForm(UUID uuid) {
        this.assignedUUID = uuid;
        this.specialLatexForm = PatreonBenefits.getPlayerSpecialForm(uuid);
    }

    @Override
    public ChangedParticles.Color3 getHairColor(int layer) {
        return ChangedParticles.Color3.WHITE;
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collections.NONE;
    }

    public UUID getAssignedUUID() {
        return assignedUUID;
    }

    @Override
    public LatexType getLatexType() {
        if (specialLatexForm == null)
            return LatexType.NEUTRAL;
        return specialLatexForm.variant().getLatexType();
    }

    @Override
    public TransfurMode getTransfurMode() {
        if (specialLatexForm == null)
            return TransfurMode.REPLICATION;
        return specialLatexForm.variant().transfurMode();
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        if (specialLatexForm == null)
            return null;
        return specialLatexForm.weightedColors().isEmpty() ? null : specialLatexForm.weightedColors().get(level.random.nextInt(specialLatexForm.weightedColors().size()));
    }

    @Override
    public LatexVariant<?> getTransfurVariant() {
        return specialLatexForm.variant();
    }

    public static void init() {}
}
