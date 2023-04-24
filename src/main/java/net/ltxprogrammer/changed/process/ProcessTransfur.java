package net.ltxprogrammer.changed.process;

import com.mojang.logging.LogUtils;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.WhiteLatexBlock;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.beast.SpecialLatex;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.network.packet.CheckForUpdatesPacket;
import net.ltxprogrammer.changed.network.packet.SyncTransfurPacket;
import net.ltxprogrammer.changed.network.packet.SyncTransfurProgressPacket;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.util.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
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

    public static final int TRANSFUR_PROGRESSION_TAKEOVER = 20000;
    public static record TransfurProgress(int ticks, ResourceLocation type) {}

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
            player.setLastHurtByMob(null);

            int next = getPlayerTransfurProgress(player).ticks + amount;
            setPlayerTransfurProgress(player, new TransfurProgress(next, type));
            return next >= TRANSFUR_PROGRESSION_TAKEOVER;
        }
    }

    public static boolean willTransfur(LivingEntity entity, int amount) {
        if (entity instanceof Player player) {
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
                return next >= TRANSFUR_PROGRESSION_TAKEOVER;
            }
        }
        else {
            float damage = amount / 1200.0f;
            float health = entity.getHealth();

            if (entity.getType().is(ChangedTags.EntityTypes.HUMANOIDS)) {
                if (health <= damage && health > 0.0F) {
                    return true;
                }

                else {
                    return false;
                }
            }

            else {
                return health <= damage && health > 0.0F;
            }
        }
    }

    public static boolean progressTransfur(LivingEntity entity, int amount, ResourceLocation type) {
        if (entity instanceof Player player)
            return progressPlayerTransfur(player, amount, type);
        else {
            float damage = amount / 1200.0f;
            float health = entity.getHealth();
            LatexVariant<?> latexVariant = ChangedRegistry.LATEX_VARIANT.get().getValue(type);

            if (entity.getType().is(ChangedTags.EntityTypes.HUMANOIDS)) {
                if (health <= damage && health > 0.0F) {
                    ProcessTransfur.transfur(entity, entity.level, latexVariant, false);
                    return true;
                }

                else {
                    entity.hurt(ChangedDamageSources.entityTransfur(null), damage);
                    return false;
                }
            }

            else {
                List<LatexVariant<?>> mobFusion = LatexVariant.getFusionCompatible(latexVariant, entity.getClass());

                if (mobFusion.isEmpty())
                    return false;

                if (health <= damage && health > 0.0F) {
                    ProcessTransfur.transfur(entity, entity.level, mobFusion.get(entity.getRandom().nextInt(mobFusion.size())), false);
                    return true;
                }

                else {
                    entity.hurt(ChangedDamageSources.entityTransfur(null), damage);
                    return false;
                }
            }
        }
    }

    public static void tickPlayerTransfurProgress(Player player) {
        if (isPlayerLatex(player))
            return;
        var progress = getPlayerTransfurProgress(player);
        if (progress.ticks >= TRANSFUR_PROGRESSION_TAKEOVER) {
            if (LatexVariant.PUBLIC_LATEX_FORMS.contains(progress.type))
                transfur(player, player.level, ChangedRegistry.LATEX_VARIANT.get().getValue(progress.type), false);
            else {
                var variant = PatreonBenefits.getPlayerSpecialVariant(player.getUUID());
                transfur(player, player.level, variant == null ? LatexVariant.FALLBACK_VARIANT : variant, false);
            }
        }

        else if (!player.level.isClientSide && progress.ticks > 0) {
            int deltaTicks = Math.max(((player.tickCount - player.getLastHurtByMobTimestamp()) / 8) - 20, 0);
            int nextTicks = Math.max(progress.ticks - deltaTicks, 0);
            setPlayerTransfurProgress(player, new TransfurProgress(nextTicks, progress.type));
        }
    }

    public static LatexVariantInstance<?> getPlayerLatexVariant(Player player) {
        if (player instanceof PlayerDataExtension ext)
            return ext.getLatexVariant();
        return null;
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
            if (livingEntity.tickCount == 0)
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
        if (!player.level.isClientSide)
            Changed.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), SyncTransfurPacket.Builder.of(player));
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

        static boolean isLatexed(LivingEntity entity) {
            return LatexVariant.getEntityVariant(entity) != null || LatexVariant.getEntityTransfur(entity) != null;
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

        else return ifPlayerLatex(Util.playerOrNull(entity), variant -> {
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

    protected static void onLivingAttackedByLatex(LivingAttackEvent event, LatexedEntity source) {
        if (source.transfur == null)
            return;

        LivingEntity entity = event.getEntityLiving();

        // First, check for fusion
        LatexVariant<?> playerVariant = LatexVariant.getEntityVariant(event.getEntityLiving());
        List<LatexVariant<?>> possibleMobFusions = List.of();
        if (playerVariant != null) {
            var possibleFusion = LatexVariant.getFusionCompatible(source.variant, playerVariant);
            if (possibleFusion.size() > 0) {
                LatexVariant<?> randomFusion = possibleFusion.get(source.entity.getRandom().nextInt(possibleFusion.size()));
                event.setCanceled(true);
                if (source.isPlayer) {
                    if (event.getEntityLiving() instanceof Player pvpLoser) {
                        transfur(source.entity, source.entity.level, playerVariant, true);
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
        if (LatexedEntity.isLatexed(event.getEntityLiving()))
            return;
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
            if (progressTransfur(event.getEntityLiving(), source.entity.level.getGameRules().getInt(ChangedGameRules.RULE_TRANSFUR_TOLERANCE),
                    source.transfur.getFormId()))
                source.entity.heal(8f);
        }

        else { // Absorption
            if (!willTransfur(event.getEntityLiving(), source.entity.level.getGameRules().getInt(ChangedGameRules.RULE_TRANSFUR_TOLERANCE))) {
                if (progressTransfur(event.getEntityLiving(), source.entity.level.getGameRules().getInt(ChangedGameRules.RULE_TRANSFUR_TOLERANCE),
                        source.transfur.getFormId()))
                    throw new RuntimeException("You got your function wrong dev! get gud!");
                return;
            }

            // Special scenario where source is NPC, and attacked is Player, transfur player with possible keepCon
            if (!source.isPlayer && event.getEntityLiving() instanceof Player &&
                    progressTransfur(event.getEntityLiving(), source.entity.level.getGameRules().getInt(ChangedGameRules.RULE_TRANSFUR_TOLERANCE),
                    source.transfur.getFormId())) {
                source.entity.discard();
                return;
            }

            if (!possibleMobFusions.isEmpty()) {
                LatexVariant<?> mobFusionVariant = possibleMobFusions.get(source.entity.getRandom().nextInt(possibleMobFusions.size()));
                if (source.entity instanceof Player sourcePlayer) {
                    float beforeHealth = sourcePlayer.getHealth();
                    getPlayerLatexVariant(sourcePlayer).unhookAll(sourcePlayer);
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
                    getPlayerLatexVariant(sourcePlayer).unhookAll(sourcePlayer);
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
        // Check for faction immunity
        LatexType factionD = LatexType.getEntityFactionLatexType(event.getEntityLiving());
        LatexType factionS = LatexType.getEntityFactionLatexType(sourceEntity);
        if (factionD == factionS && factionS != null) {
            event.setCanceled(true);
            return;
        }

        if (!getEntityAttackItem(sourceEntity).isEmpty())
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

    // Transfurs an entity, keepConscious applies to players being transfurred
    public static void transfur(LivingEntity entity, Level level, LatexVariant<?> variant, boolean keepConscious) {
        if (entity == null)
            return;
        if (entity.isDeadOrDying())
            return; // To prevent most bugs, entity has to be alive to transfur
        if (level.getGameRules().getBoolean(RULE_KEEP_BRAIN))
            keepConscious = true;
        else if (entity instanceof Player player && player.isCreative())
            keepConscious = true;
        else for (var hand : InteractionHand.values()) {
            if (entity.getItemInHand(hand).is(Items.TOTEM_OF_UNDYING)) {
                if (entity instanceof ServerPlayer serverPlayer) {
                    serverPlayer.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING), 1);
                    CriteriaTriggers.USED_TOTEM.trigger(serverPlayer, entity.getItemInHand(hand));
                }

                entity.getItemInHand(hand).shrink(1);
                entity.level.broadcastEntityEvent(entity, (byte)35);
                keepConscious = true;
                break;
            }
        }

        if (entity.level.isClientSide && keepConscious && entity instanceof Player player && UniversalDist.isLocalPlayer(player)) {
            return;
        }

        if (variant == null)
            return;
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
            }

            else if (!entity.level.isClientSide) {
                EntityVariantAssigned event = new EntityVariantAssigned(entity, variant);
                MinecraftForge.EVENT_BUS.post(event);
                if (event.variant != null)
                    event.variant.replaceEntity(entity);
            }
        }

        else {
            LatexVariant<?> current = LatexVariant.getEntityVariant(entity);
            List<LatexVariant<?>> possible = LatexVariant.getFusionCompatible(current, variant);

            if (possible.isEmpty())
                return;

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
