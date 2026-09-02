package net.ltxprogrammer.changed.world.inventory;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.ai.TamedEntityFavor;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.ltxprogrammer.changed.init.ChangedTamedEntityFavors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class TamedEntityMenu extends AbstractContainerMenu implements UpdateableMenu {
    public ChangedEntity tamedEntity;
    public final Player player;

    public TamedEntityMenu(int id, Inventory inventory, ChangedEntity tamedEntity) {
        super(ChangedMenus.TAMED_ENTITY.get(), id);
        this.tamedEntity = tamedEntity;
        this.player = inventory.player;
    }

    public TamedEntityMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(ChangedMenus.TAMED_ENTITY.get(), id);
        this.player = inv.player;

        if (extraData == null)
            return;

        this.tamedEntity = (ChangedEntity) inv.player.level().getEntity(extraData.readInt());
    }

    @Override
    public ItemStack quickMoveStack(Player viewer, int slotIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player viewer) {
        if (this.tamedEntity.isRemoved()) {
            return false;
        } else if (this.tamedEntity.getOwner() != viewer) {
            return false;
        } else {
            return !(viewer.distanceToSqr(this.tamedEntity) > 64.0D);
        }
    }

    @Override
    public int getId() {
        return containerId;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void update(CompoundTag payload, LogicalSide receiver, @Nullable ServerPlayer origin) {
        if (receiver == LogicalSide.SERVER && origin == this.tamedEntity.getOwner()) {
            switch (payload.getString("command")) {
                case "view_inventory" -> {
                    NetworkHooks.openScreen((ServerPlayer) this.player, new SimpleMenuProvider(
                            (id, inv, viewer) -> new TamedEntityInventoryMenu(id, this.player, this.tamedEntity),
                            this.tamedEntity.getDisplayName()
                    ), extraData -> {
                        extraData.writeInt(this.tamedEntity.getId());
                    });
                }
                case "cycle_follow" -> {
                    this.tamedEntity.setFollowOwner(!this.tamedEntity.isFollowingOwner());
                    this.tamedEntity.setJumping(false);
                    this.tamedEntity.getNavigation().stop();
                }
                case "cycle_target_type" -> {
                    this.tamedEntity.setTargetType(this.tamedEntity.getTargetType().cycle());
                    this.tamedEntity.setTarget(null);
                }
                case "cycle_attack_type" -> {
                    this.tamedEntity.setAttackType(this.tamedEntity.getAttackType().cycle());
                }
                case "cycle_attack_condition" -> {
                    this.tamedEntity.setAttackCondition(this.tamedEntity.getAttackCondition().cycle());
                    this.tamedEntity.setTarget(null);
                }
                case "favor_fishing" -> {
                    this.tamedEntity.setFavor(this.tamedEntity.getCurrentFavor() != ChangedTamedEntityFavors.FISHING.get() ?
                            ChangedTamedEntityFavors.FISHING.get() : ChangedTamedEntityFavors.NONE.get());
                }
                case "favor_caving" -> {
                    this.tamedEntity.setFavor(this.tamedEntity.getCurrentFavor() != ChangedTamedEntityFavors.CAVING.get() ?
                            ChangedTamedEntityFavors.CAVING.get() : ChangedTamedEntityFavors.NONE.get());
                }
                case "favor_suit_owner" -> {
                    this.tamedEntity.setFavor(this.tamedEntity.getCurrentFavor() != ChangedTamedEntityFavors.SUIT_OWNER.get() ?
                            ChangedTamedEntityFavors.SUIT_OWNER.get() : ChangedTamedEntityFavors.NONE.get());
                }
            }
        }
    }
}
