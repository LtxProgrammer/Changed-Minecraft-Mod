package net.ltxprogrammer.changed.process;

import com.mojang.logging.LogUtils;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.WhiteLatexBlock;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.OrganicLatex;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.network.packet.CheckForUpdatesPacket;
import net.ltxprogrammer.changed.network.packet.SyncTransfurPacket;
import net.ltxprogrammer.changed.network.packet.SyncTransfurProgressPacket;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
        if (player.isCreative() || player.isSpectator() || ProcessTransfur.isPlayerLatex(player))
            return false;
        boolean justHit = player.invulnerableTime == 20 && player.hurtDuration == 10;

        if (player.invulnerableTime > 10 && !justHit) {
            return getPlayerTransfurProgress(player).ticks >= TRANSFUR_PROGRESSION_TAKEOVER;
        }

        else {
            player.invulnerableTime = 20;
            player.hurtDuration = 10;
            player.hurtTime = player.hurtDuration;

            int next = getPlayerTransfurProgress(player).ticks + amount;
            setPlayerTransfurProgress(player, new TransfurProgress(next, type));
            return next >= TRANSFUR_PROGRESSION_TAKEOVER;
        }
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
            if (variant != null && LatexVariant.ALL_LATEX_FORMS.containsValue(variant))
                variant = variant.clone();

            var oldVariant = (LatexVariant<?>)latexVariantField.get(player);
            if (variant != null && oldVariant != null && variant.getFormId().equals(oldVariant.getFormId()))
                return;
            if (variant == null && oldVariant == null)
                return;
            if (oldVariant != null && oldVariant.getLatexEntity() != null)
                oldVariant.getLatexEntity().discard();
            latexVariantField.set(player, variant);
            if (variant != null)
                variant.generateForm(player, player.level).setUnderlyingPlayer(player);
            if (oldVariant != null)
                oldVariant.unhookAll(player);
            if (!player.level.isClientSide)
                Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), SyncTransfurPacket.Builder.of(player));
        } catch (Exception ignored) {}
    }

    public static void setPlayerLatexVariantNamed(Player player, ResourceLocation variant) {
        setPlayerLatexVariant(player, LatexVariant.ALL_LATEX_FORMS.get(variant));
    }

    public static boolean isPlayerLatex(Player player) {
        return getPlayerLatexVariant(player) != null;
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        WhiteLatexBlock.whiteLatexNoCollideMap.clear();
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

    private static void bonusHurt(LivingEntity entity, DamageSource p_108729_, float p_108730_, boolean overrideImmunity) {
        if (!entity.isInvulnerableTo(p_108729_) || overrideImmunity) {
            boolean justHit = entity.invulnerableTime == 20 && entity.hurtDuration == 10;

            if (justHit || entity.invulnerableTime <= 0 || overrideImmunity)
                entity.setHealth(entity.getHealth() - p_108730_);
        }
    }

    private static boolean isOrganicLatex(LivingEntity entity) {
        return entity instanceof OrganicLatex || (LatexVariant.getEntityVariant(entity) != null && LatexVariant.getEntityVariant(entity).getLatexEntity() instanceof OrganicLatex);
    }

    @SubscribeEvent
    public static void onLivingDamaged(LivingDamageEvent event) {
        if (event.getSource().isFire() && LatexVariant.getEntityVariant(event.getEntityLiving()) != null) {
            if (isOrganicLatex(event.getEntityLiving()))
                return;
            event.setAmount(event.getAmount() * 2.0F);
        }
    }

    protected static void onLivingAttackedByLatex(LivingAttackEvent event, LatexedEntity source) {
        LivingEntity entity = event.getEntityLiving();

        // First, check for fusion
        LatexVariant<?> playerVariant = LatexVariant.getEntityVariant(event.getEntityLiving());
        for (var checkVariant : LatexVariant.FUSION_LATEX_FORMS.values()) {
            if (checkVariant.isFusionOf(source.variant, playerVariant)) {
                event.setCanceled(true);
                transfur(event.getEntityLiving(), source.entity.level, source.variant, true);
                if (source.isPlayer) {
                    if (event.getEntityLiving() instanceof Player pvpLoser) {
                        pvpLoser.setLastHurtByMob(source.entity);
                        pvpLoser.hurt(ChangedDamageSources.TRANSFUR, 999999999.0f);
                    }
                    else
                        event.getEntityLiving().discard();
                }

                else {
                    source.entity.discard();
                }

                return;
            }
        }

        // Check for faction immunity
        LatexType factionD = LatexType.getEntityFactionLatexType(event.getEntityLiving());
        LatexType factionS = LatexType.getEntityFactionLatexType(source.entity);
        if (factionD == factionS && factionS != null) {
            event.setCanceled(true);
            return;
        }

        // Check if attacked entity is already latexed
        if (LatexedEntity.isLatexed(event.getEntityLiving()))
            return;
        // Check if attacked entity is not humanoid
        if (!event.getEntityLiving().getType().is(ChangedTags.EntityTypes.HUMANOIDS)) {
            bonusHurt(entity, ChangedDamageSources.TRANSFUR, 2.0f, false);
            return;
        }

        event.setCanceled(true);
        double d1 = source.entity.getX() - entity.getX();

        double d0;
        for(d0 = source.entity.getZ() - entity.getZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
            d1 = (Math.random() - Math.random()) * 0.01D;
        }

        entity.hurtDir = (float)(Mth.atan2(d0, d1) * (double)(180F / (float)Math.PI) - (double)entity.getYRot());
        entity.knockback((double)0.4F, d1, d0);

        entity.hurt(ChangedDamageSources.TRANSFUR, 0.0F);
        boolean flag = false;
        if (source.entity instanceof LatexEntity latexEntity)
            flag = latexEntity.getTransfurMode() == TransfurMode.ABSORPTION;

        if (source.transfur != null && progressTransfur(event.getEntityLiving(), source.entity.level.getGameRules().getInt(ChangedGameRules.RULE_TRANSFUR_TOLERANCE),
                source.transfur.getFormId()) && (source.transfur.transfurMode() == TransfurMode.ABSORPTION || flag)) {
            if (!source.isPlayer)
                source.entity.discard();
        }
    }

    @SubscribeEvent
    public static void onLivingAttacked(LivingAttackEvent event) {
        //The entity getting hurt is a morph. Cancel the event.
        if(event.getEntityLiving().getPersistentData().contains(LatexVariant.NBT_PLAYER_ID)) {
            event.setCanceled(true);
            return;
        }
        if (event.getSource() == ChangedDamageSources.TRANSFUR)
            return;
        if (event.getSource().isProjectile())
            return;
        if (event.getEntityLiving().isDamageSourceBlocked(event.getSource()))
            return;
        if (event.getSource() == DamageSource.CACTUS && LatexVariant.getEntityVariant(event.getEntityLiving()) != null) {
            if (isOrganicLatex(event.getEntityLiving()))
                return;
            event.setCanceled(true);
            return;
        }

        if (!(event.getSource().getEntity() instanceof LivingEntity sourceEntity))
            return;
        if (!LatexedEntity.isLatexed(sourceEntity))
            return;
        if (isOrganicLatex(sourceEntity))
            return;
        if (sourceEntity.hasPassenger(event.getEntityLiving()) || event.getEntityLiving().hasPassenger(sourceEntity)) {
            event.setCanceled(true);
            return;
        }
        if (!sourceEntity.getItemInHand(sourceEntity.getUsedItemHand()).is(Items.AIR))
            return;

        onLivingAttackedByLatex(event, new LatexedEntity(sourceEntity));
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
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof Player player) {
            LatexVariant<?> variant = getPlayerLatexVariant(player);
            if (variant != null) {
                variant.setDead();
                variant.unhookAll(player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerSpawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntityLiving() instanceof Player player) {
            LatexVariant<?> variant = getPlayerLatexVariant(player);
            if (variant != null && variant.isDead()) {
                variant.unhookAll(player);
                setPlayerLatexVariant(player, null);
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
            ChangedSounds.broadcastSound(entity, variant.sound, 1.0f, 1.0f);
            LatexType.setEntityLatexType(entity, variant.getLatexType());
            if (keepConscious && entity instanceof ServerPlayer player) {
                ChangedCriteriaTriggers.TRANSFUR.trigger(player, variant);

                LatexVariant<?> uniqueVariant = variant.clone();

                setPlayerLatexVariant(player, uniqueVariant);

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
            ChangedSounds.broadcastSound(entity, fusion.sound, 1.0f, 1.0f);

            if (entity instanceof ServerPlayer player) {
                ChangedCriteriaTriggers.TRANSFUR.trigger(player, fusion);

                LatexVariant<?> uniqueVariant = fusion.clone();

                setPlayerLatexVariant(player, uniqueVariant);

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
