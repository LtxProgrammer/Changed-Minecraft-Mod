package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.blockentity.LatexContainerRenderer;
import net.ltxprogrammer.changed.client.renderer.model.*;
import net.ltxprogrammer.changed.client.renderer.model.armor.*;
import net.ltxprogrammer.changed.client.renderer.model.hair.HairRemodel;
import net.ltxprogrammer.changed.data.DeferredModelLayerLocation;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedLayerDefinitions {
    public static final ModelLayerLocation LATEX_COAT = new ModelLayerLocation(Changed.modResource("player"), "latex_coat");
    public static final ModelLayerLocation LATEX_COAT_SLIM = new ModelLayerLocation(Changed.modResource("player_slim"), "latex_coat");

    private static void registerLayerDefinition(@Nullable DeferredModelLayerLocation location, Supplier<LayerDefinition> supplier) {
        if (location != null)
            ForgeHooksClient.registerLayerDefinition(location.get(), supplier);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(FMLCommonSetupEvent event) {
        final boolean useNewModels = Changed.config.client.useNewModels.get();

        ForgeHooksClient.registerLayerDefinition(LATEX_COAT, () ->
                LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.01F), false), 64, 64));
        ForgeHooksClient.registerLayerDefinition(LATEX_COAT_SLIM, () ->
                LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.01F), false), 64, 64));
        ForgeHooksClient.registerLayerDefinition(DarkLatexMaskModel.LAYER_LOCATION, DarkLatexMaskModel::createMask);
        ForgeHooksClient.registerLayerDefinition(TaurChestPackModel.LAYER_LOCATION, TaurChestPackModel::createPack);

        ForgeHooksClient.registerLayerDefinition(BehemothHeadModel.LAYER_LOCATION, BehemothHeadModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(BehemothHandLeftModel.LAYER_LOCATION, BehemothHandLeftModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(BehemothHandRightModel.LAYER_LOCATION, BehemothHandRightModel::createBodyLayer);

        ForgeHooksClient.registerLayerDefinition(AerosolLatexWolfModel.LAYER_LOCATION,
                useNewModels ? AerosolLatexWolfModel.Remodel::createBodyLayer : AerosolLatexWolfModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(DarkLatexDragonModel.LAYER_LOCATION,
                useNewModels ? DarkLatexDragonModel.Remodel::createBodyLayer : DarkLatexDragonModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(DarkLatexWolfFemaleModel.LAYER_LOCATION,
                useNewModels ? DarkLatexWolfFemaleModel.Remodel::createBodyLayer :  DarkLatexWolfFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(DarkLatexWolfMaleModel.LAYER_LOCATION,
                useNewModels ? DarkLatexWolfMaleModel.Remodel::createBodyLayer : DarkLatexWolfMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(DarkLatexYufengModel.LAYER_LOCATION,
                useNewModels ? DarkLatexYufengModel.Remodel::createBodyLayer : DarkLatexYufengModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(HeadlessKnightModel.LAYER_LOCATION, HeadlessKnightModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexAlienModel.LAYER_LOCATION, LatexAlienModel::createBodyLayer);
        //ForgeHooksClient.registerLayerDefinition(LatexBeeModel.LAYER_LOCATION, LatexBeeModel::createBodyLayer); // TODO: Uncomment when appropriate
        ForgeHooksClient.registerLayerDefinition(LatexBeifengModel.LAYER_LOCATION, LatexBeifengModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexBenignWolfModel.LAYER_LOCATION, LatexBenignWolfModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexBlueDragonModel.LAYER_LOCATION, LatexBlueDragonModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexBlueWolfModel.LAYER_LOCATION, LatexBlueWolfModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexCrocodileModel.LAYER_LOCATION, LatexCrocodileModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexCrystalWolfModel.LAYER_LOCATION, LatexCrystalWolfModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexCrystalWolfHornedModel.LAYER_LOCATION, LatexCrystalWolfHornedModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexDeerModel.LAYER_LOCATION, LatexDeerModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexHypnoCatModel.LAYER_LOCATION, LatexHypnoCatModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexLeafModel.LAYER_LOCATION, LatexLeafModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexMantaRayFemaleModel.LAYER_LOCATION, LatexMantaRayFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexMantaRayMaleModel.LAYER_LOCATION, LatexMantaRayMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexMedusaCatModel.LAYER_LOCATION, LatexMedusaCatModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexMermaidSharkModel.LAYER_LOCATION, LatexMermaidSharkModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexMimicPlantModel.LAYER_LOCATION, LatexMimicPlantModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexMothModel.LAYER_LOCATION, LatexMothModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexOrcaModel.LAYER_LOCATION, LatexOrcaModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexOtterModel.LAYER_LOCATION, LatexOtterModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexPinkDeerModel.LAYER_LOCATION, LatexPinkDeerModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexPinkWyvernModel.LAYER_LOCATION, LatexPinkWyvernModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexPinkYuinDragonModel.LAYER_LOCATION, LatexPinkYuinDragonModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexPurpleFoxModel.LAYER_LOCATION, LatexPurpleFoxModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexRaccoonModel.LAYER_LOCATION, LatexRaccoonModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexRedDragonModel.LAYER_LOCATION, LatexRedDragonModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexRedPandaModel.LAYER_LOCATION,
                useNewModels ? LatexRedPandaModel.Remodel::createBodyLayer : LatexRedPandaModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSharkModel.LAYER_LOCATION,
                useNewModels ? LatexSharkModel.Remodel::createBodyLayer : LatexSharkModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSharkFemaleModel.LAYER_LOCATION, LatexSharkFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSharkMaleModel.LAYER_LOCATION, LatexSharkMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSilverFoxModel.LAYER_LOCATION, LatexSilverFoxModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSirenModel.LAYER_LOCATION, LatexSirenModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSnakeModel.LAYER_LOCATION, LatexSnakeModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSniperDogModel.LAYER_LOCATION, LatexSniperDogModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSnowLeopardFemaleModel.LAYER_LOCATION,
                useNewModels ? LatexSnowLeopardFemaleModel.Remodel::createBodyLayer : LatexSnowLeopardFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSnowLeopardMaleModel.LAYER_LOCATION,
                useNewModels ? LatexSnowLeopardMaleModel.Remodel::createBodyLayer : LatexSnowLeopardMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSquidDogFemaleModel.LAYER_LOCATION, LatexSquidDogFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSquidDogMaleModel.LAYER_LOCATION, LatexSquidDogMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSquirrelModel.LAYER_LOCATION, LatexSquirrelModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexStigerModel.LAYER_LOCATION, LatexStigerModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexTigerSharkModel.LAYER_LOCATION, LatexTigerSharkModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexTrafficConeDragonModel.LAYER_LOCATION, LatexTrafficConeDragonModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexTranslucentLizardModel.LAYER_LOCATION, LatexTranslucentLizardModel::createInnerLayer);
        ForgeHooksClient.registerLayerDefinition(LatexTranslucentLizardModel.LAYER_LOCATION_OUTER, LatexTranslucentLizardModel::createOuterLayer);
        ForgeHooksClient.registerLayerDefinition(LatexWatermelonCatModel.LAYER_LOCATION, LatexWatermelonCatModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexWhiteTigerModel.LAYER_LOCATION, LatexWhiteTigerModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexYuinModel.LAYER_LOCATION, LatexYuinModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LightLatexCentaurModel.LAYER_LOCATION, LightLatexCentaurModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LightLatexKnightModel.LAYER_LOCATION, LightLatexKnightModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LightLatexKnightFusionModel.LAYER_LOCATION, LightLatexKnightFusionModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LightLatexWolfFemaleModel.LAYER_LOCATION,
                useNewModels ? LightLatexWolfFemaleModel.Remodel::createBodyLayer : LightLatexWolfFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LightLatexWolfMaleModel.LAYER_LOCATION,
                useNewModels ? LightLatexWolfMaleModel.Remodel::createBodyLayer : LightLatexWolfMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LightLatexWolfOrganicModel.LAYER_LOCATION, LightLatexWolfOrganicModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(MilkPuddingModel.LAYER_LOCATION, MilkPuddingModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(SharkModel.LAYER_LOCATION, SharkModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(WhiteLatexWolfModel.LAYER_LOCATION, WhiteLatexWolfModel::createBodyLayer);

        ForgeHooksClient.registerLayerDefinition(ArmorNoneModel.INNER_ARMOR, () -> ArmorNoneModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorNoneModel.OUTER_ARMOR, () -> ArmorNoneModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorNoTailModel.INNER_ARMOR, () -> ArmorNoTailModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorNoTailModel.OUTER_ARMOR, () -> ArmorNoTailModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorUpperBodyModel.INNER_ARMOR, () -> ArmorUpperBodyModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorUpperBodyModel.OUTER_ARMOR, () -> ArmorUpperBodyModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorAbdomenModel.INNER_ARMOR, () -> ArmorAbdomenModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorAbdomenModel.OUTER_ARMOR, () -> ArmorAbdomenModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorSnakeAbdomenModel.INNER_ARMOR, () -> ArmorSnakeAbdomenModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorSnakeAbdomenModel.OUTER_ARMOR, () -> ArmorSnakeAbdomenModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorMermaidSharkAbdomenModel.INNER_ARMOR, () -> ArmorMermaidSharkAbdomenModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorMermaidSharkAbdomenModel.OUTER_ARMOR, () -> ArmorMermaidSharkAbdomenModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorHeadlessKnightModel.INNER_ARMOR, () -> ArmorHeadlessKnightModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorHeadlessKnightModel.OUTER_ARMOR, () -> ArmorHeadlessKnightModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexYuinModel.INNER_ARMOR, () -> ArmorLatexYuinModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexYuinModel.OUTER_ARMOR, () -> ArmorLatexYuinModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexAlienModel.INNER_ARMOR, () -> ArmorLatexAlienModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexAlienModel.OUTER_ARMOR, () -> ArmorLatexAlienModel.createArmorLayer(ArmorModel.OUTER));
        if (useNewModels) {
            ForgeHooksClient.registerLayerDefinition(ArmorLatexWolfModel.RemodelFemale.INNER_ARMOR, () -> ArmorLatexWolfModel.RemodelFemale.createArmorLayer(ArmorModel.INNER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexWolfModel.RemodelFemale.OUTER_ARMOR, () -> ArmorLatexWolfModel.RemodelFemale.createArmorLayer(ArmorModel.OUTER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexWolfModel.RemodelMale.INNER_ARMOR, () -> ArmorLatexWolfModel.RemodelMale.createArmorLayer(ArmorModel.INNER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexWolfModel.RemodelMale.OUTER_ARMOR, () -> ArmorLatexWolfModel.RemodelMale.createArmorLayer(ArmorModel.OUTER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexBlueDragonModel.RemodelFemale.INNER_ARMOR, () -> ArmorLatexBlueDragonModel.RemodelFemale.createArmorLayer(ArmorModel.INNER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexBlueDragonModel.RemodelFemale.OUTER_ARMOR, () -> ArmorLatexBlueDragonModel.RemodelFemale.createArmorLayer(ArmorModel.OUTER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexBlueDragonModel.RemodelMale.INNER_ARMOR, () -> ArmorLatexBlueDragonModel.RemodelMale.createArmorLayer(ArmorModel.INNER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexBlueDragonModel.RemodelMale.OUTER_ARMOR, () -> ArmorLatexBlueDragonModel.RemodelMale.createArmorLayer(ArmorModel.OUTER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexSharkModel.RemodelFemale.INNER_ARMOR, () -> ArmorLatexSharkModel.RemodelFemale.createArmorLayer(ArmorModel.INNER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexSharkModel.RemodelFemale.OUTER_ARMOR, () -> ArmorLatexSharkModel.RemodelFemale.createArmorLayer(ArmorModel.OUTER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexSharkModel.RemodelMale.INNER_ARMOR, () -> ArmorLatexSharkModel.RemodelMale.createArmorLayer(ArmorModel.INNER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexSharkModel.RemodelMale.OUTER_ARMOR, () -> ArmorLatexSharkModel.RemodelMale.createArmorLayer(ArmorModel.OUTER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexSnowLeopardModel.RemodelFemale.INNER_ARMOR, () -> ArmorLatexSnowLeopardModel.RemodelFemale.createArmorLayer(ArmorModel.INNER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexSnowLeopardModel.RemodelFemale.OUTER_ARMOR, () -> ArmorLatexSnowLeopardModel.RemodelFemale.createArmorLayer(ArmorModel.OUTER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexSnowLeopardModel.RemodelMale.INNER_ARMOR, () -> ArmorLatexSnowLeopardModel.RemodelMale.createArmorLayer(ArmorModel.INNER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexSnowLeopardModel.RemodelMale.OUTER_ARMOR, () -> ArmorLatexSnowLeopardModel.RemodelMale.createArmorLayer(ArmorModel.OUTER));
        }

        else { // Old model exclusive

        }

        // Compatibility
        ForgeHooksClient.registerLayerDefinition(ArmorLatexWolfModel.INNER_ARMOR, () -> ArmorLatexWolfModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexWolfModel.OUTER_ARMOR, () -> ArmorLatexWolfModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexBlueDragonModel.INNER_ARMOR, () -> ArmorLatexBlueDragonModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexBlueDragonModel.OUTER_ARMOR, () -> ArmorLatexBlueDragonModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexSharkModel.INNER_ARMOR, () -> ArmorLatexSharkModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexSharkModel.OUTER_ARMOR, () -> ArmorLatexSharkModel.createArmorLayer(ArmorModel.OUTER));

        ForgeHooksClient.registerLayerDefinition(ArmorLatexOtterModel.INNER_ARMOR, () -> ArmorLatexOtterModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexOtterModel.OUTER_ARMOR, () -> ArmorLatexOtterModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexStigerModel.INNER_ARMOR, () -> ArmorLatexStigerModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexStigerModel.OUTER_ARMOR, () -> ArmorLatexStigerModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexCrocodileModel.INNER_ARMOR, () -> ArmorLatexCrocodileModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexCrocodileModel.OUTER_ARMOR, () -> ArmorLatexCrocodileModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexDragonModel.INNER_ARMOR, () -> ArmorLatexDragonModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexDragonModel.OUTER_ARMOR, () -> ArmorLatexDragonModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexSnowLeopardModel.INNER_ARMOR, () -> ArmorLatexSnowLeopardModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexSnowLeopardModel.OUTER_ARMOR, () -> ArmorLatexSnowLeopardModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexBuffSharkModel.INNER_ARMOR, () -> ArmorLatexBuffSharkModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexBuffSharkModel.OUTER_ARMOR, () -> ArmorLatexBuffSharkModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexSquidDogModel.INNER_ARMOR, () -> ArmorLatexSquidDogModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexSquidDogModel.OUTER_ARMOR, () -> ArmorLatexSquidDogModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexTrafficConeDragonModel.INNER_ARMOR, () -> ArmorLatexTrafficConeDragonModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexTrafficConeDragonModel.OUTER_ARMOR, () -> ArmorLatexTrafficConeDragonModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLightLatexCentaurModel.INNER_ARMOR, () -> ArmorLightLatexCentaurModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLightLatexCentaurModel.OUTER_ARMOR, () -> ArmorLightLatexCentaurModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLightLatexKnightModel.INNER_ARMOR, () -> ArmorLightLatexKnightModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLightLatexKnightModel.OUTER_ARMOR, () -> ArmorLightLatexKnightModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLightLatexKnightFusionModel.INNER_ARMOR, () -> ArmorLightLatexKnightFusionModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLightLatexKnightFusionModel.OUTER_ARMOR, () -> ArmorLightLatexKnightFusionModel.createArmorLayer(ArmorModel.OUTER));

        if (useNewModels) {
            ForgeHooksClient.registerLayerDefinition(HairRemodel.RIG_UPPER_LOCATION, HairRemodel::createUpperHair);
            ForgeHooksClient.registerLayerDefinition(HairRemodel.RIG_LOWER_LOCATION, HairRemodel::createLowerHair);
        }

        ForgeHooksClient.registerLayerDefinition(LatexContainerRenderer.LAYER_LOCATION, LatexContainerRenderer::createLatexFill);
    }
}
