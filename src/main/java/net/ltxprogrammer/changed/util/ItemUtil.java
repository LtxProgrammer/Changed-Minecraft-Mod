package net.ltxprogrammer.changed.util;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemUtil {
    public static ICapabilityProvider createCurioProvider(ICurio curioInfo) {
        return new CurioProvider(curioInfo);
    }

    public static class CurioProvider implements ICapabilityProvider {
        final LazyOptional<ICurio> capability;

        CurioProvider(ICurio curio) {
            this.capability = LazyOptional.of(() -> {
                return curio;
            });
        }

        @Nonnull
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return CuriosCapability.ITEM.orEmpty(cap, this.capability);
        }
    }
}
