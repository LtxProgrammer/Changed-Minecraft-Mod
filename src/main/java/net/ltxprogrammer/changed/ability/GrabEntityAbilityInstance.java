package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.network.packet.GrabEntityPacket;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;
import java.util.UUID;

public class GrabEntityAbilityInstance extends AbstractAbilityInstance {
    @Nullable
    public LivingEntity grabbedEntity = null;

    public LivingEntity getHoveredEntity(IAbstractLatex entity) {
        if (!(entity.getEntity() instanceof Player player))
            return null;

        if (!UniversalDist.isLocalPlayer(player))
            return null;

        var hitResult = UniversalDist.getLocalHitResult();
        if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity)
            return livingEntity;
        return null;
    }

    public GrabEntityAbilityInstance(AbstractAbility<?> ability, IAbstractLatex entity) {
        super(ability, entity);
    }

    @Override
    public boolean canUse() {
        return grabbedEntity == null;
    }

    @Override
    public boolean canKeepUsing() {
        return grabbedEntity == null;
    }

    @Override
    public void startUsing() {

    }

    @Override
    public void tick() {
        var grabbedEntity = this.getHoveredEntity(entity);
        if (grabbedEntity != null && entity.getLevel().isClientSide && entity.getEntity() instanceof PlayerDataExtension ext) {
            if (!this.entity.getEntity().getBoundingBox().inflate(0.5, 0.0, 0.5).intersects(grabbedEntity.getBoundingBox()))
                return;

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
    }
}
