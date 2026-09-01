package net.ltxprogrammer.changed.network.syncher;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.entity.ai.TamedEntityAttackCondition;
import net.ltxprogrammer.changed.entity.ai.TamedEntityAttackType;
import net.ltxprogrammer.changed.entity.ai.TamedEntityFavor;
import net.ltxprogrammer.changed.entity.ai.TamedEntityTargetType;
import net.ltxprogrammer.changed.entity.decoration.WallSignVariant;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ChangedEntityDataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Changed.MODID);

    public static final RegistryObject<EntityDataSerializer<BasicPlayerInfo>> BASIC_PLAYER_INFO = REGISTRY.register("basic_player_info", () -> new EntityDataSerializer<>() {
        public void write(FriendlyByteBuf buffer, BasicPlayerInfo info) {
            var tag = new CompoundTag();
            info.save(tag);
            buffer.writeNbt(tag);
        }

        public BasicPlayerInfo read(FriendlyByteBuf buffer) {
            BasicPlayerInfo info = new BasicPlayerInfo();
            info.load(buffer.readNbt());
            return info;
        }

        public BasicPlayerInfo copy(BasicPlayerInfo info) {
            BasicPlayerInfo newInfo = new BasicPlayerInfo();
            newInfo.copyFrom(info);
            return newInfo;
        }
    });

    public static final RegistryObject<EntityDataSerializer<WallSignVariant>> WALL_SIGN_VARIANT = REGISTRY.register("wall_sign_variant", () -> EntityDataSerializer.simpleId(ChangedRegistry.WALL_SIGN_VARIANT.asIdMap()));
    public static final RegistryObject<EntityDataSerializer<TamedEntityTargetType>> TAMED_ENTITY_TARGET_TYPE = REGISTRY.register("tamed_entity_target_type", () -> EntityDataSerializer.simpleEnum(TamedEntityTargetType.class));
    public static final RegistryObject<EntityDataSerializer<TamedEntityAttackType>> TAMED_ENTITY_ATTACK_TYPE = REGISTRY.register("tamed_entity_attack_type", () -> EntityDataSerializer.simpleEnum(TamedEntityAttackType.class));
    public static final RegistryObject<EntityDataSerializer<TamedEntityAttackCondition>> TAMED_ENTITY_ATTACK_CONDITION = REGISTRY.register("tamed_entity_attack_condition", () -> EntityDataSerializer.simpleEnum(TamedEntityAttackCondition.class));
    public static final RegistryObject<EntityDataSerializer<TamedEntityFavor>> TAMED_ENTITY_FAVOR = REGISTRY.register("tamed_entity_favor", () -> EntityDataSerializer.simpleId(ChangedRegistry.TAMED_LATEX_FAVORS.asIdMap()));
}
