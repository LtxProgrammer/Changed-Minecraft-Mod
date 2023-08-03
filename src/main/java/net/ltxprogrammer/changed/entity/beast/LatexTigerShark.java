package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.ability.IAbstractLatex;
import net.ltxprogrammer.changed.ability.SimpleAbilityInstance;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LatexTigerShark extends AbstractAquaticEntity {
    protected final SimpleAbilityInstance summonSharks;

    public LatexTigerShark(EntityType<? extends LatexTigerShark> type, Level level) {
        super(type, level);
        summonSharks = registerAbility(ability -> this.wantToSummon(), new SimpleAbilityInstance(ChangedAbilities.SUMMON_SHARKS.get(), IAbstractLatex.forLatex(this)));
    }

    public boolean wantToSummon() {
        return getTarget() != null;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.SHORT_MESSY.get();
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.MALE.getStyles();
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.WHITE;
    }
}
