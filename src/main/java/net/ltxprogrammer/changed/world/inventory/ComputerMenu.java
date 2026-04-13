package net.ltxprogrammer.changed.world.inventory;

import net.ltxprogrammer.changed.block.entity.ComputerBlockEntity;
import net.ltxprogrammer.changed.computers.application.Application;
import net.ltxprogrammer.changed.computers.application.ApplicationType;
import net.ltxprogrammer.changed.init.ChangedApplications;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedMenus;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class ComputerMenu extends AbstractContainerMenu implements UpdateableMenu {
    protected final Player player;
    public ComputerBlockEntity computer;

    protected final Stack<Application> applications = new Stack<>();

    public ComputerMenu(int id, Inventory inventory, ComputerBlockEntity computer) {
        super(ChangedMenus.COMPUTER.get(), id);
        this.computer = computer;
        this.player = inventory.player;

        applications.push(ChangedApplications.DESKTOP.get().createApplication(List.of()));
    }

    public ComputerMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        super(ChangedMenus.COMPUTER.get(), id);
        this.player = inventory.player;

        if (extraData != null) {
            computer = player.level().getBlockEntity(extraData.readBlockPos(), ChangedBlockEntities.COMPUTER.get()).orElse(null);
        }

        applications.push(ChangedApplications.DESKTOP.get().createApplication(List.of()));
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return null;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
    }

    public enum Operation {
        GET_RECIPE
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
        if (receiver.isServer() && origin != null) {
            if (!payload.contains("op"))
                return;

            Operation requestedOp = Operation.valueOf(payload.getString("op"));
            switch (requestedOp) {
                case GET_RECIPE -> {
                    Path fullPath = Path.of(payload.getString("path"));
                    computer.getFileSafe(fullPath).flatMap(file -> {
                        var recipeLoc = ResourceLocation.parse(file.content);
                        return origin.serverLevel().getRecipeManager().byKey(recipeLoc);
                    }).ifPresent(recipe -> {
                        origin.awardRecipes(Collections.singleton(recipe));
                    });
                }
            }
        }
    }

    public CompoundTag requestRecipe(Path fullPath) {
        var tag = new CompoundTag();
        tag.putString("op", Operation.GET_RECIPE.name());
        tag.putString("path", fullPath.toString());
        return tag;
    }

    public Path getWorkingDir() {
        return computer.currentWorkingDirectory;
    }

    public void setWorkingDir(Path workingDir) {
        computer.currentWorkingDirectory = workingDir;
    }

    public Path getHomeDir() {
        return computer.homeDirectory;
    }

    public Path getDesktopDir() {
        return computer.homeDirectory.resolve(Path.of("Desktop/"));
    }

    public Application currentApplication() {
        return applications.peek();
    }

    /// INTERNAL
    public Application launchApplication(ApplicationType<?> applicationType, List<String> args) {
        var app = applicationType.createApplication(args);
        applications.push(app);

        return app;
    }

    /// INTERNAL
    public void closeApplication(ApplicationType<?> applicationType) {
        if (applications.peek().getType() != applicationType)
            throw new IllegalArgumentException("Application type mismatch");
        applications.pop();
    }
}
