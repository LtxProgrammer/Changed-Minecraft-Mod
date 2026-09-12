package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.world.LatexCoverGetter;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.ltxprogrammer.changed.world.LevelExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ClientLevelExtension extends LevelExtension {
    public static final ClientLevelExtension INSTANCE = new ClientLevelExtension();

    @Override
    public void destroyLatexCoverProgress(LevelAccessor level, int entityId, BlockPos blockPos, int progress) {
        ((LevelRendererExtension)Minecraft.getInstance().levelRenderer).changed$destroyLatexCoverProgress(entityId, blockPos, progress);
    }

    @Override
    public void setCoversDirty(LevelAccessor level, BlockPos blockPos, LatexCoverState oldState, LatexCoverState recordedState) {
        Minecraft.getInstance().levelRenderer.setBlocksDirty(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    @Override
    public void sendCoverUpdated(LevelAccessor level, BlockPos blockPos, LatexCoverState oldState, LatexCoverState newState, int flags) {
        Minecraft.getInstance().levelRenderer.blockChanged(level, blockPos, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), flags);
    }

    public void addDestroyLatexCoverEffect(ClientLevel level, BlockPos blockPos, LatexCoverState state, BlockState repBlockState) {
        if (!state.isAir()/* && !net.minecraftforge.client.extensions.common.IClientBlockExtensions.of(state).addDestroyEffects(state, level, blockPos, this)*/) {
            final var particleEngine = Minecraft.getInstance().particleEngine;
            VoxelShape voxelshape = state.getShape(LatexCoverGetter.wrap(level), blockPos);
            double d0 = 0.25D;
            voxelshape.forAllBoxes((p_172273_, p_172274_, p_172275_, p_172276_, p_172277_, p_172278_) -> {
                double d1 = Math.min(1.0D, p_172276_ - p_172273_);
                double d2 = Math.min(1.0D, p_172277_ - p_172274_);
                double d3 = Math.min(1.0D, p_172278_ - p_172275_);
                int i = Math.max(2, Mth.ceil(d1 / 0.25D));
                int j = Math.max(2, Mth.ceil(d2 / 0.25D));
                int k = Math.max(2, Mth.ceil(d3 / 0.25D));

                for(int l = 0; l < i; ++l) {
                    for(int i1 = 0; i1 < j; ++i1) {
                        for(int j1 = 0; j1 < k; ++j1) {
                            double d4 = ((double)l + 0.5D) / (double)i;
                            double d5 = ((double)i1 + 0.5D) / (double)j;
                            double d6 = ((double)j1 + 0.5D) / (double)k;
                            double d7 = d4 * d1 + p_172273_;
                            double d8 = d5 * d2 + p_172274_;
                            double d9 = d6 * d3 + p_172275_;

                            particleEngine.add(new TerrainParticle(level, (double)blockPos.getX() + d7, (double)blockPos.getY() + d8, (double)blockPos.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, repBlockState, blockPos).updateSprite(repBlockState, blockPos));
                        }
                    }
                }

            });
        }
    }

    @Override
    public void customLevelEvent(LevelAccessor level, @Nullable Player source, int type, BlockPos pos, int data) {
        final ClientLevel clientLevel = (ClientLevel)level;
        switch (type) {
            case 2001:
                LatexCoverState coverState = ChangedLatexTypes.getLatexCoverStateIDMap().byId(data);
                if (!coverState.isAir()) {
                    SoundType soundtype = coverState.getSoundType(level, pos, null);
                    if (soundtype != null)
                        clientLevel.playLocalSound(pos, soundtype.getBreakSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F, false);

                    Block block = coverState.getType().getBlock();
                    if (block != null)
                        addDestroyLatexCoverEffect(clientLevel, pos, coverState, block.defaultBlockState());
                }
                break;
        }
    }
}
