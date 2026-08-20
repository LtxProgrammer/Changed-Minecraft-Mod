package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.client.animations.LimbExtensions;
import net.ltxprogrammer.changed.client.animations.ModelPartIdentifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class ShortsItem extends ClothingItem implements Shorts {
    public ShortsItem() {
        super();
    }

    public ShortsItem(Properties properties) {
        super(properties);
    }

    @Override
    public void hideModelParts(ItemStack stack, Entity entity, EquipmentSlot renderSlot, Consumer<ModelPartIdentifier> partsToHide) {
        super.hideModelParts(stack, entity, renderSlot, partsToHide);
        partsToHide.accept(ModelPartIdentifier.forExtension(LimbExtensions.TAIL));
        partsToHide.accept(ModelPartIdentifier.forExtension(LimbExtensions.LEFT_LEG_PARTS, "lower"));
        partsToHide.accept(ModelPartIdentifier.forExtension(LimbExtensions.RIGHT_LEG_PARTS, "lower"));
    }
}
