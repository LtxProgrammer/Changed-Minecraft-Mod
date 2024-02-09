package net.ltxprogrammer.changed.process;

import com.mojang.logging.LogUtils;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.IAbstractLatex;
import net.ltxprogrammer.changed.block.WhiteLatexBlock;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.beast.SpecialLatex;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.network.packet.BasicPlayerInfoPacket;
import net.ltxprogrammer.changed.network.packet.CheckForUpdatesPacket;
import net.ltxprogrammer.changed.network.packet.SyncTransfurPacket;
import net.ltxprogrammer.changed.network.packet.SyncTransfurProgressPacket;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.enchantments.LatexProtectionEnchantment;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.ltxprogrammer.changed.init.ChangedGameRules.RULE_KEEP_BRAIN;

@Mod.EventBusSubscriber(modid = Changed.MODID)
public class ProcessTransfur {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static record TransfurProgress(float progress, LatexVariant<?> variant) {}

    public static void setPlayerTransfurProgress(Player player, @NotNull TransfurProgress progress) {
        if (!(player instanceof PlayerDataExtension ext))
            return;

        var oldProgress = ext.getTransfurProgress();
        if (progress.equals(oldProgress))
            return;
        ext.setTransfurProgress(progress);
        if (!player.level.isClientSide)
            Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new SyncTransfurProgressPacket(player.getUUID(), progress));
    }

    public static TransfurProgress getPlayerTransfurProgress(Player player) {
        if (!(player instanceof PlayerDataExtension ext))
            return null;
        return ext.getTransfurProgress();
    }

    public static boolean progressPlayerTransfur(Player player, float amount, LatexVariant<?> latexVariant) {
        return progressPlayerTransfur(player, amount, latexVariant, null);
    }

    public static boolean progressPlayerTransfur(Player player, float amount, LatexVariant<?> latexVariant, @Nullable LatexEntity source) {
        if (player.isCreative() || player.isSpectator() || ProcessTransfur.isPlayerLatex(player))
            return false;
        boolean justHit = player.invulnerableTime == 20 && player.hurtDuration == 10;

        if (player.invulnerableTime > 10 && !justHit) {
            return false;
        }

        else {
            player.invulnerableTime = 20;
            player.hurtDuration = 10;
            player.hurtTime = player.hurtDuration;
            player.setLastHurtByMob(null);

            amount = LatexProtectionEnchantment.getLatexProtection(player, amount);
            if (ChangedCompatibility.isPlayerUsedByOtherMod(player)) {
                setPlayerTransfurProgress(player, new TransfurProgress(0, latexVariant));
                player.hurt(DamageSource.mobAttack(source == null ? latexVariant.getEntityType().create(player.level) : source), amount);
                return false;
            }

            float old = getPlayerTransfurProgress(player).progress;
            float next = old + amount;
            float max = Changed.config.server.transfurTolerance.get().floatValue();
            setPlayerTransfurProgress(player, new TransfurProgress(next, latexVariant));
            if (next >= max && old < max) {
                if (LatexVariant.PUBLIC_LATEX_FORMS.contains(latexVariant.getFormId()))
                    transfur(player, player.level, latexVariant, false, source);
                else {
                    var variant = PatreonBenefits.getPlayerSpecialVariant(player.getUUID());
                    transfur(player, player.level, variant == null ? LatexVariant.FALLBACK_VARIANT : variant, false, source);
                }

                return true;
            }

            return false;
        }
    }

    public static boolean willTransfur(LivingEntity entity, float amount) {
        amount = LatexProtectionEnchantment.getLatexProtection(entity, amount);

        if (entity instanceof Player player) {
            if (player.isCreative() || player.isSpectator() || ProcessTransfur.isPlayerLatex(player))
                return false;
            boolean justHit = player.invulnerableTime == 20 && player.hurtDuration == 10;

            if (player.invulnerableTime > 10 && !justHit) {
                return getPlayerTransfurProgress(player).progress >= Changed.config.server.transfurTolerance.get().floatValue();
            }

            else {
                player.invulnerableTime = 20;
                player.hurtDuration = 10;
                player.hurtTime = player.hurtDuration;

                float next = getPlayerTransfurProgress(player).progress + amount;
                return next >= Changed.config.server.transfurTolerance.get().floatValue();
            }
        }
        else {
            float health = entity.getHealth();
            float scale = 20.0f / Math.max(0.1f, Changed.config.server.transfurTolerance.get().floatValue());

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

    public static boolean progressTransfur(LivingEntity entity, float amount, LatexVariant<?> latexVariant) {
        return progressTransfur(entity, amount, latexVariant, null);
    }

    public static boolean progressTransfur(LivingEntity entity, float amount, LatexVariant<?> latexVariant, @Nullable LatexEntity source) {
        if (entity instanceof Player player)
            return progressPlayerTransfur(player, amount, latexVariant, source);
        else {
            amount = LatexProtectionEnchantment.getLatexProtection(entity, amount);
            float health = entity.getHealth();
            float scale = 20.0f / Math.max(0.1f, Changed.config.server.transfurTolerance.get().floatValue());

            if (entity.getType().is(ChangedTags.EntityTypes.HUMANOIDS)) {
                if (health <= amount * scale && health > 0.0F) {
                    ProcessTransfur.transfur(entity, entity.level, latexVariant, false, source);
                    return true;
                }

                else {
                    entity.hurt(ChangedDamageSources.entityTransfur(null), amount * scale);
                    return false;
                }
            }

            else {
                List<LatexVariant<?>> mobFusion = LatexVariant.getFusionCompatible(latexVariant, entity.getClass());

                if (mobFusion.isEmpty())
                    return false;

                if (health <= amount * scale && health > 0.0F) {
                    ProcessTransfur.transfur(entity, entity.level, mobFusion.get(entity.getRandom().nextInt(mobFusion.size())), false, source);
                    return true;
                }

                else {
                    entity.hurt(ChangedDamageSources.entityTransfur(null), amount * scale);
                    return false;
                }
            }
        }
    }

    public static boolean progressTransfur(LivingEntity entity, float amount, ResourceLocation type) {
        return progressTransfur(entity, amount, ChangedRegistry.LATEX_VARIANT.get().getValue(type));
    }

    public static LivingEntity changeTransfur(LivingEntity entity, LatexVariant<?> latexVariant) {
        if (entity instanceof Player player) {
            setPlayerLatexVariant(player, latexVariant);
            return player;
        } else {
            return latexVariant.replaceEntity(entity);
        }
    }

    public static void tickPlayerTransfurProgress(Player player) {
        if (isPlayerLatex(player))
            return;
        var progress = getPlayerTransfurProgress(player);
        if (progress.progress >= Changed.config.server.transfurTolerance.get().floatValue()) {
            if (LatexVariant.PUBLIC_LATEX_FORMS.contains(progress.variant.getFormId()))
                transfur(player, player.level, progress.variant, false);
            else {
                var variant = PatreonBenefits.getPlayerSpecialVariant(player.getUUID());
                transfur(player, player.level, variant == null ? LatexVariant.FALLBACK_VARIANT : variant, false);
            }
        }

        else if (!player.level.isClientSide && progress.progress > 0) {
            int deltaTicks = Math.max(((player.tickCount - player.getLastHurtByMobTimestamp()) / 8) - 20, 0);
            float nextTicks = Math.max(progress.progress - (deltaTicks * 0.001f), 0);
            setPlayerTransfurProgress(player, new TransfurProgress(nextTicks, progress.variant));
        }
    }

    public static LatexVariantInstance<?> getPlayerLatexVariant(Player player) {
        if (player instanceof PlayerDataExtension ext)
            return ext.getLatexVariant();
        return null;
    }

    public static class KeepConsciousEvent extends Event {
        public final Player player;
        public final boolean keepConscious;
        public boolean shouldKeepConscious;

        public KeepConsciousEvent(Player player, boolean keepConscious) {
            this.player = player;
            this.keepConscious = keepConscious;
        }

        @Override
        public boolean isCancelable() {
            return false;
        }
    }

    public static class EntityVariantAssigned extends Event {
        public final LivingEntity livingEntity;
        public final @Nullable LatexVariant<?> previousVariant;
        public final @Nullable LatexVariant<?> originalVariant;
        public @Nullable LatexVariant<?> variant;

        public EntityVariantAssigned(LivingEntity livingEntity, @Nullable LatexVariant<?> variant) {
            this.livingEntity = livingEntity;
            this.previousVariant = LatexVariant.getEntityVariant(livingEntity);
            this.originalVariant = variant;
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
            public final @Nullable LatexVariant<?> oldVariant;
            public final @Nullable LatexVariant<?> newVariant;

            public ChangedVariant(LivingEntity livingEntity, @Nullable LatexVariant<?> variant) {
                this.livingEntity = livingEntity;
                this.oldVariant = LatexVariant.getEntityVariant(livingEntity);
                this.newVariant = variant;
            }

            @Override
            public boolean isCancelable() {
                return false;
            }
        }
     }

    @Contract("_, null -> null; _, !null -> !null")
    public static @Nullable LatexVariantInstance<?> setPlayerLatexVariant(Player player, @Nullable LatexVariant<?> ogVariant) {
        PlayerDataExtension playerDataExtension = (PlayerDataExtension)player;
        EntityVariantAssigned event = new EntityVariantAssigned(player, ogVariant);
        MinecraftForge.EVENT_BUS.post(event);
        @Nullable LatexVariant<?> variant = event.variant;
        if (variant != null && !event.isRedundant())
            MinecraftForge.EVENT_BUS.post(new EntityVariantAssigned.ChangedVariant(player, variant));

        if (ChangedCompatibility.isPlayerUsedByOtherMod(player))
            variant = null;

        if (player instanceof ServerPlayer serverPlayer && variant != null)
            ChangedCriteriaTriggers.TRANSFUR.trigger(serverPlayer, variant);

        var oldVariant = playerDataExtension.getLatexVariant();
        if (variant != null && oldVariant != null && variant.getFormId().equals(oldVariant.getFormId()))
            return oldVariant;
        if (variant == null && oldVariant == null)
            return oldVariant;
        if (oldVariant != null && oldVariant.getLatexEntity() != null)
            oldVariant.getLatexEntity().discard();
        LatexVariantInstance<?> instance = LatexVariantInstance.variantFor(variant, player);
        playerDataExtension.setLatexVariant(instance);
        if (variant != null)
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0 + variant.additionalHealth);
        else
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0);
        if (oldVariant != null)
            oldVariant.unhookAll(player);
        if (player instanceof ServerPlayer serverPlayer)
            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> serverPlayer), SyncTransfurPacket.Builder.of(player));
        return instance;
    }

    public static LatexVariantInstance<?> setPlayerLatexVariantNamed(Player player, ResourceLocation variant) {
        return setPlayerLatexVariant(player, ChangedRegistry.LATEX_VARIANT.get().getValue(variant));
    }

    public static boolean isPlayerLatex(Player player) {
        if (player instanceof PlayerDataExtension ext)
            return ext.isLatex();
        return false;
    }

    public static <R> R ifPlayerLatex(Player player, Function<LatexVariantInstance<?>, R> isLatex, Supplier<R> notLatex) {
        LatexVariantInstance<?> variant = getPlayerLatexVariant(player);
        return variant != null ? isLatex.apply(variant) : notLatex.get();
    }

    public static <R> R ifPlayerLatex(Player player, Function<LatexVariantInstance<?>, R> isLatex) {
        LatexVariantInstance<?> variant = getPlayerLatexVariant(player);
        return variant != null ? isLatex.apply(variant) : null;
    }

    public static boolean ifPlayerLatex(Player player, Consumer<LatexVariantInstance<?>> isLatex, Runnable notLatex) {
        LatexVariantInstance<?> variant = getPlayerLatexVariant(player);
        if (variant != null)
            isLatex.accept(variant);
        else
            notLatex.run();
        return variant != null;
    }

    public static boolean ifPlayerLatex(Player player, Consumer<LatexVariantInstance<?>> isLatex) {
        LatexVariantInstance<?> variant = getPlayerLatexVariant(player);
        if (variant != null)
            isLatex.accept(variant);
        return variant != null;
    }

    public static boolean ifPlayerLatex(Player player, BiConsumer<Player, LatexVariantInstance<?>> isLatex) {
        LatexVariantInstance<?> variant = getPlayerLatexVariant(player);
        if (variant != null)
            isLatex.accept(player, variant);
        return variant != null;
    }

    // Checks if player is either not latex or is organic latex
    public static boolean isPlayerOrganic(Player player) {
        var variant = getPlayerLatexVariant(player);
        return variant == null || variant.getParent().getEntityType().is(ChangedTags.EntityTypes.ORGANIC_LATEX);
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        WhiteLatexBlock.whiteLatexNoCollideMap.clear();
    }

    public static class LatexedEntity {
        final LivingEntity entity;
        @Nullable
        final LatexVariant<?> variant;
        @NotNull
        final LatexVariant<?> transfur;
        final boolean isPlayer;
        @Nullable
        final LatexVariantInstance<?> playerVariant;

        LatexedEntity(LivingEntity entity) {
            this.entity = entity;
            this.variant = LatexVariant.getEntityVariant(entity);
            this.transfur = LatexVariant.getEntityTransfur(entity);
            this.isPlayer = entity instanceof Player;
            this.playerVariant = isPlayer ? ProcessTransfur.getPlayerLatexVariant((Player)entity) : null;
        }

        @Nullable
        public LatexEntity getLatexEntity() {
            if (isPlayer)
                return this.playerVariant == null ? null : this.playerVariant.getLatexEntity();
            else if (entity instanceof LatexEntity latexEntity)
                return latexEntity;
            return null;
        }

        static boolean isLatexed(LivingEntity entity) {
            return LatexVariant.getEntityVariant(entity) != null || LatexVariant.getEntityTransfur(entity) != null;
        }

        boolean haveTransfurMode() {
            if (entity instanceof LatexEntity latexEntity)
                return latexEntity.getTransfurMode() != TransfurMode.NONE;
            else if (playerVariant != null)
                return playerVariant.transfurMode != TransfurMode.NONE;
            return false;
        }

        boolean wantAbsorption() {
            boolean doesAbsorption;
            if (entity instanceof LatexEntity latexEntity)
                doesAbsorption = latexEntity.getTransfurMode() == TransfurMode.ABSORPTION;
            else if (playerVariant != null)
                doesAbsorption = playerVariant.transfurMode == TransfurMode.ABSORPTION;
            else if (variant != null)
                doesAbsorption = variant.transfurMode() == TransfurMode.ABSORPTION;
            else if (transfur.transfurMode() == TransfurMode.ABSORPTION)
                doesAbsorption = true;
            else if (transfur.getEntityType() == ChangedEntities.SPECIAL_LATEX.get())
                doesAbsorption = true;
            else
                doesAbsorption = false;

            return doesAbsorption;
        }
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

    private static boolean isOrganicLatex(LivingEntity entity) {
        if (entity instanceof LatexEntity latex) {
            if (latex instanceof SpecialLatex specialLatex)
                return specialLatex.getCurrentData() != null && specialLatex.getCurrentData().organic();
            else
                return entity.getType().is(ChangedTags.EntityTypes.ORGANIC_LATEX);
        }

        else return ifPlayerLatex(EntityUtil.playerOrNull(entity), variant -> {
            if (variant.getParent().getEntityType().is(ChangedTags.EntityTypes.ORGANIC_LATEX))
                return true;
            else if (variant.getLatexEntity() instanceof SpecialLatex special &&
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
        if (LatexVariant.getEntityVariant(event.getEntityLiving()) == null)
            return;

        if (event.getSource() instanceof EntityDamageSource entityDamageSource && entityDamageSource.getEntity() instanceof LivingEntity livingEntity) {
            if (event.getEntityLiving() instanceof LatexEntity latexEntity)
                latexEntity.onDamagedBy(latexEntity, livingEntity);
            ifPlayerLatex(EntityUtil.playerOrNull(event.getEntityLiving()), (player, variant) -> {
                variant.getLatexEntity().onDamagedBy(player, livingEntity);
            });
        }

        if (isOrganicLatex(event.getEntityLiving()))
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

    public static void killPlayerBy(Player player, LivingEntity source) {
        player.hurt(ChangedDamageSources.entityTransfur(source), Float.MAX_VALUE);
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

    protected static void onLivingAttackedByLatex(LivingAttackEvent event, LatexedEntity source) {
        if (source.transfur == null)
            return;
        if (!source.haveTransfurMode())
            return;

        LivingEntity entity = event.getEntityLiving();
        float amount = difficultyAdjustTransfurAmount(entity.level.getDifficulty(), event.getAmount() * (source.isPlayer ? 3.0f : 1.0f));

        // First, check for fusion
        LatexVariant<?> playerVariant = LatexVariant.getEntityVariant(event.getEntityLiving());
        List<LatexVariant<?>> possibleMobFusions = List.of();
        if (playerVariant != null) {
            var possibleFusion = LatexVariant.getFusionCompatible(source.variant, playerVariant);
            if (possibleFusion.size() > 0) {
                event.setCanceled(true);
                if (source.isPlayer) {
                    if (event.getEntityLiving() instanceof Player pvpLoser) {
                        transfur(source.entity, source.entity.level, source.variant, true);
                        killPlayerBy(pvpLoser, source.entity);
                    } else {
                        transfur(event.getEntityLiving(), source.entity.level, source.variant, true);
                        event.getEntityLiving().discard();
                    }
                } else {
                    transfur(event.getEntityLiving(), source.entity.level, source.variant, true);
                    source.entity.discard();
                }

                return;
            }
        }

        else {
            LatexVariant<?> checkSource = source.variant != null ? source.variant : source.transfur;

            possibleMobFusions = LatexVariant.getFusionCompatible(checkSource, event.getEntityLiving().getClass());
        }

        // Check if attacked entity is already latexed
        if (LatexedEntity.isLatexed(event.getEntityLiving())) {
            if (!possibleMobFusions.isEmpty()) {
                var mobFusionVariant = Util.getRandom(possibleMobFusions, source.entity.getRandom());

                if (source.entity instanceof Player sourcePlayer) {
                    float beforeHealth = sourcePlayer.getHealth();
                    setPlayerLatexVariant(sourcePlayer, mobFusionVariant);
                    sourcePlayer.setHealth(beforeHealth);
                }

                else {
                    source.entity.discard();
                    source = new LatexedEntity(mobFusionVariant.getEntityType().create(source.entity.level));
                    source.entity.level.addFreshEntity(source.entity);
                }

                source.entity.heal(14.0f); // Heal 7 hearts, and teleport to old entity location
                var pos = event.getEntityLiving().position();
                source.entity.teleportTo(pos.x, pos.y, pos.z);
                source.entity.setYRot(event.getEntityLiving().getYRot());
                source.entity.setXRot(event.getEntityLiving().getXRot());

                event.getEntityLiving().discard();
            }
            return;
        }
        // Check if attacked entity is not humanoid
        if (possibleMobFusions.isEmpty() && !event.getEntityLiving().getType().is(ChangedTags.EntityTypes.HUMANOIDS)) {
            bonusHurt(entity, ChangedDamageSources.entityTransfur(source.entity), 2.0f, false);
            return;
        }

        event.getEntityLiving().setLastHurtByMob(source.entity);
        event.setCanceled(true);
        double d1 = source.entity.getX() - entity.getX();

        double d0;
        for(d0 = source.entity.getZ() - entity.getZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
            d1 = (Math.random() - Math.random()) * 0.01D;
        }

        entity.hurtDir = (float)(Mth.atan2(d0, d1) * (double)(180F / (float)Math.PI) - (double)entity.getYRot());
        entity.knockback((double)0.4F, d1, d0);

        if(entity instanceof Player)
            ChangedSounds.broadcastSound(entity, ChangedSounds.BLOW1, 1, entity.level.random.nextFloat() * 0.1F + 0.9F);
            
        entity.hurt(ChangedDamageSources.entityTransfur(source.entity), 0.0F);
        boolean doesAbsorption = source.wantAbsorption();
        if (!possibleMobFusions.isEmpty())
            doesAbsorption = true;

        if (!doesAbsorption) { // Replication
            if (progressTransfur(event.getEntityLiving(), amount, source.transfur, source.getLatexEntity()))
                source.entity.heal(8f);
        }

        else { // Absorption
            if (!willTransfur(event.getEntityLiving(), amount)) {
                progressTransfur(event.getEntityLiving(), amount, source.transfur, source.getLatexEntity());
                return;
            }

            // Special scenario where source is NPC, and attacked is Player, transfur player with possible keepCon
            if (!source.isPlayer && event.getEntityLiving() instanceof Player &&
                    progressTransfur(event.getEntityLiving(), amount, source.transfur, source.getLatexEntity())) {
                source.entity.discard();
                return;
            }

            if (!possibleMobFusions.isEmpty()) {
                LatexVariant<?> mobFusionVariant = possibleMobFusions.get(source.entity.getRandom().nextInt(possibleMobFusions.size()));
                if (source.entity instanceof Player sourcePlayer) {
                    float beforeHealth = sourcePlayer.getHealth();
                    setPlayerLatexVariant(sourcePlayer, mobFusionVariant);
                    sourcePlayer.setHealth(beforeHealth);
                }

                else {
                    source.entity.discard();
                    source = new LatexedEntity(mobFusionVariant.getEntityType().create(source.entity.level));
                    source.entity.level.addFreshEntity(source.entity);
                }
            }

            else if (source.variant == null || !source.variant.getFormId().equals(source.transfur.getFormId())) {
                if (source.entity instanceof Player sourcePlayer) {
                    float beforeHealth = sourcePlayer.getHealth();
                    setPlayerLatexVariant(sourcePlayer, source.transfur);
                    sourcePlayer.setHealth(beforeHealth);
                }

                else {
                    source.entity.discard();
                    source = new LatexedEntity(source.transfur.getEntityType().create(source.entity.level));
                    source.entity.level.addFreshEntity(source.entity);
                }
            }

            source.entity.heal(14.0f); // Heal 7 hearts, and teleport to old entity location
            var pos = event.getEntityLiving().position();
            source.entity.teleportTo(pos.x, pos.y, pos.z);
            source.entity.setYRot(event.getEntityLiving().getYRot());
            source.entity.setXRot(event.getEntityLiving().getXRot());

            // Should be one-hit absorption here
            if (event.getEntityLiving() instanceof Player loserPlayer) {
                killPlayerBy(loserPlayer, source.entity);
            }

            else {
                event.getEntityLiving().discard();
            }

            ChangedSounds.broadcastSound(source.entity, source.transfur.sound, 1.0F, 1.0F);
        }
    }

    @SubscribeEvent
    public static void onLivingAttacked(LivingAttackEvent event) {
        //The entity getting hurt is a morph. Cancel the event.
        if(event.getEntityLiving().getPersistentData().contains(LatexVariant.NBT_PLAYER_ID)) {
            event.setCanceled(true);
            return;
        }
        if (event.getSource() instanceof ChangedDamageSources.TransfurDamageSource)
            return;
        if (event.getSource().isProjectile())
            return;
        if (event.getEntityLiving().isDamageSourceBlocked(event.getSource()))
            return;
        if (event.getSource() == DamageSource.CACTUS && LatexVariant.getEntityVariant(event.getEntityLiving()) != null) {
            if (!isOrganicLatex(event.getEntityLiving()))
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
        // Check for faction immunity
        LatexType factionD = LatexType.getEntityFactionLatexType(event.getEntityLiving());
        LatexType factionS = LatexType.getEntityFactionLatexType(sourceEntity);
        if (factionD == factionS && factionS != null) {
            event.setCanceled(true);
            return;
        }

        if (!getEntityAttackItem(sourceEntity).isEmpty())
            return;

        if (event.getEntityLiving() instanceof Player player && (player.isCreative() || player.isSpectator() || ChangedCompatibility.isPlayerUsedByOtherMod(player)))
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

    public static void transfur(LivingEntity entity, Level level, LatexVariant<?> variant, boolean keepConscious) {
        transfur(entity, level, variant, keepConscious, null);
    }

    // Transfurs an entity, keepConscious applies to players being transfurred
    public static void transfur(LivingEntity entity, Level level, LatexVariant<?> variant, boolean keepConscious, @Nullable LatexEntity source) {
        if (entity == null)
            return;
        if (entity.isDeadOrDying())
            return; // To prevent most bugs, entity has to be alive to transfur
        if (level.getGameRules().getBoolean(RULE_KEEP_BRAIN))
            keepConscious = true;
        else if (entity instanceof Player player) {
            if (player.isCreative())
                keepConscious = true;
            else {
                KeepConsciousEvent event = new KeepConsciousEvent(player, keepConscious);
                MinecraftForge.EVENT_BUS.post(event);
                keepConscious = event.keepConscious;
            }
        }

        if (!keepConscious) {
            for (var hand : InteractionHand.values()) {
                if (entity.getItemInHand(hand).is(Items.TOTEM_OF_UNDYING)) {
                    if (entity instanceof ServerPlayer serverPlayer) {
                        serverPlayer.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING), 1);
                        CriteriaTriggers.USED_TOTEM.trigger(serverPlayer, entity.getItemInHand(hand));
                    }

                    entity.getItemInHand(hand).shrink(1);
                    entity.level.broadcastEntityEvent(entity, (byte) 35);
                    keepConscious = true;
                    break;
                }
            }
        }

        if (entity.level.isClientSide && keepConscious && entity instanceof Player player && UniversalDist.isLocalPlayer(player)) {
            return;
        }

        if (variant == null)
            return;

        final BiConsumer<IAbstractLatex, LatexVariant<?>> onReplicate = (iAbstractLatex, variant1) -> {
            if (source != null)
                source.onReplicateOther(iAbstractLatex, variant1);
        };

        if (!LatexType.hasLatexType(entity)) {
            ChangedSounds.broadcastSound(entity, variant.sound, 1.0f, 1.0f);
            if (keepConscious && entity instanceof ServerPlayer player) {
                setPlayerLatexVariant(player, variant);

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
                onReplicate.accept(IAbstractLatex.forPlayer(player), variant);
            }

            else if (!entity.level.isClientSide) {
                EntityVariantAssigned event = new EntityVariantAssigned(entity, variant);
                MinecraftForge.EVENT_BUS.post(event);
                if (event.variant != null)
                    onReplicate.accept(IAbstractLatex.forLatex(event.variant.replaceEntity(entity)), event.variant);
            }
        }

        else {
            LatexVariant<?> current = LatexVariant.getEntityVariant(entity);
            List<LatexVariant<?>> possible = LatexVariant.getFusionCompatible(current, variant);

            if (possible.isEmpty())
                return;
            if (entity instanceof Player player) {
                var instance = getPlayerLatexVariant(player);
                if (instance != null && instance.ageAsVariant > entity.level.getGameRules().getInt(ChangedGameRules.RULE_FUSABILITY_DURATION_PLAYER))
                    return;
            }

            LatexVariant<?> fusion = possible.get(level.random.nextInt(possible.size()));
            ChangedSounds.broadcastSound(entity, fusion.sound, 1.0f, 1.0f);

            if (entity instanceof ServerPlayer player) {
                setPlayerLatexVariant(player, fusion);

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
                EntityVariantAssigned event = new EntityVariantAssigned(entity, fusion);
                MinecraftForge.EVENT_BUS.post(event);
                if (event.variant != null)
                    event.variant.replaceEntity(entity);
            }
        }
    }
}
