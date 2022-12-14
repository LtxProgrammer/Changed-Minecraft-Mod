package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.beast.WhiteLatexWolf;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedCriteriaTriggers;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

public interface WhiteLatexTransportInterface extends NonLatexCoverableBlock {
    Map<LivingEntity, Boolean> whiteLatexNoCollideMap = new HashMap<>();

    static boolean isEntityInWhiteLatex(LivingEntity entity) {
        return whiteLatexNoCollideMap.containsKey(entity);
    }
    static final String TRANSPORT_TAG = Changed.modResourceStr("white_latex_transport");

    static void multiplyMotion(Player player, float mul) {
        player.setDeltaMovement(player.getDeltaMovement().multiply(mul, mul, mul));
    }

    static void entityEnterLatex(LivingEntity entity, BlockPos pos) {
        if (LatexVariant.getEntityVariant(entity) != null && !(LatexVariant.getEntityVariant(entity).getLatexEntity() instanceof WhiteLatexWolf))
            return;

        if (isEntityInWhiteLatex(entity) || entity.isDeadOrDying())
            return;

        ProcessTransfur.transfur(entity, entity.level, LatexVariant.WHITE_LATEX_WOLF, false);

        entity.getPersistentData().putBoolean(TRANSPORT_TAG, true);
        whiteLatexNoCollideMap.put(entity, true);

        entity.setPose(Pose.SWIMMING);
        entity.refreshDimensions();
        entity.setSwimming(true);
        entity.setInvisible(true);
        entity.setInvulnerable(true);

        entity.playSound(ChangedSounds.POISON, 1.0f, 1.0f);
        entity.moveTo(pos, entity.getYRot(), entity.getXRot());
    }

    @Mod.EventBusSubscriber
    class EventSubscriber {
        static boolean whiteLatex(BlockState blockState) {
            return blockState.getBlock() instanceof WhiteLatexTransportInterface || (blockState.getProperties().contains(COVERED) && blockState.getValue(COVERED) == LatexType.WHITE_LATEX);
        }

        @SubscribeEvent
        static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.player.getPersistentData().contains(TRANSPORT_TAG))
                entityEnterLatex(event.player, new BlockPos(event.player.getBlockX(), event.player.getBlockY(), event.player.getBlockZ()));

            BlockPos pos = new BlockPos(event.player.getBlockX(), event.player.getBlockY(), event.player.getBlockZ());
            BlockState blockState = event.player.level.getBlockState(pos);
            BlockState blockStateEye = event.player.level.getBlockState(event.player.eyeBlockPosition());

            if (!isEntityInWhiteLatex(event.player)) {
                if (whiteLatex(blockState) || whiteLatex(blockStateEye)) {
                    if (ProcessTransfur.isPlayerLatex(event.player) && ProcessTransfur.getPlayerLatexVariant(event.player).getLatexType() == LatexType.WHITE_LATEX) {
                        entityEnterLatex(event.player, new BlockPos(event.player.getBlockX(), event.player.getBlockY(), event.player.getBlockZ()));
                    }

                    else if (ProcessTransfur.getPlayerLatexVariant(event.player).getLatexType().isHostileTo(LatexType.WHITE_LATEX))
                        event.player.hurt(ChangedDamageSources.WHITE_LATEX, 2.0f);

                    else if (!ProcessTransfur.isPlayerLatex(event.player) && ProcessTransfur.progressPlayerTransfur(event.player, 4800, LatexVariant.WHITE_LATEX_WOLF.getFormId()))
                        entityEnterLatex(event.player, new BlockPos(event.player.getBlockX(), event.player.getBlockY(), event.player.getBlockZ()));
                }
            }

            else {
                var form = ProcessTransfur.getPlayerLatexVariant(event.player);

                event.player.setPose(Pose.SWIMMING);
                event.player.refreshDimensions();
                event.player.setSwimming(true);
                event.player.heal(0.0625F);

                multiplyMotion(event.player, 1.05F);
                if (form != null)
                    form.ticksWhiteLatex++;
                else
                    ProcessTransfur.transfur(event.player, event.player.level, LatexVariant.WHITE_LATEX_WOLF, false);

                if (!(whiteLatex(blockState) || whiteLatex(blockStateEye))) {
                    whiteLatexNoCollideMap.remove(event.player);
                    event.player.getPersistentData().remove(TRANSPORT_TAG);
                    event.player.playSound(ChangedSounds.POISON, 1.0f, 1.0f);
                    event.player.refreshDimensions();
                    event.player.setInvisible(false);
                    event.player.setInvulnerable(false);
                    if (form != null)
                        form.ticksWhiteLatex = 0;
                }

                else if (event.player instanceof ServerPlayer serverPlayer && form != null)
                    ChangedCriteriaTriggers.WHITE_LATEX_FUSE.trigger(serverPlayer, form.ticksWhiteLatex);
            }
        }
    }
}
