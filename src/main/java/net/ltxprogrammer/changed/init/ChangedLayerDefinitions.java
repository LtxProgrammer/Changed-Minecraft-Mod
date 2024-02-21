package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.blockentity.LatexContainerRenderer;
import net.ltxprogrammer.changed.client.renderer.blockentity.PillowRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.model.*;
import net.ltxprogrammer.changed.client.renderer.model.armor.*;
import net.ltxprogrammer.changed.client.renderer.model.hair.HairRemodel;
import net.ltxprogrammer.changed.client.tfanimations.TransfurHelper;
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
        ForgeHooksClient.registerLayerDefinition(CustomEyesLayer.HEAD, CustomEyesLayer::createHead);
        ForgeHooksClient.registerLayerDefinition(DarkLatexMaskModel.LAYER_LOCATION, DarkLatexMaskModel::createMask);
        ForgeHooksClient.registerLayerDefinition(GasMaskModel.LAYER_LOCATION, GasMaskModel::createMask);
        ForgeHooksClient.registerLayerDefinition(GasMaskModel.LAYER_LOCATION_SNOUTED, GasMaskModel::createMaskSnouted);
        ForgeHooksClient.registerLayerDefinition(DuctPlayerModel.LAYER_LOCATION, DuctPlayerModel::createRoot);
        ForgeHooksClient.registerLayerDefinition(TaurChestPackModel.LAYER_LOCATION, TaurChestPackModel::createPack);

        ForgeHooksClient.registerLayerDefinition(TransfurHelper.TRANSFUR_HELPER, TransfurHelper::createBodyLayer);

        ForgeHooksClient.registerLayerDefinition(BehemothHeadModel.LAYER_LOCATION, BehemothHeadModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(BehemothHandLeftModel.LAYER_LOCATION, BehemothHandLeftModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(BehemothHandRightModel.LAYER_LOCATION, BehemothHandRightModel::createBodyLayer);

        ForgeHooksClient.registerLayerDefinition(AerosolLatexWolfModel.LAYER_LOCATION, AerosolLatexWolfModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(DarkLatexDragonModel.LAYER_LOCATION, DarkLatexDragonModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(BlackGooWolfFemaleModel.LAYER_LOCATION, BlackGooWolfFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(BlackGooWolfMaleModel.LAYER_LOCATION, BlackGooWolfMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(BlackGooWolfPartialModel.LAYER_LOCATION_HUMAN, () -> BlackGooWolfPartialModel.createHumanLayer(false));
        ForgeHooksClient.registerLayerDefinition(BlackGooWolfPartialModel.LAYER_LOCATION_GOO, () -> BlackGooWolfPartialModel.createGooLayer(false));
        ForgeHooksClient.registerLayerDefinition(BlackGooWolfPartialModel.LAYER_LOCATION_HUMAN_SLIM, () -> BlackGooWolfPartialModel.createHumanLayer(true));
        ForgeHooksClient.registerLayerDefinition(BlackGooWolfPartialModel.LAYER_LOCATION_GOO_SLIM, () -> BlackGooWolfPartialModel.createGooLayer(true));
        ForgeHooksClient.registerLayerDefinition(BlackGooPupModel.LAYER_LOCATION, BlackGooPupModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(BlackGooYufengModel.LAYER_LOCATION, BlackGooYufengModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(PhageLatexWolfFemaleModel.LAYER_LOCATION, PhageLatexWolfFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(PhageLatexWolfMaleModel.LAYER_LOCATION, PhageLatexWolfMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(HeadlessKnightModel.LAYER_LOCATION, HeadlessKnightModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooAlienModel.LAYER_LOCATION, GooAlienModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooBeeModel.LAYER_LOCATION, GooBeeModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(BeifengModel.LAYER_LOCATION, BeifengModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(BenignGooWolfModel.LAYER_LOCATION, BenignGooWolfModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(BlueGooDragonModel.LAYER_LOCATION, BlueGooDragonModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(BlueGooWolfModel.LAYER_LOCATION, BlueGooWolfModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooCrocodileModel.LAYER_LOCATION, GooCrocodileModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexCrystalWolfModel.LAYER_LOCATION, LatexCrystalWolfModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexCrystalWolfHornedModel.LAYER_LOCATION, LatexCrystalWolfHornedModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooDeerModel.LAYER_LOCATION, GooDeerModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexFennecFoxModel.LAYER_LOCATION, LatexFennecFoxModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooHumanModel.LAYER_LOCATION, () -> GooHumanModel.createBodyLayer(false));
        ForgeHooksClient.registerLayerDefinition(GooHumanModel.LAYER_LOCATION_SLIM, () -> GooHumanModel.createBodyLayer(true));
        ForgeHooksClient.registerLayerDefinition(LatexHypnoCatModel.LAYER_LOCATION, LatexHypnoCatModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexKeonWolfModel.LAYER_LOCATION, LatexKeonWolfModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexLeafModel.LAYER_LOCATION, LatexLeafModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexMantaRayFemaleModel.LAYER_LOCATION,
                useNewModels ? LatexMantaRayFemaleModel.Remodel::createBodyLayer : LatexMantaRayFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexMantaRayMaleModel.LAYER_LOCATION,
                useNewModels ? LatexMantaRayMaleModel.Remodel::createBodyLayer : LatexMantaRayMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexMedusaCatModel.LAYER_LOCATION, LatexMedusaCatModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexMermaidSharkModel.LAYER_LOCATION, LatexMermaidSharkModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexMimicPlantModel.LAYER_LOCATION, LatexMimicPlantModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexMingCatModel.LAYER_LOCATION, LatexMingCatModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexMothModel.LAYER_LOCATION, LatexMothModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooOrcaModel.LAYER_LOCATION, GooOrcaModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooOtterModel.LAYER_LOCATION, GooOtterModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexPinkDeerModel.LAYER_LOCATION, LatexPinkDeerModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexPinkWyvernModel.LAYER_LOCATION, LatexPinkWyvernModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexPinkYuinDragonModel.LAYER_LOCATION, LatexPinkYuinDragonModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexPurpleFoxModel.LAYER_LOCATION, LatexPurpleFoxModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexRaccoonModel.LAYER_LOCATION, LatexRaccoonModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexRedDragonModel.LAYER_LOCATION, LatexRedDragonModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexRedPandaModel.LAYER_LOCATION, LatexRedPandaModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooSharkModel.LAYER_LOCATION, GooSharkModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooSharkFemaleModel.LAYER_LOCATION,
                /*useNewModels ? LatexSharkFemaleModel.Remodel::createBodyLayer :*/ GooSharkFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooSharkMaleModel.LAYER_LOCATION,
                /*useNewModels ? LatexSharkMaleModel.Remodel::createBodyLayer :*/ GooSharkMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooSirenModel.LAYER_LOCATION, GooSirenModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooSnakeModel.LAYER_LOCATION, GooSnakeModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSniperDogModel.LAYER_LOCATION, LatexSniperDogModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooSnowLeopardFemaleModel.LAYER_LOCATION, GooSnowLeopardFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooSnowLeopardMaleModel.LAYER_LOCATION, GooSnowLeopardMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooSquidDogFemaleModel.LAYER_LOCATION, GooSquidDogFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooSquidDogMaleModel.LAYER_LOCATION, GooSquidDogMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooSquirrelModel.LAYER_LOCATION, GooSquirrelModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooStigerModel.LAYER_LOCATION, GooStigerModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooTigerSharkModel.LAYER_LOCATION, GooTigerSharkModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooTrafficConeDragonModel.LAYER_LOCATION, GooTrafficConeDragonModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooTranslucentLizardModel.LAYER_LOCATION, GooTranslucentLizardModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooWatermelonCatModel.LAYER_LOCATION, GooWatermelonCatModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(WhiteGooTigerModel.LAYER_LOCATION, WhiteGooTigerModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(GooYuinModel.LAYER_LOCATION, GooYuinModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(WhiteGooCentaurModel.LAYER_LOCATION, WhiteGooCentaurModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(WhiteGooKnightModel.LAYER_LOCATION, WhiteGooKnightModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(WhiteGooKnightFusionModel.LAYER_LOCATION, WhiteGooKnightFusionModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(WhiteGooWolfFemaleModel.LAYER_LOCATION, WhiteGooWolfFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(WhiteGooWolfMaleModel.LAYER_LOCATION, WhiteGooWolfMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(MilkPuddingModel.LAYER_LOCATION, MilkPuddingModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(SharkModel.LAYER_LOCATION, SharkModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(PureWhiteGooWolfModel.LAYER_LOCATION, PureWhiteGooWolfModel::createBodyLayer);

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
            ForgeHooksClient.registerLayerDefinition(ArmorLatexTrafficConeDragonModel.RemodelMale.INNER_ARMOR, () -> ArmorLatexTrafficConeDragonModel.RemodelMale.createArmorLayer(ArmorModel.INNER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexTrafficConeDragonModel.RemodelMale.OUTER_ARMOR, () -> ArmorLatexTrafficConeDragonModel.RemodelMale.createArmorLayer(ArmorModel.OUTER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexSharkModel.RemodelFemale.INNER_ARMOR, () -> ArmorLatexSharkModel.RemodelFemale.createArmorLayer(ArmorModel.INNER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexSharkModel.RemodelFemale.OUTER_ARMOR, () -> ArmorLatexSharkModel.RemodelFemale.createArmorLayer(ArmorModel.OUTER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexSharkModel.RemodelMale.INNER_ARMOR, () -> ArmorLatexSharkModel.RemodelMale.createArmorLayer(ArmorModel.INNER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexSharkModel.RemodelMale.OUTER_ARMOR, () -> ArmorLatexSharkModel.RemodelMale.createArmorLayer(ArmorModel.OUTER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexSnowLeopardModel.RemodelFemale.INNER_ARMOR, () -> ArmorLatexSnowLeopardModel.RemodelFemale.createArmorLayer(ArmorModel.INNER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexSnowLeopardModel.RemodelFemale.OUTER_ARMOR, () -> ArmorLatexSnowLeopardModel.RemodelFemale.createArmorLayer(ArmorModel.OUTER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexSnowLeopardModel.RemodelMale.INNER_ARMOR, () -> ArmorLatexSnowLeopardModel.RemodelMale.createArmorLayer(ArmorModel.INNER));
            ForgeHooksClient.registerLayerDefinition(ArmorLatexSnowLeopardModel.RemodelMale.OUTER_ARMOR, () -> ArmorLatexSnowLeopardModel.RemodelMale.createArmorLayer(ArmorModel.OUTER));

            ForgeHooksClient.registerLayerDefinition(ArmorUpperBodyModel.RemodelFemale.INNER_ARMOR, () -> ArmorUpperBodyModel.RemodelFemale.createArmorLayer(ArmorModel.INNER));
            ForgeHooksClient.registerLayerDefinition(ArmorUpperBodyModel.RemodelFemale.OUTER_ARMOR, () -> ArmorUpperBodyModel.RemodelFemale.createArmorLayer(ArmorModel.OUTER));
            ForgeHooksClient.registerLayerDefinition(ArmorUpperBodyModel.RemodelMale.INNER_ARMOR, () -> ArmorUpperBodyModel.RemodelMale.createArmorLayer(ArmorModel.INNER));
            ForgeHooksClient.registerLayerDefinition(ArmorUpperBodyModel.RemodelMale.OUTER_ARMOR, () -> ArmorUpperBodyModel.RemodelMale.createArmorLayer(ArmorModel.OUTER));

            ForgeHooksClient.registerLayerDefinition(ArmorAbdomenModel.Remodel.INNER_ARMOR, () -> ArmorAbdomenModel.Remodel.createArmorLayer(ArmorModel.INNER));
            ForgeHooksClient.registerLayerDefinition(ArmorAbdomenModel.Remodel.OUTER_ARMOR, () -> ArmorAbdomenModel.Remodel.createArmorLayer(ArmorModel.OUTER));;
            ForgeHooksClient.registerLayerDefinition(ArmorMermaidSharkAbdomenModel.Remodel.INNER_ARMOR, () -> ArmorMermaidSharkAbdomenModel.Remodel.createArmorLayer(ArmorModel.INNER));
            ForgeHooksClient.registerLayerDefinition(ArmorMermaidSharkAbdomenModel.Remodel.OUTER_ARMOR, () -> ArmorMermaidSharkAbdomenModel.Remodel.createArmorLayer(ArmorModel.OUTER));
        }

        else { // Old model exclusive

        }

        // Compatibility
        ForgeHooksClient.registerLayerDefinition(ArmorLatexWolfModel.INNER_ARMOR, () -> ArmorLatexWolfModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexWolfModel.OUTER_ARMOR, () -> ArmorLatexWolfModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorMaleWolfModel.INNER_ARMOR, () -> ArmorMaleWolfModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorMaleWolfModel.OUTER_ARMOR, () -> ArmorMaleWolfModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorFemaleWolfModel.INNER_ARMOR, () -> ArmorFemaleWolfModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorFemaleWolfModel.OUTER_ARMOR, () -> ArmorFemaleWolfModel.createArmorLayer(ArmorModel.OUTER));

        ForgeHooksClient.registerLayerDefinition(ArmorLatexBeeModel.INNER_ARMOR, () -> ArmorLatexBeeModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexBeeModel.OUTER_ARMOR, () -> ArmorLatexBeeModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexMaleCatModel.INNER_ARMOR, () -> ArmorLatexMaleCatModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexMaleCatModel.OUTER_ARMOR, () -> ArmorLatexMaleCatModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexFemaleCatModel.INNER_ARMOR, () -> ArmorLatexFemaleCatModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexFemaleCatModel.OUTER_ARMOR, () -> ArmorLatexFemaleCatModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexMaleDragonModel.INNER_ARMOR, () -> ArmorLatexMaleDragonModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexMaleDragonModel.OUTER_ARMOR, () -> ArmorLatexMaleDragonModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexFemaleDragonModel.INNER_ARMOR, () -> ArmorLatexFemaleDragonModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexFemaleDragonModel.OUTER_ARMOR, () -> ArmorLatexFemaleDragonModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexBigTailDragonModel.INNER_ARMOR, () -> ArmorLatexBigTailDragonModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexBigTailDragonModel.OUTER_ARMOR, () -> ArmorLatexBigTailDragonModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexMaleWingedDragonModel.INNER_ARMOR, () -> ArmorLatexMaleWingedDragonModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexMaleWingedDragonModel.OUTER_ARMOR, () -> ArmorLatexMaleWingedDragonModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexFemaleWingedDragonModel.INNER_ARMOR, () -> ArmorLatexFemaleWingedDragonModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexFemaleWingedDragonModel.OUTER_ARMOR, () -> ArmorLatexFemaleWingedDragonModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexSharkModel.INNER_ARMOR, () -> ArmorLatexSharkModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexSharkModel.OUTER_ARMOR, () -> ArmorLatexSharkModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexMaleSharkModel.INNER_ARMOR, () -> ArmorLatexMaleSharkModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexMaleSharkModel.OUTER_ARMOR, () -> ArmorLatexMaleSharkModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexFemaleSharkModel.INNER_ARMOR, () -> ArmorLatexFemaleSharkModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexFemaleSharkModel.OUTER_ARMOR, () -> ArmorLatexFemaleSharkModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexMaleSquidDogModel.INNER_ARMOR, () -> ArmorLatexMaleSquidDogModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexMaleSquidDogModel.OUTER_ARMOR, () -> ArmorLatexMaleSquidDogModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexFemaleSquidDogModel.INNER_ARMOR, () -> ArmorLatexFemaleSquidDogModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexFemaleSquidDogModel.OUTER_ARMOR, () -> ArmorLatexFemaleSquidDogModel.createArmorLayer(ArmorModel.OUTER));

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
        ForgeHooksClient.registerLayerDefinition(PillowRenderer.LAYER_LOCATION, PillowRenderer::createBodyLayer);
    }
}
