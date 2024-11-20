package net.ltxprogrammer.changed.process;

import com.mojang.logging.LogUtils;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.latexvariant.CheckCondition;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.beast.SpecialLatex;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.network.packet.BasicPlayerInfoPacket;
import net.ltxprogrammer.changed.network.packet.CheckForUpdatesPacket;
import net.ltxprogrammer.changed.network.packet.SyncTransfurPacket;
import net.ltxprogrammer.changed.network.packet.SyncTransfurProgressPacket;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.ltxprogrammer.changed.world.enchantments.LatexProtectionEnchantment;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.ltxprogrammer.changed.init.ChangedGameRules.RULE_KEEP_BRAIN;

@Mod.EventBusSubscriber(modid = Changed.MODID)
public class ProcessTransfur {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void setPlayerTransfurProgress(Player player, float progress) {
        if (!(player instanceof PlayerDataExtension ext))
            return;

        var oldProgress = ext.getTransfurProgress();
        if (progress == oldProgress)
            return;
        ext.setTransfurProgress(progress);
        if (!player.level.isClientSide)
            Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SyncTransfurProgressPacket(player.getUUID(), progress));
    }

    public static float getPlayerTransfurProgress(Player player) {
        if (!(player instanceof PlayerDataExtension ext))
            return 0.0f;
        return ext.getTransfurProgress();
    }

    public static float checkBlocked(LivingEntity blocker, float amount, IAbstractChangedEntity source) {
        if (source == null || amount <= 0.0f)
            return amount;

        if (!(blocker instanceof LivingEntityDataExtension ext))
            return amount;

        float blocked = 0.0f;
        final var pseudoSource = DamageSource.mobAttack(source.getEntity());
        if (blocker.isDamageSourceBlocked(pseudoSource)) {
            net.minecraftforge.event.entity.living.ShieldBlockEvent ev = net.minecraftforge.common.ForgeHooks.onShieldBlock(blocker, pseudoSource, amount);
            if(!ev.isCanceled()) {
                if (ev.shieldTakesDamage()) ext.do_hurtCurrentlyUsedShield(amount);
                blocked = ev.getBlockedDamage();
                amount -= ev.getBlockedDamage();
                if (!pseudoSource.isProjectile()) {
                    ext.do_blockUsingShield(source.getEntity());
                }

                if (blocker instanceof ServerPlayer serverPlayer) {
                    if (blocked > 0.0F && blocked < 3.4028235E37F) {
                        serverPlayer.awardStat(Stats.CUSTOM.get(Stats.DAMAGE_BLOCKED_BY_SHIELD), Math.round(blocked * 10.0F));
                    }
                }

                blocker.level.broadcastEntityEvent(blocker, (byte)29);
            }
        }

        return amount;
    }

    public static boolean progressPlayerTransfur(Player player, float amount, TransfurVariant<?> transfurVariant, TransfurContext context) {
        if (player.isCreative() || player.isSpectator() || ProcessTransfur.isPlayerPermTransfurred(player))
            return false;
        if (player.isDeadOrDying() || player.isRemoved())
            return false;
        boolean justHit = player.invulnerableTime == 20 && player.hurtDuration == 10;

        if (player.invulnerableTime > 10 && !justHit) {
            return false;
        }

        else {
            amount = LatexProtectionEnchantment.getLatexProtection(player, amount);
            if (ChangedCompatibility.isPlayerUsedByOtherMod(player)) {
                setPlayerTransfurProgress(player, 0.0f);
                player.hurt(DamageSource.mobAttack(context.source == null ? transfurVariant.getEntityType().create(player.level) : context.source.getEntity()), amount);
                return false;
            }

            if (amount <= 0.0f)
                return false;

            player.invulnerableTime = 20;
            player.hurtDuration = 10;
            player.hurtTime = player.hurtDuration;
            player.setLastHurtByMob(null);

            float old = getPlayerTransfurProgress(player);
            float next = old + amount;
            float max = (float)ProcessTransfur.getEntityTransfurTolerance(player);
            setPlayerTransfurProgress(player, next);
            if (next >= max && old < max) {
                if (TransfurVariant.getPublicTransfurVariants().anyMatch(transfurVariant::equals))
                    transfur(player, player.level, transfurVariant, false, context);
                else {
                    var variant = PatreonBenefits.getPlayerSpecialVariant(player.getUUID());
                    transfur(player, player.level, variant == null ? ChangedTransfurVariants.FALLBACK_VARIANT.get() : variant, false, context);
                }

                return true;
            }

            return false;
        }
    }

    public static boolean willTransfur(LivingEntity entity, float amount) {
        amount = LatexProtectionEnchantment.getLatexProtection(entity, amount);

        if (entity instanceof Player player) {
            if (player.isCreative() || player.isSpectator() || ProcessTransfur.isPlayerPermTransfurred(player))
                return false;
            boolean justHit = player.invulnerableTime == 20 && player.hurtDuration == 10;

            if (player.invulnerableTime > 10 && !justHit) {
                return getPlayerTransfurProgress(player) >= ProcessTransfur.getEntityTransfurTolerance(player);
            }

            else {
                /*player.invulnerableTime = 20;
                player.hurtDuration = 10;
                player.hurtTime = player.hurtDuration;*/

                float next = getPlayerTransfurProgress(player) + amount;
                return next >= ProcessTransfur.getEntityTransfurTolerance(player);
            }
        }
        else {
            float health = entity.getHealth();
            float scale = 20.0f / Math.max(0.1f, (float)ProcessTransfur.getEntityTransfurTolerance(entity));

            if (entity.getType().is(ChangedTags.EntityTypes.HUMANOIDS)) {
                if (health <= amount * scale && health > 0.0F) {
                    return true;
                }

                else {
                    return false;
                }
            }

            else {
                return health <= amount * scale && health > 0.0F;
            }
        }
    }

    @Deprecated
    public static boolean progressTransfur(LivingEntity entity, float amount, TransfurVariant<?> transfurVariant) {
        return progressTransfur(entity, amount, transfurVariant, TransfurContext.hazard(TransfurCause.GRAB_REPLICATE));
    }

    public static boolean progressTransfur(LivingEntity entity, float amount, TransfurVariant<?> transfurVariant, TransfurContext context) {
        if (entity instanceof Player player)
            return progressPlayerTransfur(player, amount, transfurVariant, context);
        else {
            if (entity.isDeadOrDying() || entity.isRemoved())
                return false;

            amount = LatexProtectionEnchantment.getLatexProtection(entity, amount);
            float health = entity.getHealth();
            float scale = 20.0f / Math.max(0.1f, (float)ProcessTransfur.getEntityTransfurTolerance(entity));

            if (entity.getType().is(ChangedTags.EntityTypes.HUMANOIDS)) {
                if (health <= amount * scale && health > 0.0F) {
                    ProcessTransfur.transfur(entity, entity.level, transfurVariant, false, context);
                    return true;
                }

                else {
                    entity.hurt(ChangedDamageSources.entityTransfur(context.source), amount * scale);
                    return false;
                }
            }

            else {
                List<TransfurVariant<?>> mobFusion = ChangedFusions.INSTANCE.getFusionsFor(transfurVariant, entity.getClass()).toList();

                if (mobFusion.isEmpty())
                    return false;

                if (health <= amount * scale && health > 0.0F) {
                    ProcessTransfur.transfur(entity, entity.level, Util.getRandom(mobFusion, entity.getRandom()), false, context);
                    return true;
                }

                else {
                    entity.hurt(ChangedDamageSources.entityTransfur(context.source), amount * scale);
                    return false;
                }
            }
        }
    }

    public static LivingEntity changeTransfur(LivingEntity entity, TransfurVariant<?> transfurVariant) {
        if (entity instanceof Player player) {
            setPlayerTransfurVariant(player, transfurVariant);
            return player;
        } else {
            return transfurVariant.replaceEntity(entity).getEntity();
        }
    }

    public static void tickPlayerTransfurProgress(Player player) {
        if (isPlayerTransfurred(player))
            return;
        var progress = getPlayerTransfurProgress(player);
        if (!player.level.isClientSide && progress > 0) {
            int deltaTicks = Math.max(((player.tickCount - player.getLastHurtByMobTimestamp()) / 8) - 20, 0);
            float nextTicks = Math.max(progress - (deltaTicks * 0.001f), 0);
            setPlayerTransfurProgress(player, nextTicks);
        }
    }

    public static TransfurVariantInstance<?> getPlayerTransfurVariant(Player player) {
        if (player instanceof PlayerDataExtension ext)
            return ext.getTransfurVariant();
        return null;
    }

    public static @NotNull Optional<TransfurVariantInstance<?>> getPlayerTransfurVariantSafe(Player player) {
        if (player instanceof PlayerDataExtension ext)
            return Optional.ofNullable(ext.getTransfurVariant());
        return Optional.empty();
    }

    public static class KeepConsciousEvent extends Event {
        public final Player player;
        public final boolean keepConscious;
        public boolean shouldKeepConscious;

        public KeepConsciousEvent(Player player, boolean keepConscious) {
            this.player = player;
            this.keepConscious = keepConscious;
            this.shouldKeepConscious = keepConscious;
        }

        @Override
        public boolean isCancelable() {
            return false;
        }
    }

    public static double getEntityTransfurTolerance(LivingEntity entity) {
        return entity.getAttributeValue(ChangedAttributes.TRANSFUR_TOLERANCE.get());
    }

    public static class EntityVariantAssigned extends Event {
        public final LivingEntity livingEntity;
        public final @Nullable
        TransfurVariant<?> previousVariant;
        public final @Nullable
        TransfurVariant<?> originalVariant;
        public final @Nullable
        TransfurCause cause;
        public @Nullable
        TransfurVariant<?> variant;

        public EntityVariantAssigned(LivingEntity livingEntity, @Nullable TransfurVariant<?> variant, @Nullable TransfurCause cause) {
            this.livingEntity = livingEntity;
            this.previousVariant = TransfurVariant.getEntityVariant(livingEntity);
            this.originalVariant = variant;
            this.cause = cause;

            this.variant = variant;
        }

        @Override
        public boolean isCancelable() {
            return false;
        }

        // Event may be fired every couple of ticks from the sync packet
        public boolean isRedundant() {
            if (livingEntity.tickCount < 20)
                return true;
            else if (previousVariant == originalVariant)
                return true;
            else if (previousVariant == null)
                return false;
            else if (originalVariant == null)
                return false;
            return previousVariant.getEntityType() == originalVariant.getEntityType();
        }

        public static class ChangedVariant extends Event {
            public final LivingEntity livingEntity;
            public final @Nullable
            TransfurVariant<?> oldVariant;
            public final @Nullable
            TransfurVariant<?> newVariant;
            public final @Nullable
            TransfurCause cause;

            public ChangedVariant(LivingEntity livingEntity, @Nullable TransfurVariant<?> variant, @Nullable TransfurCause cause) {
                this.livingEntity = livingEntity;
                this.oldVariant = TransfurVariant.getEntityVariant(livingEntity);
                this.newVariant = variant;
                this.cause = cause;
            }

            @Override
            public boolean isCancelable() {
                return false;
            }
        }
    }

    public static void removePlayerTransfurVariant(Player player) {
        setPlayerTransfurVariant(player, null, null, player.level.getGameRules().getBoolean(ChangedGameRules.RULE_DO_TRANSFUR_ANIMATION) ? 0.0f : 1.0f);
    }

    @Deprecated
    public static @Nullable TransfurVariantInstance<?> setPlayerTransfurVariant(Player player, @Nullable TransfurVariant<?> ogVariant) {
        return setPlayerTransfurVariant(player, ogVariant, null, player.level.getGameRules().getBoolean(ChangedGameRules.RULE_DO_TRANSFUR_ANIMATION) ? 0.0f : 1.0f);
    }

    public static @Nullable TransfurVariantInstance<?> setPlayerTransfurVariant(Player player, @Nullable TransfurVariant<?> ogVariant, @Nullable TransfurCause cause) {
        return setPlayerTransfurVariant(player, ogVariant, cause, player.level.getGameRules().getBoolean(ChangedGameRules.RULE_DO_TRANSFUR_ANIMATION) ? 0.0f : 1.0f);
    }

    public static TransfurVariantInstance<?> setPlayerTransfurVariant(Player player, @Nullable TransfurVariant<?> ogVariant, @Nullable TransfurCause cause, float progress) {
        return setPlayerTransfurVariant(player, ogVariant, cause, progress, false);
    }

    @Contract("_, null, _, _, _ -> null; _, !null, _, _, _ -> !null")
    public static @Nullable
    TransfurVariantInstance<?> setPlayerTransfurVariant(Player player, @Nullable TransfurVariant<?> ogVariant,
                                                        @Nullable TransfurCause cause,
                                                        float progress,
                                                        boolean temporaryFromSuit) {
        PlayerDataExtension playerDataExtension = (PlayerDataExtension)player;
        EntityVariantAssigned event = new EntityVariantAssigned(player, ogVariant, cause);
        Changed.postModEvent(event);
        @Nullable TransfurVariant<?> variant = event.variant;

        if (ChangedCompatibility.isPlayerUsedByOtherMod(player))
            variant = null;

        var oldVariant = playerDataExtension.getTransfurVariant();
        if (variant != null && oldVariant != null && variant.getFormId().equals(oldVariant.getFormId()))
            return oldVariant;
        if (variant == null && oldVariant == null)
            return oldVariant;
        if (oldVariant != null && oldVariant.getChangedEntity() != null)
            oldVariant.getChangedEntity().discard();
        TransfurVariantInstance<?> instance = TransfurVariantInstance.variantFor(variant, player);
        playerDataExtension.setTransfurVariant(instance);

        if (instance != null) {
            instance.transfurProgressionO = progress;
            instance.transfurProgression = progress;
        }

        if (oldVariant != null) {
            oldVariant.unhookAll(player);
            if (instance != null) { // TODO transition between variants
                instance.willSurviveTransfur = oldVariant.willSurviveTransfur;
                instance.transfurProgressionO = oldVariant.transfurProgressionO;
                instance.transfurProgression = oldVariant.transfurProgression;
                instance.transfurContext = oldVariant.transfurContext;
            }
        }

        if (instance != null && cause != null)
            instance.transfurContext = instance.transfurContext.withCause(cause);

        if (instance != null)
            instance.setTemporaryForSuit(temporaryFromSuit);
        if (instance != null && !instance.isTemporaryFromSuit()) {
            player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
        } else {
            player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
        }

        if (variant != null && !event.isRedundant() && !instance.isTemporaryFromSuit()) {
            Changed.postModEvent(new EntityVariantAssigned.ChangedVariant(player, variant, cause));
            ChangedFunctionTags.ON_TRANSFUR.execute(ServerLifecycleHooks.getCurrentServer(), player);
        }

        if (player instanceof ServerPlayer serverPlayer)
            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> serverPlayer), SyncTransfurPacket.Builder.of(player));
        return instance;
    }

    public static TransfurVariantInstance<?> setPlayerTransfurVariantNamed(Player player, ResourceLocation variant) {
        return setPlayerTransfurVariant(player, ChangedRegistry.TRANSFUR_VARIANT.get().getValue(variant));
    }

    public static boolean isPlayerPermTransfurred(Player player) {
        return getPlayerTransfurVariantSafe(player).map(variant -> !variant.isTemporaryFromSuit()).orElse(false);
    }

    public static boolean isPlayerTransfurred(Player player) {
        if (player instanceof PlayerDataExtension ext)
            return ext.isTransfurred();
        return false;
    }

    public static <R> R ifPlayerTransfurred(Player player, Function<TransfurVariantInstance<?>, R> isLatex, Supplier<R> notLatex) {
        TransfurVariantInstance<?> variant = getPlayerTransfurVariant(player);
        return variant != null ? isLatex.apply(variant) : notLatex.get();
    }

    public static <R> R ifPlayerTransfurred(Player player, Function<TransfurVariantInstance<?>, R> isLatex) {
        TransfurVariantInstance<?> variant = getPlayerTransfurVariant(player);
        return variant != null ? isLatex.apply(variant) : null;
    }

    public static boolean ifPlayerTransfurred(Player player, Consumer<TransfurVariantInstance<?>> isLatex, Runnable notLatex) {
        TransfurVariantInstance<?> variant = getPlayerTransfurVariant(player);
        if (variant != null)
            isLatex.accept(variant);
        else
            notLatex.run();
        return variant != null;
    }

    public static boolean ifPlayerTransfurred(Player player, Consumer<TransfurVariantInstance<?>> isLatex) {
        TransfurVariantInstance<?> variant = getPlayerTransfurVariant(player);
        if (variant != null)
            isLatex.accept(variant);
        return variant != null;
    }

    public static boolean ifPlayerTransfurred(Player player, BiConsumer<Player, TransfurVariantInstance<?>> isLatex) {
        TransfurVariantInstance<?> variant = getPlayerTransfurVariant(player);
        if (variant != null)
            isLatex.accept(player, variant);
        return variant != null;
    }

    // Checks if player is either not latex or is organic latex
    public static boolean isPlayerNotLatex(Player player) {
        var variant = getPlayerTransfurVariant(player);
        return variant == null || !variant.getParent().getEntityType().is(ChangedTags.EntityTypes.LATEX);
    }

    public static boolean isPlayerLatex(Player player) {
        var variant = getPlayerTransfurVariant(player);
        return variant != null && variant.getParent().getEntityType().is(ChangedTags.EntityTypes.LATEX);
    }

    public static Optional<TransfurVariant<?>> getEntityVariant(LivingEntity livingEntity) {
        if (livingEntity instanceof ChangedEntity entity)
            return Optional.ofNullable(entity.getSelfVariant());
        else if (livingEntity instanceof Player player)
            return Optional.ofNullable(((PlayerDataExtension)player).getTransfurVariant()).map(TransfurVariantInstance::getParent);
        return Optional.empty();
    }

    public static boolean hasVariant(LivingEntity entity) {
        return TransfurVariant.getEntityVariant(entity) != null || TransfurVariant.getEntityTransfur(entity) != null;
    }

    private static void bonusHurt(LivingEntity entity, DamageSource source, float damage, boolean overrideImmunity) {
        if (!entity.isInvulnerableTo(source) || overrideImmunity) {
            boolean justHit = entity.invulnerableTime == 20 && entity.hurtDuration == 10;

            if (justHit || entity.invulnerableTime <= 0 || overrideImmunity) {
                if (entity.getHealth() - damage > 0)
                    entity.setHealth(entity.getHealth() - damage);
                else
                    entity.hurt(source, Float.MAX_VALUE);
            }
        }
    }

    private static boolean isNonGoo(LivingEntity entity) {
        if (entity instanceof ChangedEntity latex) {
            if (latex instanceof SpecialLatex specialLatex)
                return specialLatex.getCurrentData() != null && specialLatex.getCurrentData().organic();
            else
                return !entity.getType().is(ChangedTags.EntityTypes.LATEX);
        }

        else return ifPlayerTransfurred(EntityUtil.playerOrNull(entity), variant -> {
            if (!variant.getParent().getEntityType().is(ChangedTags.EntityTypes.LATEX))
                return true;
            else if (variant.getChangedEntity() instanceof SpecialLatex special &&
                    special.getCurrentData() != null && special.getCurrentData().organic())
                return true;
            return false;
        }, () -> true);
    }

    public static ItemStack getEntityAttackItem(LivingEntity entity) {
        return entity.swingingArm != null ? entity.getItemInHand(entity.swingingArm) : ItemStack.EMPTY;
    }

    @SubscribeEvent
    public static void onLivingDamaged(LivingDamageEvent event) {
        if (TransfurVariant.getEntityVariant(event.getEntityLiving()) == null)
            return;

        if (event.getSource() instanceof EntityDamageSource entityDamageSource && entityDamageSource.getEntity() instanceof LivingEntity livingEntity) {
            if (event.getEntityLiving() instanceof ChangedEntity changedEntity)
                changedEntity.onDamagedBy(livingEntity);
            ifPlayerTransfurred(EntityUtil.playerOrNull(event.getEntityLiving()), (player, variant) -> {
                variant.getChangedEntity().onDamagedBy(livingEntity);
            });
        }

        if (isNonGoo(event.getEntityLiving()))
            return;

        if (event.getSource() instanceof EntityDamageSource entityDamageSource && entityDamageSource.getEntity() instanceof LivingEntity livingEntity) {
            if (getEntityAttackItem(livingEntity).is(ChangedTags.Items.TSC_WEAPON)) {
                event.setAmount(event.getAmount() * 1.5F);
            }
        }

        if (event.getSource().isFire()) {
            event.setAmount(event.getAmount() * 2.0F);
        }
    }

    public static boolean killPlayerByAbsorption(Player player, LivingEntity source) {
        player.hurt(ChangedDamageSources.entityAbsorb(source), Float.MAX_VALUE);
        if (!Float.isFinite(player.getHealth()))
            player.setHealth(0.0f);
        return player.isDeadOrDying();
    }

    public static boolean killPlayerByTransfur(Player player, LivingEntity source) {
        player.hurt(ChangedDamageSources.entityTransfur(source), Float.MAX_VALUE);
        if (!Float.isFinite(player.getHealth()))
            player.setHealth(0.0f);
        return player.isDeadOrDying();
    }

    public static float difficultyAdjustTransfurAmount(Difficulty difficulty, float amount) {
        if (difficulty == Difficulty.EASY) {
            amount = Math.min(amount / 2.0F + 1.0F, amount);
        }

        if (difficulty == Difficulty.HARD) {
            amount = amount * 3.0F / 2.0F;
        }

        return amount;
    }

    @SubscribeEvent
    public static void onLivingAttacked(LivingAttackEvent event) {
        //The entity getting hurt is a morph. Cancel the event.
        if(event.getEntityLiving().getPersistentData().contains(TransfurVariant.NBT_PLAYER_ID)) {
            event.setCanceled(true);
            return;
        }
        if (event.getSource() instanceof ChangedDamageSources.TransfurDamageSource)
            return;
        if (event.getSource().isProjectile())
            return;
        if (event.getEntityLiving().isDamageSourceBlocked(event.getSource()))
            return;
        if (event.getSource() == DamageSource.CACTUS && TransfurVariant.getEntityVariant(event.getEntityLiving()) != null) {
            if (!isNonGoo(event.getEntityLiving()))
                event.setCanceled(true);
            return;
        }

        if (!(event.getSource().getEntity() instanceof LivingEntity sourceEntity))
            return;
        if (!hasVariant(sourceEntity))
            return;
        if (isNonGoo(sourceEntity))
            return;
        if (sourceEntity.hasPassenger(event.getEntityLiving()) || event.getEntityLiving().hasPassenger(sourceEntity)) {
            event.setCanceled(true);
            return;
        }
        // Check for faction immunity
        LatexType factionD = LatexType.getEntityFactionLatexType(event.getEntityLiving());
        LatexType factionS = LatexType.getEntityFactionLatexType(sourceEntity);
        if (factionD == factionS && factionS != null) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        //The entity dying is a morph. Cancel the event.
        if (event.getEntityLiving().getPersistentData().contains(TransfurVariant.NBT_PLAYER_ID)) {
            event.setCanceled(true);
            return;
        }
    }

    private static int worldTickCount = 0;
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.side.isServer()) {
            worldTickCount++;

            if (worldTickCount % 60 == 0) { // Discrete sync packet
                ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(serverPlayer -> {
                    // Latex variant
                    Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> serverPlayer), SyncTransfurPacket.Builder.of(serverPlayer));
                    Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY.with(() -> serverPlayer), BasicPlayerInfoPacket.Builder.of(serverPlayer));
                });
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

    @Deprecated
    public static void transfur(LivingEntity entity, Level level, TransfurVariant<?> variant, boolean keepConscious) {
        transfur(entity, level, variant, keepConscious, TransfurContext.hazard(TransfurCause.GRAB_REPLICATE));
    }

    public static void forceNearbyToRetarget(Level level, LivingEntity entity) {
        for (ChangedEntity changedEntity : level.getEntitiesOfClass(ChangedEntity.class, entity.getBoundingBox().inflate(64))) {
            if (changedEntity.getLastHurtByMob() == entity) {
                changedEntity.setLastHurtByMob(null);
            }

            if (changedEntity.getTarget() == entity) {
                changedEntity.setTarget(null);
                changedEntity.targetSelector.tick();
                changedEntity.targetSelector.getRunningGoals().forEach(WrappedGoal::stop);
            }
        }
    }

    // Transfurs an entity, keepConscious applies to players being transfurred
    public static void transfur(LivingEntity entity, Level level, TransfurVariant<?> variant, boolean keepConscious, TransfurContext context) {
        if (entity == null)
            return;
        if (entity.isDeadOrDying())
            return;//
        // To prevent most bugs, entity has to be alive to transfur
        if(entity instanceof ServerPlayer player){
            CheckCondition compatibility = new CheckCondition(player);
            if ((compatibility.isConditionMet()))
                return;
        }
        if (level.getGameRules().getBoolean(RULE_KEEP_BRAIN))
            keepConscious = true;
        else if (entity instanceof Player player) {
            if (player.isCreative())
                keepConscious = true;
            else {
                KeepConsciousEvent event = new KeepConsciousEvent(player, keepConscious);
                Changed.postModEvent(event);
                keepConscious = event.shouldKeepConscious;
            }
        }

        final boolean doAnimation = level.getGameRules().getBoolean(ChangedGameRules.RULE_DO_TRANSFUR_ANIMATION);

        if (entity.level.isClientSide) {
            return;
        }

        if (variant == null)
            return;

        final BiConsumer<IAbstractChangedEntity, TransfurVariant<?>> onReplicate = (iAbstractLatex, variant1) -> {
            if (context.source != null)
                context.source.getChangedEntity().onReplicateOther(iAbstractLatex, variant1);
        };

        if (!LatexType.hasLatexType(entity)) {
            ChangedSounds.broadcastSound(entity, variant.sound, 1.0f, 1.0f);
            if ((keepConscious || doAnimation) && entity instanceof ServerPlayer player) {
                var instance = setPlayerTransfurVariant(player, variant, context.cause, doAnimation ? 0.0f : 1.0f);
                instance.willSurviveTransfur = keepConscious;
                instance.transfurContext = context;

                forceNearbyToRetarget(level, player);

                player.heal(10.0F);

                LOGGER.info("Transfurred " + entity + " into " + variant);
                onReplicate.accept(IAbstractChangedEntity.forPlayer(player), variant);
            }

            else if (!entity.level.isClientSide) {
                EntityVariantAssigned event = new EntityVariantAssigned(entity, variant, context.cause);
                Changed.postModEvent(event);
                if (event.variant != null)
                    onReplicate.accept(event.variant.replaceEntity(entity, context.source), event.variant);
            }
        }

        else {
            TransfurVariant<?> current = TransfurVariant.getEntityVariant(entity);
            List<TransfurVariant<?>> possible = ChangedFusions.INSTANCE.getFusionsFor(current, variant).toList();

            if (possible.isEmpty())
                return;
            if (entity instanceof Player player) {
                var instance = getPlayerTransfurVariant(player);
                if (instance != null && instance.ageAsVariant > entity.level.getGameRules().getInt(ChangedGameRules.RULE_FUSABILITY_DURATION_PLAYER))
                    return;
            }

            TransfurVariant<?> fusion = possible.get(level.random.nextInt(possible.size()));
            ChangedSounds.broadcastSound(entity, fusion.sound, 1.0f, 1.0f);

            if (entity instanceof ServerPlayer player) {
                setPlayerTransfurVariant(player, fusion, context.cause);

                // Force retargeting
                for (ChangedEntity changedEntity : level.getEntitiesOfClass(ChangedEntity.class, player.getBoundingBox().inflate(64))) {
                    if (changedEntity.getLastHurtByMob() == player) {
                        changedEntity.setLastHurtByMob(null);
                    }

                    if (changedEntity.getTarget() == player) {
                        changedEntity.setTarget(null);
                        changedEntity.targetSelector.tick();
                        changedEntity.targetSelector.getRunningGoals().forEach(WrappedGoal::stop);
                    }
                }

                player.heal(10.0F);

                LOGGER.info("Fused " + entity + " with " + variant);
            }

            else if (!entity.level.isClientSide) {
                EntityVariantAssigned event = new EntityVariantAssigned(entity, fusion, context.cause);
                Changed.postModEvent(event);
                if (event.variant != null)
                    event.variant.replaceEntity(entity, context.source);
            }
        }
    }
}
