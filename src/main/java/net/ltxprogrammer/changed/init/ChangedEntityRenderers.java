package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.*;
import net.ltxprogrammer.changed.client.renderer.particle.GasParticleRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedEntityRenderers {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        final boolean useNewModels = Changed.config.client.useNewModels.get();

        event.registerEntityRenderer(ChangedEntities.BEHEMOTH_HEAD.get(), BehemothHeadRenderer::new);
        event.registerEntityRenderer(ChangedEntities.BEHEMOTH_HAND_LEFT.get(), BehemothHandLeftRenderer::new);
        event.registerEntityRenderer(ChangedEntities.BEHEMOTH_HAND_RIGHT.get(), BehemothHandRightRenderer::new);

        event.registerEntityRenderer(ChangedEntities.AEROSOL_LATEX_WOLF.get(),
                useNewModels ? AerosolLatexWolfRenderer.Remodel::new : AerosolLatexWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_DRAGON.get(),
                useNewModels ? DarkLatexDragonRenderer.Remodel::new : DarkLatexDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_WOLF_FEMALE.get(),
                useNewModels ? DarkLatexWolfFemaleRenderer.Remodel::new : DarkLatexWolfFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_WOLF_MALE.get(),
                useNewModels ? DarkLatexWolfMaleRenderer.Remodel::new : DarkLatexWolfMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_YUFENG.get(),
                useNewModels ? DarkLatexYufengRenderer.Remodel::new : DarkLatexYufengRenderer::new);
        event.registerEntityRenderer(ChangedEntities.HEADLESS_KNIGHT.get(), HeadlessKnightRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_ALIEN.get(), LatexAlienRenderer::new);
        //event.registerEntityRenderer(ChangedEntities.LATEX_BEE.get(), LatexBeeRenderer::new); // TODO: Uncomment when appropriate
        event.registerEntityRenderer(ChangedEntities.LATEX_BEIFENG.get(),
                useNewModels ? LatexBeifengRenderer.Remodel::new : LatexBeifengRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_BENIGN_WOLF.get(), LatexBenignWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_BLUE_DRAGON.get(), LatexBlueDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_BLUE_WOLF.get(),
                useNewModels ? LatexBlueWolfRenderer.Remodel::new : LatexBlueWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_CROCODILE.get(), LatexCrocodileRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_CRYSTAL_WOLF.get(), LatexCrystalWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_CRYSTAL_WOLF_HORNED.get(), LatexCrystalWolfHornedRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_DEER.get(), LatexDeerRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_HYPNO_CAT.get(),
                useNewModels ? LatexHypnoCatRenderer.Remodel::new : LatexHypnoCatRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_LEAF.get(), LatexLeafRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MANTA_RAY_FEMALE.get(), LatexMantaRayFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MANTA_RAY_MALE.get(), LatexMantaRayMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MEDUSA_CAT.get(), LatexMedusaCatRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MIMIC_PLANT.get(), LatexMimicPlantRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MERMAID_SHARK.get(), LatexMermaidSharkRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MOTH.get(), LatexMothRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_ORCA.get(), LatexOrcaRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_OTTER.get(), LatexOtterRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_PINK_DEER.get(), LatexPinkDeerRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_PINK_WYVERN.get(), LatexPinkWyvernRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_PINK_YUIN_DRAGON.get(), LatexPinkYuinDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_PURPLE_FOX.get(),
                useNewModels ? LatexPurpleFoxRenderer.Remodel::new : LatexPurpleFoxRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_RACCOON.get(), LatexRaccoonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_RED_DRAGON.get(), LatexRedDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_RED_PANDA.get(),
                useNewModels ? LatexRedPandaRenderer.Remodel::new : LatexRedPandaRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SHARK.get(),
                useNewModels ? LatexSharkRenderer.Remodel::new : LatexSharkRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SHARK_FEMALE.get(),
                useNewModels ? LatexSharkFemaleRenderer.Remodel::new : LatexSharkFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SHARK_MALE.get(),
                useNewModels ? LatexSharkMaleRenderer.Remodel::new : LatexSharkMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SILVER_FOX.get(), LatexSilverFoxRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SIREN.get(), LatexSirenRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SNAKE.get(), LatexSnakeRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SNIPER_DOG.get(), LatexSniperDogRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SNOW_LEOPARD_FEMALE.get(),
                useNewModels ? LatexSnowLeopardFemaleRenderer.Remodel::new : LatexSnowLeopardFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SNOW_LEOPARD_MALE.get(),
                useNewModels ? LatexSnowLeopardMaleRenderer.Remodel::new : LatexSnowLeopardMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SQUID_DOG_FEMALE.get(), LatexSquidDogFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SQUID_DOG_MALE.get(), LatexSquidDogMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SQUIRREL.get(), LatexSquirrelRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_STIGER.get(), LatexStigerRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_TIGER_SHARK.get(),
                useNewModels ? LatexTigerSharkRenderer.Remodel::new : LatexTigerSharkRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_TRAFFIC_CONE_DRAGON.get(), LatexTrafficConeDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_TRANSLUCENT_LIZARD.get(), LatexTranslucentLizardRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_WATERMELON_CAT.get(),
                useNewModels ? LatexWatermelonCatRenderer.Remodel::new : LatexWatermelonCatRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_WHITE_TIGER.get(), LatexWhiteTigerRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_YUIN.get(), LatexYuinRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_CENTAUR.get(), LightLatexCentaurRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_KNIGHT.get(), LightLatexKnightRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_KNIGHT_FUSION.get(), LightLatexKnightFusionRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_WOLF_FEMALE.get(),
                useNewModels ? LightLatexWolfFemaleRenderer.Remodel::new : LightLatexWolfFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_WOLF_MALE.get(),
                useNewModels ? LightLatexWolfMaleRenderer.Remodel::new : LightLatexWolfMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_WOLF_ORGANIC.get(), LightLatexWolfOrganicRenderer::new);
        event.registerEntityRenderer(ChangedEntities.MILK_PUDDING.get(), MilkPuddingRenderer::new);
        event.registerEntityRenderer(ChangedEntities.SHARK.get(), SharkRenderer::new);
        event.registerEntityRenderer(ChangedEntities.WHITE_LATEX_WOLF.get(),
                useNewModels ? WhiteLatexWolfRenderer.Remodel::new : WhiteLatexWolfRenderer::new);

        event.registerEntityRenderer(ChangedEntities.SPECIAL_LATEX.get(), SpecialLatexRenderer::new);

        event.registerEntityRenderer(ChangedEntities.ENTITY_CONTAINER.get(), EntityContainerRenderer::new);

        event.registerEntityRenderer(ChangedEntities.LATEX_INKBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ChangedEntities.GAS_PARTICLE.get(), GasParticleRenderer::new);
    }
}
