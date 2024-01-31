package net.ltxprogrammer.changed.init;

import com.google.common.collect.ImmutableMap;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.*;
import net.ltxprogrammer.changed.client.renderer.particle.GasParticleRenderer;
import net.ltxprogrammer.changed.entity.beast.DarkLatexWolfPartial;
import net.ltxprogrammer.changed.entity.beast.LatexHuman;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedEntityRenderers {
    private static Map<String, EntityRenderer<? extends DarkLatexWolfPartial>> partialRenderers = ImmutableMap.of();
    private static Map<String, EntityRenderer<? extends LatexHuman>> humanRenderers = ImmutableMap.of();

    @Nullable
    public static <T extends Entity> EntityRenderer<? super T> getRenderer(T entity) {
        if (entity instanceof DarkLatexWolfPartial partial) {
            String s = partial.getModelName();
            EntityRenderer<? extends DarkLatexWolfPartial> entityrenderer = partialRenderers.get(s);
            return (EntityRenderer) (entityrenderer != null ? entityrenderer : partialRenderers.get("default"));
        } else if (entity instanceof LatexHuman human) {
            String s = human.getModelName();
            EntityRenderer<? extends LatexHuman> entityrenderer = humanRenderers.get(s);
            return (EntityRenderer) (entityrenderer != null ? entityrenderer : humanRenderers.get("default"));
        }

        return null; // Default to registered renderer
    }

    public static void registerComplexRenderers(EntityRendererProvider.Context context) {
        partialRenderers = new ImmutableMap.Builder<String, EntityRenderer<? extends DarkLatexWolfPartial>>()
                .put("default", new DarkLatexWolfPartialRenderer(context, false))
                .put("slim", new DarkLatexWolfPartialRenderer(context, true)).build();
        humanRenderers = new ImmutableMap.Builder<String, EntityRenderer<? extends LatexHuman>>()
                .put("default", new LatexHumanRenderer(context, false))
                .put("slim", new LatexHumanRenderer(context, true)).build();
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        final boolean useNewModels = Changed.config.client.useNewModels.get();

        event.registerEntityRenderer(ChangedEntities.BEHEMOTH_HEAD.get(), BehemothHeadRenderer::new);
        event.registerEntityRenderer(ChangedEntities.BEHEMOTH_HAND_LEFT.get(), BehemothHandLeftRenderer::new);
        event.registerEntityRenderer(ChangedEntities.BEHEMOTH_HAND_RIGHT.get(), BehemothHandRightRenderer::new);

        event.registerEntityRenderer(ChangedEntities.AEROSOL_LATEX_WOLF.get(), AerosolLatexWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_DRAGON.get(), DarkLatexDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_WOLF_FEMALE.get(), DarkLatexWolfFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_WOLF_MALE.get(), DarkLatexWolfMaleRenderer::new);
        //event.registerEntityRenderer(ChangedEntities.DARK_LATEX_WOLF_PARTIAL.get(), DarkLatexWolfPartialRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_PUP.get(), DarkLatexPupRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_YUFENG.get(), DarkLatexYufengRenderer::new);
        event.registerEntityRenderer(ChangedEntities.HEADLESS_KNIGHT.get(), HeadlessKnightRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_ALIEN.get(), LatexAlienRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_BEE.get(), LatexBeeRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_BEIFENG.get(), LatexBeifengRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_BENIGN_WOLF.get(), LatexBenignWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_BLUE_DRAGON.get(), LatexBlueDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_BLUE_WOLF.get(), LatexBlueWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_CROCODILE.get(), LatexCrocodileRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_CRYSTAL_WOLF.get(), LatexCrystalWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_CRYSTAL_WOLF_HORNED.get(), LatexCrystalWolfHornedRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_DEER.get(), LatexDeerRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_FENNEC_FOX.get(), LatexFennecFoxRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_HYPNO_CAT.get(), LatexHypnoCatRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_KEON_WOLF.get(), LatexKeonWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_LEAF.get(), LatexLeafRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MANTA_RAY_FEMALE.get(),
                useNewModels ? LatexMantaRayFemaleRenderer.Remodel::new : LatexMantaRayFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MANTA_RAY_MALE.get(),
                useNewModels ? LatexMantaRayMaleRenderer.Remodel::new : LatexMantaRayMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MEDUSA_CAT.get(), LatexMedusaCatRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MIMIC_PLANT.get(), LatexMimicPlantRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MING_CAT.get(), LatexMingCatRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MERMAID_SHARK.get(),
                useNewModels ? LatexMermaidSharkRenderer.Remodel::new : LatexMermaidSharkRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MOTH.get(), LatexMothRenderer::new);
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
        event.registerEntityRenderer(ChangedEntities.LATEX_SIREN.get(),
                useNewModels ? LatexSirenRenderer.Remodel::new : LatexSirenRenderer::new);
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
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_CENTAUR.get(), LightLatexCentaurRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_KNIGHT.get(), LightLatexKnightRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_KNIGHT_FUSION.get(), LightLatexKnightFusionRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_WOLF_FEMALE.get(), LightLatexWolfFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_WOLF_MALE.get(), LightLatexWolfMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_WOLF_ORGANIC.get(), LightLatexWolfOrganicRenderer::new);
        event.registerEntityRenderer(ChangedEntities.MILK_PUDDING.get(), MilkPuddingRenderer::new);
        event.registerEntityRenderer(ChangedEntities.SHARK.get(), SharkRenderer::new);
        event.registerEntityRenderer(ChangedEntities.WHITE_LATEX_WOLF.get(), WhiteLatexWolfRenderer::new);

        event.registerEntityRenderer(ChangedEntities.SPECIAL_LATEX.get(), SpecialLatexRenderer::new);

        event.registerEntityRenderer(ChangedEntities.SEAT_ENTITY.get(), SeatEntityRenderer::new);

        event.registerEntityRenderer(ChangedEntities.LATEX_INKBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ChangedEntities.GAS_PARTICLE.get(), GasParticleRenderer::new);
    }
}
