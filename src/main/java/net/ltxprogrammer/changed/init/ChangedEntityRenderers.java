package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.client.renderer.*;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedEntityRenderers {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ChangedEntities.BEHEMOTH_HEAD.get(), BehemothHeadRenderer::new);
        event.registerEntityRenderer(ChangedEntities.BEHEMOTH_HAND_LEFT.get(), BehemothHandLeftRenderer::new);
        event.registerEntityRenderer(ChangedEntities.BEHEMOTH_HAND_RIGHT.get(), BehemothHandRightRenderer::new);

        event.registerEntityRenderer(ChangedEntities.AEROSOL_LATEX_WOLF.get(), AerosolLatexWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_DRAGON.get(), DarkLatexDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_WOLF_FEMALE.get(), DarkLatexWolfFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_WOLF_MALE.get(), DarkLatexWolfMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_YUFENG.get(), DarkLatexYufengRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_BEIFENG.get(), LatexBeifengRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_BENIGN_WOLF.get(), LatexBenignWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_BLUE_DRAGON.get(), LatexBlueDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_BLUE_WOLF.get(), LatexBlueWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_CROCODILE.get(), LatexCrocodileRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_CRYSTAL_WOLF.get(), LatexCrystalWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_DEER.get(), LatexDeerRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_HYPNO_CAT.get(), LatexHypnoCatRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_LEAF.get(), LatexLeafRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MANTA_RAY_FEMALE.get(), LatexMantaRayFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MANTA_RAY_MALE.get(), LatexMantaRayMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MIMIC_PLANT.get(), LatexMimicPlantRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_MERMAID_SHARK.get(), LatexMermaidSharkRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_ORCA.get(), LatexOrcaRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_PINK_WYVERN.get(), LatexPinkWyvernRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_PURPLE_FOX.get(), LatexPurpleFoxRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_RACCOON.get(), LatexRaccoonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_RED_DRAGON.get(), LatexRedDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_RED_PANDA.get(), LatexRedPandaRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SHARK.get(), LatexSharkRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SHARK_FEMALE.get(), LatexSharkFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SHARK_MALE.get(), LatexSharkMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SILVER_FOX.get(), LatexSilverFoxRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SIREN.get(), LatexSirenRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SNIPER_DOG.get(), LatexSniperDogRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SNOW_LEOPARD_FEMALE.get(), LatexSnowLeopardFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SNOW_LEOPARD_MALE.get(), LatexSnowLeopardMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SQUID_DOG.get(), LatexSquidDogRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_STIGER.get(), LatexStigerRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_TIGER_SHARK.get(), LatexTigerSharkRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_TRAFFIC_CONE_DRAGON.get(), LatexTrafficConeDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_TRANSLUCENT_LIZARD.get(), LatexTranslucentLizardRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_WATERMELON_CAT.get(), LatexWatermelonCatRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_YUIN.get(), LatexYuinRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_CENTAUR.get(), LightLatexCentaurRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_KNIGHT.get(), LightLatexKnightRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_KNIGHT_FUSION.get(), LightLatexKnightFusionRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_WOLF_FEMALE.get(), LightLatexWolfFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_WOLF_MALE.get(), LightLatexWolfMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_WOLF_ORGANIC.get(), LightLatexWolfOrganicRenderer::new);
        event.registerEntityRenderer(ChangedEntities.MILK_PUDDING.get(), MilkPuddingRenderer::new);
        event.registerEntityRenderer(ChangedEntities.WHITE_LATEX_WOLF.get(), WhiteLatexWolfRenderer::new);

        event.registerEntityRenderer(ChangedEntities.SPECIAL_LATEX.get(), SpecialLatexRenderer::new);

        event.registerEntityRenderer(ChangedEntities.ENTITY_CONTAINER.get(), EntityContainerRenderer::new);

        event.registerEntityRenderer(ChangedEntities.LATEX_INKBALL.get(), ThrownItemRenderer::new);
    }
}
