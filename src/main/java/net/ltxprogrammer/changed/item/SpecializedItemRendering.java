package net.ltxprogrammer.changed.item;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface SpecializedItemRendering {
    static boolean isGUI(ItemTransforms.TransformType type) {
        return type == ItemTransforms.TransformType.GUI || type == ItemTransforms.TransformType.GROUND || type == ItemTransforms.TransformType.FIXED;
    }

    @Nullable
    default ModelResourceLocation getEmissiveModelLocation(ItemStack itemStack, ItemTransforms.TransformType type) { return null; }
    ModelResourceLocation getModelLocation(ItemStack itemStack, ItemTransforms.TransformType type);
    void loadSpecialModels(Consumer<ResourceLocation> loader);

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    class Event {
        @SubscribeEvent
        public static void onModelRegistryEvent(ModelRegistryEvent event) {
            Registry.ITEM.forEach(item -> {
                if (item instanceof SpecializedItemRendering specialized)
                    specialized.loadSpecialModels(ForgeModelBakery::addSpecialModel);
            });
        }
    }
}
