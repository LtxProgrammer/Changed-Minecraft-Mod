package net.ltxprogrammer.changed.world.inventory;

import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AbilityRadialMenu extends AbstractContainerMenu {
    public static final Component CONTAINER_TITLE = new TranslatableComponent("container.changed.radial_ability");

    public final static HashMap<String, Object> guistate = new HashMap<>();

    public final Container container;
    public final ContainerData data;
    public final Level world;
    public final Player player;
    public final LatexVariantInstance<?> variant;
    public int x, y, z;

    private final Map<Integer, Slot> customSlots = new HashMap<>();

    public AbilityRadialMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, new SimpleContainer(1), new SimpleContainerData(1));
    }

    public AbilityRadialMenu(int id, Inventory inv, Container p_38971_, ContainerData p_38972_) {
        super(ChangedMenus.ABILITY_RADIAL, id);
        this.container = p_38971_;
        this.data = p_38972_;
        this.world = inv.player.level;
        this.player = inv.player;
        this.variant = ProcessTransfur.getPlayerLatexVariant(player);
        this.customSlots.put(0, this.addSlot(new Slot(p_38971_, 0, 9999, 9999) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        }));
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
