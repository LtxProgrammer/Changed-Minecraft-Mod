package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.fluid.AbstractLatexFluid;
import net.ltxprogrammer.changed.fluid.AerosolLatexFluid;
import net.ltxprogrammer.changed.fluid.DarkLatexFluid;
import net.ltxprogrammer.changed.fluid.WhiteLatexFluid;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ChangedFluids { // TODO BUG: Optifine tints fluids tagged with #water blue,
    public static final DeferredRegister<Fluid> REGISTRY = DeferredRegister.create(ForgeRegistries.FLUIDS, Changed.MODID);
    public static final RegistryObject<AbstractLatexFluid> AEROSOL_LATEX = register("aerosol_latex", AerosolLatexFluid.Source::new);
    public static final RegistryObject<AbstractLatexFluid> AEROSOL_LATEX_FLOWING = register("aerosol_latex_flowing", AerosolLatexFluid.Flowing::new);
    public static final RegistryObject<AbstractLatexFluid> DARK_LATEX = register("dark_latex", DarkLatexFluid.Source::new);
    public static final RegistryObject<AbstractLatexFluid> DARK_LATEX_FLOWING = register("dark_latex_flowing", DarkLatexFluid.Flowing::new);
    public static final RegistryObject<AbstractLatexFluid> WHITE_LATEX = register("white_latex", WhiteLatexFluid.Source::new);
    public static final RegistryObject<AbstractLatexFluid> WHITE_LATEX_FLOWING = register("white_latex_flowing", WhiteLatexFluid.Flowing::new);

    public static <T extends Fluid, F extends T> RegistryObject<T> register(String name, Supplier<F> fluid) {
        return REGISTRY.register(name, fluid);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientSideHandler {
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            ItemBlockRenderTypes.setRenderLayer(AEROSOL_LATEX.get(), renderType -> renderType == RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(AEROSOL_LATEX_FLOWING.get(), renderType -> renderType == RenderType.translucent());
        }
    }
}
