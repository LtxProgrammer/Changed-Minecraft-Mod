package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;
import java.util.UUID;

public class GrabEntityAbilityInstance extends AbstractAbilityInstance {
    @Nullable
    public LivingEntity grabbedEntity = null;
    public boolean relinquishControl = false;
    public boolean suited = false;

    public boolean shouldRenderLatex() {
        return !shouldRenderGrabbedEntity();
    }

    public boolean shouldRenderGrabbedEntity() {
        return suited && relinquishControl && grabbedEntity != null;
    }

    public boolean shouldAnimateArms() {
        return grabbedEntity != null && !suited;
    }

    public LivingEntity getHoveredEntity(IAbstractLatex entity) {
        if (!(entity.getEntity() instanceof Player player))
            return null;

        if (!UniversalDist.isLocalPlayer(player))
            return null;

        var hitResult = UniversalDist.getLocalHitResult();
        if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
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
        return grabbedEntity == null ? AbstractAbility.UseType.HOLD : AbstractAbility.UseType.INSTANT;
    }

    @Override
    public void onSelected() {
        this.entity.displayClientMessage(new TextComponent("Hold Z to grab an entity"), true);
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canKeepUsing() {
        return true;
    }

    private void releaseEntity() {
        if (this.grabbedEntity == null) return;

        if (this.grabbedEntity instanceof LivingEntityDataExtension ext)
            ext.setGrabbedBy(null);

        if (this.entity.getEntity() instanceof Player player && player.level.isClientSide)
            Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.release(player, this.grabbedEntity));
        this.grabbedEntity.noPhysics = false;
        this.grabbedEntity = null;
        this.suited = false;
        this.relinquishControl = false;
    }

    @Override
    public void onRemove() {
        releaseEntity();
    }

    public void tickIdle() { // Called every tick of LatexVariantInstance, for variants that have this ability
        if (this.grabbedEntity != null) {
            if (this.grabbedEntity instanceof LivingEntityDataExtension ext)
                ext.setGrabbedBy(this.entity.getEntity());
            this.grabbedEntity.noPhysics = true;

            LatexVariantInstance.syncEntityPosRotWithEntity(this.grabbedEntity, this.entity.getEntity());

            if (this.grabbedEntity.isDeadOrDying() || this.grabbedEntity.isRemoved()) {
                this.releaseEntity();
            }
        }
    }

    @Override
    public void startUsing() {
        if (this.grabbedEntity != null)
            this.releaseEntity();
    }

    @Override
    public void tick() {
        var grabbedEntity = this.getHoveredEntity(entity);
        if (grabbedEntity != null && entity.getLevel().isClientSide && entity.getEntity() instanceof PlayerDataExtension ext) {
            if (!this.entity.getEntity().getBoundingBox().inflate(0.5, 0.0, 0.5).intersects(grabbedEntity.getBoundingBox()))
                return;

            this.entity.getEntity().swing(InteractionHand.MAIN_HAND);
            this.grabbedEntity = grabbedEntity;
            Changed.PACKET_HANDLER.sendToServer(GrabEntityPacket.initialGrab((Player)entity.getEntity(), grabbedEntity));
        }
    }

    @Override
    public void stopUsing() {

    }

    @Override
    public void saveData(CompoundTag tag) {
        super.saveData(tag);
        if (grabbedEntity != null)
            tag.putUUID("GrabbedEntity", grabbedEntity.getUUID());
        tag.putBoolean("RelinquishControl", relinquishControl);
        tag.putBoolean("Suited", suited);
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
        this.relinquishControl = tag.getBoolean("RelinquishControl");
        this.suited = tag.getBoolean("Suited");
    }
}
