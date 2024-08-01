package net.ltxprogrammer.changed.init;

import com.google.common.collect.ImmutableMap;
import net.ltxprogrammer.changed.client.RegisterComplexRenderersEvent;
import net.ltxprogrammer.changed.client.renderer.*;
import net.ltxprogrammer.changed.client.renderer.particle.GasParticleRenderer;
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

import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedEntityRenderers {
    private static Map<EntityType<? extends ComplexRenderer>, Map<String, EntityRenderer<? extends ComplexRenderer>>> complexRenderers = ImmutableMap.of();

    public static <T extends Entity> Optional<EntityRenderer<? super T>> getComplexRenderer(T entity) {
        if (!(entity instanceof ComplexRenderer complexRenderer))
            return Optional.empty();

        final var renderers = Optional.ofNullable(complexRenderers.get(entity.getType()));
        return renderers.map(map -> (EntityRenderer<? super T>)map.getOrDefault(complexRenderer.getModelName(), map.get("default")));
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

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ChangedEntities.BEHEMOTH_HEAD.get(), BehemothHeadRenderer::new);
        event.registerEntityRenderer(ChangedEntities.BEHEMOTH_HAND_LEFT.get(), BehemothHandLeftRenderer::new);
        event.registerEntityRenderer(ChangedEntities.BEHEMOTH_HAND_RIGHT.get(), BehemothHandRightRenderer::new);

        event.registerEntityRenderer(ChangedEntities.GAS_WOLF.get(), GasWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_DRAGON.get(), DarkLatexDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_WOLF_FEMALE.get(), DarkLatexWolfFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_WOLF_MALE.get(), DarkLatexWolfMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_WOLF_PUP.get(), DarkLatexWolfPupRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_YUFENG.get(), DarkLatexYufengRenderer::new);
        event.registerEntityRenderer(ChangedEntities.PHAGE_LATEX_WOLF_FEMALE.get(), PhageLatexWolfFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.PHAGE_LATEX_WOLF_MALE.get(), PhageLatexWolfMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.HEADLESS_KNIGHT.get(), HeadlessKnightRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_ALIEN.get(), LatexAlienRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_BEE.get(), LatexBeeRenderer::new);
        event.registerEntityRenderer(ChangedEntities.BEIFENG.get(), LatexBeifengRenderer::new);
        event.registerEntityRenderer(ChangedEntities.BENIGN_LATEX_WOLF.get(), LatexBenignWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.BLUE_LATEX_DRAGON.get(), LatexBlueDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.BLUE_LATEX_WOLF.get(), LatexBlueWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_CROCODILE.get(), LatexCrocodileRenderer::new);
        event.registerEntityRenderer(ChangedEntities.CRYSTAL_WOLF.get(), LatexCrystalWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.CRYSTAL_WOLF_HORNED.get(), LatexCrystalWolfHornedRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_DEER.get(), LatexDeerRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_FENNEC_FOX.get(), LatexFennecFoxRenderer::new);
        event.registerEntityRenderer(ChangedEntities.GREEN_LIZARD.get(), GreenLizardRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_HYPNO_CAT.get(), LatexHypnoCatRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_KEON_WOLF.get(), LatexKeonWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_LEAF.get(), LatexLeafRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MANTA_RAY_FEMALE.get(), LatexMantaRayFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MANTA_RAY_MALE.get(), LatexMantaRayMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MEDUSA_CAT.get(), LatexMedusaCatRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MIMIC_PLANT.get(), LatexMimicPlantRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MING_CAT.get(), LatexMingCatRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MERMAID_SHARK.get(), LatexMermaidSharkRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MOTH.get(), LatexMothRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MUTANT_BLOODCELL_WOLF.get(), LatexMutantBloodcellWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_ORCA.get(), LatexOrcaRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_OTTER.get(), LatexOtterRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_PINK_DEER.get(), LatexPinkDeerRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_PINK_WYVERN.get(), LatexPinkWyvernRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_PINK_YUIN_DRAGON.get(), LatexPinkYuinDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_PURPLE_FOX.get(), LatexPurpleFoxRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_RACCOON.get(), LatexRaccoonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_RED_DRAGON.get(), LatexRedDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_RED_PANDA.get(), LatexRedPandaRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SHARK.get(), LatexSharkRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SHARK_FEMALE.get(),
                /*useNewModels ? LatexSharkFemaleRenderer.Remodel::new :*/ LatexSharkFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SHARK_MALE.get(),
                /*useNewModels ? LatexSharkMaleRenderer.Remodel::new :*/ LatexSharkMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SIREN.get(), LatexSirenRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SNAKE.get(), LatexSnakeRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SNIPER_DOG.get(), LatexSniperDogRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SNOW_LEOPARD_FEMALE.get(), LatexSnowLeopardFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SNOW_LEOPARD_MALE.get(), LatexSnowLeopardMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SQUID_DOG_FEMALE.get(), LatexSquidDogFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SQUID_DOG_MALE.get(), LatexSquidDogMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SQUIRREL.get(), LatexSquirrelRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_STIGER.get(), LatexStigerRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_TIGER_SHARK.get(), LatexTigerSharkRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_TRAFFIC_CONE_DRAGON.get(), LatexTrafficConeDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_TRANSLUCENT_LIZARD.get(), LatexTranslucentLizardRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_WATERMELON_CAT.get(), LatexWatermelonCatRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_WHITE_TIGER.get(), LatexWhiteTigerRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_YUIN.get(), LatexYuinRenderer::new);
        event.registerEntityRenderer(ChangedEntities.WHITE_LATEX_CENTAUR.get(), WhiteLatexCentaurRenderer::new);
        event.registerEntityRenderer(ChangedEntities.WHITE_LATEX_KNIGHT.get(), WhiteLatexKnightRenderer::new);
        event.registerEntityRenderer(ChangedEntities.WHITE_LATEX_KNIGHT_FUSION.get(), WhiteLatexKnightFusionRenderer::new);
        event.registerEntityRenderer(ChangedEntities.WHITE_LATEX_WOLF_FEMALE.get(), WhiteLatexWolfFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.WHITE_LATEX_WOLF_MALE.get(), WhiteLatexWolfMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.WHITE_WOLF_MALE.get(), WhiteWolfMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.WHITE_WOLF_FEMALE.get(), WhiteWolfFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.MILK_PUDDING.get(), MilkPuddingRenderer::new);
        event.registerEntityRenderer(ChangedEntities.SHARK.get(), SharkRenderer::new);
        event.registerEntityRenderer(ChangedEntities.PURE_WHITE_LATEX_WOLF.get(), PureWhiteLatexWolfRenderer::new);

        event.registerEntityRenderer(ChangedEntities.SPECIAL_LATEX.get(), SpecialLatexRenderer::new);

        event.registerEntityRenderer(ChangedEntities.ROOMBA.get(), RoombaRenderer::new);

        event.registerEntityRenderer(ChangedEntities.SEAT_ENTITY.get(), SeatEntityRenderer::new);

        event.registerEntityRenderer(ChangedEntities.LATEX_INKBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ChangedEntities.GAS_PARTICLE.get(), GasParticleRenderer::new);
    }
}
