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
    public static final RegistryObject<BlockEntityType<CardboardBoxTallBlockEntity>> CARDBOARD_BOX_TALL = REGISTRY.register("cardboard_box", deferredProvider(CardboardBoxTallBlockEntity::new, ChangedBlocks.CARDBOARD_BOX_TALL));
    public static final RegistryObject<BlockEntityType<KeypadBlockEntity>> KEYPAD = REGISTRY.register("keypad", deferredProvider(KeypadBlockEntity::new, ChangedBlocks.KEYPAD));
    public static final RegistryObject<BlockEntityType<LatexContainerBlockEntity>> LATEX_CONTAINER = REGISTRY.register("latex_container", deferredProvider(LatexContainerBlockEntity::new, ChangedBlocks.LATEX_CONTAINER));
    public static final RegistryObject<BlockEntityType<TextBlockEntity>> TEXT_BLOCK_ENTITY = REGISTRY.register("text_block_entity", deferredProvider(TextBlockEntity::new, ChangedBlocks.NOTE, ChangedBlocks.CLIPBOARD));
    public static final RegistryObject<BlockEntityType<GasCanisterBlockEntity>> GAS_CANISTER = REGISTRY.register("gas_canister", deferredProvider(GasCanisterBlockEntity::new, ChangedBlocks.WOLF_GAS_CANISTER));
    public static final RegistryObject<BlockEntityType<GluBlockEntity>> GLU = REGISTRY.register("glu", deferredProvider(GluBlockEntity::new, ChangedBlocks.GLU_BLOCK));
    public static final RegistryObject<BlockEntityType<CardboardBoxBlockEntity>> CARDBOARD_BOX = REGISTRY.register("cardboard_container", deferredProvider(CardboardBoxBlockEntity::new, ChangedBlocks.CARDBOARD_BOX));
    public static final RegistryObject<BlockEntityType<DroppedSyringeBlockEntity>> DROPPED_SYRINGE = REGISTRY.register("dropped_syringe", deferredProvider(DroppedSyringeBlockEntity::new, ChangedBlocks.DROPPED_SYRINGE));
    public static final RegistryObject<BlockEntityType<OfficeChairBlockEntity>> OFFICE_CHAIR = REGISTRY.register("office_chair", deferredProvider(OfficeChairBlockEntity::new, ChangedBlocks.OFFICE_CHAIR));
    public static final RegistryObject<BlockEntityType<PillowBlockEntity>> PILLOW = REGISTRY.register("pillow", deferredProvider(PillowBlockEntity::new, ChangedBlocks.WHITE_PILLOW));
}
