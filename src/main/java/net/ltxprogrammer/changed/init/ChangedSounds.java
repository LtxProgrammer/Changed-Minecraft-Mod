package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public class ChangedSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Changed.MODID);

    private static RegistryObject<SoundEvent> register(String id) {
        return REGISTRY.register(id, () -> SoundEvent.createVariableRangeEvent(Changed.modResource(id)));
    }

    private static RegistryObject<SoundEvent> register(String id, Function<ResourceLocation, SoundEvent> finalizer) {
        return REGISTRY.register(id, () -> finalizer.apply(Changed.modResource(id)));
    }

    public static final RegistryObject<SoundEvent> CARDBOARD_BOX_CLOSE = register("block.cardboard_box.close");
    public static final RegistryObject<SoundEvent> CARDBOARD_BOX_OPEN = register("block.cardboard_box.open");
    public static final RegistryObject<SoundEvent> CONTAINER_BREAK = register("block.container.break");
    public static final RegistryObject<SoundEvent> CRYSTAL_EXTEND = register("block.crystal.extend");
    public static final RegistryObject<SoundEvent> GAS_HISS = register("block.gas.ambient");
    public static final RegistryObject<SoundEvent> KEYPAD_CLICK = register("block.keypad.click");
    public static final RegistryObject<SoundEvent> KEYPAD_LOCK = register("block.keypad.lock");
    public static final RegistryObject<SoundEvent> KEYPAD_UNLOCK_SUCCESS = register("block.keypad.unlock_success");
    public static final RegistryObject<SoundEvent> KEYPAD_UNLOCK_FAIL = register("block.keypad.unlock_fail");
    public static final RegistryObject<SoundEvent> LAB_DOOR_CLOSE = register("block.lab_door.close");
    public static final RegistryObject<SoundEvent> LAB_DOOR_LOCKED = register("block.lab_door.locked");
    public static final RegistryObject<SoundEvent> LAB_DOOR_OPEN = register("block.lab_door.open");
    public static final RegistryObject<SoundEvent> PUDDLE_ALERT = register("block.latex_puddle.alert");
    public static final RegistryObject<SoundEvent> LIBRARY_DOOR_CLOSE = register("block.library_door.close");
    public static final RegistryObject<SoundEvent> LIBRARY_DOOR_LOCKED = register("block.library_door.locked");
    public static final RegistryObject<SoundEvent> LIBRARY_DOOR_OPEN = register("block.library_door.open");
    public static final RegistryObject<SoundEvent> MAINTENANCE_DOOR_CLOSE = register("block.maintenance_door.close");
    public static final RegistryObject<SoundEvent> MAINTENANCE_DOOR_LOCKED = register("block.maintenance_door.locked");
    public static final RegistryObject<SoundEvent> MAINTENANCE_DOOR_OPEN = register("block.maintenance_door.open");
    public static final RegistryObject<SoundEvent> RETINAL_SCAN = register("block.retinal_scanner.scan");
    public static final RegistryObject<SoundEvent> STASIS_CHAMBER_CREATE_LATEX = register("block.stasis_chamber.create_latex");
    public static final RegistryObject<SoundEvent> STASIS_CHAMBER_DISCARD_LATEX = register("block.stasis_chamber.discard_latex");
    public static final RegistryObject<SoundEvent> STASIS_CHAMBER_DOOR_CLOSE = register("block.stasis_chamber.door_close");
    public static final RegistryObject<SoundEvent> STASIS_CHAMBER_DOOR_OPEN = register("block.stasis_chamber.door_open");
    public static final RegistryObject<SoundEvent> STASIS_CHAMBER_MODIFY_LATEX = register("block.stasis_chamber.modify_latex");
    public static final RegistryObject<SoundEvent> WHITE_LATEX_PILLAR_EXTEND = register("block.white_latex_pillar.extend");

    public static final RegistryObject<SoundEvent> BEHEMOTH_HAND_ENTER_LATEX = register("entity.behemoth.hand.enter_latex");
    public static final RegistryObject<SoundEvent> BEHEMOTH_HAND_EXIT_LATEX = register("entity.behemoth.hand.exit_latex");
    public static final RegistryObject<SoundEvent> DARK_LATEX_PUP_FORM_PUDDLE = register("entity.dark_latex_pup.form_puddle");
    public static final RegistryObject<SoundEvent> EXOSKELETON_CHIME = register("entity.exoskeleton.chime");
    public static final RegistryObject<SoundEvent> EXOSKELETON_LOCK = register("entity.exoskeleton.lock");
    public static final RegistryObject<SoundEvent> EXOSKELETON_STEP = register("entity.exoskeleton.step");
    public static final RegistryObject<SoundEvent> TRANSFUR_HURT = register("entity.generic.transfur_hurt");
    public static final RegistryObject<SoundEvent> TRANSFUR_BY_LATEX = register("entity.generic.transfur.latex");
    public static final RegistryObject<SoundEvent> TRANSFUR_BY_NOT_LATEX = register("entity.generic.transfur.not_latex");
    public static final RegistryObject<SoundEvent> ROOMBA_AMBIENT = register("entity.roomba.ambient");
    public static final RegistryObject<SoundEvent> SIREN_SING = register("entity.siren.sing");
    public static final RegistryObject<SoundEvent> TIGER_SHARK_ROAR = register("entity.tiger_shark.roar");
    public static final RegistryObject<SoundEvent> LASER_FIRE = register("entity.laser.fire");
    public static final RegistryObject<SoundEvent> LATEX_DRIP = register("entity.latex.drip");
    public static final RegistryObject<SoundEvent> ENTITY_ENTER_LATEX = register("entity.latex.enter_latex");
    public static final RegistryObject<SoundEvent> ENTITY_EXIT_LATEX = register("entity.latex.exit_latex");
    public static final RegistryObject<SoundEvent> LATEX_FUSE_ENTITY = register("entity.latex.fuse_entity");
    public static final RegistryObject<SoundEvent> LATEX_GRAB_ENTITY = register("entity.latex.grab_entity");
    public static final RegistryObject<SoundEvent> LATEX_SUIT_ENTITY = register("entity.latex.suit_entity");
    public static final RegistryObject<SoundEvent> LATEX_UNGRAB_ENTITY = register("entity.latex.ungrab_entity");
    public static final RegistryObject<SoundEvent> LATEX_UNSUIT_ENTITY = register("entity.latex.unsuit_entity");

    public static final RegistryObject<SoundEvent> UNDERWATER_BOOST = register("ability.underwater_dash.boost");
    public static final RegistryObject<SoundEvent> WING_FLAP = register("ability.wing_flap");
    public static final RegistryObject<SoundEvent> INK_SMOKE_SCREEN = register("ability.ink_smoke_screen");

    public static final RegistryObject<SoundEvent> BRA_BREAK = register("item.bra.break");
    public static final RegistryObject<SoundEvent> BRA_EQUIP = register("item.bra.equip");
    public static final RegistryObject<SoundEvent> COAT_BREAK = register("item.coat.break");
    public static final RegistryObject<SoundEvent> COAT_EQUIP = register("item.coat.equip");
    public static final RegistryObject<SoundEvent> COLLAR_BREAK = register("item.collar.break");
    public static final RegistryObject<SoundEvent> COLLAR_EQUIP = register("item.collar.equip");
    public static final RegistryObject<SoundEvent> DARK_LATEX_MASK_COMPLETE_TRANSFUR = register("item.dark_latex_mask.complete_transfur");
    public static final RegistryObject<SoundEvent> GLOVES_BREAK = register("item.gloves.break");
    public static final RegistryObject<SoundEvent> GLOVES_EQUIP = register("item.gloves.equip");
    public static final RegistryObject<SoundEvent> FACE_MASK_BREAK = register("item.face_mask.break");
    public static final RegistryObject<SoundEvent> FACE_MASK_EQUIP = register("item.face_mask.equip");
    public static final RegistryObject<SoundEvent> NECK_TIE_BREAK = register("item.neck_tie.break");
    public static final RegistryObject<SoundEvent> NECK_TIE_EQUIP = register("item.neck_tie.equip");
    public static final RegistryObject<SoundEvent> PANTS_BREAK = register("item.pants.break");
    public static final RegistryObject<SoundEvent> PANTS_EQUIP = register("item.pants.equip");
    public static final RegistryObject<SoundEvent> TSC_WEAPON_SHOCK = register("item.tsc_weapon.shock");
    public static final RegistryObject<SoundEvent> SHIRT_BREAK = register("item.shirt.break");
    public static final RegistryObject<SoundEvent> SHIRT_EQUIP = register("item.shirt.equip");
    public static final RegistryObject<SoundEvent> SHORTS_BREAK = register("item.shorts.break");
    public static final RegistryObject<SoundEvent> SHORTS_EQUIP = register("item.shorts.equip");
    public static final RegistryObject<SoundEvent> SYRINGE_PRICK = register("item.syringe.prick");
    public static final RegistryObject<SoundEvent> WETSUIT_BREAK = register("item.wetsuit.break");
    public static final RegistryObject<SoundEvent> WETSUIT_EQUIP = register("item.wetsuit.equip");

    public static final RegistryObject<SoundEvent> MUSIC_BLACK_GOO_ZONE = register("music.black_goo_zone");
    public static final RegistryObject<SoundEvent> MUSIC_CRYSTAL_ZONE = register("music.crystal_zone");
    public static final RegistryObject<SoundEvent> MUSIC_GAS_ROOM = register("music.gas_room");
    public static final RegistryObject<SoundEvent> MUSIC_LABORATORY = register("music.laboratory");
    public static final RegistryObject<SoundEvent> MUSIC_OUTSIDE_THE_TOWER = register("music.outside_the_tower");
    public static final RegistryObject<SoundEvent> MUSIC_PURO_THE_BLACK_GOO = register("music.puro_the_black_goo");
    public static final RegistryObject<SoundEvent> MUSIC_PUROS_HOME = register("music.puros_home");
    public static final RegistryObject<SoundEvent> MUSIC_THE_LIBRARY = register("music.the_library");
    public static final RegistryObject<SoundEvent> MUSIC_THE_LION_CHASE = register("music.the_lion_chase");
    public static final RegistryObject<SoundEvent> MUSIC_THE_SCARLET_CRYSTAL_MINE = register("music.the_scarlet_crystal_mine");
    public static final RegistryObject<SoundEvent> MUSIC_THE_SHARK = register("music.the_shark");
    public static final RegistryObject<SoundEvent> MUSIC_THE_SQUID_DOG = register("music.the_squid_dog");
    public static final RegistryObject<SoundEvent> MUSIC_THE_WHITE_GOO_JUNGLE = register("music.the_white_goo_jungle");
    public static final RegistryObject<SoundEvent> MUSIC_THE_WHITE_TAIL_CHASE_PART_1 = register("music.the_white_tail_chase_part_1");
    public static final RegistryObject<SoundEvent> MUSIC_THE_WHITE_TAIL_CHASE_PART_2 = register("music.the_white_tail_chase_part_2");
    public static final RegistryObject<SoundEvent> MUSIC_VENT_PIPE = register("music.vent_pipe");

    public static class Types {
        // Represents a sound type that has no sound
        public static final SoundType NONE = new SoundType(-100, 1, SoundEvents.METAL_BREAK, SoundEvents.METAL_STEP, SoundEvents.METAL_PLACE, SoundEvents.METAL_HIT, SoundEvents.METAL_FALL);
    }

    public static void broadcastSound(ServerLevel level, RegistryObject<SoundEvent> event, SoundSource source, double x, double y, double z, float volume, float pitch) {
        level.getServer().getPlayerList().broadcastAll(new ClientboundSoundPacket(
                event.getHolder().orElseThrow(), SoundSource.BLOCKS, x, y, z, volume, pitch, level.random.nextLong()));
    }

    public static void broadcastSound(ServerLevel level, RegistryObject<SoundEvent> event, BlockPos blockPos, float volume, float pitch) {
        broadcastSound(level, event, SoundSource.BLOCKS, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, volume, pitch);
    }

    public static void broadcastSound(Entity entity, RegistryObject<SoundEvent> event, float volume, float pitch) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            broadcastSound(serverLevel, event, SoundSource.NEUTRAL, entity.getX(), entity.getY(), entity.getZ(), volume, pitch);
        }
    }

    public static void broadcastSound(ServerLevel level, ResourceLocation name, SoundSource source, double x, double y, double z, float volume, float pitch) {
        level.getServer().getPlayerList().broadcastAll(new ClientboundSoundPacket(
                ForgeRegistries.SOUND_EVENTS.getHolder(name).orElseThrow(), source, x, y, z, volume, pitch, level.random.nextLong()));
    }

    public static void broadcastSound(Entity entity, ResourceLocation name, float volume, float pitch) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().broadcastAndSend(entity, new ClientboundSoundPacket(
                    ForgeRegistries.SOUND_EVENTS.getHolder(name).orElseThrow(), SoundSource.NEUTRAL, entity.getX(), entity.getY(), entity.getZ(), volume, pitch, serverLevel.random.nextLong()));
        }
    }

    public static void sendLocalSound(Player player, RegistryObject<SoundEvent> event, float volume, float pitch) {
        if (player instanceof ServerPlayer serverPlayer)
            serverPlayer.connection.send(new ClientboundSoundPacket(
                    event.getHolder().orElseThrow(), SoundSource.NEUTRAL, player.getX(), player.getY(), player.getZ(), volume, pitch, player.getRandom().nextLong()));
    }

    public static void sendLocalSound(Player player, BlockPos blockPos, RegistryObject<SoundEvent> event, float volume, float pitch) {
        if (player instanceof ServerPlayer serverPlayer)
            serverPlayer.connection.send(new ClientboundSoundPacket(
                    event.getHolder().orElseThrow(), SoundSource.NEUTRAL, blockPos.getX(), blockPos.getY(), blockPos.getZ(), volume, pitch, player.getRandom().nextLong()));
    }

    public static void sendLocalSound(Player player, Vec3 pos, RegistryObject<SoundEvent> event, float volume, float pitch) {
        if (player instanceof ServerPlayer serverPlayer)
            serverPlayer.connection.send(new ClientboundSoundPacket(
                    event.getHolder().orElseThrow(), SoundSource.NEUTRAL, pos.x(), pos.y(), pos.z(), volume, pitch, player.getRandom().nextLong()));
    }
}
