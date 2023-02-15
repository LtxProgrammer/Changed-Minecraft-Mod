package net.ltxprogrammer.changed.init;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.data.OnlineTexture;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedTextures {
    public static final Map<ResourceLocation, AbstractTexture> REGISTRY = new HashMap<>();
    public static final Map<ResourceLocation, Material> MATERIAL_MAP = new HashMap<>();

    public static Pair<ResourceLocation, ResourceLocation> EMPTY_ARMOR_SLOT_UPPER_ABDOMEN =
            registerMaterial(new Material(InventoryMenu.BLOCK_ATLAS, Changed.modResource("items/empty_armor_slot_upper_abdomen")));
    public static Pair<ResourceLocation, ResourceLocation> EMPTY_ARMOR_SLOT_LOWER_ABDOMEN =
            registerMaterial(new Material(InventoryMenu.BLOCK_ATLAS, Changed.modResource("items/empty_armor_slot_lower_abdomen")));

    private static void doOnRenderThread(RenderCall call) {
        if (!RenderSystem.isOnRenderThread())
            RenderSystem.recordRenderCall(call);
        else
            call.execute();
    }

    public static ResourceLocation register(ResourceLocation location, Supplier<AbstractTexture> texture) {
        REGISTRY.computeIfAbsent(location, ignored -> texture.get());
        return location;
    }

    public static Pair<ResourceLocation, ResourceLocation> registerMaterial(Material material) {
        MATERIAL_MAP.putIfAbsent(material.texture(), material);
        return new Pair<>(material.atlasLocation(), material.texture());
    }

    public static void lateRegisterTextureNoSave(ResourceLocation location, Supplier<AbstractTexture> texture) {
        doOnRenderThread(() -> Minecraft.getInstance().getTextureManager().register(location, texture.get()));
    }

    public static void lateRegisterTexture(ResourceLocation location, Supplier<AbstractTexture> texture) {
        doOnRenderThread(() -> Minecraft.getInstance().getTextureManager().register(location, REGISTRY.get(register(location, texture))));
    }

    private static void registerAllOnRenderThread() {
        RenderSystem.assertOnRenderThreadOrInit();

        var textureManager = Minecraft.getInstance().getTextureManager();
        PatreonBenefits.ONLINE_TEXTURES.forEach(resource -> {
            REGISTRY.put(resource.getLocation(), OnlineTexture.of(resource));
        });
        REGISTRY.forEach(textureManager::register);
    }

    @SubscribeEvent
    public static void registerAll(FMLClientSetupEvent event) {
        if (!RenderSystem.isOnRenderThread())
            RenderSystem.recordRenderCall(ChangedTextures::registerAllOnRenderThread);
        else
            registerAllOnRenderThread();
    }
}
