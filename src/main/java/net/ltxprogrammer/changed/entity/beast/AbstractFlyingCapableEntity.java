package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.ai.FlyingApproachMeleeAttackGoal;
import net.ltxprogrammer.changed.entity.ai.FlyingOrWalkingMoveControl;
import net.ltxprogrammer.changed.entity.ai.RandomStrollOrHoverGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFlyingCapableEntity extends ChangedEntity implements IFlyingCapable {
    protected final FlyingPathNavigation flyingNavigation;
    protected final GroundPathNavigation groundNavigation;

    public AbstractFlyingCapableEntity(EntityType<? extends ChangedEntity> type, Level level) {
        super(type, level);
        this.flyingNavigation = new FlyingPathNavigation(this, level);
        this.flyingNavigation.setCanOpenDoors(true);
        this.flyingNavigation.setCanFloat(true);
        this.flyingNavigation.setCanPassDoors(true);
        this.groundNavigation = new GroundPathNavigation(this, level);
        this.groundNavigation.setCanOpenDoors(true);
        this.navigation = groundNavigation;
        this.moveControl = new FlyingOrWalkingMoveControl<>(this, 10, true);
    }

    @Override
    protected @Nullable Goal makeMeleeTransfurGoal() {
        return new FlyingApproachMeleeAttackGoal<>(this, 0.4, false, 6, 4);
    }

    @Override
    protected @Nullable Goal makeWanderGoal() {
        return new RandomStrollOrHoverGoal<>(this, 0.3, 120, false,
                /* walkingTicks */ UniformInt.of(120, 1200),
                /* flyingTicks */ UniformInt.of(120, 1200));
    }

    public static AttributeSupplier.Builder createFlyingLatexAttributes() {
        return ChangedEntity.createLatexAttributes().add(Attributes.FLYING_SPEED, 1.5d);
    }

    @Override
    public void setFlying(boolean state) {
        this.setChangedEntityFlag(FLAG_IS_FLYING, state);

        var wantedNav = state ? flyingNavigation : groundNavigation;
        if (!level().isClientSide() && navigation != wantedNav) {
            navigation.stop();
            navigation = wantedNav;
        }
    }

    @Override
    public float getFlyingSpeed() {
        if (this.getUnderlyingPlayer() != null) {
            return super.getFlyingSpeed();
        }

        return isFlying() ? 0.04F : super.getFlyingSpeed();
    }

    @Override
    public boolean canSafelyLandHere() {
        var collisionContext = CollisionContext.of(this);
        var checkBounding = this.getBoundingBox().move(0.0, -1.5, 0.0);
        boolean nearGround = BlockPos.betweenClosedStream(checkBounding).anyMatch(blockPos -> {
            BlockState state = level().getBlockState(blockPos);

            var relativeCheckBounding = checkBounding.move(blockPos.multiply(-1));
            var shape = state.getCollisionShape(level(), blockPos, collisionContext);
            return shape.toAabbs().stream().anyMatch(aabb -> aabb.intersects(relativeCheckBounding));
        });

        return nearGround;
    }

    @Override
    public PathNavigation getNavigationForFlyingState(boolean flying) {
        return flying ? flyingNavigation : groundNavigation;
    }
}
