package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.beast.*;
import net.ltxprogrammer.changed.entity.variant.GenderedPair;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ChangedTransfurVariants {
    public static final DeferredRegister<TransfurVariant<?>> REGISTRY = ChangedRegistry.TRANSFUR_VARIANT.createDeferred(Changed.MODID);

    public static final RegistryObject<TransfurVariant<GasWolf>> GAS_WOLF = register("form_gas_wolf",
            TransfurVariant.Builder.of(ChangedEntities.GAS_WOLF).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation()));
    public static final RegistryObject<TransfurVariant<Beifeng>> BEIFENG = register("form_beifeng",
            TransfurVariant.Builder.of(ChangedEntities.BEIFENG).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation()));
    public static final RegistryObject<TransfurVariant<WhiteLatexWolfFemale>> WHITE_LATEX_WOLF_FEMALE = register("form_white_latex_wolf/female",
            TransfurVariant.Builder.of(ChangedEntities.WHITE_LATEX_WOLF_FEMALE).stepSize(0.7f).addAbility(ChangedAbilities.SWITCH_GENDER).absorbing());
    public static final RegistryObject<TransfurVariant<WhiteLatexWolfMale>> WHITE_LATEX_WOLF_MALE = register("form_white_latex_wolf/male",
            TransfurVariant.Builder.of(ChangedEntities.WHITE_LATEX_WOLF_MALE).stepSize(0.7f).addAbility(ChangedAbilities.SWITCH_GENDER));
    public static final RegistryObject<TransfurVariant<DarkDragon>> DARK_DRAGON = register("form_dark_dragon",
            TransfurVariant.Builder.of(ChangedEntities.DARK_DRAGON).glide().sound(ChangedSounds.SOUND3.getLocation()).faction(LatexType.DARK_LATEX));
    public static final RegistryObject<TransfurVariant<DarkLatexWolfFemale>> DARK_LATEX_WOLF_FEMALE = register("form_dark_latex_wolf/female",
            TransfurVariant.Builder.of(ChangedEntities.DARK_LATEX_WOLF_FEMALE).stepSize(0.7f).faction(LatexType.DARK_LATEX).absorbing());
    public static final RegistryObject<TransfurVariant<DarkLatexWolfMale>> DARK_LATEX_WOLF_MALE = register("form_dark_latex_wolf/male",
            TransfurVariant.Builder.of(ChangedEntities.DARK_LATEX_WOLF_MALE).stepSize(0.7f).faction(LatexType.DARK_LATEX));
    public static final RegistryObject<TransfurVariant<PhageLatexWolfFemale>> PHAGE_LATEX_WOLF_FEMALE = register("form_phage_latex_wolf/female",
            TransfurVariant.Builder.of(ChangedEntities.PHAGE_LATEX_WOLF_FEMALE).stepSize(0.7f).faction(LatexType.DARK_LATEX).absorbing());
    public static final RegistryObject<TransfurVariant<PhageLatexWolfMale>> PHAGE_LATEX_WOLF_MALE = register("form_phage_latex_wolf/male",
            TransfurVariant.Builder.of(ChangedEntities.PHAGE_LATEX_WOLF_MALE).stepSize(0.7f).faction(LatexType.DARK_LATEX));
    public static final RegistryObject<TransfurVariant<DarkLatexWolfPup>> DARK_LATEX_WOLF_PUP = register("form_dark_latex_wolf_pup",
            TransfurVariant.Builder.of(ChangedEntities.DARK_LATEX_WOLF_PUP).stepSize(0.7f).faction(LatexType.DARK_LATEX).transfurMode(TransfurMode.NONE).holdItemsInMouth().reducedFall().addAbility(ChangedAbilities.PUDDLE));
    public static final RegistryObject<TransfurVariant<DarkLatexWolfPartial>> DARK_LATEX_WOLF_PARTIAL = register("form_dark_latex_wolf_partial",
            TransfurVariant.Builder.of(ChangedEntities.DARK_LATEX_WOLF_PARTIAL).faction(LatexType.DARK_LATEX).transfurMode(TransfurMode.NONE));
    public static final RegistryObject<TransfurVariant<DarkLatexYufeng>> DARK_LATEX_YUFENG = register("form_dark_latex_yufeng",
            TransfurVariant.Builder.of(ChangedEntities.DARK_LATEX_YUFENG).glide().faction(LatexType.DARK_LATEX));
    public static final RegistryObject<TransfurVariant<LatexAlien>> LATEX_ALIEN = register("form_latex_alien",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_ALIEN).stepSize(0.7f).nightVision().absorbing());
    public static final RegistryObject<TransfurVariant<LatexBee>> LATEX_BEE = register("form_latex_bee",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_BEE).extraJumps(4).reducedFall().extraHands().addAbility(ChangedAbilities.CREATE_HONEYCOMB).breatheMode(TransfurVariant.BreatheMode.WEAK).absorbing());
    public static final RegistryObject<TransfurVariant<LatexBenignWolf>> LATEX_BENIGN_WOLF = register("form_latex_benign_wolf",
            TransfurVariant.Builder.of(ChangedEntities.BENIGN_LATEX_WOLF).noVision().disableItems().absorbing());
    public static final RegistryObject<TransfurVariant<LatexBlueDragon>> LATEX_BLUE_DRAGON = register("form_latex_blue_dragon",
            TransfurVariant.Builder.of(ChangedEntities.BLUE_LATEX_DRAGON).stepSize(0.7f));
    public static final RegistryObject<TransfurVariant<LatexBlueWolf>> LATEX_BLUE_WOLF = register("form_latex_blue_wolf",
            TransfurVariant.Builder.of(ChangedEntities.BLUE_LATEX_WOLF).stepSize(0.7f).absorbing());
    public static final RegistryObject<TransfurVariant<LatexCrocodile>> LATEX_CROCODILE = register("form_latex_crocodile",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_CROCODILE).breatheMode(TransfurVariant.BreatheMode.STRONG));
    public static final RegistryObject<TransfurVariant<CrystalWolf>> CRYSTAL_WOLF = register("form_crystal_wolf",
            TransfurVariant.Builder.of(ChangedEntities.CRYSTAL_WOLF).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation()));
    public static final RegistryObject<TransfurVariant<CrystalWolfHorned>> CRYSTAL_WOLF_HORNED = register("form_crystal_wolf_horned",
            TransfurVariant.Builder.of(ChangedEntities.CRYSTAL_WOLF_HORNED).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation()));
    public static final RegistryObject<TransfurVariant<LatexDeer>> LATEX_DEER = register("form_latex_deer",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_DEER).stepSize(0.7f));
    public static final RegistryObject<TransfurVariant<GreenLizard>> GREEN_LIZARD = register("form_green_lizard",
            TransfurVariant.Builder.of(ChangedEntities.GREEN_LIZARD).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation()));
    public static final RegistryObject<TransfurVariant<LatexHuman>> LATEX_HUMAN = register("form_latex_human",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_HUMAN).stepSize(0.6f));
    public static final RegistryObject<TransfurVariant<LatexFennecFox>> LATEX_FENNEC_FOX = register("form_latex_fennec_fox",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_FENNEC_FOX).stepSize(0.7f));
    public static final RegistryObject<TransfurVariant<LatexHypnoCat>> LATEX_HYPNO_CAT = register("form_latex_hypno_cat",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_HYPNO_CAT).jumpStrength(1.25f).stepSize(0.7f).breatheMode(TransfurVariant.BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision().addAbility(ChangedAbilities.HYPNOSIS));
    public static final RegistryObject<TransfurVariant<LatexKeonWolf>> LATEX_KEON_WOLF = register("form_latex_keon_wolf",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_KEON_WOLF).stepSize(0.7f));
    public static final RegistryObject<TransfurVariant<LatexLeaf>> LATEX_LEAF = register("form_latex_leaf",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_LEAF).stepSize(0.7f).absorbing());
    public static final RegistryObject<TransfurVariant<LatexMedusaCat>> LATEX_MEDUSA_CAT = register("form_latex_medusa_cat",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_MEDUSA_CAT).jumpStrength(1.25f).stepSize(0.7f).breatheMode(TransfurVariant.BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision().absorbing());
    public static final RegistryObject<TransfurVariant<LatexMimicPlant>> LATEX_MIMIC_PLANT = register("form_latex_mimic_plant",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_MIMIC_PLANT).stepSize(0.7f).reducedFall().absorbing());
    public static final RegistryObject<TransfurVariant<LatexMingCat>> LATEX_MING_CAT = register("form_latex_ming_cat",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_MING_CAT).jumpStrength(1.25f).stepSize(0.7f).breatheMode(TransfurVariant.BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision());
    public static final RegistryObject<TransfurVariant<LatexMoth>> LATEX_MOTH = register("form_latex_moth",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_MOTH).extraJumps(6).reducedFall().breatheMode(TransfurVariant.BreatheMode.WEAK));
    public static final RegistryObject<TransfurVariant<LatexMutantBloodcellWolf>> LATEX_MUTANT_BLODDCELL_WOLF = register("form_latex_mutant_bloodcell_wolf",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_MUTANT_BLOODCELL_WOLF).stepSize(0.7f).faction(LatexType.WHITE_LATEX).absorbing());
    public static final RegistryObject<TransfurVariant<LatexOrca>> LATEX_ORCA = register("form_latex_orca",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_ORCA).stepSize(0.7f).gills());
    public static final RegistryObject<TransfurVariant<LatexOtter>> LATEX_OTTER = register("form_latex_otter",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_OTTER).breatheMode(TransfurVariant.BreatheMode.STRONG));
    public static final RegistryObject<TransfurVariant<LatexPinkDeer>> LATEX_PINK_DEER = register("form_latex_pink_deer",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_PINK_DEER).stepSize(0.7f));
    public static final RegistryObject<TransfurVariant<LatexPinkWyvern>> LATEX_PINK_WYVERN = register("form_latex_pink_wyvern",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_PINK_WYVERN).stepSize(0.7f));
    public static final RegistryObject<TransfurVariant<LatexPinkYuinDragon>> LATEX_PINK_YUIN_DRAGON = register("form_latex_pink_yuin_dragon",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_PINK_YUIN_DRAGON).glide());
    public static final RegistryObject<TransfurVariant<LatexPurpleFox>> LATEX_PURPLE_FOX = register("form_latex_purple_fox",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_PURPLE_FOX).stepSize(0.7f));
    public static final RegistryObject<TransfurVariant<LatexRaccoon>> LATEX_RACCOON = register("form_latex_raccoon",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_RACCOON).noVision());
    public static final RegistryObject<TransfurVariant<LatexRedDragon>> LATEX_RED_DRAGON = register("form_latex_red_dragon",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_RED_DRAGON).glide());
    public static final RegistryObject<TransfurVariant<LatexRedPanda>> LATEX_RED_PANDA = register("form_latex_red_panda",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_RED_PANDA).stepSize(0.7f).reducedFall());
    public static final RegistryObject<TransfurVariant<LatexShark>> LATEX_SHARK = register("form_latex_shark",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_SHARK).stepSize(0.7f).gills().absorbing());
    public static final RegistryObject<TransfurVariant<LatexSnake>> LATEX_SNAKE = register("form_latex_snake",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_SNAKE).stepSize(1.1f).absorbing().noLegs().addAbility(ChangedAbilities.SLITHER));
    public static final RegistryObject<TransfurVariant<SniperDog>> SNIPER_DOG = register("form_sniper_dog",
            TransfurVariant.Builder.of(ChangedEntities.SNIPER_DOG).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation()));
    public static final RegistryObject<TransfurVariant<LatexSquirrel>> LATEX_SQUIRREL = register("form_latex_squirrel",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_SQUIRREL).stepSize(0.7f).reducedFall());
    public static final RegistryObject<TransfurVariant<LatexStiger>> LATEX_STIGER = register("form_latex_stiger",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_STIGER).canClimb().extraHands().nightVision().addAbility(ChangedAbilities.CREATE_COBWEB));
    public static final RegistryObject<TransfurVariant<LatexTigerShark>> LATEX_TIGER_SHARK = register("form_latex_tiger_shark",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_TIGER_SHARK).gills().addAbility(ChangedAbilities.SUMMON_SHARKS));
    public static final RegistryObject<TransfurVariant<LatexTrafficConeDragon>> LATEX_TRAFFIC_CONE_DRAGON = register("form_latex_traffic_cone_dragon",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_TRAFFIC_CONE_DRAGON).stepSize(0.7f).replicating());
    public static final RegistryObject<TransfurVariant<LatexTranslucentLizard>> LATEX_TRANSLUCENT_LIZARD = register("form_latex_translucent_lizard",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_TRANSLUCENT_LIZARD).stepSize(0.7f).absorbing());
    public static final RegistryObject<TransfurVariant<LatexWatermelonCat>> LATEX_WATERMELON_CAT = register("form_latex_watermelon_cat",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_WATERMELON_CAT).jumpStrength(1.25f).stepSize(0.7f).breatheMode(TransfurVariant.BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision().absorbing());
    public static final RegistryObject<TransfurVariant<LatexWhiteTiger>> LATEX_WHITE_TIGER = register("form_latex_white_tiger",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_WHITE_TIGER).jumpStrength(1.25f).stepSize(0.7f).breatheMode(TransfurVariant.BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision());
    public static final RegistryObject<TransfurVariant<LatexYuin>> LATEX_YUIN = register("form_latex_yuin",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_YUIN).stepSize(0.7f).reducedFall().absorbing());
    public static final RegistryObject<TransfurVariant<WhiteLatexCentaur>> WHITE_LATEX_CENTAUR = register("form_white_latex_centaur",
            TransfurVariant.Builder.of(ChangedEntities.WHITE_LATEX_CENTAUR).quadrupedal().stepSize(1.1f).cameraZOffset(7.0f / 16.0f).jumpStrength(1.25f).rideable().reducedFall());
    public static final RegistryObject<TransfurVariant<WhiteLatexKnight>> WHITE_LATEX_KNIGHT = register("form_white_latex_knight",
            TransfurVariant.Builder.of(ChangedEntities.WHITE_LATEX_KNIGHT).stepSize(0.7f).absorbing());
    public static final RegistryObject<TransfurVariant<WhiteLatexKnightFusion>> WHITE_LATEX_KNIGHT_FUSION = register("form_white_latex_knight_fusion",
            TransfurVariant.Builder.of(ChangedEntities.WHITE_LATEX_KNIGHT_FUSION).stepSize(0.7f).replicating());
    public static final RegistryObject<TransfurVariant<LatexMantaRayFemale>> LATEX_MANTA_RAY_FEMALE = register("form_latex_manta_ray/female",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_MANTA_RAY_FEMALE).stepSize(0.7f).gills().absorbing().noLegs());
    public static final RegistryObject<TransfurVariant<LatexMantaRayMale>> LATEX_MANTA_RAY_MALE = register("form_latex_manta_ray/male",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_MANTA_RAY_MALE).stepSize(0.7f).gills().replicating());
    public static final RegistryObject<TransfurVariant<LatexSiren>> LATEX_SIREN = register("form_latex_mermaid_shark/female",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_SIREN).stepSize(0.7f).gills().noLegs().absorbing().addAbility(ChangedAbilities.SIREN_SING));
    public static final RegistryObject<TransfurVariant<LatexMermaidShark>> LATEX_MERMAID_SHARK = register("form_latex_mermaid_shark/male",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_MERMAID_SHARK).stepSize(0.7f).gills().replicating().noLegs());
    public static final RegistryObject<TransfurVariant<BuffLatexSharkFemale>> LATEX_SHARK_FUSION_FEMALE = register("form_latex_shark/female",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_SHARK_FEMALE).stepSize(0.7f).gills().absorbing());
    public static final RegistryObject<TransfurVariant<BuffLatexSharkMale>> LATEX_SHARK_FUSION_MALE = register("form_latex_shark/male",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_SHARK_MALE).stepSize(0.7f).gills().replicating());
    public static final RegistryObject<TransfurVariant<LatexSnowLeopardFemale>> LATEX_SNOW_LEOPARD_FEMALE = register("form_latex_snow_leopard/female",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_SNOW_LEOPARD_FEMALE).stepSize(0.7f).breatheMode(TransfurVariant.BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision().jumpStrength(1.25f).absorbing());
    public static final RegistryObject<TransfurVariant<LatexSnowLeopardMale>> LATEX_SNOW_LEOPARD_MALE = register("form_latex_snow_leopard/male",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_SNOW_LEOPARD_MALE).stepSize(0.7f).breatheMode(TransfurVariant.BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision().jumpStrength(1.25f));

    public static final RegistryObject<TransfurVariant<LatexSquidDogFemale>> LATEX_SQUID_DOG_FEMALE = register("form_latex_squid_dog/female",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_SQUID_DOG_FEMALE).gills().extraHands().addAbility(ChangedAbilities.CREATE_INKBALL).absorbing());
    public static final RegistryObject<TransfurVariant<LatexSquidDogMale>> LATEX_SQUID_DOG_MALE = register("form_latex_squid_dog/male",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_SQUID_DOG_MALE).gills().extraHands().addAbility(ChangedAbilities.CREATE_INKBALL));

    public static final RegistryObject<TransfurVariant<WhiteWolfFemale>> WHITE_WOLF_FEMALE = register("form_white_wolf/female",
            TransfurVariant.Builder.of(ChangedEntities.WHITE_WOLF_FEMALE).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation()));
    public static final RegistryObject<TransfurVariant<WhiteWolfMale>> WHITE_WOLF_MALE = register("form_white_wolf/male",
            TransfurVariant.Builder.of(ChangedEntities.WHITE_WOLF_MALE).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation()));

    public static final RegistryObject<TransfurVariant<PureWhiteLatexWolf>> PURE_WHITE_LATEX_WOLF = register("form_pure_white_latex_wolf",
            TransfurVariant.Builder.of(ChangedEntities.PURE_WHITE_LATEX_WOLF).stepSize(0.7f).faction(LatexType.WHITE_LATEX));

    public static final Supplier<? extends TransfurVariant<?>> FALLBACK_VARIANT = WHITE_LATEX_WOLF_MALE;

    private static <T extends ChangedEntity> RegistryObject<TransfurVariant<T>> register(String name, TransfurVariant.Builder<T> builder) {
        return REGISTRY.register(name, builder::build);
    }

    public static class Gendered {
        public static final GenderedPair<WhiteLatexWolfMale, WhiteLatexWolfFemale> WHITE_LATEX_WOLVES = new GenderedPair<>(WHITE_LATEX_WOLF_MALE, WHITE_LATEX_WOLF_FEMALE);
        public static final GenderedPair<DarkLatexWolfMale, DarkLatexWolfFemale> DARK_LATEX_WOLVES = new GenderedPair<>(DARK_LATEX_WOLF_MALE, DARK_LATEX_WOLF_FEMALE);
        public static final GenderedPair<PhageLatexWolfMale, PhageLatexWolfFemale> PHAGE_LATEX_WOLVES = new GenderedPair<>(PHAGE_LATEX_WOLF_MALE, PHAGE_LATEX_WOLF_FEMALE);
        public static final GenderedPair<LatexMantaRayMale, LatexMantaRayFemale> LATEX_MANTA_RAYS = new GenderedPair<>(LATEX_MANTA_RAY_MALE, LATEX_MANTA_RAY_FEMALE);
        public static final GenderedPair<LatexMermaidShark, LatexSiren> LATEX_MERMAID_SHARKS = new GenderedPair<>(LATEX_MERMAID_SHARK, LATEX_SIREN);
        public static final GenderedPair<BuffLatexSharkMale, BuffLatexSharkFemale> LATEX_SHARK_FUSIONS = new GenderedPair<>(LATEX_SHARK_FUSION_MALE, LATEX_SHARK_FUSION_FEMALE);
        public static final GenderedPair<LatexSquidDogMale, LatexSquidDogFemale> LATEX_SQUID_DOGS = new GenderedPair<>(LATEX_SQUID_DOG_MALE, LATEX_SQUID_DOG_FEMALE);
        public static final GenderedPair<LatexSnowLeopardMale, LatexSnowLeopardFemale> LATEX_SNOW_LEOPARDS = new GenderedPair<>(LATEX_SNOW_LEOPARD_MALE, LATEX_SNOW_LEOPARD_FEMALE);
        public static final GenderedPair<WhiteWolfMale, WhiteWolfFemale> WHITE_WOLVES = new GenderedPair<>(WHITE_WOLF_MALE, WHITE_WOLF_FEMALE);
    }
}
