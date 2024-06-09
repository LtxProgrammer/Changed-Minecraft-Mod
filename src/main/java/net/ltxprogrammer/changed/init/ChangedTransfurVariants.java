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

    public static final RegistryObject<TransfurVariant<AerosolLatexWolf>> GAS_WOLF = register("form_aerosol_latex_wolf",
            TransfurVariant.Builder.of(ChangedEntities.AEROSOL_LATEX_WOLF).groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation()));
    public static final RegistryObject<TransfurVariant<LatexBeifeng>> LATEX_BEIFENG = register("form_latex_beifeng",
            TransfurVariant.Builder.of(ChangedEntities.BEIFENG).groundSpeed(1.05f).swimSpeed(1.0f).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation()));
    public static final RegistryObject<TransfurVariant<LightLatexWolfFemale>> LIGHT_LATEX_WOLF_FEMALE = register("form_light_latex_wolf/female",
            TransfurVariant.Builder.of(ChangedEntities.WHITE_GOO_WOLF_FEMALE).groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f).addAbility(ChangedAbilities.SWITCH_GENDER).absorbing());
    public static final RegistryObject<TransfurVariant<LightLatexWolfMale>> LIGHT_LATEX_WOLF_MALE = register("form_light_latex_wolf/male",
            TransfurVariant.Builder.of(ChangedEntities.WHITE_GOO_WOLF_MALE).groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f).addAbility(ChangedAbilities.SWITCH_GENDER));
    public static final RegistryObject<TransfurVariant<DarkLatexDragon>> DARK_LATEX_DRAGON = register("form_dark_latex_dragon",
            TransfurVariant.Builder.of(ChangedEntities.DARK_LATEX_DRAGON).groundSpeed(1.0F).swimSpeed(0.85f).glide().sound(ChangedSounds.SOUND3.getLocation()).faction(LatexType.DARK_LATEX));
    public static final RegistryObject<TransfurVariant<DarkLatexWolfFemale>> DARK_LATEX_WOLF_FEMALE = register("form_dark_latex_wolf/female",
            TransfurVariant.Builder.of(ChangedEntities.DARK_LATEX_WOLF_FEMALE).groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f).faction(LatexType.DARK_LATEX).absorbing());
    public static final RegistryObject<TransfurVariant<DarkLatexWolfMale>> DARK_LATEX_WOLF_MALE = register("form_dark_latex_wolf/male",
            TransfurVariant.Builder.of(ChangedEntities.DARK_LATEX_WOLF_MALE).groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f).faction(LatexType.DARK_LATEX));
    public static final RegistryObject<TransfurVariant<PhageLatexWolfFemale>> PHAGE_LATEX_WOLF_FEMALE = register("form_phage_latex_wolf/female",
            () -> TransfurVariant.Builder.of(DARK_LATEX_WOLF_FEMALE.get(), ChangedEntities.PHAGE_LATEX_WOLF_FEMALE));
    public static final RegistryObject<TransfurVariant<PhageLatexWolfMale>> PHAGE_LATEX_WOLF_MALE = register("form_phage_latex_wolf/male",
            () -> TransfurVariant.Builder.of(DARK_LATEX_WOLF_MALE.get(), ChangedEntities.PHAGE_LATEX_WOLF_MALE));
    public static final RegistryObject<TransfurVariant<DarkLatexWolfPup>> DARK_LATEX_WOLF_PUP = register("form_dark_latex_wolf_pup",
            () -> TransfurVariant.Builder.of(DARK_LATEX_WOLF_MALE.get(), ChangedEntities.DARK_LATEX_WOLF_PUP).transfurMode(TransfurMode.NONE).holdItemsInMouth().additionalHealth(-8).groundSpeed(1.25F).reducedFall().addAbility(ChangedAbilities.PUDDLE));
    public static final RegistryObject<TransfurVariant<DarkLatexWolfPartial>> DARK_LATEX_WOLF_PARTIAL = register("form_dark_latex_wolf_partial",
            TransfurVariant.Builder.of(ChangedEntities.BLACK_GOO_WOLF_PARTIAL).groundSpeed(1.025f).swimSpeed(0.975f).faction(LatexType.DARK_LATEX).transfurMode(TransfurMode.NONE));
    public static final RegistryObject<TransfurVariant<DarkLatexYufeng>> DARK_LATEX_YUFENG = register("form_dark_latex_yufeng", TransfurVariant.Builder.of(ChangedEntities.BLACK_GOO_YUFENG)
            .groundSpeed(1.0F).swimSpeed(0.85f).glide().faction(LatexType.DARK_LATEX));
    public static final RegistryObject<TransfurVariant<LatexAlien>> LATEX_ALIEN = register("form_latex_alien",
            TransfurVariant.Builder.of(ChangedEntities.GOO_ALIEN).groundSpeed(1.0f).swimSpeed(1.0f).stepSize(0.7f).nightVision().absorbing());
    public static final RegistryObject<TransfurVariant<LatexBee>> LATEX_BEE = register("form_latex_bee",
            TransfurVariant.Builder.of(ChangedEntities.GOO_BEE).groundSpeed(1.05f).swimSpeed(0.75f).extraJumps(4).reducedFall().extraHands().addAbility(ChangedAbilities.CREATE_HONEYCOMB).breatheMode(TransfurVariant.BreatheMode.WEAK).absorbing());
    public static final RegistryObject<TransfurVariant<LatexBenignWolf>> LATEX_BENIGN_WOLF = register("form_latex_benign_wolf",
            TransfurVariant.Builder.of(ChangedEntities.BENIGN_GOO_WOLF).groundSpeed(0.15f).swimSpeed(0.15f).noVision().disableItems().absorbing());
    public static final RegistryObject<TransfurVariant<LatexBlueDragon>> LATEX_BLUE_DRAGON = register("form_latex_blue_dragon", TransfurVariant.Builder.of(ChangedEntities.BLUE_GOO_DRAGON)
            .groundSpeed(1.05f).swimSpeed(0.98f).stepSize(0.7f));
    public static final RegistryObject<TransfurVariant<LatexBlueWolf>> LATEX_BLUE_WOLF = register("form_latex_blue_wolf",
            TransfurVariant.Builder.of(ChangedEntities.BLUE_GOO_WOLF).groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f).absorbing());
    public static final RegistryObject<TransfurVariant<LatexCrocodile>> LATEX_CROCODILE = register("form_latex_crocodile",
            TransfurVariant.Builder.of(ChangedEntities.GOO_CROCODILE).groundSpeed(0.925f).swimSpeed(1.1f).additionalHealth(12).breatheMode(TransfurVariant.BreatheMode.STRONG));
    public static final RegistryObject<TransfurVariant<LatexCrystalWolf>> LATEX_CRYSTAL_WOLF = register("form_latex_crystal_wolf",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_CRYSTAL_WOLF).groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation()));
    public static final RegistryObject<TransfurVariant<LatexCrystalWolfHorned>> LATEX_CRYSTAL_WOLF_HORNED = register("form_latex_crystal_wolf_horned",
            () -> TransfurVariant.Builder.of(LATEX_CRYSTAL_WOLF.get(), ChangedEntities.LATEX_CRYSTAL_WOLF_HORNED).sound(ChangedSounds.SOUND3.getLocation()));
    public static final RegistryObject<TransfurVariant<LatexDeer>> LATEX_DEER = register("form_latex_deer",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_DEER).groundSpeed(1.1f).swimSpeed(0.95f).stepSize(0.7f));
    public static final RegistryObject<TransfurVariant<GreenLizard>> GREEN_LIZARD = register("form_green_lizard",
            TransfurVariant.Builder.of(ChangedEntities.GREEN_LIZARD).groundSpeed(1.05f).swimSpeed(0.98f).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation()));
    public static final RegistryObject<TransfurVariant<LatexHuman>> LATEX_HUMAN = register("form_latex_human",
            TransfurVariant.Builder.of(ChangedEntities.GOO_HUMAN).groundSpeed(1.0f).swimSpeed(1.0f).stepSize(0.6f));
    public static final RegistryObject<TransfurVariant<LatexFennecFox>> LATEX_FENNEC_FOX = register("form_latex_fennec_fox",
            TransfurVariant.Builder.of(ChangedEntities.GOO_FENNEC_FOX).groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f));
    public static final RegistryObject<TransfurVariant<LatexHypnoCat>> LATEX_HYPNO_CAT = register("form_latex_hypno_cat",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_HYPNO_CAT).jumpStrength(1.25f).additionalHealth(2).groundSpeed(1.15f).swimSpeed(0.9f).stepSize(0.7f).breatheMode(TransfurVariant.BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision().addAbility(ChangedAbilities.HYPNOSIS));
    public static final RegistryObject<TransfurVariant<LatexKeonWolf>> LATEX_KEON_WOLF = register("form_latex_keon_wolf",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_KEON_WOLF).groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f));
    public static final RegistryObject<TransfurVariant<LatexLeaf>> LATEX_LEAF = register("form_latex_leaf",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_LEAF).groundSpeed(1.05f).swimSpeed(0.95f).stepSize(0.7f).absorbing());
    public static final RegistryObject<TransfurVariant<LatexMedusaCat>> LATEX_MEDUSA_CAT = register("form_latex_medusa_cat",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_MEDUSA_CAT).jumpStrength(1.25f).additionalHealth(2).groundSpeed(1.15f).swimSpeed(0.9f).stepSize(0.7f).breatheMode(TransfurVariant.BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision().absorbing());
    public static final RegistryObject<TransfurVariant<LatexMimicPlant>> LATEX_MIMIC_PLANT = register("form_latex_mimic_plant",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_MIMIC_PLANT).groundSpeed(1.05f).swimSpeed(0.95f).stepSize(0.7f).reducedFall().absorbing());
    public static final RegistryObject<TransfurVariant<LatexMingCat>> LATEX_MING_CAT = register("form_latex_ming_cat",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_MING_CAT).jumpStrength(1.25f).additionalHealth(2).groundSpeed(1.15f).swimSpeed(0.9f).stepSize(0.7f).breatheMode(TransfurVariant.BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision());
    public static final RegistryObject<TransfurVariant<LatexMoth>> LATEX_MOTH = register("form_latex_moth",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_MOTH).groundSpeed(1.05f).swimSpeed(0.75f).extraJumps(6).reducedFall().breatheMode(TransfurVariant.BreatheMode.WEAK));
    public static final RegistryObject<TransfurVariant<LatexMutantBloodcellWolf>> LATEX_MUTANT_BLODDCELL_WOLF = register("form_latex_mutant_bloodcell_wolf",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_MUTANT_BLOODCELL_WOLF ).groundSpeed(1.05f).swimSpeed(0.9f).stepSize(0.7f).faction(LatexType.WHITE_LATEX).absorbing());
    public static final RegistryObject<TransfurVariant<LatexOrca>> LATEX_ORCA = register("form_latex_orca",
            TransfurVariant.Builder.of(ChangedEntities.GOO_ORCA).groundSpeed(0.875f).swimSpeed(1.30f).stepSize(0.7f).gills());
    public static final RegistryObject<TransfurVariant<LatexOtter>> LATEX_OTTER = register("form_latex_otter",
            TransfurVariant.Builder.of(ChangedEntities.GOO_OTTER).groundSpeed(1.05f).swimSpeed(1.2f).breatheMode(TransfurVariant.BreatheMode.STRONG));
    public static final RegistryObject<TransfurVariant<LatexPinkDeer>> LATEX_PINK_DEER = register("form_latex_pink_deer",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_PINK_DEER).groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f));
    public static final RegistryObject<TransfurVariant<LatexPinkWyvern>> LATEX_PINK_WYVERN = register("form_latex_pink_wyvern",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_PINK_WYVERN).groundSpeed(1.05f).swimSpeed(0.95f).stepSize(0.7f));
    public static final RegistryObject<TransfurVariant<LatexPinkYuinDragon>> LATEX_PINK_YUIN_DRAGON = register("form_latex_pink_yuin_dragon",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_PINK_YUIN_DRAGON).groundSpeed(1.0F).swimSpeed(0.85f).glide());
    public static final RegistryObject<TransfurVariant<LatexPurpleFox>> LATEX_PURPLE_FOX = register("form_latex_purple_fox",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_PURPLE_FOX).groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f));
    public static final RegistryObject<TransfurVariant<LatexRaccoon>> LATEX_RACCOON = register("form_latex_raccoon",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_RACCOON).groundSpeed(0.95f).swimSpeed(0.97f).noVision());
    public static final RegistryObject<TransfurVariant<LatexRedDragon>> LATEX_RED_DRAGON = register("form_latex_red_dragon",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_RED_DRAGON).groundSpeed(1.0F).swimSpeed(0.85f).glide());
    public static final RegistryObject<TransfurVariant<LatexRedPanda>> LATEX_RED_PANDA = register("form_latex_red_panda",
            TransfurVariant.Builder.of(ChangedEntities.GOO_RED_PANDA).groundSpeed(1.05f).swimSpeed(0.95f).stepSize(0.7f).reducedFall());
    public static final RegistryObject<TransfurVariant<LatexShark>> LATEX_SHARK = register("form_latex_shark",
            TransfurVariant.Builder.of(ChangedEntities.GOO_SHARK).groundSpeed(0.875f).swimSpeed(1.30f).stepSize(0.7f).gills().absorbing());
    public static final RegistryObject<TransfurVariant<LatexSnake>> LATEX_SNAKE = register("form_latex_snake",
            TransfurVariant.Builder.of(ChangedEntities.GOO_SNAKE).groundSpeed(1.0F).swimSpeed(0.95f).additionalHealth(6).stepSize(1.1f).absorbing().noLegs().addAbility(ChangedAbilities.SLITHER));
    public static final RegistryObject<TransfurVariant<LatexSniperDog>> LATEX_SNIPER_DOG = register("form_latex_sniper_dog",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_SNIPER_DOG).groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation()));
    public static final RegistryObject<TransfurVariant<LatexSquirrel>> LATEX_SQUIRREL = register("form_latex_squirrel",
            TransfurVariant.Builder.of(ChangedEntities.GOO_SQUIRREL).groundSpeed(1.1f).swimSpeed(0.9f).stepSize(0.7f).reducedFall());
    public static final RegistryObject<TransfurVariant<LatexStiger>> LATEX_STIGER = register("form_latex_stiger",
            TransfurVariant.Builder.of(ChangedEntities.GOO_STIGER).canClimb().extraHands().nightVision().addAbility(ChangedAbilities.CREATE_COBWEB));
    public static final RegistryObject<TransfurVariant<LatexTigerShark>> LATEX_TIGER_SHARK = register("form_latex_tiger_shark",
            TransfurVariant.Builder.of(ChangedEntities.GOO_TIGER_SHARK).groundSpeed(0.925f).swimSpeed(1.2f).additionalHealth(8).gills().addAbility(ChangedAbilities.SUMMON_SHARKS));
    public static final RegistryObject<TransfurVariant<LatexTrafficConeDragon>> LATEX_TRAFFIC_CONE_DRAGON = register("form_latex_traffic_cone_dragon",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_TRAFFIC_CONE_DRAGON).groundSpeed(1.05f).swimSpeed(0.95f).stepSize(0.7f).replicating());
    public static final RegistryObject<TransfurVariant<LatexTranslucentLizard>> LATEX_TRANSLUCENT_LIZARD = register("form_latex_translucent_lizard",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_TRANSLUCENT_LIZARD).groundSpeed(1.05f).swimSpeed(1.0f).stepSize(0.7f).absorbing());
    public static final RegistryObject<TransfurVariant<LatexWatermelonCat>> LATEX_WATERMELON_CAT = register("form_latex_watermelon_cat",
            TransfurVariant.Builder.of(ChangedEntities.GOO_WATERMELON_CAT).jumpStrength(1.25f).additionalHealth(2).groundSpeed(1.15f).swimSpeed(0.9f).stepSize(0.7f).breatheMode(TransfurVariant.BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision().absorbing());
    public static final RegistryObject<TransfurVariant<LatexWhiteTiger>> LATEX_WHITE_TIGER = register("form_latex_white_tiger",
            TransfurVariant.Builder.of(ChangedEntities.LATEX_WHITE_TIGER).jumpStrength(1.25f).additionalHealth(2).groundSpeed(1.15f).swimSpeed(0.9f).stepSize(0.7f).breatheMode(TransfurVariant.BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision());
    public static final RegistryObject<TransfurVariant<LatexYuin>> LATEX_YUIN = register("form_latex_yuin",
            TransfurVariant.Builder.of(ChangedEntities.GOO_YUIN).groundSpeed(1.05f).swimSpeed(0.98f).stepSize(0.7f).reducedFall().absorbing());
    public static final RegistryObject<TransfurVariant<LightLatexCentaur>> LIGHT_LATEX_CENTAUR = register("form_light_latex_centaur",
            TransfurVariant.Builder.of(ChangedEntities.WHITE_GOO_CENTAUR).quadrupedal().groundSpeed(1.20f).swimSpeed(0.9f).stepSize(1.1f).additionalHealth(10).cameraZOffset(7.0f / 16.0f).jumpStrength(1.25f).rideable().reducedFall());
    public static final RegistryObject<TransfurVariant<LightLatexKnight>> LIGHT_LATEX_KNIGHT = register("form_light_latex_knight",
            TransfurVariant.Builder.of(ChangedEntities.WHITE_GOO_KNIGHT).groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f).absorbing());
    public static final RegistryObject<TransfurVariant<LightLatexKnightFusion>> LIGHT_LATEX_KNIGHT_FUSION = register("form_light_latex_knight_fusion",
            () -> TransfurVariant.Builder.of(LIGHT_LATEX_KNIGHT.get(), ChangedEntities.WHITE_GOO_KNIGHT_FUSION).replicating().additionalHealth(8).fusionOf(LIGHT_LATEX_WOLF_MALE.get(), LIGHT_LATEX_KNIGHT.get()));
    public static final RegistryObject<TransfurVariant<LatexMantaRayFemale>> LATEX_MANTA_RAY_FEMALE = register("form_latex_manta_ray/female",
            () -> TransfurVariant.Builder.of(LATEX_SHARK.get(), ChangedEntities.LATEX_MANTA_RAY_FEMALE).groundSpeed(0.26F).swimSpeed(1.9F).absorbing().additionalHealth(8).noLegs());
    public static final RegistryObject<TransfurVariant<LatexMantaRayMale>> LATEX_MANTA_RAY_MALE = register("form_latex_manta_ray/male",
            () -> TransfurVariant.Builder.of(LATEX_SHARK.get(), ChangedEntities.LATEX_MANTA_RAY_MALE).replicating());
    public static final RegistryObject<TransfurVariant<LatexSiren>> LATEX_SIREN = register("form_latex_mermaid_shark/female",
            () -> TransfurVariant.Builder.of(LATEX_SHARK.get(), ChangedEntities.LATEX_SIREN).groundSpeed(0.26F).swimSpeed(1.9F).additionalHealth(8).noLegs().absorbing().addAbility(ChangedAbilities.SIREN_SING));
    public static final RegistryObject<TransfurVariant<LatexMermaidShark>> LATEX_MERMAID_SHARK = register("form_latex_mermaid_shark/male",
            () -> TransfurVariant.Builder.of(LATEX_SHARK.get(), ChangedEntities.LATEX_MERMAID_SHARK).groundSpeed(0.26F).swimSpeed(1.9F).additionalHealth(8).replicating().noLegs());
    public static final RegistryObject<TransfurVariant<LatexSharkFemale>> LATEX_SHARK_FUSION_FEMALE = register("form_latex_shark/female",
            () -> TransfurVariant.Builder.of(LATEX_SHARK.get(), ChangedEntities.LATEX_SHARK_FEMALE).groundSpeed(0.9f).swimSpeed(1.35f).stepSize(0.7f).additionalHealth(8).absorbing().fusionOf(LATEX_SHARK.get(), Shark.class));
    public static final RegistryObject<TransfurVariant<LatexSharkMale>> LATEX_SHARK_FUSION_MALE = register("form_latex_shark/male",
            () -> TransfurVariant.Builder.of(LATEX_SHARK.get(), ChangedEntities.LATEX_SHARK_MALE).groundSpeed(0.9f).swimSpeed(1.35f).stepSize(0.7f).additionalHealth(8).replicating().fusionOf(LATEX_SHARK.get(), Shark.class));
    public static final RegistryObject<TransfurVariant<LatexSnowLeopardFemale>> LATEX_SNOW_LEOPARD_FEMALE = register("form_latex_snow_leopard/female",
            TransfurVariant.Builder.of(ChangedEntities.GOO_SNOW_LEOPARD_FEMALE).groundSpeed(1.15f).swimSpeed(0.9f).stepSize(0.7f).breatheMode(TransfurVariant.BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision().jumpStrength(1.25f).additionalHealth(2).absorbing());
    public static final RegistryObject<TransfurVariant<LatexSnowLeopardMale>> LATEX_SNOW_LEOPARD_MALE = register("form_latex_snow_leopard/male",
            TransfurVariant.Builder.of(ChangedEntities.GOO_SNOW_LEOPARD_MALE).groundSpeed(1.15f).swimSpeed(0.9f).stepSize(0.7f).breatheMode(TransfurVariant.BreatheMode.WEAK).reducedFall().scares(Creeper.class).nightVision().jumpStrength(1.25f).additionalHealth(2));

    public static final RegistryObject<TransfurVariant<LatexSquidDogFemale>> LATEX_SQUID_DOG_FEMALE = register("form_latex_squid_dog/female",
            TransfurVariant.Builder.of(ChangedEntities.GOO_SQUID_DOG_FEMALE).groundSpeed(0.925f).swimSpeed(1.1f).additionalHealth(10).gills().extraHands().addAbility(ChangedAbilities.CREATE_INKBALL).absorbing());
    public static final RegistryObject<TransfurVariant<LatexSquidDogMale>> LATEX_SQUID_DOG_MALE = register("form_latex_squid_dog/male",
            TransfurVariant.Builder.of(ChangedEntities.GOO_SQUID_DOG_MALE).groundSpeed(0.925f).swimSpeed(1.1f).additionalHealth(10).gills().extraHands().addAbility(ChangedAbilities.CREATE_INKBALL));

    public static final RegistryObject<TransfurVariant<WhiteWolfFemale>> WHITE_WOLF_FEMALE = register("form_white_wolf/female",
            TransfurVariant.Builder.of(ChangedEntities.WHITE_WOLF_FEMALE).groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation()));
    public static final RegistryObject<TransfurVariant<WhiteWolfMale>> WHITE_WOLF_MALE = register("form_white_wolf/male",
            TransfurVariant.Builder.of(ChangedEntities.WHITE_WOLF_MALE).groundSpeed(1.075f).swimSpeed(0.95f).stepSize(0.7f).sound(ChangedSounds.SOUND3.getLocation()));

    public static final RegistryObject<TransfurVariant<WhiteLatexWolf>> WHITE_LATEX_WOLF = register("form_white_latex_wolf",
            TransfurVariant.Builder.of(ChangedEntities.PURE_WHITE_GOO_WOLF).groundSpeed(1.05f).swimSpeed(0.9f).stepSize(0.7f).faction(LatexType.WHITE_LATEX));

    public static final Supplier<? extends TransfurVariant<?>> FALLBACK_VARIANT = LIGHT_LATEX_WOLF_MALE;

    private static <T extends ChangedEntity> RegistryObject<TransfurVariant<T>> register(String name, TransfurVariant.Builder<T> builder) {
        return REGISTRY.register(name, builder::build);
    }

    private static <T extends ChangedEntity> RegistryObject<TransfurVariant<T>> register(String name, Supplier<TransfurVariant.Builder<T>> builder) {
        return REGISTRY.register(name, () -> builder.get().build());
    }

    public static class Gendered {
        public static final GenderedPair<LightLatexWolfMale, LightLatexWolfFemale> LIGHT_LATEX_WOLVES = new GenderedPair<>(LIGHT_LATEX_WOLF_MALE, LIGHT_LATEX_WOLF_FEMALE);
        public static final GenderedPair<DarkLatexWolfMale, DarkLatexWolfFemale> DARK_LATEX_WOLVES = new GenderedPair<>(DARK_LATEX_WOLF_MALE, DARK_LATEX_WOLF_FEMALE);
        public static final GenderedPair<PhageLatexWolfMale, PhageLatexWolfFemale> PHAGE_LATEX_WOLVES = new GenderedPair<>(PHAGE_LATEX_WOLF_MALE, PHAGE_LATEX_WOLF_FEMALE);
        public static final GenderedPair<LatexMantaRayMale, LatexMantaRayFemale> LATEX_MANTA_RAYS = new GenderedPair<>(LATEX_MANTA_RAY_MALE, LATEX_MANTA_RAY_FEMALE);
        public static final GenderedPair<LatexMermaidShark, LatexSiren> LATEX_MERMAID_SHARKS = new GenderedPair<>(LATEX_MERMAID_SHARK, LATEX_SIREN);
        public static final GenderedPair<LatexSharkMale, LatexSharkFemale> LATEX_SHARK_FUSIONS = new GenderedPair<>(LATEX_SHARK_FUSION_MALE, LATEX_SHARK_FUSION_FEMALE);
        public static final GenderedPair<LatexSquidDogMale, LatexSquidDogFemale> LATEX_SQUID_DOGS = new GenderedPair<>(LATEX_SQUID_DOG_MALE, LATEX_SQUID_DOG_FEMALE);
        public static final GenderedPair<LatexSnowLeopardMale, LatexSnowLeopardFemale> LATEX_SNOW_LEOPARDS = new GenderedPair<>(LATEX_SNOW_LEOPARD_MALE, LATEX_SNOW_LEOPARD_FEMALE);
        public static final GenderedPair<WhiteWolfMale, WhiteWolfFemale> WHITE_WOLVES = new GenderedPair<>(WHITE_WOLF_MALE, WHITE_WOLF_FEMALE);
    }
}
