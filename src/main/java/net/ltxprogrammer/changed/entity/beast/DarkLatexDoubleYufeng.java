package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.ai.LookAtPlayerButNotHostGoal;
import net.ltxprogrammer.changed.entity.ai.LookWithPrimaryHead;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
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

public class DarkLatexDoubleYufeng extends DarkLatexYufeng implements DoubleHeadedEntity {
    protected float yHead2Rot = 0.0f;
    protected float yHead2RotO = 0.0f;
    protected float xHead2Rot = 0.0f;
    protected float xHead2RotO = 0.0f;

    protected double lyHead2Rot;
    protected double lxHead2Rot;
    protected int lerpHead2Steps;

    public final GoalSelector head2Goals;
    protected LookControl head2LookControl = new LookControl(this) {
        @Override
        protected boolean resetXRotOnTick() {
            return false;
        }
    };

    public DarkLatexDoubleYufeng(EntityType<? extends DarkLatexDoubleYufeng> p_19870_, Level level) {
        super(p_19870_, level);
        head2Goals = new GoalSelector(level.getProfilerSupplier());

        this.head2Goals.addGoal(5, new LookWithPrimaryHead<>(this, 0.3F));
        this.head2Goals.addGoal(6, new LookAtPlayerButNotHostGoal(this, Player.class, 7.0F));
        this.head2Goals.addGoal(7, new RandomLookAroundGoal(this));
        this.head2Goals.addGoal(8, new LookAtPlayerGoal(this, ChangedEntity.class, 7.0F, 0.2F));
        this.head2Goals.addGoal(8, new LookAtPlayerGoal(this, Villager.class, 7.0F, 0.2F));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("yHead2Rot", yHead2Rot);
        tag.putFloat("yHead2RotO", yHead2RotO);
        tag.putFloat("xHead2Rot", xHead2Rot);
        tag.putFloat("xHead2RotO", xHead2RotO);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        yHead2Rot = tag.getFloat("yHead2Rot");
        yHead2RotO = tag.getFloat("yHead2RotO");
        xHead2Rot = tag.getFloat("xHead2Rot");
        xHead2RotO = tag.getFloat("xHead2RotO");
    }

    // Built in vanilla look goals interface with yHeadRot, xRot, and lookControl
    private void swapHeadRot() {
        float saved_yHeadRot = this.yHeadRot;
        float saved_xRot = this.getXRot();
        LookControl saved_lookControl = this.lookControl;

        this.yHeadRot = this.yHead2Rot;
        this.setXRot(this.xHead2Rot);
        this.lookControl = this.head2LookControl;

        this.yHead2Rot = saved_yHeadRot;
        this.xHead2Rot = saved_xRot;
        this.head2LookControl = saved_lookControl;
    }

    @Override
    public void variantTick(Level level) {
        super.variantTick(level);

        this.yHead2RotO = this.yHead2Rot;
        this.xHead2RotO = this.xHead2Rot;

        if (this.lerpHead2Steps > 0) {
            this.yHead2Rot += (float)Mth.wrapDegrees(this.lyHead2Rot - (double)this.yHead2Rot) / (float)this.lerpHead2Steps;
            this.xHead2Rot += (float)Mth.wrapDegrees(this.lxHead2Rot - (double)this.xHead2Rot) / (float)this.lerpHead2Steps;
            --this.lerpHead2Steps;
        }

        MinecraftServer server = level.getServer();

        if (server != null) {

            this.swapHeadRot();

            int i = server.getTickCount() + this.getId();
            if (i % 2 != 0 && this.tickCount > 1) {
                level.getProfiler().push("head2Goals");
                this.head2Goals.tickRunningGoals(false);
                level.getProfiler().pop();
            } else {
                level.getProfiler().push("head2Goals");
                this.head2Goals.tick();
                level.getProfiler().pop();
            }

            this.getLookControl().tick();

            this.swapHeadRot();
        }
    }

    @Override
    public float getHead2XRot(float partialTicks) {
        return Mth.rotLerp(partialTicks, xHead2RotO, xHead2Rot);
    }

    @Override
    public float getHead2YRot(float partialTicks) {
        return Mth.rotLerp(partialTicks, yHead2RotO, yHead2Rot);
    }

    @Override
    public void setHead2XRot(float value) {
        this.xHead2Rot = value;
    }

    @Override
    public void setHead2YRot(float value) {
        this.yHead2Rot = value;
    }

    public Vec3 getLookAngle2() {
        return this.calculateViewVector(this.xHead2Rot, this.yHead2Rot);
    }

    @Override
    public void lerpHead2To(float yRot, float xRot, int steps) {
        this.lyHead2Rot = yRot;
        this.lxHead2Rot = xRot;
        this.lerpHead2Steps = steps;
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.0);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.85);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(28.0);
    }

    @Override
    public TransfurVariant<?> getTransfurVariant() {
        return ChangedTransfurVariants.DARK_LATEX_YUFENG.get();
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }
}
