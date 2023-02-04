package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.entity.BedsideIVRackBlockEntity;
import net.ltxprogrammer.changed.block.entity.CardboardBoxBlockEntity;
import net.ltxprogrammer.changed.block.entity.PurifierBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class ChangedBlockEntities {
    private static <T extends BlockEntity> Supplier<BlockEntityType<T>> deferredProvider(BlockEntityType.BlockEntitySupplier<T> entitySupplier, RegistryObject<? extends Block> block) {
        return () -> BlockEntityType.Builder.of(entitySupplier, block.get()).build(null);
    }

    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Changed.MODID);
    public static final RegistryObject<BlockEntityType<PurifierBlockEntity>> PURIFIER = REGISTRY.register("purifier", deferredProvider(PurifierBlockEntity::new, ChangedBlocks.PURIFIER));
    public static final RegistryObject<BlockEntityType<BedsideIVRackBlockEntity>> BEDSIDE_IV_RACK = REGISTRY.register("bedside_iv_rack", deferredProvider(BedsideIVRackBlockEntity::new, ChangedBlocks.BEDSIDE_IV_RACK));
    public static final RegistryObject<BlockEntityType<CardboardBoxBlockEntity>> CARDBOARD_BOX = REGISTRY.register("cardboard_box", deferredProvider(CardboardBoxBlockEntity::new, ChangedBlocks.CARDBOARD_BOX));
}
