package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.SimpleAbilityInstance;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class LatexTigerShark extends AbstractAquaticEntity {
    protected final SimpleAbilityInstance summonSharks;

    public LatexTigerShark(EntityType<? extends LatexTigerShark> type, Level level) {
        super(type, level);
        summonSharks = registerAbility(ability -> this.wantToSummon(), new SimpleAbilityInstance(ChangedAbilities.SUMMON_SHARKS.get(), IAbstractChangedEntity.forEntity(this)));
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.925);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.24);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(28.0);
    }

    public boolean wantToSummon() {
        return getTarget() != null;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#969696");
    }
}
