package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.world.features.structures.FacilityPieces;
import net.ltxprogrammer.changed.world.features.structures.SurfaceNBTPiece;
import net.ltxprogrammer.changed.world.features.structures.facility.FacilitySinglePiece;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;

public class ChangedStructurePieceTypes {
    public static final DeferredRegister<StructurePieceType> REGISTRY = DeferredRegister.create(Registry.STRUCTURE_PIECE_REGISTRY, Changed.MODID);

    public static final RegistryObject<StructurePieceType> NBT = setTemplatePieceId(SurfaceNBTPiece::new, "nbt");
    public static final RegistryObject<StructurePieceType> FACILITY_SINGLE = setTemplatePieceId(FacilitySinglePiece.StructureInstance::new, "facility_single");

    private static RegistryObject<StructurePieceType> setFullContextPieceId(StructurePieceType type, String name) {
        return REGISTRY.register(name.toLowerCase(Locale.ROOT), () -> type);
    }

    private static RegistryObject<StructurePieceType> setPieceId(StructurePieceType.ContextlessType piece, String name) {
        return setFullContextPieceId(piece, name);
    }

    private static RegistryObject<StructurePieceType> setTemplatePieceId(StructurePieceType.StructureTemplateType piece, String name) {
        return setFullContextPieceId(piece, name);
    }
}
