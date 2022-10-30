package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.client.renderer.model.*;
import net.ltxprogrammer.changed.client.renderer.model.armor.*;
import net.ltxprogrammer.changed.data.DelayLoadedModel;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedLayerDefinitions {
    @SubscribeEvent
    public static void registerLayerDefinitions(FMLCommonSetupEvent event) {
        ForgeHooksClient.registerLayerDefinition(AerosolLatexWolfModel.LAYER_LOCATION, AerosolLatexWolfModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(DarkLatexDragonFemaleModel.LAYER_LOCATION, DarkLatexDragonFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(DarkLatexDragonMaleModel.LAYER_LOCATION, DarkLatexDragonMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(DarkLatexWolfFemaleModel.LAYER_LOCATION, DarkLatexWolfFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(DarkLatexWolfMaleModel.LAYER_LOCATION, DarkLatexWolfMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(DarkLatexYufengModel.LAYER_LOCATION, DarkLatexYufengModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexBeifengModel.LAYER_LOCATION, LatexBeifengModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexBlueDragonModel.LAYER_LOCATION, LatexBlueDragonModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexCrystalWolfModel.LAYER_LOCATION, LatexCrystalWolfModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexHypnoCatModel.LAYER_LOCATION, LatexHypnoCatModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexLeafModel.LAYER_LOCATION, LatexLeafModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexOrcaModel.LAYER_LOCATION, LatexOrcaModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSharkFemaleModel.LAYER_LOCATION, LatexSharkFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSharkMaleModel.LAYER_LOCATION, LatexSharkMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSilverFoxModel.LAYER_LOCATION, LatexSilverFoxModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSnowLeopardFemaleModel.LAYER_LOCATION, LatexSnowLeopardFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSnowLeopardMaleModel.LAYER_LOCATION, LatexSnowLeopardMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexSquidDogModel.LAYER_LOCATION, LatexSquidDogModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexTigerSharkModel.LAYER_LOCATION, LatexTigerSharkModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexTrafficConeDragonModel.LAYER_LOCATION, LatexTrafficConeDragonModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexWatermelonCatFemaleModel.LAYER_LOCATION, LatexWatermelonCatFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LatexWatermelonCatMaleModel.LAYER_LOCATION, LatexWatermelonCatMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LightLatexCentaurModel.LAYER_LOCATION, LightLatexCentaurModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LightLatexKnightModel.LAYER_LOCATION, LightLatexKnightModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LightLatexKnightFusionModel.LAYER_LOCATION, LightLatexKnightFusionModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LightLatexWolfFemaleModel.LAYER_LOCATION, LightLatexWolfFemaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(LightLatexWolfMaleModel.LAYER_LOCATION, LightLatexWolfMaleModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(MilkPuddingModel.LAYER_LOCATION, MilkPuddingModel::createBodyLayer);
        ForgeHooksClient.registerLayerDefinition(WhiteLatexWolfModel.LAYER_LOCATION, WhiteLatexWolfModel::createBodyLayer);

        ForgeHooksClient.registerLayerDefinition(ArmorLatexWolfModel.INNER_ARMOR, () -> ArmorLatexWolfModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexWolfModel.OUTER_ARMOR, () -> ArmorLatexWolfModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexSharkModel.INNER_ARMOR, () -> ArmorLatexSharkModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexSharkModel.OUTER_ARMOR, () -> ArmorLatexSharkModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexDragonModel.INNER_ARMOR, () -> ArmorLatexDragonModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexDragonModel.OUTER_ARMOR, () -> ArmorLatexDragonModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexSnowLeopardModel.INNER_ARMOR, () -> ArmorLatexSnowLeopardModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexSnowLeopardModel.OUTER_ARMOR, () -> ArmorLatexSnowLeopardModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexTrafficConeDragonModel.INNER_ARMOR, () -> ArmorLatexTrafficConeDragonModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexTrafficConeDragonModel.OUTER_ARMOR, () -> ArmorLatexTrafficConeDragonModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexBlueDragonModel.INNER_ARMOR, () -> ArmorLatexBlueDragonModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexBlueDragonModel.OUTER_ARMOR, () -> ArmorLatexBlueDragonModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLightLatexCentaurModel.INNER_ARMOR, () -> ArmorLightLatexCentaurModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLightLatexCentaurModel.OUTER_ARMOR, () -> ArmorLightLatexCentaurModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLightLatexKnightModel.INNER_ARMOR, () -> ArmorLightLatexKnightModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLightLatexKnightModel.OUTER_ARMOR, () -> ArmorLightLatexKnightModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLightLatexKnightFusionModel.INNER_ARMOR, () -> ArmorLightLatexKnightFusionModel.createArmorLayer(ArmorModel.INNER));
        ForgeHooksClient.registerLayerDefinition(ArmorLightLatexKnightFusionModel.OUTER_ARMOR, () -> ArmorLightLatexKnightFusionModel.createArmorLayer(ArmorModel.OUTER));
        ForgeHooksClient.registerLayerDefinition(ArmorLatexTrafficConeDragonModel.OUTER_ARMOR, () -> ArmorLatexTrafficConeDragonModel.createArmorLayer(ArmorModel.OUTER));

        PatreonBenefits.LAYER_LOCATIONS.forEach((uuid, location) -> {
            PatreonBenefits.SpecialLatexForm form = PatreonBenefits.getPlayerSpecialForm(uuid);
            if (form == null)
                return;

            ForgeHooksClient.registerLayerDefinition(location.get(), () ->
                    form.model().createBodyLayer(
                            DelayLoadedModel.HUMANOID_PART_FIXER,
                            DelayLoadedModel.HUMANOID_GROUP_FIXER));
        });

        ArmorModelLayerLocation.ARMOR_LAYER_LOCATIONS.forEach((uuid, locations) -> {
            PatreonBenefits.SpecialLatexForm form = PatreonBenefits.getPlayerSpecialForm(uuid);
            if (form == null)
                return;

            ForgeHooksClient.registerLayerDefinition(locations.inner().get(), () ->
                    form.armorModelInner().createBodyLayer(
                            DelayLoadedModel.HUMANOID_PART_FIXER,
                            DelayLoadedModel.HUMANOID_GROUP_FIXER));
            ForgeHooksClient.registerLayerDefinition(locations.outer().get(), () ->
                    form.armorModelOuter().createBodyLayer(
                            DelayLoadedModel.HUMANOID_PART_FIXER,
                            DelayLoadedModel.HUMANOID_GROUP_FIXER));
        });
    }
}
