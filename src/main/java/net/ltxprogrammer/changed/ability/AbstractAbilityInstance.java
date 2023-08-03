package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public abstract class AbstractAbilityInstance {
    public final AbstractAbility<?> ability;
    public final IAbstractLatex entity;
    private final AbstractAbility.Controller controller;

    public AbstractAbilityInstance(AbstractAbility<?> ability, IAbstractLatex entity) {
        this.ability = ability;
        this.entity = entity;

        this.controller = new AbstractAbility.Controller(this);
    }

    public abstract boolean canUse();
    public abstract boolean canKeepUsing();

    public abstract void startUsing();
    public abstract void tick();
    public abstract void stopUsing();

    // Called when the player loses the variant (death or untransfur)
    public void onRemove() {}

    // A unique tag for the ability is provided when saving/reading data. If no data is saved to the tag, then readData does not run
    public void saveData(CompoundTag tag) {
        var controllerTag = new CompoundTag();
        controller.saveData(controllerTag);
        tag.put("Controller", controllerTag);
    }
    public void readData(CompoundTag tag) {
        if (tag.contains("Controller"))
            controller.readData(tag.getCompound("Controller"));
    }

    public final ResourceLocation getTexture() {
        return ability.getTexture(entity);
    }
    public final AbstractAbility.UseType getUseType() {
        return ability.getUseType(entity);
    }

    public AbstractAbility.Controller getController() {
        return controller;
    }
}
