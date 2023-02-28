package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.entity.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class ChangedBlockEntities {
    @SafeVarargs
    private static <T extends BlockEntity> Supplier<BlockEntityType<T>> deferredProvider(BlockEntityType.BlockEntitySupplier<T> entitySupplier, Supplier<? extends Block>... block) {
        return () -> new BlockEntityType<>(entitySupplier, Arrays.stream(block).map(Supplier::get).collect(Collectors.toSet()), null);
    }

    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Changed.MODID);
    public static final RegistryObject<BlockEntityType<PurifierBlockEntity>> PURIFIER = REGISTRY.register("purifier", deferredProvider(PurifierBlockEntity::new, ChangedBlocks.PURIFIER));
    public static final RegistryObject<BlockEntityType<BedsideIVRackBlockEntity>> BEDSIDE_IV_RACK = REGISTRY.register("bedside_iv_rack", deferredProvider(BedsideIVRackBlockEntity::new, ChangedBlocks.BEDSIDE_IV_RACK));
    public static final RegistryObject<BlockEntityType<CardboardBoxBlockEntity>> CARDBOARD_BOX = REGISTRY.register("cardboard_box", deferredProvider(CardboardBoxBlockEntity::new, ChangedBlocks.CARDBOARD_BOX));
    public static final RegistryObject<BlockEntityType<KeypadBlockEntity>> KEYPAD = REGISTRY.register("keypad", deferredProvider(KeypadBlockEntity::new, ChangedBlocks.KEYPAD));
    public static final RegistryObject<BlockEntityType<LatexContainerBlockEntity>> LATEX_CONTAINER = REGISTRY.register("latex_container", deferredProvider(LatexContainerBlockEntity::new, ChangedBlocks.LATEX_CONTAINER));
    public static final RegistryObject<BlockEntityType<TextBlockEntity>> TEXT_BLOCK_ENTITY = REGISTRY.register("text_block_entity", deferredProvider(TextBlockEntity::new, ChangedBlocks.NOTE, ChangedBlocks.CLIPBOARD));
}
