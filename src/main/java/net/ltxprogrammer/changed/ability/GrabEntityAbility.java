package net.ltxprogrammer.changed.ability;

public class GrabEntityAbility extends AbstractAbility<GrabEntityAbilityInstance> {
    public GrabEntityAbility() {
        super(GrabEntityAbilityInstance::new);
    }

    @Override
    public void tickCharge(IAbstractLatex entity, float ticks) {
        /*var enemy = this.getHoveredEntity(entity);
        if (enemy != null) {
            entity.getEntity().xxa = (float)entity.getEntity().getLookAngle().x;
            entity.getEntity().zza = (float)entity.getEntity().getLookAngle().z;
        }*/
    }

    @Override
    public UseType getUseType(IAbstractLatex entity) {
        return UseType.HOLD;
    }
}
