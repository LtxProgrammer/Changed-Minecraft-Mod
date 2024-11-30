package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class RenderTypeOverride implements MultiBufferSource {
    private final MultiBufferSource actualSource;
    private final Function<RenderType, RenderType> mapper;

    public RenderTypeOverride(MultiBufferSource actualSource, Function<RenderType, RenderType> mapper) {
        this.actualSource = actualSource;
        this.mapper = mapper;
    }

    @Override
    public @NotNull VertexConsumer getBuffer(@NotNull RenderType renderType) {
        return actualSource.getBuffer(mapper.apply(renderType));
    }
}
