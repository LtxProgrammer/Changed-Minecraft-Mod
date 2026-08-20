package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.client.animations.ModelPartIdentifier;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ClothingItem extends Item implements Clothing, ExtendedItemProperties {
    public static String INTERACT_INSTRUCTIONS = "changed.instruction.clothing_state";
    public static BooleanProperty CLOSED = BooleanProperty.create("closed");

    public StateDefinition<ClothingItem, ClothingState> stateDefinition;
    public ClothingState defaultClothingState;

    public ClothingItem() {
        this(new Properties().durability(5));
    }

    public ClothingItem(Properties properties) {
        super(properties);
        StateDefinition.Builder<ClothingItem, ClothingState> builder = new StateDefinition.Builder<>(this);
        this.createClothingStateDefinition(builder);
        this.stateDefinition = builder.create(ClothingItem::defaultClothingState, ClothingState::new);
        this.registerDefaultState(this.stateDefinition.any());
        DispenserBlock.registerBehavior(this, AccessoryItem.DISPENSE_ITEM_BEHAVIOR);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, builder, tooltipFlag);
        if (tooltipFlag.isAdvanced())
            builder.add((Component.literal(this.getClothingState(stack).toString())).withStyle(ChatFormatting.DARK_GRAY));
    }

    protected void addInteractInstructions(Consumer<Component> builder) {
        builder.accept(Component.translatable(INTERACT_INSTRUCTIONS, Minecraft.getInstance().options.keyUse.getTranslatedKeyMessage())
                .withStyle(ChatFormatting.GRAY));
    }

    protected void createClothingStateDefinition(StateDefinition.Builder<ClothingItem, ClothingState> builder) {

    }

    public ClothingState defaultClothingState() {
        return this.defaultClothingState;
    }

    @SuppressWarnings("unchecked")
    public ClothingState getClothingState(ItemStack stack) {
        var compoundTag = stack.getTag();
        if (compoundTag == null)
            return this.defaultClothingState();

        var stateData = compoundTag.getCompound("state");
        AtomicReference<ClothingState> evaluatedState = new AtomicReference<>(this.defaultClothingState());
        stateData.getAllKeys().forEach(propertyName -> {
            Property property = this.stateDefinition.getProperty(propertyName);
            if (property == null)
                return;

            property.getValue(stateData.getString(propertyName)).ifPresent(value -> {
                evaluatedState.set(evaluatedState.get().setValue(property, (Comparable)value));
            });
        });
        return evaluatedState.getAcquire();
    }

    @SuppressWarnings("unchecked")
    public void setClothingState(ItemStack stack, ClothingState state) {
        CompoundTag tag = new CompoundTag();
        state.getProperties().forEach(property -> {
            tag.putString(property.getName(), ((Property) property).getName(state.getValue((Property) property)));
        });

        stack.getOrCreateTag().put("state", tag);
    }

    protected final void registerDefaultState(ClothingState clothingState) {
        this.defaultClothingState = clothingState;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    /// Clothing items should override {@link ClothingItem#getClothingTexture}
    @Override
    public final String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        var texture = getClothingTexture(
                stack,
                this.getClothingState(stack),
                entity,
                slot,
                type
        );

        return texture == null ? null : texture.toString();
    }

    @Override
    public final @Nullable ResourceLocation getTexture(ItemStack stack, Entity entity, EquipmentSlot renderSlot, @Nullable String type) {
        return getClothingTexture(stack, this.getClothingState(stack), entity, renderSlot, type);
    }

    @Nullable
    protected ResourceLocation getClothingTexture(ItemStack stack, ClothingState clothingState, Entity wearer, EquipmentSlot renderSlot, @Nullable String type) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return ResourceLocation.fromNamespaceAndPath(
                itemId.getNamespace(),
                type == null ?
                        String.format("textures/models/%s.png", itemId.getPath()) :
                        String.format("textures/models/%s_%s.png", itemId.getPath(), type)
        );
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        var stack = player.getItemInHand(hand);

        return AccessorySlots.getForEntity(player).map(slots -> {
            var copy = stack.copy();
            if (slots.quickMoveStack(stack)) {
                AccessorySlots.equipEventAndSound(player, copy);
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
            }

            return InteractionResultHolder.pass(stack);
        }).orElse(InteractionResultHolder.pass(stack));
    }
}
