package net.ltxprogrammer.changed.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.ltxprogrammer.changed.client.LatexCoveredBlocks;
import net.ltxprogrammer.changed.item.LoopedRecordItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow protected abstract void renderChunkLayer(RenderType p_172994_, PoseStack p_172995_, double p_172996_, double p_172997_, double p_172998_, Matrix4f p_172999_);
    @Shadow @Final private Map<BlockPos, SoundInstance> playingRecords;
    @Shadow @Final private Minecraft minecraft;
    @Shadow protected abstract void notifyNearbyEntities(Level p_109551_, BlockPos p_109552_, boolean p_109553_);
    @Shadow @Nullable private ClientLevel level;

    @Inject(method = "renderChunkLayer", at = @At("RETURN"))
    public void postRenderLayer(RenderType type, PoseStack pose, double x, double y, double z, Matrix4f matrix, CallbackInfo callback) {
        if (type == RenderType.solid()) {
            LatexCoveredBlocks.isRenderingChangedBlockLayer = true;
            renderChunkLayer(LatexCoveredBlocks.latexSolid(), pose, x, y, z, matrix);
            LatexCoveredBlocks.isRenderingChangedBlockLayer = false;
        }
        else if (type == RenderType.cutoutMipped()) {
            LatexCoveredBlocks.isRenderingChangedBlockLayer = true;
            renderChunkLayer(LatexCoveredBlocks.latexCutoutMipped(), pose, x, y, z, matrix);
            LatexCoveredBlocks.isRenderingChangedBlockLayer = false;
        }
        else if (type == RenderType.cutout()) {
            LatexCoveredBlocks.isRenderingChangedBlockLayer = true;
            renderChunkLayer(LatexCoveredBlocks.latexCutout(), pose, x, y, z, matrix);
            LatexCoveredBlocks.isRenderingChangedBlockLayer = false;
        }
    }

    @Inject(method = "playStreamingMusic(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/RecordItem;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;forRecord(Lnet/minecraft/sounds/SoundEvent;DDD)Lnet/minecraft/client/resources/sounds/SimpleSoundInstance;"), cancellable = true)
    public void orPlayLoopedTrack(SoundEvent event, BlockPos pos, RecordItem musicDiscItem, CallbackInfo callback) {
        if (musicDiscItem instanceof LoopedRecordItem) {
            callback.cancel();

            SoundInstance simplesoundinstance = new SimpleSoundInstance(event.getLocation(), SoundSource.RECORDS, 4.0F, 1.0F, true, 0, SoundInstance.Attenuation.LINEAR, pos.getX(), pos.getY(), pos.getZ(), false);
            this.playingRecords.put(pos, simplesoundinstance);
            this.minecraft.getSoundManager().play(simplesoundinstance);

            this.notifyNearbyEntities(this.level, pos, event != null);
        }
    }
}
