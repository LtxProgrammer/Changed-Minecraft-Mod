package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;
import java.util.UUID;

public class GrabEntityAbilityInstance extends AbstractAbilityInstance {
    @Nullable
    public LivingEntity grabbedEntity = null;
    public boolean grabbedHasControl = false;
    public boolean suited = false;
    public float grabStrength = 0.0f;
    public float grabStrengthO = 0.0f;
    public float suitTransition = 0.0f;
    public float suitTransitionO = 0.0f;

    public boolean shouldRenderLatex() {
        return !shouldRenderGrabbedEntity();
    }

    public boolean shouldRenderGrabbedEntity() {
        return suited && grabbedHasControl && grabbedEntity != null;
    }

    public boolean shouldAnimateArms() {
        return grabbedEntity != null && !suited;
    }

    public float getGrabStrength(float partialTicks) {
        return Mth.lerp(partialTicks, grabStrengthO, grabStrength);
    }

    public float getSuitTransitionProgress(float partialTicks) {
        return Mth.lerp(partialTicks, suitTransitionO, suitTransition) / 3.0f;
    }

    public LivingEntity getHoveredEntity(IAbstractLatex entity) {
        if (!(entity.getEntity() instanceof Player player))
            return null;

        if (!UniversalDist.isLocalPlayer(player))
            return null;

        var hitResult = UniversalDist.getLocalHitResult();
        if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof Player targetPlayer && ProcessTransfur.isPlayerLatex(targetPlayer))
                return null;
            if (livingEntity.getType().is(ChangedTags.EntityTypes.HUMANOIDS) || livingEntity instanceof Player)
                return livingEntity;
        }
        return null;
    }

    public GrabEntityAbilityInstance(AbstractAbility<?> ability, IAbstractLatex entity) {
        super(ability, entity);
    }

    @Override
    public AbstractAbility.UseType getUseType() {
        return AbstractAbility.UseType.HOLD;
    }

    @Override
    public void onSelected() {
        if (entity.getLevel().isClientSide)
            this.entity.displayClientMessage(new TranslatableComponent("ability.changed.grab_entity.how_to_grab", KeyReference.ABILITY.getName(entity.getLevel())), true);
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canKeepUsing() {
        return true;
    }

    public void releaseEntity() {
        this.grabbedHasControl = false;
        this.grabStrength = 0.0f;
        this.suitTransition = 0.0f;
        if (this.grabbedEntity == null) return;

        if (this.grabbedEntity instanceof LivingEntityDataExtension ext)
            ext.setGrabbedBy(null);

        if (this.entity.getEntity() instanceof Player player && player.level.isClientSide)
            Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.release(player, this.grabbedEntity));
        this.grabbedEntity.setInvisible(false);
        this.grabbedEntity.noPhysics = false;
        this.grabbedEntity = null;
        this.suited = false;
        this.attackDown = false;
        this.useDown = false;
    }

    public void suitEntity(LivingEntity entity) {
        getController().resetHoldTicks();
        entity.setInvisible(true);
        if (this.grabbedEntity == entity) {
            this.suited = true;
            this.grabStrength = 1.0f;
            return;
        }

        this.releaseEntity();
        this.grabbedEntity = entity;
        this.suited = true;
        this.grabStrength = 1.0f;
    }

    public void grabEntity(LivingEntity entity) {
        getController().resetHoldTicks();
        if (this.grabbedEntity == entity) {
            this.suited = false;
            this.grabbedHasControl = false;
            this.grabStrength = 1.0f;
            return;
        }

        this.releaseEntity();
        this.grabbedEntity = entity;
        this.grabStrength = 1.0f;
    }

    @Override
    public void onRemove() {
        releaseEntity();
    }

    void handleInstructions(Level level) {
        if (!level.isClientSide) return;

        if (instructionTicks == 180)
            this.entity.displayClientMessage(new TranslatableComponent("ability.changed.grab_entity.how_to_release", KeyReference.ABILITY.getName(level)), true);
        else if (instructionTicks == 120)
            this.entity.displayClientMessage(new TranslatableComponent("ability.changed.grab_entity.how_to_transfur", KeyReference.ATTACK.getName(level)), true);
        else if (instructionTicks == 60)
            this.entity.displayClientMessage(new TranslatableComponent("ability.changed.grab_entity.how_to_suit", KeyReference.USE.getName(level)), true);
        if (instructionTicks > 0)
            instructionTicks--;

        if (instructionTicks == -120)
            this.entity.displayClientMessage(new TranslatableComponent("ability.changed.grab_entity.how_to_release", KeyReference.ABILITY.getName(level)), true);
        else if (instructionTicks == -60)
            this.entity.displayClientMessage(new TranslatableComponent("ability.changed.grab_entity.how_to_toggle_control", KeyReference.ABILITY.getName(level)), true);
        if (instructionTicks < 0)
            instructionTicks++;
    }

    int instructionTicks = 0;
    public boolean attackDown = false;
    public boolean useDown = false;
    public void tickIdle() { // Called every tick of LatexVariantInstance, for variants that have this ability
        this.grabStrengthO = this.grabStrength;
        this.suitTransitionO = this.suitTransition;

        if (this.grabbedEntity != null) {
            Level level = entity.getLevel();

            handleInstructions(level);

            if (this.grabbedEntity instanceof LivingEntityDataExtension ext)
                ext.setGrabbedBy(this.entity.getEntity());
            this.grabbedEntity.noPhysics = !this.grabbedHasControl;

            if (!grabbedHasControl)
                LatexVariantInstance.syncEntityPosRotWithEntity(this.grabbedEntity, this.entity.getEntity());
            else
                LatexVariantInstance.syncEntityPosRotWithEntity(this.entity.getEntity(), this.grabbedEntity);

            if (this.entity.getEntity() instanceof Player player && (player.isDeadOrDying() || player.isRemoved() || player.isSpectator())) {
                this.releaseEntity();
                return;
            }

            if (this.grabbedEntity instanceof Player player && (player.isSpectator() || ProcessTransfur.isPlayerLatex(player))) {
                this.releaseEntity();
                return;
            }

            if (this.grabbedEntity.isDeadOrDying() || this.grabbedEntity.isRemoved() || this.grabStrength <= 0.0f) {
                this.releaseEntity();
                return;
            }

            if (UniversalDist.getLocalPlayer() == this.entity.getEntity()) {
                boolean attackKeyDown = KeyReference.ATTACK.isDown(level);
                boolean useKeyDown = KeyReference.USE.isDown(level);
                if (attackKeyDown != attackDown || useKeyDown != useDown)
                    Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.keyState(attackKeyDown, useKeyDown));
                attackDown = attackKeyDown;
                useDown = useKeyDown;
            }

            if (attackDown && !suited) { // TODO progress absorption while suited
                if (ProcessTransfur.progressTransfur(this.grabbedEntity, 4.0f, entity.getLatexEntity().getTransfurVariant()))
                    this.releaseEntity();
            }

            else if (useDown) {
                this.suitTransition += 0.075f;
            }

            if (this.suitTransition > 0.0f) {
                this.suitTransition = Math.max(0.0f, this.suitTransition - 0.025f);

                if (this.suitTransition > 3.0f) { // 3 seconds
                    this.suited = true;
                    this.suitTransition = 0.0f;

                    if (this.entity.getEntity() instanceof Player player && player.level.isClientSide) {
                        Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.suitGrab(player, this.grabbedEntity));
                        this.instructionTicks = -120;
                        this.grabStrength = 1.0f;
                    }
                }
            }
        }
    }

    @Override
    public void startUsing() {

    }

    @Override
    public void tick() {
        if (this.grabbedEntity != null) {
            if (this.getController().getHoldTicks() >= 40)
                this.releaseEntity();
            return;
        }

        var grabbedEntity = this.getHoveredEntity(entity);
        if (grabbedEntity != null && entity.getLevel().isClientSide && entity.getEntity() instanceof PlayerDataExtension ext) {
            if (!this.entity.getEntity().getBoundingBox().inflate(0.5, 0.0, 0.5).intersects(grabbedEntity.getBoundingBox()))
                return;

            this.grabbedEntity = grabbedEntity;
            Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.initialGrab((Player)entity.getEntity(), grabbedEntity));
            this.grabStrength = 1.0f;
            this.instructionTicks = 180;
            getController().resetHoldTicks();
        }
    }

    @Override
    public void stopUsing() {
        if (this.grabbedEntity != null && suited && this.getController().getHoldTicks() < 40) {
            this.grabbedHasControl = !this.grabbedHasControl;
            this.grabbedEntity.noPhysics = !this.grabbedHasControl;
        }
    }

    @Override
    public void saveData(CompoundTag tag) {
        super.saveData(tag);
        if (grabbedEntity != null)
            tag.putUUID("GrabbedEntity", grabbedEntity.getUUID());
        tag.putBoolean("GrabbedHasControl", grabbedHasControl);
        tag.putBoolean("Suited", suited);
        tag.putFloat("GrabStrength", grabStrength);
        tag.putFloat("SuitTransition", suitTransition);
    }

    @Override
    public void readData(CompoundTag tag) {
        super.readData(tag);
        if (tag.contains("GrabbedEntity")) {
            final UUID entityUUID = tag.getUUID("GrabbedEntity");
            this.entity.getLevel().getEntities(this.entity.getEntity(), this.entity.getEntity().getBoundingBox().inflate(1.0)).forEach(foundEntity -> {
                if (foundEntity instanceof LivingEntity livingEntity && livingEntity.getUUID().equals(entityUUID)) {
                    this.grabbedEntity = livingEntity;
                }
            });
        }
        this.grabbedHasControl = tag.getBoolean("GrabbedHasControl");
        this.suited = tag.getBoolean("Suited");
        this.grabStrength = tag.getFloat("GrabStrength");
        this.suitTransition = tag.getFloat("SuitTransition");
    }
}
