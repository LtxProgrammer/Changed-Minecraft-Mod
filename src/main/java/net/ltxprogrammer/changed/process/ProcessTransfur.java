package net.ltxprogrammer.changed.process;

import com.mojang.logging.LogUtils;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.ILatexAssimilatedEntity;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.ai.*;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.entity.AccessoryEntities;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.network.packet.*;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.world.enchantments.LatexProtectionEnchantment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Changed.MODID)
public class ProcessTransfur {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<ResourceLocation, EntityAssimilationBehavior<?>> ASSIMILATED_MOB_TRANSFUR_LOGIC = new HashMap<>();

    public static <T extends LivingEntity> void registerMobAssimilation(EntityType<T> entityType, EntityAssimilationBehavior<T> entityAssimilationBehavior) {
        ASSIMILATED_MOB_TRANSFUR_LOGIC.put(ForgeRegistries.ENTITY_TYPES.getKey(entityType), entityAssimilationBehavior);
    }

    public static <T extends LivingEntity> void registerMobAssimilation(RegistryObject<EntityType<T>> entityType, EntityAssimilationBehavior<T> entityAssimilationBehavior) {
        ASSIMILATED_MOB_TRANSFUR_LOGIC.put(entityType.getId(), entityAssimilationBehavior);
    }

    public static <T extends LivingEntity> EntityAssimilationBehavior<T> getDefaultEntityAssimilationBehavior(T entity) {
        if (entity instanceof Player)
            return (EntityAssimilationBehavior<T>) EntityAssimilationBehavior.defaultPlayer();
        else if (entity.getType().is(ChangedTags.EntityTypes.HUMANOIDS))
            return (EntityAssimilationBehavior<T>) EntityAssimilationBehavior.defaultHumanoid();
        else
            return null;
    }

    public static <T extends LivingEntity> EntityAssimilationBehavior<T> getEntityAssimilationBehavior(T entity) {
        if (entity == null)
            return null;
        var key = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (!ASSIMILATED_MOB_TRANSFUR_LOGIC.containsKey(key))
            return getDefaultEntityAssimilationBehavior(entity);
        return (EntityAssimilationBehavior<T>) ASSIMILATED_MOB_TRANSFUR_LOGIC.get(key);
    }

    /**
     * Computes the assimilation behavior that occurs between the victim and the source.
     */
    public static @Nullable AssimilationBehavior computeAssimilationBehavior(LivingEntity assimilationVictim, @Nullable LatexAssimilationDecision<?> decision) {
        if (decision == null)
            return null;
        var fusionBehavior = ChangedFusions.INSTANCE.getFusionBehavior(assimilationVictim, decision.context());
        if (fusionBehavior != null)
            return fusionBehavior;

        var behavior = getEntityAssimilationBehavior(assimilationVictim);
        if (behavior == null)
            return null;

        var event = new TransfurEvents.LatexAssimilationDecisionEvent(assimilationVictim, decision);
        if (Changed.postModEvent(event))
            return null;

        return behavior.latexAssimilateVictimBehavior(assimilationVictim, event.getDecision());
    }

    /**
     * Computes the assimilation behavior that occurs between the victim and the source.
     */
    public static @Nullable AssimilationBehavior computeAssimilationBehavior(LivingEntity assimilationVictim, @Nullable NonLatexAssimilationDecision<?> decision) {
        if (decision == null)
            return null;

        var behavior = getEntityAssimilationBehavior(assimilationVictim);
        if (behavior == null)
            return null;

        var event = new TransfurEvents.NonLatexAssimilationDecisionEvent(assimilationVictim, decision);
        if (Changed.postModEvent(event))
            return null;

        return behavior.nonLatexAssimilateVictimBehavior(assimilationVictim, event.getDecision());
    }

    /**
     * Computes the assimilation behavior that occurs between the victim and the source.
     */
    public static @Nullable AssimilationBehavior computeAssimilationBehavior(LivingEntity assimilationTarget, @Nullable ImmediateTransfurDecision<?> decision) {
        if (decision == null)
            return null;

        var behavior = getEntityAssimilationBehavior(assimilationTarget);
        if (behavior == null)
            return null;

        var event = new TransfurEvents.ImmediateTransfurDecisionEvent(assimilationTarget, decision);
        if (Changed.postModEvent(event))
            return null;

        return behavior.immediateTransfurTargetBehavior(assimilationTarget, decision);
    }

    // Intended to apply statuses on the source entity
    public static void onAbsorbEntity(IAbstractChangedEntity source) {
        if (Changed.postModEvent(new TransfurEvents.AbsorbedEntityEvent(source)))
            return;

        source.getEntity().heal(14.0f); // Heal 7 hearts
        if (source.getEntity() instanceof Player player) {
            player.getFoodData().eat(Foods.COOKED_BEEF.getNutrition(), Foods.COOKED_BEEF.getSaturationModifier()); // Equivalent to eating one Cooked beef
        }
        source.getEntity().addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * 20, 0)); // 30s Strength I

        // TODO: maybe make transfurring a necessity for latexes? A boost to their traits that decays over time since transfurring others.
    }

    // Intended to apply statuses on the source entity
    public static void onAssimilateEntity(IAbstractChangedEntity source) {
        if (Changed.postModEvent(new TransfurEvents.AssimilatedEntityEvent(source)))
            return;

        source.getEntity().heal(4.0f); // Heal 2 hearts
        if (source.getEntity() instanceof Player player) {
            player.getFoodData().eat(Foods.COOKIE.getNutrition(), Foods.COOKIE.getSaturationModifier()); // Equivalent to eating one Cookie
        }
    }

    public static void onNewlyTransfurred(IAbstractChangedEntity entity) {
        forceNearbyToRetarget(entity.getLevel(), entity.getEntity());
        if (Changed.postModEvent(new TransfurEvents.NewlyTransfurredEntityEvent(entity)))
            return;

        entity.getEntity().heal(10.0f); // Heal 5 hearts
        if (entity.getEntity() instanceof Player player) {
            player.getFoodData().eat(10, 1f); // Not really equivalent to anything, but more than cooked meat
        }
    }

    public static void onNewlyAssimilated(ILatexAssimilatedEntity entity) {
        forceNearbyToRetarget(entity.getLevel(), entity.getEntity());
        if (Changed.postModEvent(new TransfurEvents.NewlyAssimilatedEntityEvent(entity)))
            return;

        entity.getEntity().heal(10.0f); // Heal 5 hearts
    }

    public static void onNewlyFused(IAbstractChangedEntity entity) {
        if (Changed.postModEvent(new TransfurEvents.NewlyFusedEntityEvent(entity)))
            return;

        entity.getEntity().heal(10.0f); // Heal 5 hearts
        if (entity.getEntity() instanceof Player player) {
            player.getFoodData().eat(10, 1f); // Not really equivalent to anything, but more than cooked meat
        }
    }

    public static void setPlayerTransfurProgress(Player player, float progress) {
        if (!(player instanceof PlayerDataExtension ext))
            return;

        var oldProgress = ext.getTransfurProgress();
        if (progress == oldProgress)
            return;
        ext.setTransfurProgress(progress);
        if (!player.level().isClientSide)
            Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SyncTransfurProgressPacket(player.getId(), progress));
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
        final var pseudoSource = blocker.level().damageSources().mobAttack(source.getEntity());
        if (blocker.isDamageSourceBlocked(pseudoSource)) {
            net.minecraftforge.event.entity.living.ShieldBlockEvent ev = net.minecraftforge.common.ForgeHooks.onShieldBlock(blocker, pseudoSource, amount);
            if(!ev.isCanceled()) {
                if (ev.shieldTakesDamage()) ext.do_hurtCurrentlyUsedShield(amount);
                blocked = ev.getBlockedDamage();
                amount -= ev.getBlockedDamage();
                if (!pseudoSource.is(DamageTypeTags.IS_PROJECTILE)) {
                    ext.do_blockUsingShield(source.getEntity());
                }

                if (blocker instanceof ServerPlayer serverPlayer) {
                    if (blocked > 0.0F && blocked < 3.4028235E37F) {
                        serverPlayer.awardStat(Stats.CUSTOM.get(Stats.DAMAGE_BLOCKED_BY_SHIELD), Math.round(blocked * 10.0F));
                    }
                }

                blocker.level().broadcastEntityEvent(blocker, (byte)29);
            }
        }

        return amount;
    }

    @Deprecated
    public static boolean progressTransfur(LivingEntity entity, float amount, TransfurVariant<?> transfurVariant) {
        return progressTransfur(entity, amount, transfurVariant, TransfurContext.hazard(TransfurCause.GRAB_REPLICATE));
    }

    @Deprecated
    public static boolean progressTransfur(LivingEntity entity, float amount, TransfurVariant<?> transfurVariant, TransfurContext context) {
        return progressTransfur(entity, LatexAssimilationDecision.strong(
                context.cause() == TransfurCause.GRAB_ABSORB ? LatexAssimilationDecision.Method.ABSORPTION : LatexAssimilationDecision.Method.REPLICATION,
                transfurVariant,
                context,
                amount
        ));
    }

    public static boolean progressTransfur(LivingEntity entity, LatexAssimilationDecision<?> decision) {
        var behavior = computeAssimilationBehavior(entity, decision);
        if (behavior == null)
            return false;

        AtomicBoolean completed = new AtomicBoolean(false);
        behavior.appendTransfurListener(newEntity -> {
            completed.set(true);
        }).stepAssimilate();

        return completed.getAcquire();
    }

    public static boolean progressTransfur(LivingEntity entity, NonLatexAssimilationDecision<?> decision) {
        var behavior = computeAssimilationBehavior(entity, decision);
        if (behavior == null)
            return false;

        AtomicBoolean completed = new AtomicBoolean(false);
        behavior.appendTransfurListener(newEntity -> {
            completed.set(true);
        }).stepAssimilate();

        return completed.getAcquire();
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
        if (player.level().isClientSide)
            return;

        var progress = getPlayerTransfurProgress(player);
        if (progress <= 0) {
            var event = new TransfurEvents.TickPlayerTransfurProgressEvent(player, progress, 0f);
            if (Changed.postModEvent(event))
                return;

            setPlayerTransfurProgress(player, Math.max(progress + event.getDeltaProgress(), 0));
        }

        else {
            int deltaTicks = Math.max(((player.tickCount - player.getLastHurtByMobTimestamp()) / 8) - 20, 0);
            float scaledDeltaTicks = Math.max(-(deltaTicks * 0.001f), 0);

            var event = new TransfurEvents.TickPlayerTransfurProgressEvent(player, progress, scaledDeltaTicks);
            if (Changed.postModEvent(event))
                return;

            setPlayerTransfurProgress(player, Math.max(progress + event.getDeltaProgress(), 0));
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

    public static class TransfurAttackEvent extends Event {
        public final LivingEntity target;
        public final TransfurVariant<?> variant;
        public final TransfurContext context;

        public TransfurAttackEvent(LivingEntity target, TransfurVariant<?> variant, TransfurContext context) {
            this.target = target;
            this.variant = variant;
            this.context = context;
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    public static class KeepConsciousEvent extends Event {
        public final Player player;
        public final TransfurVariant<?> variant;
        public final TransfurContext context;
        public final boolean keepConscious;
        public boolean shouldKeepConscious;

        public KeepConsciousEvent(Player player, TransfurVariant<?> variant, TransfurContext context, boolean keepConscious) {
            this.player = player;
            this.variant = variant;
            this.context = context;
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
        TransfurContext context;
        public @Nullable
        TransfurVariant<?> variant;

        public EntityVariantAssigned(LivingEntity livingEntity, @Nullable TransfurVariant<?> variant, @Nullable TransfurContext context) {
            this.livingEntity = livingEntity;
            this.previousVariant = TransfurVariant.getEntityVariant(livingEntity);
            this.originalVariant = variant;
            this.context = context;

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
            TransfurContext context;

            public ChangedVariant(LivingEntity livingEntity, @Nullable TransfurVariant<?> variant, @Nullable TransfurContext context) {
                this.livingEntity = livingEntity;
                this.oldVariant = TransfurVariant.getEntityVariant(livingEntity);
                this.newVariant = variant;
                this.context = context;
            }

            @Override
            public boolean isCancelable() {
                return false;
            }
        }
    }

    public static void removePlayerTransfurVariant(Player player) {
        setPlayerTransfurVariant(player, null, null, player.level().getGameRules().getBoolean(ChangedGameRules.RULE_DO_TRANSFUR_ANIMATION) ? 0.0f : 1.0f);
    }

    @Deprecated
    public static @Nullable TransfurVariantInstance<?> setPlayerTransfurVariant(Player player, @Nullable TransfurVariant<?> ogVariant) {
        return setPlayerTransfurVariant(player, ogVariant, null, player.level().getGameRules().getBoolean(ChangedGameRules.RULE_DO_TRANSFUR_ANIMATION) ? 0.0f : 1.0f);
    }

    public static @Nullable TransfurVariantInstance<?> setPlayerTransfurVariant(Player player, @Nullable TransfurVariant<?> ogVariant, @Nullable TransfurCause cause) {
        return setPlayerTransfurVariant(player, ogVariant, TransfurContext.hazard(cause), player.level().getGameRules().getBoolean(ChangedGameRules.RULE_DO_TRANSFUR_ANIMATION) ? 0.0f : 1.0f);
    }

    public static @Nullable TransfurVariantInstance<?> setPlayerTransfurVariant(Player player, @Nullable TransfurVariant<?> ogVariant, @Nullable TransfurContext context) {
        return setPlayerTransfurVariant(player, ogVariant, context, player.level().getGameRules().getBoolean(ChangedGameRules.RULE_DO_TRANSFUR_ANIMATION) ? 0.0f : 1.0f);
    }

    public static @Nullable TransfurVariantInstance<?> setPlayerTransfurVariant(Player player, @Nullable TransfurVariant<?> ogVariant, @Nullable TransfurContext context, float progress) {
        return setPlayerTransfurVariant(player, ogVariant, context, progress, false);
    }

    public static @Nullable
    TransfurVariantInstance<?> setPlayerTransfurVariant(Player player, @Nullable TransfurVariant<?> ogVariant,
                                                        @Nullable TransfurContext context,
                                                        float progress,
                                                        boolean temporaryFromSuit) {
        return setPlayerTransfurVariant(player, ogVariant, context, progress, temporaryFromSuit, variant -> {});
    }

    public static @Nullable
    TransfurVariantInstance<?> setPlayerTransfurVariant(Player player, @Nullable TransfurVariant<?> ogVariant,
                                                        @Nullable TransfurContext context,
                                                        float progress,
                                                        boolean temporaryFromSuit, Consumer<TransfurVariantInstance<?>> preProcess) {
        PlayerDataExtension playerDataExtension = (PlayerDataExtension)player;
        EntityVariantAssigned event = new EntityVariantAssigned(player, ogVariant, context);
        Changed.postModEvent(event);
        @Nullable TransfurVariant<?> variant = event.variant;

        if (ChangedCompatibility.isPlayerUsedByOtherMod(player))
            variant = null;

        var oldVariant = playerDataExtension.getTransfurVariant();
        if (variant != null && oldVariant != null && variant == oldVariant.getParent())
            return oldVariant;
        if (variant == null && oldVariant == null)
            return null;
        if (oldVariant != null && oldVariant.getChangedEntity() != null)
            oldVariant.getChangedEntity().discard();
        TransfurVariantInstance<?> instance = TransfurVariantInstance.variantFor(variant, player);
        if (instance != null) {
            preProcess.accept(instance);
            Changed.postModEvent(new TransfurEvents.PreProcessTransfurVariantInstanceEvent(player, variant, context, instance, progress, temporaryFromSuit));
        }
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

        if (instance != null) {
            if (context != null)
                instance.transfurContext = context;

            instance.setTemporaryForSuit(temporaryFromSuit);
        }

        player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));

        if (variant != null && !event.isRedundant() && !instance.isTemporaryFromSuit()) {
            Changed.postModEvent(new EntityVariantAssigned.ChangedVariant(player, variant, context));
            ChangedFunctionTags.ON_TRANSFUR.execute(ServerLifecycleHooks.getCurrentServer(), player);
        }

        AccessoryEntities.INSTANCE.forceReloadAccessories(player);
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
        return TransfurVariant.getEntityVariant(entity) != null;
    }

    public static boolean isMobAssimilated(LivingEntity entity) {
        if (entity instanceof PathFinderMobDataExtension ext)
            return ext.isLatexAssimilated();
        return false;
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
            return !entity.getType().is(ChangedTags.EntityTypes.LATEX);
        }

        else return ifPlayerTransfurred(EntityUtil.playerOrNull(entity), variant -> {
            return !variant.getParent().getEntityType().is(ChangedTags.EntityTypes.LATEX);
        }, () -> true);
    }

    public static ItemStack getEntityAttackItem(LivingEntity entity) {
        return entity.swingingArm != null ? entity.getItemInHand(entity.swingingArm) : ItemStack.EMPTY;
    }

    @SubscribeEvent
    public static void onLivingDamaged(LivingDamageEvent event) {
        if (TransfurVariant.getEntityVariant(event.getEntity()) == null)
            return;

        if (event.getSource().getEntity() instanceof LivingEntity livingEntity) {
            IAbstractChangedEntity.forEitherSafe(livingEntity)
                    .map(IAbstractChangedEntity::getChangedEntity)
                    .ifPresent(victim -> victim.onDamagedBy(livingEntity));
        }

        if (isNonGoo(event.getEntity()))
            return;

        if (event.getSource().getEntity() instanceof LivingEntity livingEntity &&
                getEntityAttackItem(livingEntity).is(ChangedTags.Items.TSC_WEAPON)) {
            event.setAmount(event.getAmount() * 1.5F);
        }

        else if (event.getSource().is(ChangedTags.DamageTypes.LATEX_WEAK_TO)) {
            event.setAmount(event.getAmount() * 1.5F);
        }
    }

    public static boolean killPlayerByAbsorption(Player player, LivingEntity source) {
        player.invulnerableTime = 0;
        player.hurt(ChangedDamageSources.entityAbsorb(player.level().registryAccess(), source), Float.MAX_VALUE);
        if (!Float.isFinite(player.getHealth()))
            player.setHealth(0.0f);
        return player.isDeadOrDying();
    }

    public static boolean killPlayerByTransfur(Player player, LivingEntity source) {
        player.invulnerableTime = 0;
        player.hurt(ChangedDamageSources.entityTransfur(player.level().registryAccess(), source), Float.MAX_VALUE);
        if (!Float.isFinite(player.getHealth()))
            player.setHealth(0.0f);
        return player.isDeadOrDying();
    }

    public static float difficultyAdjustTransfurAmount(Difficulty difficulty, float amount) {
        return difficultyAdjustTransfurAmount(difficulty, amount, null);
    }

    public static float difficultyAdjustTransfurAmount(Difficulty difficulty, float amount, @Nullable IAbstractChangedEntity source) {
        if (source != null && source.isPlayer())
            return amount;

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
        if(event.getEntity().getPersistentData().contains(TransfurVariant.NBT_PLAYER_ID)) {
            event.setCanceled(true);
            return;
        }
        if (event.getSource().is(ChangedTags.DamageTypes.IS_TRANSFUR))
            return;
        if (event.getSource().is(DamageTypeTags.IS_PROJECTILE))
            return;
        if (event.getEntity().isDamageSourceBlocked(event.getSource()))
            return;
        if (event.getSource().is(ChangedTags.DamageTypes.LATEX_IMMUNE_TO) && !isNonGoo(event.getEntity())) {
            event.setCanceled(true);
            return;
        }

        if (!(event.getSource().getEntity() instanceof LivingEntity sourceEntity))
            return;
        if (!hasVariant(sourceEntity))
            return;
        if (isNonGoo(sourceEntity))
            return;
        if (sourceEntity.hasPassenger(event.getEntity()) || event.getEntity().hasPassenger(sourceEntity)) {
            event.setCanceled(true);
            return;
        }
        if (event.getSource().is(ChangedTags.DamageTypes.IGNORES_FACTION_IMMUNITY))
            return;
        // Check for faction immunity
        LatexType factionD = LatexType.getEntityLatexType(event.getEntity());
        LatexType factionS = LatexType.getEntityLatexType(sourceEntity);
        if (factionD != null && factionS != null && factionS.isFriendlyTo(factionD)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        //The entity dying is a morph. Cancel the event.
        if (event.getEntity().getPersistentData().contains(TransfurVariant.NBT_PLAYER_ID)) {
            event.setCanceled(true);
            return;
        }
    }

    @Deprecated
    public static void transfur(LivingEntity entity, Level level, TransfurVariant<?> variant, boolean keepConscious) {
        transfur(entity, level, variant, keepConscious, TransfurContext.hazard(TransfurCause.GRAB_REPLICATE));
    }

    public static void forceNearbyToRetarget(Level level, LivingEntity entity) {
        for (PathfinderMob targettingEntity : level.getEntitiesOfClass(PathfinderMob.class, entity.getBoundingBox().inflate(64))) {
            if (!(targettingEntity instanceof ChangedEntity) && !ProcessTransfur.isMobAssimilated(targettingEntity))
                continue;

            if (targettingEntity.getLastHurtByMob() == entity) {
                targettingEntity.setLastHurtByMob(null);
            }

            if (targettingEntity.getTarget() == entity) {
                targettingEntity.setTarget(null);
                targettingEntity.targetSelector.tick();
                targettingEntity.targetSelector.getRunningGoals().forEach(WrappedGoal::stop);
            }
        }
    }

    // Transfurs an entity, keepConscious applies to players being transfurred
    @Deprecated
    public static void transfur(LivingEntity entity, Level level, TransfurVariant<?> variant, boolean keepConscious, TransfurContext context) {
        var source = context.source() == null ? null : context.source().left().orElse(null);

        var decision = keepConscious ?
                ImmediateTransfurDecision.safe(variant, context.cause(), source) :
                ImmediateTransfurDecision.unsafe(variant, context.cause(), source);

        transfur(entity, decision);
    }

    public static void transfur(LivingEntity entity, ImmediateTransfurDecision<?> decision) {
        var behavior = computeAssimilationBehavior(entity, decision);

        if (behavior != null)
            behavior.stepAssimilate();
    }
}
