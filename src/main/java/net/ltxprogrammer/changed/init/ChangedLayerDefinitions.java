package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.blockentity.LatexContainerRenderer;
import net.ltxprogrammer.changed.client.renderer.blockentity.PillowRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.*;
import net.ltxprogrammer.changed.client.renderer.model.armor.*;
import net.ltxprogrammer.changed.client.renderer.model.hair.HairRemodel;
import net.ltxprogrammer.changed.client.tfanimations.TransfurHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedLayerDefinitions {
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        final boolean useNewModels = Changed.config.client.useNewModels.get();

        event.registerLayerDefinition(CustomEyesLayer.HEAD, CustomEyesLayer::createHead);
        event.registerLayerDefinition(DarkLatexMaskModel.LAYER_LOCATION, DarkLatexMaskModel::createMask);
        event.registerLayerDefinition(TransfurCapeLayer.LAYER_LOCATION, TransfurCapeLayer::createCape);
        event.registerLayerDefinition(TransfurCapeLayer.LAYER_LOCATION_SHORT, TransfurCapeLayer::createShortCape);
        event.registerLayerDefinition(GasMaskModel.LAYER_LOCATION, GasMaskModel::createMask);
        event.registerLayerDefinition(GasMaskModel.LAYER_LOCATION_SNOUTED, GasMaskModel::createMaskSnouted);
        event.registerLayerDefinition(GasMaskModel.LAYER_LOCATION_LARGE_SNOUTED, GasMaskModel::createMaskLargeSnouted);
        event.registerLayerDefinition(DuctPlayerModel.LAYER_LOCATION, DuctPlayerModel::createRoot);
        event.registerLayerDefinition(TaurChestPackModel.LAYER_LOCATION, TaurChestPackModel::createPack);

        event.registerLayerDefinition(TransfurHelper.TRANSFUR_HELPER, TransfurHelper::createBodyLayer);

        event.registerLayerDefinition(BehemothHeadModel.LAYER_LOCATION, BehemothHeadModel::createBodyLayer);
        event.registerLayerDefinition(BehemothHandLeftModel.LAYER_LOCATION, BehemothHandLeftModel::createBodyLayer);
        event.registerLayerDefinition(BehemothHandRightModel.LAYER_LOCATION, BehemothHandRightModel::createBodyLayer);

        event.registerLayerDefinition(GasWolfModel.LAYER_LOCATION, GasWolfModel::createBodyLayer);
        event.registerLayerDefinition(DarkLatexDragonModel.LAYER_LOCATION, DarkLatexDragonModel::createBodyLayer);
        event.registerLayerDefinition(DarkLatexWolfFemaleModel.LAYER_LOCATION, DarkLatexWolfFemaleModel::createBodyLayer);
        event.registerLayerDefinition(DarkLatexWolfMaleModel.LAYER_LOCATION, DarkLatexWolfMaleModel::createBodyLayer);
        event.registerLayerDefinition(DarkLatexWolfPartialModel.LAYER_LOCATION_HUMAN, () -> DarkLatexWolfPartialModel.createHumanLayer(false));
        event.registerLayerDefinition(DarkLatexWolfPartialModel.LAYER_LOCATION_LATEX, () -> DarkLatexWolfPartialModel.createLatexLayer(false));
        event.registerLayerDefinition(DarkLatexWolfPartialModel.LAYER_LOCATION_HUMAN_SLIM, () -> DarkLatexWolfPartialModel.createHumanLayer(true));
        event.registerLayerDefinition(DarkLatexWolfPartialModel.LAYER_LOCATION_LATEX_SLIM, () -> DarkLatexWolfPartialModel.createLatexLayer(true));
        event.registerLayerDefinition(DarkLatexWolfPupModel.LAYER_LOCATION, DarkLatexWolfPupModel::createBodyLayer);
        event.registerLayerDefinition(DarkLatexYufengModel.LAYER_LOCATION, DarkLatexYufengModel::createBodyLayer);
        event.registerLayerDefinition(PhageLatexWolfFemaleModel.LAYER_LOCATION, PhageLatexWolfFemaleModel::createBodyLayer);
        event.registerLayerDefinition(PhageLatexWolfMaleModel.LAYER_LOCATION, PhageLatexWolfMaleModel::createBodyLayer);
        event.registerLayerDefinition(HeadlessKnightModel.LAYER_LOCATION, HeadlessKnightModel::createBodyLayer);
        event.registerLayerDefinition(LatexAlienModel.LAYER_LOCATION, LatexAlienModel::createBodyLayer);
        event.registerLayerDefinition(LatexBeeModel.LAYER_LOCATION, LatexBeeModel::createBodyLayer);
        event.registerLayerDefinition(LatexBeifengModel.LAYER_LOCATION, LatexBeifengModel::createBodyLayer);
        event.registerLayerDefinition(LatexBenignWolfModel.LAYER_LOCATION, LatexBenignWolfModel::createBodyLayer);
        event.registerLayerDefinition(LatexBlueDragonModel.LAYER_LOCATION, LatexBlueDragonModel::createBodyLayer);
        event.registerLayerDefinition(LatexBlueWolfModel.LAYER_LOCATION, LatexBlueWolfModel::createBodyLayer);
        event.registerLayerDefinition(LatexCrocodileModel.LAYER_LOCATION, LatexCrocodileModel::createBodyLayer);
        event.registerLayerDefinition(LatexCrystalWolfModel.LAYER_LOCATION, LatexCrystalWolfModel::createBodyLayer);
        event.registerLayerDefinition(LatexCrystalWolfHornedModel.LAYER_LOCATION, LatexCrystalWolfHornedModel::createBodyLayer);
        event.registerLayerDefinition(LatexDeerModel.LAYER_LOCATION, LatexDeerModel::createBodyLayer);
        event.registerLayerDefinition(LatexFennecFoxModel.LAYER_LOCATION, LatexFennecFoxModel::createBodyLayer);
        event.registerLayerDefinition(GreenLizardModel.LAYER_LOCATION, GreenLizardModel::createBodyLayer);
        event.registerLayerDefinition(LatexHumanModel.LAYER_LOCATION, () -> LatexHumanModel.createBodyLayer(false));
        event.registerLayerDefinition(LatexHumanModel.LAYER_LOCATION_SLIM, () -> LatexHumanModel.createBodyLayer(true));
        event.registerLayerDefinition(LatexHypnoCatModel.LAYER_LOCATION, LatexHypnoCatModel::createBodyLayer);
        event.registerLayerDefinition(LatexKeonWolfModel.LAYER_LOCATION, LatexKeonWolfModel::createBodyLayer);
        event.registerLayerDefinition(LatexLeafModel.LAYER_LOCATION, LatexLeafModel::createBodyLayer);
        event.registerLayerDefinition(LatexMantaRayFemaleModel.LAYER_LOCATION, LatexMantaRayFemaleModel::createBodyLayer);
        event.registerLayerDefinition(LatexMantaRayMaleModel.LAYER_LOCATION, LatexMantaRayMaleModel::createBodyLayer);
        event.registerLayerDefinition(LatexMedusaCatModel.LAYER_LOCATION, LatexMedusaCatModel::createBodyLayer);
        event.registerLayerDefinition(LatexMermaidSharkModel.LAYER_LOCATION, LatexMermaidSharkModel::createBodyLayer);
        event.registerLayerDefinition(LatexMimicPlantModel.LAYER_LOCATION, LatexMimicPlantModel::createBodyLayer);
        event.registerLayerDefinition(LatexMingCatModel.LAYER_LOCATION, LatexMingCatModel::createBodyLayer);
        event.registerLayerDefinition(LatexMothModel.LAYER_LOCATION, LatexMothModel::createBodyLayer);
        event.registerLayerDefinition(LatexMutantBloodcellWolfModel.LAYER_LOCATION, LatexMutantBloodcellWolfModel::createBodyLayer);
        event.registerLayerDefinition(LatexOrcaModel.LAYER_LOCATION, LatexOrcaModel::createBodyLayer);
        event.registerLayerDefinition(LatexOtterModel.LAYER_LOCATION, LatexOtterModel::createBodyLayer);
        event.registerLayerDefinition(LatexPinkDeerModel.LAYER_LOCATION, LatexPinkDeerModel::createBodyLayer);
        event.registerLayerDefinition(LatexPinkWyvernModel.LAYER_LOCATION, LatexPinkWyvernModel::createBodyLayer);
        event.registerLayerDefinition(LatexPinkYuinDragonModel.LAYER_LOCATION, LatexPinkYuinDragonModel::createBodyLayer);
        event.registerLayerDefinition(LatexPurpleFoxModel.LAYER_LOCATION, LatexPurpleFoxModel::createBodyLayer);
        event.registerLayerDefinition(LatexRaccoonModel.LAYER_LOCATION, LatexRaccoonModel::createBodyLayer);
        event.registerLayerDefinition(LatexRedDragonModel.LAYER_LOCATION, LatexRedDragonModel::createBodyLayer);
        event.registerLayerDefinition(LatexRedPandaModel.LAYER_LOCATION, LatexRedPandaModel::createBodyLayer);
        event.registerLayerDefinition(LatexSharkModel.LAYER_LOCATION, LatexSharkModel::createBodyLayer);
        event.registerLayerDefinition(BuffLatexSharkFemaleModel.LAYER_LOCATION, BuffLatexSharkFemaleModel::createBodyLayer);
        event.registerLayerDefinition(BuffLatexSharkMaleModel.LAYER_LOCATION, BuffLatexSharkMaleModel::createBodyLayer);
        event.registerLayerDefinition(LatexSirenModel.LAYER_LOCATION, LatexSirenModel::createBodyLayer);
        event.registerLayerDefinition(LatexSnakeModel.LAYER_LOCATION, LatexSnakeModel::createBodyLayer);
        event.registerLayerDefinition(LatexSniperDogModel.LAYER_LOCATION, LatexSniperDogModel::createBodyLayer);
        event.registerLayerDefinition(LatexSnowLeopardFemaleModel.LAYER_LOCATION, LatexSnowLeopardFemaleModel::createBodyLayer);
        event.registerLayerDefinition(LatexSnowLeopardMaleModel.LAYER_LOCATION, LatexSnowLeopardMaleModel::createBodyLayer);
        event.registerLayerDefinition(LatexSquidDogFemaleModel.LAYER_LOCATION, LatexSquidDogFemaleModel::createBodyLayer);
        event.registerLayerDefinition(LatexSquidDogMaleModel.LAYER_LOCATION, LatexSquidDogMaleModel::createBodyLayer);
        event.registerLayerDefinition(LatexSquirrelModel.LAYER_LOCATION, LatexSquirrelModel::createBodyLayer);
        event.registerLayerDefinition(LatexStigerModel.LAYER_LOCATION, LatexStigerModel::createBodyLayer);
        event.registerLayerDefinition(LatexTigerSharkModel.LAYER_LOCATION, LatexTigerSharkModel::createBodyLayer);
        event.registerLayerDefinition(LatexTrafficConeDragonModel.LAYER_LOCATION, LatexTrafficConeDragonModel::createBodyLayer);
        event.registerLayerDefinition(LatexTranslucentLizardModel.LAYER_LOCATION, LatexTranslucentLizardModel::createBodyLayer);
        event.registerLayerDefinition(LatexWatermelonCatModel.LAYER_LOCATION, LatexWatermelonCatModel::createBodyLayer);
        event.registerLayerDefinition(LatexWhiteTigerModel.LAYER_LOCATION, LatexWhiteTigerModel::createBodyLayer);
        event.registerLayerDefinition(LatexYuinModel.LAYER_LOCATION, LatexYuinModel::createBodyLayer);
        event.registerLayerDefinition(WhiteLatexCentaurModel.LAYER_LOCATION, WhiteLatexCentaurModel::createBodyLayer);
        event.registerLayerDefinition(WhiteLatexKnightModel.LAYER_LOCATION, WhiteLatexKnightModel::createBodyLayer);
        event.registerLayerDefinition(WhiteLatexKnightFusionModel.LAYER_LOCATION, WhiteLatexKnightFusionModel::createBodyLayer);
        event.registerLayerDefinition(WhiteLatexWolfFemaleModel.LAYER_LOCATION, WhiteLatexWolfFemaleModel::createBodyLayer);
        event.registerLayerDefinition(WhiteLatexWolfMaleModel.LAYER_LOCATION, WhiteLatexWolfMaleModel::createBodyLayer);
        event.registerLayerDefinition(MilkPuddingModel.LAYER_LOCATION, MilkPuddingModel::createBodyLayer);
        event.registerLayerDefinition(SharkModel.LAYER_LOCATION, SharkModel::createBodyLayer);
        event.registerLayerDefinition(PureWhiteLatexWolfModel.LAYER_LOCATION, PureWhiteLatexWolfModel::createBodyLayer);

        event.registerLayerDefinition(ArmorNoneModel.INNER_ARMOR, () -> ArmorNoneModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorNoneModel.OUTER_ARMOR, () -> ArmorNoneModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorNoTailModel.INNER_ARMOR, () -> ArmorNoTailModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorNoTailModel.OUTER_ARMOR, () -> ArmorNoTailModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorUpperBodyModel.INNER_ARMOR, () -> ArmorUpperBodyModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorUpperBodyModel.OUTER_ARMOR, () -> ArmorUpperBodyModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorSnakeAbdomenModel.INNER_ARMOR, () -> ArmorSnakeAbdomenModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorSnakeAbdomenModel.OUTER_ARMOR, () -> ArmorSnakeAbdomenModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorHeadlessKnightModel.INNER_ARMOR, () -> ArmorHeadlessKnightModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorHeadlessKnightModel.OUTER_ARMOR, () -> ArmorHeadlessKnightModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexAlienModel.INNER_ARMOR, () -> ArmorLatexAlienModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexAlienModel.OUTER_ARMOR, () -> ArmorLatexAlienModel.createArmorLayer(ArmorModel.OUTER));

        // v --- ARMOR / ENTITY --- ^

        event.registerLayerDefinition(ArmorLatexMaleWolfModel.INNER_ARMOR, () -> ArmorLatexMaleWolfModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexMaleWolfModel.OUTER_ARMOR, () -> ArmorLatexMaleWolfModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexFemaleWolfModel.INNER_ARMOR, () -> ArmorLatexFemaleWolfModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexFemaleWolfModel.OUTER_ARMOR, () -> ArmorLatexFemaleWolfModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorFemaleMantaRayUpperBodyModel.INNER_ARMOR, () -> ArmorFemaleMantaRayUpperBodyModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorFemaleMantaRayUpperBodyModel.OUTER_ARMOR, () -> ArmorFemaleMantaRayUpperBodyModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorFemaleMantaRayAbdomenModel.INNER_ARMOR, () -> ArmorFemaleMantaRayAbdomenModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorFemaleMantaRayAbdomenModel.OUTER_ARMOR, () -> ArmorFemaleMantaRayAbdomenModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorMermaidSharkAbdomenModel.INNER_ARMOR, () -> ArmorMermaidSharkAbdomenModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorMermaidSharkAbdomenModel.OUTER_ARMOR, () -> ArmorMermaidSharkAbdomenModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorMermaidSharkUpperBodyModel.INNER_ARMOR, () -> ArmorMermaidSharkUpperBodyModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorMermaidSharkUpperBodyModel.OUTER_ARMOR, () -> ArmorMermaidSharkUpperBodyModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorSirenUpperBodyModel.INNER_ARMOR, () -> ArmorSirenUpperBodyModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorSirenUpperBodyModel.OUTER_ARMOR, () -> ArmorSirenUpperBodyModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorSirenAbdomenModel.INNER_ARMOR, () -> ArmorSirenAbdomenModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorSirenAbdomenModel.OUTER_ARMOR, () -> ArmorSirenAbdomenModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexBeeModel.INNER_ARMOR, () -> ArmorLatexBeeModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexBeeModel.OUTER_ARMOR, () -> ArmorLatexBeeModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexDeerModel.INNER_ARMOR, () -> ArmorLatexDeerModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexDeerModel.OUTER_ARMOR, () -> ArmorLatexDeerModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexMaleCatModel.INNER_ARMOR, () -> ArmorLatexMaleCatModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexMaleCatModel.OUTER_ARMOR, () -> ArmorLatexMaleCatModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexFemaleCatModel.INNER_ARMOR, () -> ArmorLatexFemaleCatModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexFemaleCatModel.OUTER_ARMOR, () -> ArmorLatexFemaleCatModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexMaleDragonModel.INNER_ARMOR, () -> ArmorLatexMaleDragonModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexMaleDragonModel.OUTER_ARMOR, () -> ArmorLatexMaleDragonModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexFemaleDragonModel.INNER_ARMOR, () -> ArmorLatexFemaleDragonModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexFemaleDragonModel.OUTER_ARMOR, () -> ArmorLatexFemaleDragonModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexBigTailDragonModel.INNER_ARMOR, () -> ArmorLatexBigTailDragonModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexBigTailDragonModel.OUTER_ARMOR, () -> ArmorLatexBigTailDragonModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexMaleWingedDragonModel.INNER_ARMOR, () -> ArmorLatexMaleWingedDragonModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexMaleWingedDragonModel.OUTER_ARMOR, () -> ArmorLatexMaleWingedDragonModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexFemaleWingedDragonModel.INNER_ARMOR, () -> ArmorLatexFemaleWingedDragonModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexFemaleWingedDragonModel.OUTER_ARMOR, () -> ArmorLatexFemaleWingedDragonModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexMaleSharkModel.INNER_ARMOR, () -> ArmorLatexMaleSharkModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexMaleSharkModel.OUTER_ARMOR, () -> ArmorLatexMaleSharkModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexFemaleSharkModel.INNER_ARMOR, () -> ArmorLatexFemaleSharkModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexFemaleSharkModel.OUTER_ARMOR, () -> ArmorLatexFemaleSharkModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexMaleSquidDogModel.INNER_ARMOR, () -> ArmorLatexMaleSquidDogModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexMaleSquidDogModel.OUTER_ARMOR, () -> ArmorLatexMaleSquidDogModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexFemaleSquidDogModel.INNER_ARMOR, () -> ArmorLatexFemaleSquidDogModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexFemaleSquidDogModel.OUTER_ARMOR, () -> ArmorLatexFemaleSquidDogModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexWhiteKnightModel.INNER_ARMOR, () -> ArmorLatexWhiteKnightModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexWhiteKnightModel.OUTER_ARMOR, () -> ArmorLatexWhiteKnightModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexCentaurUpperModel.INNER_ARMOR, () -> ArmorLatexCentaurUpperModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexCentaurUpperModel.OUTER_ARMOR, () -> ArmorLatexCentaurUpperModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexCentaurLowerModel.INNER_ARMOR, () -> ArmorLatexCentaurLowerModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexCentaurLowerModel.OUTER_ARMOR, () -> ArmorLatexCentaurLowerModel.createArmorLayer(ArmorModel.OUTER));

        event.registerLayerDefinition(ArmorLatexOtterModel.INNER_ARMOR, () -> ArmorLatexOtterModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexOtterModel.OUTER_ARMOR, () -> ArmorLatexOtterModel.createArmorLayer(ArmorModel.OUTER));
        event.registerLayerDefinition(ArmorLatexStigerModel.INNER_ARMOR, () -> ArmorLatexStigerModel.createArmorLayer(ArmorModel.INNER));
        event.registerLayerDefinition(ArmorLatexStigerModel.OUTER_ARMOR, () -> ArmorLatexStigerModel.createArmorLayer(ArmorModel.OUTER));

        if (useNewModels) {
            event.registerLayerDefinition(HairRemodel.RIG_UPPER_LOCATION, HairRemodel::createUpperHair);
            event.registerLayerDefinition(HairRemodel.RIG_LOWER_LOCATION, HairRemodel::createLowerHair);
        }

        event.registerLayerDefinition(RoombaModel.LAYER_LOCATION, RoombaModel::createBodyLayer);

        event.registerLayerDefinition(LatexContainerRenderer.LAYER_LOCATION, LatexContainerRenderer::createLatexFill);
        event.registerLayerDefinition(PillowRenderer.LAYER_LOCATION, PillowRenderer::createBodyLayer);
    }
}
