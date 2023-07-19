package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractAbilityInstance {
    public final AbstractAbility<?> ability;
    public final Player player;
    public final LatexVariantInstance<?> variant;
    private final AbstractAbility.Controller controller;

    public AbstractAbilityInstance(AbstractAbility<?> ability, Player player, LatexVariantInstance<?> variant) {
        this.ability = ability;
        this.player = player;
        this.variant = variant;

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
    public void saveData(CompoundTag tag) {}
    public void readData(CompoundTag tag) {}

    public final ResourceLocation getTexture() {
        return ability.getTexture(player, variant);
    }

    public AbstractAbility.Controller getController() {
        return controller;
    }
}
