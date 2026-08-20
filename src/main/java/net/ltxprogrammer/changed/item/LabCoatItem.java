package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.client.animations.LimbExtensions;
import net.ltxprogrammer.changed.client.animations.ModelPartIdentifier;
import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class LabCoatItem extends ClothingItem {
    public LabCoatItem() {
        this.registerDefaultState(this.stateDefinition.any().setValue(CLOSED, false));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> builder, TooltipFlag tooltipFlag) {
        if (level != null && level.isClientSide) {
            this.addInteractInstructions(builder::add);
        }

        super.appendHoverText(stack, level, builder, tooltipFlag);
    }

    @Override
    protected void createClothingStateDefinition(StateDefinition.Builder<ClothingItem, ClothingState> builder) {
        super.createClothingStateDefinition(builder);
        builder.add(CLOSED);
    }

    @Override
    public void accessoryInteract(AccessorySlotContext<?> slotContext) {
        super.accessoryInteract(slotContext);
        this.setClothingState(slotContext.stack(), this.getClothingState(slotContext.stack()).cycle(CLOSED));
        SoundEvent changeSound = this.getEquipSound(slotContext.stack());
        if (changeSound != null)
            slotContext.wearer().playSound(changeSound, 1F, 1F);
    }

    @Override
    protected @Nullable ResourceLocation getClothingTexture(ItemStack stack, ClothingState clothingState, Entity wearer, EquipmentSlot renderSlot, @Nullable String type) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (clothingState.getValue(CLOSED))
            return ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(),
                    String.format("textures/models/%s_closed.png", itemId.getPath()));
        else
            return ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(),
                    String.format("textures/models/%s.png", itemId.getPath()));
    }

    @Override
    public void hideModelParts(ItemStack stack, Entity entity, EquipmentSlot renderSlot, Consumer<ModelPartIdentifier> partsToHide) {
        super.hideModelParts(stack, entity, renderSlot, partsToHide);
        partsToHide.accept(ModelPartIdentifier.forExtension(LimbExtensions.TAIL));
        partsToHide.accept(ModelPartIdentifier.forExtension(LimbExtensions.LEFT_LEG_PARTS, "foot"));
        partsToHide.accept(ModelPartIdentifier.forExtension(LimbExtensions.RIGHT_LEG_PARTS, "foot"));
    }

    @Override
    public SoundEvent getEquipSound(ItemStack itemStack) {
        return ChangedSounds.COAT_EQUIP.get();
    }

    @Override
    public SoundEvent getBreakSound(ItemStack itemStack) {
        return ChangedSounds.COAT_BREAK.get();
    }
}
