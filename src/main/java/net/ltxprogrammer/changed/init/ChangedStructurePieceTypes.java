package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.world.features.structures.*;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;

public class ChangedStructurePieceTypes {
    public static final DeferredRegister<StructurePieceType> REGISTRY = DeferredRegister.create(Registry.STRUCTURE_PIECE_REGISTRY, Changed.MODID);

    public static final RegistryObject<StructurePieceType> NBT = setPieceId(SurfaceNBTPiece::new, "nbt");

    private static RegistryObject<StructurePieceType> setFullContextPieceId(StructurePieceType type, String name) {
        return REGISTRY.register(name.toLowerCase(Locale.ROOT), () -> type);
    }

    private static RegistryObject<StructurePieceType> setPieceId(StructurePieceType.ContextlessType p_210153_, String p_210154_) {
        return setFullContextPieceId(p_210153_, p_210154_);
    }

    private static RegistryObject<StructurePieceType> setTemplatePieceId(StructurePieceType.StructureTemplateType p_210156_, String p_210157_) {
        return setFullContextPieceId(p_210156_, p_210157_);
    }
}
