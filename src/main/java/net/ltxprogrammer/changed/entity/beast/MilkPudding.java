package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.ai.LatexAssimilationDecision;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class MilkPudding extends ChangedEntity {
    public MilkPudding(EntityType<? extends MilkPudding> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public static void init() {
        SpawnPlacements.register(ChangedEntities.MILK_PUDDING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL
                        && Monster.isDarkEnoughToSpawn(world, pos, random) && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)));
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(8.0);
        attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(12.0);
        attributes.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.9);
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.ABSORPTION;
    }

    @Override
    protected TransfurVariant<?> getTransfurVariant(LatexAssimilationDecision.Method method) {
        return ChangedTransfurVariants.Gendered.WHITE_LATEX_WOLVES.getRandomVariant(random);
    }

    @Override
    public LatexAssimilationDecision<?> makeLatexAssimilationDecision(TransfurCause cause, LivingEntity targetEntity) {
        IAbstractChangedEntity self = IAbstractChangedEntity.forEntity(this);

        return LatexAssimilationDecision.weak(LatexAssimilationDecision.Method.ABSORPTION,
                ChangedTransfurVariants.Gendered.WHITE_LATEX_WOLVES.getRandomVariant(random),
                self.absorb(),
                this.computeTransfurDamage());
    }

    @Override
    public TransfurVariant<?> getSelfVariant() {
        return null;
    }
}
