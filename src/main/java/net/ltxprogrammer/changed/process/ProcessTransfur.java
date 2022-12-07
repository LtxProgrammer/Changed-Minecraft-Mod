package net.ltxprogrammer.changed.process;

import com.mojang.logging.LogUtils;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.WhiteLatexBlock;
import net.ltxprogrammer.changed.command.CommandTransfur;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.network.packet.CheckForUpdatesPacket;
import net.ltxprogrammer.changed.network.packet.SyncTransfurPacket;
import net.ltxprogrammer.changed.network.packet.SyncTransfurProgressPacket;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.profiling.jfr.event.WorldLoadFinishedEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;

import static net.ltxprogrammer.changed.init.ChangedGameRules.RULE_KEEP_BRAIN;


@Mod.EventBusSubscriber(modid = Changed.MODID)
public class ProcessTransfur {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final int TRANSFUR_PROGRESSION_TAKEOVER = 24000;
    public static record TransfurProgress(int ticks, ResourceLocation type) {}

    public static void setPlayerTransfurProgress(Player player, TransfurProgress progress) {
        try {
            var oldProgress = (TransfurProgress)latexVariantField.get(player);
            if (progress != null && oldProgress != null && progress.equals(oldProgress))
                return;
            if (progress == null && oldProgress == null)
                return;
            transfurProgressField.set(player, progress);
            if (player instanceof ServerPlayer serverPlayer)
                Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncTransfurProgressPacket(progress));
        } catch (Exception ignored) {}
    }

    public static TransfurProgress getPlayerTransfurProgress(Player player) {
        try {
            return (TransfurProgress) transfurProgressField.get(player);
        } catch (Exception ignored) {}
        return null;
    }

    public static boolean progressPlayerTransfur(Player player, int amount, ResourceLocation type) {
        if (player.isCreative() || player.isSpectator())
            return false;
        int next = getPlayerTransfurProgress(player).ticks + amount;
        setPlayerTransfurProgress(player, new TransfurProgress(next, type));
        return next >= TRANSFUR_PROGRESSION_TAKEOVER;
    }

    public static boolean progressTransfur(LivingEntity entity, int amount, ResourceLocation type) {
        if (entity instanceof Player player)
            return progressPlayerTransfur(player, amount, type);
        else {
            float damage = amount / 1200.0f;
            float health = entity.getHealth();
            LatexVariant<?> latexVariant = LatexVariant.ALL_LATEX_FORMS.getOrDefault(type, LatexVariant.LIGHT_LATEX_WOLF.male());

            if (entity.getType().is(ChangedTags.EntityTypes.HUMANOIDS)) {
                if (health <= damage && health > 0.0F) {
                    ProcessTransfur.transfur(entity, entity.level, latexVariant, false);
                    return true;
                }

                else {
                    entity.hurt(ChangedDamageSources.TRANSFUR, damage);
                    return false;
                }
            }

            else {
                List<LatexVariant<?>> mobFusion = new ArrayList<>();
                for (var checkVariant : LatexVariant.MOB_FUSION_LATEX_FORMS.values()) {
                    if (checkVariant.isFusionOf(latexVariant, entity.getClass())) {
                        mobFusion.add(checkVariant);
                    }
                }
                if (mobFusion.isEmpty())
                    return false;

                if (health <= damage && health > 0.0F) {
                    ProcessTransfur.transfur(entity, entity.level, mobFusion.get(entity.getRandom().nextInt(mobFusion.size())), false);
                    return true;
                }

                else {
                    entity.hurt(ChangedDamageSources.TRANSFUR, damage);
                    return false;
                }
            }
        }
    }

    private static long lastDayTime = 0;
    public static void tickPlayerTransfurProgress(Player player) {
        if (isPlayerLatex(player))
            return;
        var progress = getPlayerTransfurProgress(player);
        if (progress.ticks >= TRANSFUR_PROGRESSION_TAKEOVER) {
            if (LatexVariant.PUBLIC_LATEX_FORMS.containsKey(progress.type))
                transfur(player, player.level, LatexVariant.ALL_LATEX_FORMS.get(progress.type), false);
            else {
                var variant = PatreonBenefits.getPlayerSpecialVariant(player.getUUID());
                transfur(player, player.level, variant == null ? LatexVariant.LIGHT_LATEX_WOLF.male() : variant, false);
            }
        }

        else {
            if (lastDayTime > player.level.getDayTime())
                lastDayTime = player.level.getDayTime() - 1;
            setPlayerTransfurProgress(player, new TransfurProgress(progress.ticks > 0 ? (int) (progress.ticks - (player.level.getDayTime() - lastDayTime)) : 0, progress.type));
            lastDayTime = player.level.getDayTime();
        }
    }

    private static final Field latexVariantField;
    private static final Field transfurProgressField;

    static {
        Field latexVariantField1;
        Field transfurProgressField1;
        try {
            latexVariantField1 = Player.class.getField("latexVariant");
            transfurProgressField1 = Player.class.getField("transfurProgress");
        } catch (NoSuchFieldException e) {
            latexVariantField1 = null;
            transfurProgressField1 = null;
            e.printStackTrace();
        }
        latexVariantField = latexVariantField1;
        transfurProgressField = transfurProgressField1;
    }

    public static LatexVariant<?> getPlayerLatexVariant(Player player) {
        try {
            return (LatexVariant<?>) latexVariantField.get(player);
        } catch (Exception ignored) {}
        return null;
    }

    public static void setPlayerLatexVariant(Player player, @Nullable LatexVariant<?> variant) {
        try {
            var oldVariant = (LatexVariant<?>)latexVariantField.get(player);
            if (variant != null && oldVariant != null && variant.getFormId().equals(oldVariant.getFormId()))
                return;
            if (variant == null && oldVariant == null)
                return;
            if (oldVariant != null && oldVariant.getLatexEntity() != null)
                oldVariant.getLatexEntity().discard();
            latexVariantField.set(player, variant);
            if (variant != null)
                variant.generateForm(player, player.level);
            if (oldVariant != null)
                oldVariant.unhookAll(player);
        } catch (Exception ignored) {}
    }

    public static void setPlayerLatexVariantNamed(Player player, ResourceLocation variant) {
        var type = LatexVariant.ALL_LATEX_FORMS.get(variant);
        setPlayerLatexVariant(player, type == null ? null : type.clone());
    }

    public static boolean isPlayerLatex(Player player) {
        return getPlayerLatexVariant(player) != null;
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        WhiteLatexBlock.whiteLatexNoCollideMap.clear();
        CACHED_FORMS.clear();
    }

    public static class LatexedEntity {
        final LivingEntity entity;
        final LatexVariant<?> variant;
        @NotNull
        final LatexVariant<?> transfur;
        final boolean isPlayer;

        LatexedEntity(LivingEntity entity) {
            this.entity = entity;
            this.variant = LatexVariant.getEntityVariant(entity);
            this.transfur = LatexVariant.getEntityTransfur(entity);
            this.isPlayer = entity instanceof Player;
        }

        static boolean isLatexed(LivingEntity entity) {
            return LatexVariant.getEntityVariant(entity) != null || LatexVariant.getEntityTransfur(entity) != null;
        }
    }

    static void nonLatexedEntityKillingBlowByLatexEntity(LivingAttackEvent event, LivingEntity target, LatexedEntity source) {
        boolean flag = false;
        List<LatexVariant<?>> mobFusion = new ArrayList<>();
        for (var checkVariant : LatexVariant.MOB_FUSION_LATEX_FORMS.values()) {
            if (checkVariant.isFusionOf(source.transfur, event.getEntityLiving().getClass())) {
                mobFusion.add(checkVariant);
            }
        }
        if (mobFusion.isEmpty()) {
            if (source.entity instanceof LatexEntity latexEntity)
                flag = latexEntity.getTransfurMode() == TransfurMode.ABSORPTION;

            if (source.variant == null || source.variant.transfurMode() == TransfurMode.ABSORPTION || flag) {
                source.entity.heal(14f);
                if (source.entity instanceof Player player) {
                    player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() + 10);
                    source.entity.setPos(target.position());
                    target.discard();

                    if (source.variant == null || !source.transfur.getFormId().equals(source.variant.getFormId())) {
                        ProcessTransfur.getPlayerLatexVariant(player).unhookAll(player);
                        ProcessTransfur.setPlayerLatexVariant(player, source.transfur.clone());

                        Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), SyncTransfurPacket.Builder.of(player));
                    }
                }
                else {
                    LatexEntity newEntity = source.transfur.replaceEntity(target);
                    source.entity.discard();
                }
                ChangedSounds.broadcastSound(source.entity, ChangedSounds.POISON, 1.0f, 1.0f);
            }

            else {
                source.entity.heal(4f);
                transfur(target, target.level, source.transfur, false);
            }
        }

        else {
            LatexVariant<?> fuseVariant = mobFusion.get(target.getRandom().nextInt(mobFusion.size()));
            source.entity.heal(14f);
            if (source.entity instanceof Player player) {
                player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() + 10);
                source.entity.setPos(target.position());
                target.discard();

                if (source.variant == null || !fuseVariant.getFormId().equals(source.variant.getFormId())) {
                    ProcessTransfur.getPlayerLatexVariant(player).unhookAll(player);
                    ProcessTransfur.setPlayerLatexVariant(player, fuseVariant.clone());

                    Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), SyncTransfurPacket.Builder.of(player));
                }
            }
            else {
                LatexEntity newEntity = fuseVariant.replaceEntity(target);
                source.entity.discard();
            }
            ChangedSounds.broadcastSound(source.entity, ChangedSounds.POISON, 1.0f, 1.0f);
        }

        event.setCanceled(true);
    }

    static void nonLatexedPlayerAttackedByLatexedEntity(LivingAttackEvent event, Player target, LatexedEntity source) {
        event.setCanceled(true);
        target.hurt(ChangedDamageSources.TRANSFUR, 0.25f);
        target.heal(0.25f);
        boolean flag = false;
        if (source.entity instanceof LatexEntity latexEntity)
            flag = latexEntity.getTransfurMode() == TransfurMode.ABSORPTION;

        if (source.transfur != null && progressPlayerTransfur(target, target.level.getGameRules().getInt(ChangedGameRules.RULE_TRANSFUR_TOLERANCE), source.transfur.getFormId()) && (source.transfur.transfurMode() == TransfurMode.ABSORPTION || flag)) {
            if (!source.isPlayer)
                source.entity.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    @SubscribeEvent
    public static void onLivingAttacked(LivingAttackEvent event) {
        if (event.getSource() == ChangedDamageSources.TRANSFUR)
            return;
        if (event.getSource() == DamageSource.CACTUS && LatexVariant.getEntityVariant(event.getEntityLiving()) != null) {
            event.setCanceled(true);
            return;
        }
        if (event.getSource().isFire() && LatexVariant.getEntityVariant(event.getEntityLiving()) != null) {
            event.getEntityLiving().hurt(DamageSource.GENERIC, 1.5f);
            return;
        }
        if (!(event.getSource().getEntity() instanceof LivingEntity sourceEntity))
            return;
        if (event.getSource().isProjectile())
            return;
        if (event.getEntityLiving().isBlocking())
            return;

        if (sourceEntity.hasPassenger(event.getEntityLiving()) || event.getEntityLiving().hasPassenger(sourceEntity)) {
            event.setCanceled(true);
            return;
        }

        LatexVariant<?> sourceVariant = LatexVariant.getEntityVariant(sourceEntity);
        LatexVariant<?> playerVariant = LatexVariant.getEntityVariant(event.getEntityLiving());
        for (var checkVariant : LatexVariant.FUSION_LATEX_FORMS.values()) {
            if (checkVariant.isFusionOf(sourceVariant, playerVariant)) {
                event.setCanceled(true);
                transfur(event.getEntityLiving(), sourceEntity.level, sourceVariant, true);
                if (sourceEntity instanceof Player) {
                    if (event.getEntityLiving() instanceof Player pvpLoser)
                        pvpLoser.kill();
                    else
                        event.getEntityLiving().discard();
                }

                else {
                    sourceEntity.discard();
                }

                return;
            }
        }

        LatexType factionD = LatexType.getEntityFactionLatexType(event.getEntityLiving());
        LatexType factionS = LatexType.getEntityFactionLatexType(event.getSource().getEntity());
        if (factionD == factionS && factionS != null) {
            event.setCanceled(true);
            return;
        }

        boolean mobFusion = false;
        for (var checkVariant : LatexVariant.MOB_FUSION_LATEX_FORMS.values()) {
            if (checkVariant.isFusionOf(sourceVariant, event.getEntityLiving().getClass())) {
                mobFusion = true;
                break;
            }
        }
        if (!event.getEntityLiving().getType().is(ChangedTags.EntityTypes.HUMANOIDS) && !mobFusion)
            return;

        if ((LatexVariant.getEntityVariant(event.getEntityLiving()) != null) == (LatexVariant.getEntityTransfur(sourceEntity) != null))
            return;

        float damageAdjustment = sourceEntity instanceof Player && sourceEntity.getItemInHand(sourceEntity.getUsedItemHand()).is(Items.AIR) ? 4.0f : 1.0f;
        if (damageAdjustment > 1.0f)
            event.getEntityLiving().hurt(ChangedDamageSources.TRANSFUR, event.getAmount() * (damageAdjustment - 1.0f));

        //The entity getting hurt is a morph. Cancel the event.
        if(event.getEntityLiving().getPersistentData().contains(LatexVariant.NBT_PLAYER_ID)) {
            event.setCanceled(true);
        }

        else if (event.getEntityLiving() instanceof Player player && !isPlayerLatex(player) && sourceEntity.getItemInHand(sourceEntity.getUsedItemHand()).is(Items.AIR)) {
            nonLatexedPlayerAttackedByLatexedEntity(event, player, new LatexedEntity(sourceEntity));
        }

        else if (event.getEntityLiving().getHealth() - (event.getAmount() * damageAdjustment) <= 0.0F && LatexedEntity.isLatexed(sourceEntity) && !LatexedEntity.isLatexed(event.getEntityLiving())
                && sourceEntity.getItemInHand(sourceEntity.getUsedItemHand()).is(Items.AIR)) {
            nonLatexedEntityKillingBlowByLatexEntity(event, event.getEntityLiving(), new LatexedEntity(sourceEntity));
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        //The entity dying is a morph. Cancel the event.
        if (event.getEntityLiving().getPersistentData().contains(LatexVariant.NBT_PLAYER_ID)) {
            event.setCanceled(true);
            return;
        }
    }

    private static int worldTickCount = 0;
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.side.isServer()) {
            worldTickCount++;

            if (worldTickCount % 120 == 0) { // Discrete sync packet
                SyncTransfurPacket.Builder builder = new SyncTransfurPacket.Builder();
                ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(builder::addPlayer);

                Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), builder.build());
            }

            if (worldTickCount % 1200 == 0) {
                try {
                    if (PatreonBenefits.checkForUpdates())
                        Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new CheckForUpdatesPacket(PatreonBenefits.currentVersion));
                }

                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            LatexVariant<?> variant = getPlayerLatexVariant(event.player);

            if (variant != null && !variant.isDead()) {
                try {
                    variant.tick(event.player);
                    variant.getLatexEntity().visualTick(event.player.level);
                    variant.getLatexEntity().effectTick(event.player.level, event.player);
                } catch (Exception x) {
                    x.printStackTrace();
                }

                if (event.side.isServer())
                    CACHED_FORMS.put(event.player.getUUID(), variant.getFormId());
            }

            else if (event.side.isServer() && !event.player.level.getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM))
                CACHED_FORMS.remove(event.player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof Player player) {
            LatexVariant<?> variant = getPlayerLatexVariant(player);
            if (variant != null && !player.level.getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM)) {
                variant.setDead();
                variant.unhookAll(player);
            }
        }
    }

    private static Map<UUID, ResourceLocation> CACHED_FORMS = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerSpawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntityLiving() instanceof Player player) {
            LatexVariant<?> variant = getPlayerLatexVariant(player);
            if (variant != null && variant.isDead()) {
                variant.unhookAll(player);
                setPlayerLatexVariant(player, null);
                if (!player.level.isClientSide)
                    Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), SyncTransfurPacket.Builder.of(player));
            }

            if (variant == null && CACHED_FORMS.containsKey(player.getUUID())) {
                setPlayerLatexVariantNamed(player, CACHED_FORMS.get(player.getUUID()));
                if (!player.level.isClientSide)
                    Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), SyncTransfurPacket.Builder.of(player));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SyncTransfurPacket.Builder builder = new SyncTransfurPacket.Builder();
            player.getServer().getPlayerList().getPlayers().forEach(builder::addPlayer);

            Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), builder.build());
        }
    }

    // Transfurs an entity, keepConscious applies to players being transfurred
    public static void transfur(LivingEntity entity, Level level, LatexVariant<?> variant, boolean keepConscious) {
        if (entity.isDeadOrDying())
            return; // To prevent most bugs, entity has to be alive to transfur
        if (level.getGameRules().getBoolean(RULE_KEEP_BRAIN) || (entity instanceof Player player && player.isCreative()))
            keepConscious = true;

        if (entity.level.isClientSide && keepConscious && entity instanceof LocalPlayer player) {
            return;
        }

        if (entity == null)
            return;
        if (variant == null)
            return;
        if (!LatexType.hasLatexType(entity)) {
            ChangedSounds.broadcastSound(entity, ChangedSounds.POISON, 1.0f, 1.0f);
            LatexType.setEntityLatexType(entity, variant.getLatexType());
            if (keepConscious && entity instanceof ServerPlayer player) {
                ChangedCriteriaTriggers.TRANSFUR.trigger(player, variant);

                LatexVariant<?> uniqueVariant = variant.clone();

                setPlayerLatexVariant(player, uniqueVariant);

                Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), SyncTransfurPacket.Builder.of(player));

                // Force retargeting
                for (LatexEntity latexEntity : level.getEntitiesOfClass(LatexEntity.class, player.getBoundingBox().inflate(64))) {
                    if (latexEntity.getLastHurtByMob() == player) {
                        latexEntity.setLastHurtByMob(null);
                    }

                    if (latexEntity.getTarget() == player) {
                        latexEntity.setTarget(null);
                        latexEntity.targetSelector.tick();
                        latexEntity.targetSelector.getRunningGoals().forEach(WrappedGoal::stop);
                    }
                }

                player.heal(10.0F);

                LOGGER.info("Transfurred " + entity + " into " + variant);
            }

            else if (!entity.level.isClientSide) {
                variant.replaceEntity(entity);
            }
        }

        else {
            LatexVariant<?> current = LatexVariant.getEntityVariant(entity);
            List<LatexVariant<?>> possible = new ArrayList<>();
            for (var checkVariant : LatexVariant.FUSION_LATEX_FORMS.values()) {
                if (checkVariant.isFusionOf(current, variant))
                    possible.add(checkVariant);
            }

            if (possible.isEmpty())
                return;

            LatexVariant<?> fusion = possible.get(level.random.nextInt(possible.size()));
            ChangedSounds.broadcastSound(entity, ChangedSounds.POISON, 1.0f, 1.0f);

            if (entity instanceof ServerPlayer player) {
                ChangedCriteriaTriggers.TRANSFUR.trigger(player, fusion);

                LatexVariant<?> uniqueVariant = fusion.clone();

                setPlayerLatexVariant(player, uniqueVariant);

                Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), SyncTransfurPacket.Builder.of(player));

                // Force retargeting
                for (LatexEntity latexEntity : level.getEntitiesOfClass(LatexEntity.class, player.getBoundingBox().inflate(64))) {
                    if (latexEntity.getLastHurtByMob() == player) {
                        latexEntity.setLastHurtByMob(null);
                    }

                    if (latexEntity.getTarget() == player) {
                        latexEntity.setTarget(null);
                        latexEntity.targetSelector.tick();
                        latexEntity.targetSelector.getRunningGoals().forEach(WrappedGoal::stop);
                    }
                }

                player.heal(10.0F);

                LOGGER.info("Fused " + entity + " with " + variant);
            }

            else if (!entity.level.isClientSide) {
                fusion.replaceEntity(entity);
            }
        }
    }
}
