package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.ai.LookAtPlayerButNotHostGoal;
import net.ltxprogrammer.changed.entity.ai.LookWithPrimaryHead;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public class PureWhiteLatexCerberus extends WhiteLatexEntity implements TripleHeadedEntity {
    public static final float SCALE = 1.2F;

    protected float yLeftHeadRot = 0.0f;
    protected float yLeftHeadRotO = 0.0f;
    protected float xLeftHeadRot = 0.0f;
    protected float xLeftHeadRotO = 0.0f;

    protected double lyLeftHeadRot;
    protected double lxLeftHeadRot;
    protected int lerpLeftHeadSteps;

    protected float yRightHeadRot = 0.0f;
    protected float yRightHeadRotO = 0.0f;
    protected float xRightHeadRot = 0.0f;
    protected float xRightHeadRotO = 0.0f;

    protected double lyRightHeadRot;
    protected double lxRightHeadRot;
    protected int lerpRightHeadSteps;

    public final GoalSelector leftHeadGoals;
    protected LookControl leftHeadLookControl = new NamedLookControl(this, "LeftHeadLookControl");
    public final GoalSelector rightHeadGoals;
    protected LookControl rightHeadLookControl = new NamedLookControl(this, "RightHeadLookControl");

    public PureWhiteLatexCerberus(EntityType<? extends PureWhiteLatexCerberus> p_19870_, Level level) {
        super(p_19870_, level);

        leftHeadGoals = new GoalSelector(level.getProfilerSupplier());

        this.leftHeadGoals.addGoal(5, new LookWithPrimaryHead<>(this, 0.05F));
        this.leftHeadGoals.addGoal(6, new LookAtPlayerButNotHostGoal(this, Player.class, 7.0F));
        this.leftHeadGoals.addGoal(7, new RandomLookAroundGoal(this));
        this.leftHeadGoals.addGoal(8, new LookAtPlayerGoal(this, ChangedEntity.class, 7.0F, 0.2F));
        this.leftHeadGoals.addGoal(8, new LookAtPlayerGoal(this, Villager.class, 7.0F, 0.2F));


        rightHeadGoals = new GoalSelector(level.getProfilerSupplier());

        this.rightHeadGoals.addGoal(5, new LookWithPrimaryHead<>(this, 0.05F));
        this.rightHeadGoals.addGoal(6, new LookAtPlayerButNotHostGoal(this, Player.class, 7.0F));
        this.rightHeadGoals.addGoal(7, new RandomLookAroundGoal(this));
        this.rightHeadGoals.addGoal(8, new LookAtPlayerGoal(this, ChangedEntity.class, 7.0F, 0.2F));
        this.rightHeadGoals.addGoal(8, new LookAtPlayerGoal(this, Villager.class, 7.0F, 0.2F));
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.95);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.9);
    }

    private void swapLeftHeadRot() {
        float saved_yHeadRot = this.yHeadRot;
        float saved_xRot = this.getXRot();
        LookControl saved_lookControl = this.lookControl;

        this.yHeadRot = this.yLeftHeadRot;
        this.setXRot(this.xLeftHeadRot);
        this.lookControl = this.leftHeadLookControl;

        this.yLeftHeadRot = saved_yHeadRot;
        this.xLeftHeadRot = saved_xRot;
        this.leftHeadLookControl = saved_lookControl;
    }

    private void swapRightHeadRot() {
        float saved_yHeadRot = this.yHeadRot;
        float saved_xRot = this.getXRot();
        LookControl saved_lookControl = this.lookControl;

        this.yHeadRot = this.yRightHeadRot;
        this.setXRot(this.xRightHeadRot);
        this.lookControl = this.rightHeadLookControl;

        this.yRightHeadRot = saved_yHeadRot;
        this.xRightHeadRot = saved_xRot;
        this.rightHeadLookControl = saved_lookControl;
    }

    @Override
    public void variantTick(Level level) {
        super.variantTick(level);

        this.yLeftHeadRotO = this.yLeftHeadRot;
        this.xLeftHeadRotO = this.xLeftHeadRot;

        this.yRightHeadRotO = this.yRightHeadRot;
        this.xRightHeadRotO = this.xRightHeadRot;

        if (this.lerpLeftHeadSteps > 0) {
            this.yLeftHeadRot += (float)Mth.wrapDegrees(this.lyLeftHeadRot - (double)this.yLeftHeadRot) / (float)this.lerpLeftHeadSteps;
            this.xLeftHeadRot += (float)Mth.wrapDegrees(this.lxLeftHeadRot - (double)this.xLeftHeadRot) / (float)this.lerpLeftHeadSteps;
            --this.lerpLeftHeadSteps;
        }

        if (this.lerpRightHeadSteps > 0) {
            this.yRightHeadRot += (float)Mth.wrapDegrees(this.lyRightHeadRot - (double)this.yRightHeadRot) / (float)this.lerpRightHeadSteps;
            this.xRightHeadRot += (float)Mth.wrapDegrees(this.lxRightHeadRot - (double)this.xRightHeadRot) / (float)this.lerpRightHeadSteps;
            --this.lerpRightHeadSteps;
        }

        MinecraftServer server = level.getServer();

        if (server != null) {
            this.swapLeftHeadRot(); // (Main, Left, Right) -> (Left, Main, Right)

            int i = server.getTickCount() + this.getId();
            if (i % 2 != 0 && this.tickCount > 1) {
                level.getProfiler().push("leftHeadGoals");
                this.leftHeadGoals.tickRunningGoals(false);
                level.getProfiler().pop();
            } else {
                level.getProfiler().push("leftHeadGoals");
                this.leftHeadGoals.tick();
                level.getProfiler().pop();
            }

            this.getLookControl().tick();

            this.swapRightHeadRot(); // (Left, Main, Right) -> (Right, Main, Left)

            if (i % 2 != 0 && this.tickCount > 1) {
                level.getProfiler().push("rightHeadGoals");
                this.rightHeadGoals.tickRunningGoals(false);
                level.getProfiler().pop();
            } else {
                level.getProfiler().push("rightHeadGoals");
                this.rightHeadGoals.tick();
                level.getProfiler().pop();
            }

            this.getLookControl().tick();

            this.swapRightHeadRot(); // (Right, Main, Left) -> (Left, Main, Right)
            this.swapLeftHeadRot(); // (Left, Main, Right) -> (Main, Left, Right)
        }
    }

    @Override
    public float getLeftHeadYRot(float partialTicks) {
        return Mth.rotLerp(partialTicks, yLeftHeadRotO, yLeftHeadRot);
    }

    @Override
    public float getLeftHeadXRot(float partialTicks) {
        return Mth.rotLerp(partialTicks, xLeftHeadRotO, xLeftHeadRot);
    }

    @Override
    public void setLeftHeadYRot(float value) {
        yLeftHeadRot = value;
    }

    @Override
    public void setLeftHeadXRot(float value) {
        xLeftHeadRot = value;
    }

    @Override
    public Vec3 getLookAngleLeft() {
        return this.calculateViewVector(this.xLeftHeadRot, this.yLeftHeadRot);
    }

    @Override
    public void lerpLeftHeadTo(float yRot, float xRot, int steps) {
        this.lyLeftHeadRot = yRot;
        this.lxLeftHeadRot = xRot;
        this.lerpLeftHeadSteps = steps;
    }

    @Override
    public float getRightHeadYRot(float partialTicks) {
        return Mth.rotLerp(partialTicks, yRightHeadRotO, yRightHeadRot);
    }

    @Override
    public float getRightHeadXRot(float partialTicks) {
        return Mth.rotLerp(partialTicks, xRightHeadRotO, xRightHeadRot);
    }

    @Override
    public void setRightHeadYRot(float value) {
        yRightHeadRot = value;
    }

    @Override
    public void setRightHeadXRot(float value) {
        xRightHeadRot = value;
    }

    @Override
    public Vec3 getLookAngleRight() {
        return this.calculateViewVector(this.xRightHeadRot, this.yRightHeadRot);
    }

    @Override
    public void lerpRightHeadTo(float yRot, float xRot, int steps) {
        this.lyRightHeadRot = yRot;
        this.lxRightHeadRot = xRot;
        this.lerpRightHeadSteps = steps;
    }
}
