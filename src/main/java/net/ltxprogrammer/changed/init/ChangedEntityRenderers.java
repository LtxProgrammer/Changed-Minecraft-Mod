package net.ltxprogrammer.changed.init;

import com.google.common.collect.ImmutableMap;
import net.ltxprogrammer.changed.client.RegisterComplexRenderersEvent;
import net.ltxprogrammer.changed.client.renderer.*;
import net.ltxprogrammer.changed.client.renderer.particle.GasParticleRenderer;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.ComplexRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedEntityRenderers {
    private static final List<EntityType<? extends ChangedEntity>> copyPlayerLayers = new ArrayList<>();
    private static Map<EntityType<? extends ComplexRenderer>, Map<String, EntityRenderer<? extends ComplexRenderer>>> complexRenderers = ImmutableMap.of();

    public static <T extends Entity> Optional<EntityRenderer<? super T>> getComplexRenderer(T entity) {
        if (!(entity instanceof ComplexRenderer complexRenderer))
            return Optional.empty();

        final var renderers = Optional.ofNullable(complexRenderers.get(entity.getType()));
        return renderers.map(map -> (EntityRenderer<? super T>)map.getOrDefault(complexRenderer.getModelName(), map.get("default")));
    }

    public static List<EntityType<? extends ChangedEntity>> getCopyPlayerLayers() {
        return copyPlayerLayers;
    }

    public static Stream<EntityRenderer<? extends ComplexRenderer>> getComplexRenderers(String name) {
        return complexRenderers.values().stream().map(entityRenderers -> entityRenderers.get(name));
    }

    public static void registerComplexRenderers(EntityRendererProvider.Context context) {
        final var event = new RegisterComplexRenderersEvent();
        ModLoader.get().postEvent(event);
        complexRenderers = event.build(context);
    }

    @SubscribeEvent
    public static void registerComplexEntityRenderers(RegisterComplexRenderersEvent event) {
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_WOLF_PARTIAL.get(), "default", DarkLatexWolfPartialRenderer.forModelSize(false));
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_WOLF_PARTIAL.get(), "slim", DarkLatexWolfPartialRenderer.forModelSize(true));
        event.registerEntityRenderer(ChangedEntities.LATEX_HUMAN.get(), "default", LatexHumanRenderer.forModelSize(false));
        event.registerEntityRenderer(ChangedEntities.LATEX_HUMAN.get(), "slim", LatexHumanRenderer.forModelSize(true));
    }

    private static <T extends ChangedEntity> void registerHumanoid(EntityRenderersEvent.RegisterRenderers event, EntityType<? extends T> entityType, EntityRendererProvider<T> entityRendererProvider) {
        copyPlayerLayers.add(entityType);
        event.registerEntityRenderer(entityType, entityRendererProvider);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        copyPlayerLayers.clear();

        event.registerEntityRenderer(ChangedEntities.BEHEMOTH_HEAD.get(), BehemothHeadRenderer::new);
        event.registerEntityRenderer(ChangedEntities.BEHEMOTH_HAND_LEFT.get(), BehemothHandLeftRenderer::new);
        event.registerEntityRenderer(ChangedEntities.BEHEMOTH_HAND_RIGHT.get(), BehemothHandRightRenderer::new);

        registerHumanoid(event, ChangedEntities.GAS_WOLF.get(), GasWolfRenderer::new);
        registerHumanoid(event, ChangedEntities.DARK_DRAGON.get(), DarkLatexDragonRenderer::new);
        registerHumanoid(event, ChangedEntities.DARK_LATEX_WOLF_FEMALE.get(), DarkLatexWolfFemaleRenderer::new);
        registerHumanoid(event, ChangedEntities.DARK_LATEX_WOLF_MALE.get(), DarkLatexWolfMaleRenderer::new);
        registerHumanoid(event, ChangedEntities.DARK_LATEX_WOLF_PUP.get(), DarkLatexWolfPupRenderer::new);
        registerHumanoid(event, ChangedEntities.DARK_LATEX_YUFENG.get(), DarkLatexYufengRenderer::new);
        registerHumanoid(event, ChangedEntities.PHAGE_LATEX_WOLF_FEMALE.get(), PhageLatexWolfFemaleRenderer::new);
        registerHumanoid(event, ChangedEntities.PHAGE_LATEX_WOLF_MALE.get(), PhageLatexWolfMaleRenderer::new);
        registerHumanoid(event, ChangedEntities.HEADLESS_KNIGHT.get(), HeadlessKnightRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_ALIEN.get(), LatexAlienRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_BEE.get(), LatexBeeRenderer::new);
        registerHumanoid(event, ChangedEntities.BEIFENG.get(), LatexBeifengRenderer::new);
        registerHumanoid(event, ChangedEntities.BENIGN_LATEX_WOLF.get(), LatexBenignWolfRenderer::new);
        registerHumanoid(event, ChangedEntities.BLUE_LATEX_DRAGON.get(), LatexBlueDragonRenderer::new);
        registerHumanoid(event, ChangedEntities.BLUE_LATEX_WOLF.get(), LatexBlueWolfRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_CROCODILE.get(), LatexCrocodileRenderer::new);
        registerHumanoid(event, ChangedEntities.CRYSTAL_WOLF.get(), LatexCrystalWolfRenderer::new);
        registerHumanoid(event, ChangedEntities.CRYSTAL_WOLF_HORNED.get(), LatexCrystalWolfHornedRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_DEER.get(), LatexDeerRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_FENNEC_FOX.get(), LatexFennecFoxRenderer::new);
        registerHumanoid(event, ChangedEntities.GREEN_LIZARD.get(), GreenLizardRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_HYPNO_CAT.get(), LatexHypnoCatRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_KEON_WOLF.get(), LatexKeonWolfRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_LEAF.get(), LatexLeafRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_MANTA_RAY_FEMALE.get(), LatexMantaRayFemaleRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_MANTA_RAY_MALE.get(), LatexMantaRayMaleRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_MEDUSA_CAT.get(), LatexMedusaCatRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_MIMIC_PLANT.get(), LatexMimicPlantRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_MING_CAT.get(), LatexMingCatRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_MERMAID_SHARK.get(), LatexMermaidSharkRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_MOTH.get(), LatexMothRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_MUTANT_BLOODCELL_WOLF.get(), LatexMutantBloodcellWolfRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_ORCA.get(), LatexOrcaRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_OTTER.get(), LatexOtterRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_PINK_DEER.get(), LatexPinkDeerRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_PINK_WYVERN.get(), LatexPinkWyvernRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_PINK_YUIN_DRAGON.get(), LatexPinkYuinDragonRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_PURPLE_FOX.get(), LatexPurpleFoxRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_RACCOON.get(), LatexRaccoonRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_RED_DRAGON.get(), LatexRedDragonRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_RED_PANDA.get(), LatexRedPandaRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_SHARK.get(), LatexSharkRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_SHARK_FEMALE.get(), BuffLatexSharkFemaleRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_SHARK_MALE.get(), BuffLatexSharkMaleRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_SIREN.get(), LatexSirenRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_SNAKE.get(), LatexSnakeRenderer::new);
        registerHumanoid(event, ChangedEntities.SNIPER_DOG.get(), LatexSniperDogRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_SNOW_LEOPARD_FEMALE.get(), LatexSnowLeopardFemaleRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_SNOW_LEOPARD_MALE.get(), LatexSnowLeopardMaleRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_SQUID_DOG_FEMALE.get(), LatexSquidDogFemaleRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_SQUID_DOG_MALE.get(), LatexSquidDogMaleRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_SQUIRREL.get(), LatexSquirrelRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_STIGER.get(), LatexStigerRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_TIGER_SHARK.get(), LatexTigerSharkRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_TRAFFIC_CONE_DRAGON.get(), LatexTrafficConeDragonRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_TRANSLUCENT_LIZARD.get(), LatexTranslucentLizardRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_WATERMELON_CAT.get(), LatexWatermelonCatRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_WHITE_TIGER.get(), LatexWhiteTigerRenderer::new);
        registerHumanoid(event, ChangedEntities.LATEX_YUIN.get(), LatexYuinRenderer::new);
        registerHumanoid(event, ChangedEntities.WHITE_LATEX_CENTAUR.get(), WhiteLatexCentaurRenderer::new);
        registerHumanoid(event, ChangedEntities.WHITE_LATEX_KNIGHT.get(), WhiteLatexKnightRenderer::new);
        registerHumanoid(event, ChangedEntities.WHITE_LATEX_KNIGHT_FUSION.get(), WhiteLatexKnightFusionRenderer::new);
        registerHumanoid(event, ChangedEntities.WHITE_LATEX_WOLF_FEMALE.get(), WhiteLatexWolfFemaleRenderer::new);
        registerHumanoid(event, ChangedEntities.WHITE_LATEX_WOLF_MALE.get(), WhiteLatexWolfMaleRenderer::new);
        registerHumanoid(event, ChangedEntities.WHITE_WOLF_MALE.get(), WhiteWolfMaleRenderer::new);
        registerHumanoid(event, ChangedEntities.WHITE_WOLF_FEMALE.get(), WhiteWolfFemaleRenderer::new);
        registerHumanoid(event, ChangedEntities.PURE_WHITE_LATEX_WOLF.get(), PureWhiteLatexWolfRenderer::new);

        event.registerEntityRenderer(ChangedEntities.MILK_PUDDING.get(), MilkPuddingRenderer::new);
        event.registerEntityRenderer(ChangedEntities.SHARK.get(), SharkRenderer::new);

        event.registerEntityRenderer(ChangedEntities.SPECIAL_LATEX.get(), SpecialLatexRenderer::new);

        event.registerEntityRenderer(ChangedEntities.ROOMBA.get(), RoombaRenderer::new);

        event.registerEntityRenderer(ChangedEntities.SEAT_ENTITY.get(), SeatEntityRenderer::new);

        event.registerEntityRenderer(ChangedEntities.LATEX_INKBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ChangedEntities.GAS_PARTICLE.get(), GasParticleRenderer::new);
    }
}
