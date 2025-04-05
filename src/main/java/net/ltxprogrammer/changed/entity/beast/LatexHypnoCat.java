package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.SimpleAbilityInstance;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;

public class LatexHypnoCat extends ChangedEntity implements GenderedEntity, PatronOC {
    protected final SimpleAbilityInstance hypnosis;

    public LatexHypnoCat(EntityType<? extends LatexHypnoCat> type, Level level) {
        super(type, level);
        hypnosis = registerAbility(ability -> this.wantToHypno(), new SimpleAbilityInstance(ChangedAbilities.HYPNOSIS.get(), IAbstractChangedEntity.forEntity(this)));
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        AttributePresets.catLike(attributes);
    }

    @Override
    public int getTicksRequiredToFreeze() { return 200; }

    @Override
    public Gender getGender() {
        return Gender.MALE;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public LatexType getLatexType() {
        return LatexType.NEUTRAL;
    }

    public boolean wantToHypno() {
        return getTarget() != null;
    }

    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.getColor("#333333");
    }
}
