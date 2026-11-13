package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.world.features.structures.facility.types.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ChangedFacilityPieceTypes {
    public static final DeferredRegister<PieceType<?>> REGISTRY = ChangedRegistry.FACILITY_PIECE_TYPES.createDeferred(Changed.MODID);

    /**
     * The first piece to be placed in facility generation. Will always connect to a "changed:staircase_start"
     */
    public static final RegistryObject<EntranceType> ENTRANCE = REGISTRY.register("entrance", EntranceType::new);
    /**
     * Start of staircase generation. Next piece will always be a "changed:staircase_section"
     */
    public static final RegistryObject<StaircaseStartType> STAIRCASE_START = REGISTRY.register("staircase_start", StaircaseStartType::new);
    /**
     * Section of staircase generation. Next piece will likely be a "changed:staircase_section" or else a "changed:staircase_end"
     */
    public static final RegistryObject<StaircaseSectionType> STAIRCASE_SECTION = REGISTRY.register("staircase_section", StaircaseSectionType::new);
    /**
     * Section of staircase generation that has a landing. Next piece will likely be a "changed:staircase_section" or else a "changed:staircase_end"
     * Also neighbors into a split piece
     */
    public static final RegistryObject<StaircaseLandingType> STAIRCASE_LANDING = REGISTRY.register("staircase_landing", StaircaseLandingType::new);
    /**
     * End of staircase generation. Next piece will always be a "changed:split"
     */
    public static final RegistryObject<StaircaseEndType> STAIRCASE_END = REGISTRY.register("staircase_end", StaircaseEndType::new);
    /**
     * Hallway/corridor. Takes you from one place to another, and has one entrance and one exit.
     * Next piece could be a "changed:corridor", "changed:split", "changed:transition", "changed:room"
     */
    public static final RegistryObject<CorridorType> CORRIDOR = REGISTRY.register("corridor", CorridorType::new);
    /**
     * Splits generation into 2 or more paths. Next piece could be a "changed:corridor", "changed:transition", "changed:room"
     */
    public static final RegistryObject<SplitType> SPLIT = REGISTRY.register("split", SplitType::new);
    /**
     * Transitions between zones. Next piece will always be a "changed:corridor"
     */
    public static final RegistryObject<TransitionType> TRANSITION = REGISTRY.register("transition", TransitionType::new);
    /**
     * Emergency piece that is used to seal generation failures. Never generated initially
     */
    public static final RegistryObject<SealType> SEAL = REGISTRY.register("seal", SealType::new);
    /**
     * End of a generation path. Also is used if generation fails, prioritized over a "changed:seal"
     */
    public static final RegistryObject<RoomType> ROOM = REGISTRY.register("room", RoomType::new);
}
