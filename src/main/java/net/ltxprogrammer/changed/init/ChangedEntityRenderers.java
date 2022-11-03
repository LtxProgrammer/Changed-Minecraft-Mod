package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.client.renderer.*;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedEntityRenderers {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ChangedEntities.AEROSOL_LATEX_WOLF.get(), AerosolLatexWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_DRAGON_FEMALE.get(), DarkLatexDragonFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_DRAGON_MALE.get(), DarkLatexDragonMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_WOLF_FEMALE.get(), DarkLatexWolfFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_WOLF_MALE.get(), DarkLatexWolfMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.DARK_LATEX_YUFENG.get(), DarkLatexYufengRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_BEIFENG.get(), LatexBeifengRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_BLUE_DRAGON.get(), LatexBlueDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_CRYSTAL_WOLF.get(), LatexCrystalWolfRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_HYPNO_CAT.get(), LatexHypnoCatRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_LEAF.get(), LatexLeafRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_ORCA.get(), LatexOrcaRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_RED_DRAGON.get(), LatexRedDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SHARK_FEMALE.get(), LatexSharkFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SHARK_MALE.get(), LatexSharkMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SILVER_FOX.get(), LatexSilverFoxRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SNOW_LEOPARD_FEMALE.get(), LatexSnowLeopardFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SNOW_LEOPARD_MALE.get(), LatexSnowLeopardMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_SQUID_DOG.get(), LatexSquidDogRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_TIGER_SHARK.get(), LatexTigerSharkRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_TRAFFIC_CONE_DRAGON.get(), LatexTrafficConeDragonRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_WATERMELON_CAT_FEMALE.get(), LatexWatermelonCatFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LATEX_WATERMELON_CAT_MALE.get(), LatexWatermelonCatMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_CENTAUR.get(), LightLatexCentaurRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_KNIGHT.get(), LightLatexKnightRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_KNIGHT_FUSION.get(), LightLatexKnightFusionRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_WOLF_FEMALE.get(), LightLatexWolfFemaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.LIGHT_LATEX_WOLF_MALE.get(), LightLatexWolfMaleRenderer::new);
        event.registerEntityRenderer(ChangedEntities.MILK_PUDDING.get(), MilkPuddingRenderer::new);
        event.registerEntityRenderer(ChangedEntities.WHITE_LATEX_WOLF.get(), WhiteLatexWolfRenderer::new);

        event.registerEntityRenderer(ChangedEntities.SPECIAL_LATEX.get(), SpecialLatexRenderer::new);

        event.registerEntityRenderer(ChangedEntities.ENTITY_CONTAINER.get(), EntityContainerRenderer::new);
    }
}
