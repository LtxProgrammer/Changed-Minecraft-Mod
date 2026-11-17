package net.ltxprogrammer.changed.entity;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.DuctBlock;
import net.ltxprogrammer.changed.entity.latex.LatexSwimMover;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.util.InputWrapper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface PlayerMover<T extends PlayerMoverInstance> {
    default T createInstance() {
        return createInstance(this);
    }

    T createInstance(PlayerMover<?> parent);

    class DefaultMover extends PlayerMoverInstance {
        public DefaultMover(PlayerMover<?> parent) {
            super(parent);
        }

        @Override
        public void aiStep(Player player, InputWrapper input, LogicalSide side) {

        }

        @Override
        public void serverAiStep(Player player, InputWrapper input, LogicalSide side) {

        }

        @Override
        public boolean shouldRemoveMover(Player player, InputWrapper input, LogicalSide side) {
            return true;
        }
    }

    DeferredRegister<PlayerMover<?>> REGISTRY = ChangedRegistry.PLAYER_MOVER.createDeferred(Changed.MODID);

    RegistryObject<PlayerMover<DefaultMover>> DEFAULT_MOVER = REGISTRY.register("default", () -> DefaultMover::new);
    RegistryObject<PlayerMover<DuctBlock.DuctMover>> DUCT_MOVER = REGISTRY.register("duct", () -> DuctBlock.DuctMover::new);
    RegistryObject<PlayerMover<LatexSwimMover>> LATEX_SWIM = REGISTRY.register("latex_swim", () -> LatexSwimMover::new);
}
