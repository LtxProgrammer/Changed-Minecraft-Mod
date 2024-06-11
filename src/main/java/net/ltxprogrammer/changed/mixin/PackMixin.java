package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.data.PackExtender;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Pack.class)
public abstract class PackMixin implements PackExtender {
    @Unique boolean shouldIncludeByDefault = true;

    @Override
    public boolean includeByDefault() {
        return shouldIncludeByDefault;
    }

    @Override
    public void setIncludeByDefault(boolean value) {
        this.shouldIncludeByDefault = value;
    }
}
