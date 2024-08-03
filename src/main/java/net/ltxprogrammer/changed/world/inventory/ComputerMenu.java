package net.ltxprogrammer.changed.world.inventory;

import net.ltxprogrammer.changed.block.TextEnterable;
import net.ltxprogrammer.changed.computers.DiscData;
import net.ltxprogrammer.changed.computers.File;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ComputerMenu extends TextMenu {
    private DiscData data;
    private Path workingDir = Path.of("");
    private ItemStack serverDisk;

    private final Map<Integer, Slot> customSlots = new HashMap<>();

    public ComputerMenu(int id, Inventory inventory, BlockPos pos, BlockState state, TextEnterable textMenuBlockEntity) {
        super(ChangedMenus.COMPUTER, id, inventory, pos, state, textMenuBlockEntity);
    }

    public ComputerMenu(int id, Inventory inventory, ItemStack disk) {
        super(ChangedMenus.COMPUTER, id, inventory, null);
        this.serverDisk = disk;
        this.data = new DiscData(disk.getOrCreateTag());
        inventory.removeItem(serverDisk);
        textCopy = disk.getOrCreateTag().getString("Text");
    }

    public ComputerMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        super(ChangedMenus.COMPUTER, id, inventory, extraData);

        if (extraData != null) {
            serverDisk = null;
            data = null;
            //inventory.removeItem(extraData.readInt(), 1);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public boolean canEditExisting() {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        player.addItem(serverDisk);
    }

    public enum Operation {
        GET_RECIPE
    }

    @Override
    public void update(CompoundTag payload, LogicalSide receiver) {
        if (receiver.isServer() && serverDisk != null && data != null) {
            if (payload.contains("op")) {
                Operation op = Operation.valueOf(payload.getString("op"));

                switch (op) {
                    case GET_RECIPE -> {
                        data.getFileSafe(Path.of(payload.getString("path"))).flatMap(file -> player.level.getRecipeManager().byKey(ResourceLocation.tryParse(file.content))).ifPresent(recipe -> {
                            player.awardRecipes(Collections.singleton(recipe));
                        });
                    }
                }
            }

            /*if (payload.contains("Text")) {
                textCopy = payload.getString("Text");
                textCopyLastReceived = textCopy;
                serverDisk.getOrCreateTag().putString("Text", textCopy);
            }

            this.setDirty(payload);*/
        } else if (receiver.isClient()) {
            /*textCopy = payload.getString("Text");
            textCopyLastReceived = textCopy;
            serverDisk.getOrCreateTag().putString("Text", textCopy);*/

            // Handled by the server
        }
    }

    public CompoundTag requestRecipe(Path fullPath) {
        var tag = new CompoundTag();
        tag.putString("op", Operation.GET_RECIPE.name());
        tag.putString("path", fullPath.toString());
        return tag;
    }

    public DiscData getData() {
        return data;
    }

    public Path getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(Path workingDir) {
        this.workingDir = workingDir;
    }
}
