package net.ltxprogrammer.changed.init;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntList;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.ILatexAssimilatedEntity;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.animation.*;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.network.packet.AnimationEventPacket;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;

public class ChangedAnimationEvents {
    public static DeferredRegister<AnimationEvent<?>> REGISTRY = ChangedRegistry.ANIMATION_EVENTS.createDeferred(Changed.MODID);

    public static RegistryObject<AnimationEvent<TransfurAnimationParameters>> TRANSFUR = register("transfur", TransfurAnimationParameters.CODEC);

    public static RegistryObject<AnimationEvent<StasisAnimationParameters>> STASIS_IDLE = register("stasis_idle", StasisAnimationParameters.CODEC);
    public static RegistryObject<AnimationEvent<StunAnimationParameters>> SHOCK_STUN = register("shock_stun", StunAnimationParameters.CODEC);

    private static <T extends AnimationParameters> RegistryObject<AnimationEvent<T>> register(String name, Codec<T> parameters) {
        return REGISTRY.register(name, () -> new AnimationEvent<>(parameters));
    }

    public static <T extends AnimationParameters> void broadcastEntityAnimation(LivingEntity livingEntity, AnimationEvent<T> event, @Nullable T parameters) {
        if (livingEntity.level().isClientSide) return; // Should only be called on the server

        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity),
                new AnimationEventPacket<>(livingEntity.getId(), event, null, parameters, IntList.of(), List.of()));
    }

    public static <T extends AnimationParameters> void broadcastEntityAnimation(LivingEntity livingEntity, AnimationEvent<T> event, AnimationCategory category, @Nullable T parameters) {
        if (livingEntity.level().isClientSide) return; // Should only be called on the server

        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity),
                new AnimationEventPacket<>(livingEntity.getId(), event, category, parameters, IntList.of(), List.of()));
    }

    public static <T extends AnimationParameters> void broadcastEntityAnimation(LivingEntity livingEntity, AnimationEventPacket<T> packet) {
        if (livingEntity.level().isClientSide) return; // Should only be called on the server

        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity), packet);
    }

    public static void broadcastTransfurAnimation(LivingEntity livingEntity, TransfurVariant<?> variant, TransfurContext context) {
        if (livingEntity.level().isClientSide) return; // Should only be called on the server
        if (!livingEntity.level().getGameRules().getBoolean(ChangedGameRules.RULE_DO_TRANSFUR_ANIMATION)) return;

        if (context.source() != null)
            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity),
                    AnimationEventPacket.Builder.of(livingEntity, TRANSFUR.get(), AnimationCategory.TRANSFUR,
                            new TransfurAnimationParameters(variant, context.cause())).addEntity(context.source()
                            .map(IAbstractChangedEntity::getEntity, ILatexAssimilatedEntity::getEntity)).build());
        else
            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity),
                    AnimationEventPacket.Builder.of(livingEntity, TRANSFUR.get(), AnimationCategory.TRANSFUR,
                            new TransfurAnimationParameters(variant, context.cause())).build());
    }
}
