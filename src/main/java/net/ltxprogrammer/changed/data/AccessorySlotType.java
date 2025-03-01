package net.ltxprogrammer.changed.data;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class AccessorySlotType extends ForgeRegistryEntry<AccessorySlotType> {
    private TagKey<Item> itemTagKey = null;
    private ResourceLocation noItemTexture = null;

    public TagKey<Item> getItemTag() {
        if (itemTagKey != null)
            return itemTagKey;

        itemTagKey = TagKey.create(Registry.ITEM_REGISTRY, this.getRegistryName());
        return itemTagKey;
    }

    public boolean canHoldItem(ItemStack itemStack) {
        return itemStack.is(this.getItemTag());
    }

    public ResourceLocation getNoItemTexture() {
        if (noItemTexture != null)
            return noItemTexture;

        ResourceLocation id = this.getRegistryName();
        noItemTexture = new ResourceLocation(id.getNamespace(), "items/empty_" + id.getPath() + "_slot");
        return noItemTexture;
    }
}
