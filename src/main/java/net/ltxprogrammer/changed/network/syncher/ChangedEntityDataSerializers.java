package net.ltxprogrammer.changed.network.syncher;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.entity.ai.DarkLatexAttackCondition;
import net.ltxprogrammer.changed.entity.ai.DarkLatexAttackType;
import net.ltxprogrammer.changed.entity.ai.DarkLatexFavor;
import net.ltxprogrammer.changed.entity.ai.DarkLatexTargetType;
import net.ltxprogrammer.changed.entity.decoration.WallSignVariant;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
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
    public static final RegistryObject<EntityDataSerializer<DarkLatexTargetType>> DARK_LATEX_TARGET_TYPE = REGISTRY.register("dark_latex_target_type", () -> EntityDataSerializer.simpleEnum(DarkLatexTargetType.class));
    public static final RegistryObject<EntityDataSerializer<DarkLatexAttackType>> DARK_LATEX_ATTACK_TYPE = REGISTRY.register("dark_latex_attack_type", () -> EntityDataSerializer.simpleEnum(DarkLatexAttackType.class));
    public static final RegistryObject<EntityDataSerializer<DarkLatexAttackCondition>> DARK_LATEX_ATTACK_CONDITION = REGISTRY.register("dark_latex_attack_condition", () -> EntityDataSerializer.simpleEnum(DarkLatexAttackCondition.class));
    public static final RegistryObject<EntityDataSerializer<DarkLatexFavor>> DARK_LATEX_FAVOR = REGISTRY.register("dark_latex_favor", () -> EntityDataSerializer.simpleEnum(DarkLatexFavor.class));
}
