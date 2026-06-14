package net.ltxprogrammer.changed.entity.variant;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.AtomicDouble;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.*;
import net.ltxprogrammer.changed.ability.tree.AbilityTreeInstance;
import net.ltxprogrammer.changed.ability.tree.AbilityTrees;
import net.ltxprogrammer.changed.ability.tree.NodeEffect;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.entity.AccessoryEntities;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.item.ExtendedItemProperties;
import net.ltxprogrammer.changed.network.packet.BasicPlayerInfoPacket;
import net.ltxprogrammer.changed.network.packet.SyncMoversPacket;
import net.ltxprogrammer.changed.network.packet.SyncTransfurPacket;
import net.ltxprogrammer.changed.process.Pale;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.*;
import net.ltxprogrammer.changed.world.LatexCoverHitResult;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.ltxprogrammer.changed.world.enchantments.FormFittingEnchantment;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Changed.MODID)
public abstract class TransfurVariantInstance<T extends ChangedEntity> {
    private static final Cacheable<AttributeMap> DEFAULT_PLAYER_ATTRIBUTES = Cacheable.of(() -> new AttributeMap(Player.createAttributes().build()));

    protected final TransfurVariant<T> parent;
    protected final T entity;
    private final Player host;
    public final Map<AbstractAbility<?>, AbstractAbilityInstance> abilityInstances = new Object2ObjectArrayMap<>();

    public AbstractAbility<?> selectedAbility = null;
    public AbstractAbility<?> menuAbility = null;
    public KeyStateTracker abilityKey = new KeyStateTracker();
    public TransfurMode transfurMode;
    public TransfurVariant.BreatheMode breatheMode;
    public VisionType visionType;
    public MiningStrength miningStrength;
    public UseItemMode itemUseMode;
    public int ageAsVariant = 0;
    protected int jumpCharges = 0;
    protected double flightStamina = 0.0d;
    private boolean dead;
    public int ticksFlying;
    protected int ticksRechargingFlightStamina;
    protected int ticksSinceLastAbilityActivity = 0;
    private int ticksInWaveVision = 0;

    private final Map<Attribute, Double> previousAttributes = new HashMap<>();
    private final Map<Attribute, Double> newAttributes = new HashMap<>();
    public float transfurProgressionO = 0.0f;
    public float transfurProgression = 0.0f;
    public TransfurContext transfurContext = TransfurContext.hazard(TransfurCause.ATTACK_REPLICATE_LEFT);
    public boolean willSurviveTransfur = true;
    protected boolean isTemporaryFromSuit = false;

    protected final Map<VariantFeature, Double> variantFeatures = new HashMap<>();
    protected final Multimap<Codec<? extends NodeEffect>, NodeEffect> activeNodeEffects = HashMultimap.create();

    @SuppressWarnings("unchecked")
    public <E extends NodeEffect> void visitActiveNodeEffects(Codec<E> codec, Consumer<E> visitor) {
        activeNodeEffects.get(codec).forEach(nodeEffect -> visitor.accept((E)nodeEffect));
    }

    public double getFeatureLevel(VariantFeature feature) {
        return variantFeatures.computeIfAbsent(feature, key -> {
            AtomicDouble level = new AtomicDouble(0.0);

            visitActiveNodeEffects(ChangedAbilityTreeCodecs.ENABLE_FEATURE_EFFECT.get(), featureNode -> {
                if (featureNode.feature != key)
                    return;
                switch (key.combinator) {
                    case MAX ->
                            level.updateAndGet(current -> Math.max(featureNode.factor, current));
                    case SUM ->
                            level.updateAndGet(current -> featureNode.factor + current);
                    case BINARY ->
                            level.updateAndGet(current -> {
                                if (current >= 1.0d || featureNode.factor >= 1.0d)
                                    return 1.0d;
                                return 0.0d;
                            });
                }
            });

            return level.get();
        });
    }

    public boolean hasFeature(VariantFeature feature) {
        return getFeatureLevel(feature) > 0.0;
    }

    public void setNodeEffects(List<NodeEffect> nodeEffects) {
        this.variantFeatures.clear();
        this.activeNodeEffects.clear();
        nodeEffects.forEach(nodeEffect -> this.activeNodeEffects.put(nodeEffect.getCodec(), nodeEffect));
    }

    public TransfurVariant.BreatheMode getBreatheMode() {
        if (breatheMode == TransfurVariant.BreatheMode.NOT_REQUIRED)
            return TransfurVariant.BreatheMode.NOT_REQUIRED;

        boolean breatheAir = !hasFeature(ChangedVariantFeatures.BREATHE_DENY_AIR.get());
        boolean breatheWater = hasFeature(ChangedVariantFeatures.BREATHE_ACCEPT_WATER.get());

        if (breatheAir && breatheWater)
            return TransfurVariant.BreatheMode.ANY;
        if (breatheAir)
            return TransfurVariant.BreatheMode.NORMAL;
        if (breatheWater)
            return TransfurVariant.BreatheMode.WATER;
        return TransfurVariant.BreatheMode.CANNOT;
    }

    public void refreshAttributes() {
        newAttributes.clear();
    }

    private void captureBaseline(Map<Attribute, Double> baseValues, AttributeMap attributeMap) {
        baseValues.clear();
        baseValues.putAll(getBaseAttributeValues(attributeMap));
    }

    protected Map<Attribute, Double> getBaseAttributeValues(AttributeMap attributeMap) {
        Map<Attribute, Double> map = new HashMap<>();
        ForgeRegistries.ATTRIBUTES.getValues().stream()
                .filter(attributeMap::hasAttribute)
                .forEach(attribute -> map.put(attribute, attributeMap.getBaseValue(attribute)));
        return map;
    }

    public CompoundTag saveForNetwork() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("ageAsVariant", ageAsVariant);
        tag.putInt("jumpCharges", jumpCharges);
        if (flightStamina > 0.0d)
            tag.putDouble("flightStamina", flightStamina);
        tag.putBoolean("dead", dead);
        tag.putInt("ticksFlying", ticksFlying);

        tag.put("previousAttributes", TagUtil.createMap(previousAttributes, (attribute, base, map) ->
            map.putDouble(ForgeRegistries.ATTRIBUTES.getKey(attribute).toString(), base)
        ));
        tag.put("newAttributes", TagUtil.createMap(newAttributes, (attribute, base, map) ->
            map.putDouble(ForgeRegistries.ATTRIBUTES.getKey(attribute).toString(), base)
        ));
        tag.putFloat("transfurProgressionO", transfurProgressionO);
        tag.putFloat("transfurProgression", transfurProgression);
        tag.putBoolean("willSurviveTransfur", willSurviveTransfur);

        tag.put("transfurContext", transfurContext.toTag());
        tag.putBoolean("isTemporaryFromSuit", isTemporaryFromSuit);

        tag.put("abilities", this.saveAbilities());

        var entityData = entity.savePlayerVariantData();
        if (!entityData.isEmpty())
            tag.put("entityData", entityData);

        return tag;
    }

    public CompoundTag saveForStorage() {
        return this.saveForNetwork();
    }

    public void load(CompoundTag tag) {
        ageAsVariant = tag.getInt("ageAsVariant");
        jumpCharges = tag.getInt("jumpCharges");
        flightStamina = tag.getDouble("flightStamina");
        dead = tag.getBoolean("dead");
        ticksFlying = tag.getInt("ticksFlying");

        TagUtil.readMap(tag.getCompound("previousAttributes"), (key, map) ->
                Util.ifElse(Optional.ofNullable(ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.parse(key))), attribute ->
                        previousAttributes.put(attribute, map.getDouble(key)),
                        () -> TagUtil.LOGGER.warn("Missing registered attribute {}", key))
        );
        TagUtil.readMap(tag.getCompound("newAttributes"), (key, map) ->
                Util.ifElse(Optional.ofNullable(ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.parse(key))), attribute ->
                        newAttributes.put(attribute, map.getDouble(key)),
                        () -> TagUtil.LOGGER.warn("Missing registered attribute {}", key))
        );

        if (previousAttributes.isEmpty() && newAttributes.isEmpty()) {
            captureBaseline(previousAttributes, DEFAULT_PLAYER_ATTRIBUTES.get());
            captureBaseline(newAttributes, entity.getAttributes());
            mapAttributes(this.host, previousAttributes, TransfurVariantInstance::noOp,
                    newAttributes, TransfurVariantInstance::correctScaling, getMorphProgression());
        }

        if (previousAttributes.isEmpty())
            captureBaseline(previousAttributes, DEFAULT_PLAYER_ATTRIBUTES.get());

        if (newAttributes.isEmpty())
            captureBaseline(newAttributes, entity.getAttributes());

        transfurProgressionO = tag.getFloat("transfurProgressionO");
        transfurProgression = tag.getFloat("transfurProgression");

        willSurviveTransfur = tag.getBoolean("willSurviveTransfur");
        isTemporaryFromSuit = tag.getBoolean("isTemporaryFromSuit");

        transfurContext = TransfurContext.fromTag(tag.getCompound("transfurContext"), host.level());

        this.loadAbilities(tag.getCompound("abilities"));

        if (tag.contains("entityData"))
            entity.readPlayerVariantData(tag.getCompound("entityData"));
    }

    public void handleRespawn() {
        captureBaseline(previousAttributes, host.getAttributes());
        mapAttributes(this.host, previousAttributes, TransfurVariantInstance::noOp,
                newAttributes, TransfurVariantInstance::correctScaling, getMorphProgression());
    }

    public int getTicksSinceLastAbilityActivity() {
        return ticksSinceLastAbilityActivity;
    }

    public void resetTicksSinceLastAbilityActivity() {
        this.ticksSinceLastAbilityActivity = 0;
    }

    public float getTransfurProgression(float partial) {
        return Mth.lerp(Mth.positiveModulo(partial, 1.0f), transfurProgressionO, transfurProgression);
    }

    public float getMorphProgression() {
        return Transition.easeInOutSine(Mth.clamp(Mth.map(transfurProgression, 0.45f, 0.8f, 0.0f, 1.0f), 0.0f, 1.0f));
    }

    public float getMorphProgression(float partial) {
        return Transition.easeInOutSine(Mth.clamp(Mth.map(getTransfurProgression(partial), 0.45f, 0.8f, 0.0f, 1.0f), 0.0f, 1.0f));
    }

    public boolean isTransfurring() {
        return transfurProgression < 1f;
    }

    public boolean shouldApplyAbilities() {
        return transfurProgression >= 1f;
    }

    public Color3 getTransfurColor() {
        return getChangedEntity().getTransfurColor(this.transfurContext.cause());
    }

    public TransfurVariantInstance(TransfurVariant<T> parent, Player host) {
        this.parent = parent;
        this.entity = parent.generateForm(host, host.level());
        this.host = host;

        this.transfurMode = parent.transfurMode;
        this.breatheMode = parent.breatheMode;
        this.visionType = parent.visionType;
        this.miningStrength = parent.miningStrength;
        this.itemUseMode = parent.itemUseMode;
    }

    @Nullable
    public static TransfurVariantInstance<?> variantFor(@Nullable TransfurVariant<?> variant, @NotNull Player host) {
        return variant != null ? UniversalDist.createVariantFor(variant, host) : null;
    }

    public boolean isTemporaryFromSuit() {
        return isTemporaryFromSuit;
    }

    public void setTemporaryForSuit(boolean value) {
        this.isTemporaryFromSuit = value;
    }

    public boolean checkForTemporary() {
        final var grabber = GrabEntityAbility.getGrabber(this.host);

        if (!isTemporaryFromSuit && grabber != null) {
            var ability = grabber.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (ability == null || ability.grabbedEntity != this.host)
                return false;

            return false;
        }

        return false;
    }

    public TransfurVariant<T> getParent() {
        return parent;
    }

    public ResourceLocation getFormId() {
        return parent.getFormId();
    }

    public T getChangedEntity() {
        return entity;
    }

    public Player getHost() {
        return host;
    }

    public <A extends AbstractAbilityInstance> boolean hasAbility(AbstractAbility<A> ability) {
        return abilityInstances.containsKey(ability);
    }

    public <A extends AbstractAbilityInstance> A getAbilityInstance(AbstractAbility<A> ability) {
        try {
            return (A) abilityInstances.get(ability);
        } catch (Exception unused) {
            return null;
        }
    }

    public <A extends AbstractAbilityInstance> boolean ifHasAbility(AbstractAbility<A> ability, Consumer<A> consumer) {
        try {
            A instance = (A)abilityInstances.get(ability);
            if (instance != null)
                consumer.accept(instance);
            return instance != null;
        } catch (Exception unused) {
            return false;
        }
    }

    @SubscribeEvent
    public static void onEntityAttack(LivingAttackEvent event) {
        if (GrabEntityAbility.isEntityNoControl(event.getSource().getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityRightClick(PlayerInteractEvent.EntityInteract event) {
        if (GrabEntityAbility.isEntityNoControl(event.getEntity())) {
            event.setCanceled(true);
            return;
        }

        ProcessTransfur.ifPlayerTransfurred(event.getEntity(), variant -> {
            if (!variant.getItemUseMode().canUseHand(event.getHand()))
                event.setCanceled(true);
        });
    }

    @SubscribeEvent
    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        if (GrabEntityAbility.isEntityNoControl(event.getEntity())) {
            event.setCanceled(true);
            return;
        }

        ProcessTransfur.ifPlayerTransfurred(event.getEntity(), variant -> {
            if (!variant.getItemUseMode().canUseHand(event.getHand()))
                event.setCanceled(true);
        });
    }

    @SubscribeEvent
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (GrabEntityAbility.isEntityNoControl(event.getEntity())) {
            event.setCanceled(true);
            return;
        }

        ProcessTransfur.ifPlayerTransfurred(event.getEntity(), variant -> {
            if (!variant.getItemUseMode().interactWithBlocks)
                event.setCanceled(true);
        });

        if (event.getHitVec() instanceof LatexCoverHitResult) {
            event.setCancellationResult(LatexCoverState.handleInteractionEvent(event));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        if (GrabEntityAbility.isEntityNoControl(event.getEntity())) {
            event.setCanceled(true);
            return;
        }

        ProcessTransfur.ifPlayerTransfurred(event.getEntity(), variant -> {
            if (!variant.getItemUseMode().breakBlocks && !event.getEntity().getAbilities().instabuild)
                event.setCanceled(true);
        });
    }

    @SubscribeEvent
    public static void onLivingFallEvent(LivingFallEvent event) {
        var attributes = event.getEntity().getAttributes();
        if (attributes.hasAttribute(ChangedAttributes.FALL_RESISTANCE.get())) {
            event.setDistance(event.getDistance() / (float) attributes.getValue(ChangedAttributes.FALL_RESISTANCE.get()));
        }
    }

    public EntityDimensions getTransfurDimensions(Pose pose, EntityDimensions preTransfurDimensions) {
        ChangedEntity changedEntity = getChangedEntity();
        final float morphProgress = getMorphProgression();

        if (morphProgress < 1f) {
            final var latexDim = changedEntity.getDimensions(pose);
            float width = Mth.lerp(morphProgress, preTransfurDimensions.width, latexDim.width);
            float height = Mth.lerp(morphProgress, preTransfurDimensions.height, latexDim.height);

            return new EntityDimensions(width, height, latexDim.fixed);
        } else {
            return changedEntity.getDimensions(pose);
        }
    }

    public float getTransfurEyeHeight(Pose pose, float preTransfurEyeHeight) {
        ChangedEntity changedEntity = getChangedEntity();
        final float morphProgress = getMorphProgression();

        if (morphProgress < 1f) {
            return Mth.lerp(morphProgress, preTransfurEyeHeight, changedEntity.getEyeHeight(pose));
        } else {
            return changedEntity.getEyeHeight(pose);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        Pale.tickPaleExposure(event.player);
        ProcessTransfur.ifPlayerTransfurred(event.player, instance -> {
            if (ChangedCompatibility.isPlayerUsedByOtherMod(event.player)) {
                ProcessTransfur.removePlayerTransfurVariant(event.player);
                return;
            }

            instance.tick();
            if (!event.player.isSpectator()) {
                if (!instance.entity.level().isClientSide)
                    instance.entity.tickLeash();
                instance.getChangedEntity().variantTick(event.player.level());
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            ProcessTransfur.ifPlayerTransfurred(player, instance -> {
                instance.setDead();
                instance.unhookAll(player);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerSpawn(PlayerEvent.PlayerRespawnEvent event) {
        ProcessTransfur.ifPlayerTransfurred(event.getEntity(), instance -> {
            if (instance.isDead() && !event.getEntity().level().getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM))
                ProcessTransfur.removePlayerTransfurVariant(event.getEntity());
        });

        AccessoryEntities.INSTANCE.forceReloadAccessories(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(
                    Changed.PACKET_HANDLER.toVanillaPacket(BasicPlayerInfoPacket.EMPTY, NetworkDirection.PLAY_TO_CLIENT)
            );

            SyncTransfurPacket.Builder builderTf = new SyncTransfurPacket.Builder();
            builderTf.addPlayer(serverPlayer, true);
            if (builderTf.worthSending())
                serverPlayer.connection.send(
                        Changed.PACKET_HANDLER.toVanillaPacket(builderTf.build(), NetworkDirection.PLAY_TO_CLIENT)
                );

            SyncMoversPacket.Builder builderMover = new SyncMoversPacket.Builder();
            builderMover.addPlayer(serverPlayer, true);
            if (builderMover.worthSending())
                serverPlayer.connection.send(
                        Changed.PACKET_HANDLER.toVanillaPacket(builderMover.build(), NetworkDirection.PLAY_TO_CLIENT)
                );

            serverPlayer.connection.send(
                    Changed.PACKET_HANDLER.toVanillaPacket(AccessoryEntities.INSTANCE.syncPacket(serverPlayer), NetworkDirection.PLAY_TO_CLIENT)
            );

            AbilityTreeInstance.getForPlayer(serverPlayer).updateTrees(serverPlayer);
            serverPlayer.connection.send(
                    Changed.PACKET_HANDLER.toVanillaPacket(AbilityTrees.INSTANCE.syncPacket(serverPlayer), NetworkDirection.PLAY_TO_CLIENT)
            );
        }

        /*else if (event.getEntity() instanceof Player localPlayer && UniversalDist.isLocalPlayer(localPlayer)) {
            Changed.PACKET_HANDLER.sendToServer(BasicPlayerInfoPacket.Builder.of(localPlayer));

            QueryTransfurPacket.Builder builderTf = new QueryTransfurPacket.Builder();
            builderTf.addPlayer(localPlayer);
            localPlayer.level().players().forEach(builderTf::addPlayer);

            Changed.PACKET_HANDLER.sendToServer(builderTf.build());
        }*/
    }

    public void setDead() {
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }

    public int getJumpCharges() { return jumpCharges; }
    public void decJumpCharges() { jumpCharges -= 1; }

    protected double lerp(double a, double b, double x) {
        return a * (1 - x) + b * x;
    }

    protected double clamp(double min, double max, double x) {
        return Math.max(Math.min(x, max), min);
    }

    protected static class EntitySyncTeleporter implements ITeleporter {
        public static EntitySyncTeleporter INSTANCE = new EntitySyncTeleporter();

        @Override
        public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
            Entity newEntity = entity.getType().create(destWorld);
            if (newEntity != null) {
                newEntity.restoreFrom(entity);
                // Position and velocity will be handled after placement
                destWorld.addDuringTeleport(newEntity);
            }
            return newEntity;
        }
    }

    public static void syncEntityPosRotWithEntity(LivingEntity set, LivingEntity get) {
        set.setDeltaMovement(get.getDeltaMovement());
        set.setPos(get.getX(), get.getY(), get.getZ());
        set.setXRot(get.getXRot());
        set.setYRot(get.getYRot());

        set.xRotO = get.xRotO;
        set.yRotO = get.yRotO;
        set.xOld = get.xOld;
        set.yOld = get.yOld;
        set.zOld = get.zOld;
        set.yBodyRot = get.yBodyRot;
        set.yBodyRotO = get.yBodyRotO;
        set.yHeadRot = get.yHeadRot;
        set.yHeadRotO = get.yHeadRotO;
        set.xo = get.xo;
        set.yo = get.yo;
        set.zo = get.zo;
        set.xxa = get.xxa;
        set.yya = get.yya;
        set.zza = get.zza;
        set.walkDist = get.walkDist;
        set.walkDistO = get.walkDistO;
        set.moveDist = get.moveDist;
    }

    public static void syncEntityAndPlayer(ChangedEntity living, Player player) {
        living.xCloak = player.xCloak;
        living.yCloak = player.yCloak;
        living.zCloak = player.zCloak;
        living.xCloakO = player.xCloakO;
        living.yCloakO = player.yCloakO;
        living.zCloakO = player.zCloakO;

        living.oBob = player.oBob;
        living.bob = player.bob;
        living.tickCount = player.tickCount;
        living.getActiveEffectsMap().clear();
        living.setUnderlyingPlayer(player);

        living.mirrorLiving(player);

        //Entity stuff
        living.setHealth(living.getMaxHealth() * (player.getHealth() / player.getMaxHealth()));
        living.setAirSupply(player.getAirSupply());

        living.setInvisible(player.isInvisible());
        living.setInvulnerable(player.isInvulnerable());

        living.setUUID(player.getUUID());

        living.setGlowingTag(player.isCurrentlyGlowing());

        //EntityRendererManager stuff
        living.setRemainingFireTicks(player.getRemainingFireTicks());
        living.setTicksFrozen(player.getTicksFrozen());
        living.setArrowCount(player.getArrowCount());

        //Sync potions for rendering purposes
        living.getActiveEffectsMap().putAll(player.getActiveEffectsMap());

        TagUtil.replace(player.getPersistentData(), living.getPersistentData());

        specialEntityPlayerSync(living, player);
    }

    private static void specialEntityPlayerSync(ChangedEntity living, Player player) {
        living.setLeftHanded(player.getMainArm() == HumanoidArm.LEFT);
        living.setAggressive(player.isUsingItem());
    }

    public static void syncInventory(ChangedEntity living, Player player, boolean reset) {
        for (EquipmentSlot value : EquipmentSlot.values()) {
            boolean shouldReset = reset && (value == EquipmentSlot.MAINHAND || value == EquipmentSlot.OFFHAND);
            if(!ItemStack.isSameItem(living.getItemBySlot(value), shouldReset ? ItemStack.EMPTY : player.getItemBySlot(value))) {
                living.setItemSlot(value, shouldReset ? ItemStack.EMPTY : player.getItemBySlot(value).copy());
            }
        }
    }

    public void sync(Player player) {
        if (entity == null) return;

        syncInventory(entity, player, true); //reset the inventory so the entity doesn't actually use our equipment when ticking.

        syncEntityAndPlayer(entity, player);

        syncInventory(entity, player, false); //sync the inventory for rendering purposes.

        //latexForm.getDataManager().setClean(); //we don't want to flood the client with packets for an entity it can't find.
    }

    public boolean canWear(Player player, ItemStack itemStack, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND)
            return true;
        if (itemStack.isEmpty())
            return true;
        itemStack = FormFittingEnchantment.getFormFitted(player, itemStack, slot);
        if (itemStack.getItem() instanceof ExtendedItemProperties wearableItem) {
            if (!wearableItem.allowedInSlot(itemStack, player, slot))
                return false;
        }

        else { // Default expected entity shapes
            boolean shapeFits = switch (slot) {
                case HEAD -> getEntityShape().getHeadShape() == ClothingShape.Head.ANTHRO;
                case CHEST -> getEntityShape().getTorsoShape() == ClothingShape.Torso.ANTHRO;
                case LEGS -> getEntityShape().getLegsShape() == ClothingShape.Legs.BIPEDAL;
                case FEET -> getEntityShape().getFeetShape() == ClothingShape.Feet.BIPEDAL;
                default -> true;
            };

            if (!shapeFits)
                return false;
        }

        if (!entity.isItemAllowedInSlot(itemStack, slot))
            return false;

        return true;
    }

    protected static double correctScaling(Attribute attribute, double original) {
        if (attribute == Attributes.MOVEMENT_SPEED)
            return original * 0.1;
        if (attribute == ForgeMod.SWIM_SPEED.get())
            return original * Mth.map(original, 1.0, 5.0, 1.0, 0.75);
        if (attribute == ChangedAttributes.GRAB_STRUGGLE_STRENGTH.get())
            return original * (GrabEntityAbilityInstance.GRAB_STRENGTH_DECAY_PLAYER / GrabEntityAbilityInstance.GRAB_STRENGTH_DECAY);
        return ChangedCompatibility.correctAttributeScaling(attribute, original);
    }

    protected static double noOp(Attribute attribute, double original) {
        return original;
    }

    protected void mapAttributes(Player player, Map<Attribute, Double> variantAttributes, BiFunction<Attribute, Double, Double> fixer) {
        mapAttributes(player, variantAttributes, fixer, variantAttributes, fixer, 1.0f);
    }

    protected void mapAttributes(Player player, Map<Attribute, Double> variantAttributes0, BiFunction<Attribute, Double, Double> fixer0, Map<Attribute, Double> variantAttributes1, BiFunction<Attribute, Double, Double> fixer1, float alpha) {
        final var hostAttributes = player.getAttributes();

        float healthPercentage = player.getHealth() / player.getMaxHealth();

        ForgeRegistries.ATTRIBUTES.getValues().stream().filter(variantAttributes0::containsKey).filter(variantAttributes1::containsKey)
                .forEach(attribute -> {
                    final var hostAttributeInstance = hostAttributes.getInstance(attribute);
                    if (hostAttributeInstance == null) return;

                    final double base0 = fixer0.apply(attribute, variantAttributes0.get(attribute));
                    final double base1 = fixer1.apply(attribute, variantAttributes1.get(attribute));
                    final double newBase = Mth.lerp(alpha, base0, base1);

                    hostAttributeInstance.setBaseValue(newBase);
                });

        player.getAbilities().setWalkingSpeed((float) hostAttributes.getInstance(Attributes.MOVEMENT_SPEED).getBaseValue());
        player.setHealth(healthPercentage * player.getMaxHealth());
    }

    protected void checkBreakItems(Player player) {
        if (!this.getParent().is(ChangedTags.TransfurVariants.BREAK_ITEMS_ON_TF))
            return;
        if (player.getAbilities().instabuild)
            return;

        float morph = getMorphProgression();

        ItemUtil.getWearingItems(entity, ChangedTags.Items.WILL_BREAK_ON_TF).forEach(slottedItem -> {
            final ItemStack itemStack = slottedItem.itemStack();
            int currentDamage = itemStack.getDamageValue();
            int newDamage = (int) Math.ceil(Mth.lerp(morph, 0, itemStack.getMaxDamage()));

            if (newDamage > currentDamage)
                itemStack.setDamageValue(newDamage);
            if (newDamage >= itemStack.getMaxDamage()) {
                player.awardStat(Stats.ITEM_BROKEN.get(itemStack.getItem()));
                slottedItem.slot().ifLeft(player::broadcastBreakEvent).ifRight(slotType -> AccessorySlots.onBrokenAccessory(player, slotType));

                itemStack.shrink(1);
            }
        });
    }

    protected void tickTransfurProgress() {
        transfurProgressionO = transfurProgression;
        if (transfurProgression < 1f) {
            transfurProgression += (1.0f / transfurContext.cause().getDuration()) * 0.05f;
            if (!host.level().getGameRules().getBoolean(ChangedGameRules.RULE_DO_TRANSFUR_ANIMATION)) {
                transfurProgressionO = 1f;
                transfurProgression = 1f;
            }

            if (host.level().getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_BRAIN)) {
                willSurviveTransfur = true;
            }

            checkBreakItems(host);
            mapAttributes(host, previousAttributes, TransfurVariantInstance::noOp,
                    newAttributes, TransfurVariantInstance::correctScaling, getMorphProgression());

            if (transfurProgression >= 1f && willSurviveTransfur) {
                AccessoryEntities.INSTANCE.forceReloadAccessories(host);
            }
        }
    }

    protected boolean meetsCriteriaForFlying() {
        if (host.getFoodData().getFoodLevel() <= 6.0F)
            return false;
        if (host.isEyeInFluidType(ForgeMod.WATER_TYPE.get()))
            return false;
        if (host.getVehicle() != null)
            return false;
        if (getFlightStamina() <= 0.0d)
            return false;
        return true;
    }

    public double getFlightStamina() {
        return hasFeature(ChangedVariantFeatures.FLIGHT_UNLIMITED_STAMINA.get()) ? 9999.0d : flightStamina;
    }

    public void chargeFlightStamina(double cost) {
        if (hasFeature(ChangedVariantFeatures.FLIGHT_UNLIMITED_STAMINA.get())) {
            // Charge hunger
            flightStamina = getFlightStamina();
        } else {
            flightStamina = Math.max(
                    getFlightStamina() - cost,
                    0.0d
            );
            ticksRechargingFlightStamina = 0;
        }
    }

    public boolean canElytraGlide() {
        return hasFeature(ChangedVariantFeatures.FLIGHT_PASSIVE_GLIDE.get());
    }

    public boolean canCreativeFly() {
        return hasFeature(ChangedVariantFeatures.FLIGHT.get());
    }

    /// Called when fall flying and not using elytra-like item
    public boolean tickGliding() {
        if (shouldApplyAbilities()) {
            if (!host.isCreative() && !host.isSpectator()) {
                double staminaEfficiency = getFeatureLevel(ChangedVariantFeatures.GLIDE_STAMINA_EFFICIENCY.get());
                if (staminaEfficiency < 1.0d) {
                    chargeFlightStamina((1.0d - staminaEfficiency) * 0.05);
                    host.displayClientMessage(Component.literal("Flight Stamina: " + flightStamina), true);
                }

                return getFlightStamina() > 0.0d;
            }

            return true;
        }

        return false;
    }

    protected void tickFlying() {
        if (this.canCreativeFly() && shouldApplyAbilities()) {
            if (!host.isCreative() && !host.isSpectator()) {
                boolean meetsCriteria = this.meetsCriteriaForFlying();

                if (!meetsCriteria && host.getAbilities().mayfly) {
                    host.getAbilities().mayfly = false;
                    host.getAbilities().flying = false;
                    host.onUpdateAbilities();
                } else if (meetsCriteria && !host.getAbilities().mayfly) {
                    host.getAbilities().mayfly = true;
                    host.onUpdateAbilities();
                }

                if (host.getAbilities().flying) {
                    double staminaEfficiency = getFeatureLevel(ChangedVariantFeatures.FLIGHT_STAMINA_EFFICIENCY.get());
                    if (staminaEfficiency < 1.0d) {
                        chargeFlightStamina((1.0d - staminaEfficiency) * 0.05);
                        host.displayClientMessage(Component.literal("Flight Stamina: " + flightStamina), true);
                    }

                    float horizontalPenalty = host.isSprinting() ? 0.825f : 0.8f;
                    float verticalPenalty = host.getDeltaMovement().y > 0.0 ? 0.45f : 0.8f;
                    host.setDeltaMovement(host.getDeltaMovement().multiply(horizontalPenalty, verticalPenalty, horizontalPenalty)); // Speed penalty
                }
            }

            if (!host.isSpectator() && host.getAbilities().flying)
                ticksFlying++;
            else
                ticksFlying = 0;
        } else
            ticksFlying = 0;

        double maxFlightStamina = host.getAttributeValue(ChangedAttributes.MAX_FLIGHT_STAMINA.get());
        if ((host.onGround() || host.onClimbable()) && ticksRechargingFlightStamina <= 0 && getFlightStamina() < maxFlightStamina) {
            ticksRechargingFlightStamina = 1; // Start recharging flight stamina
        }

        if (getFlightStamina() >= maxFlightStamina) {
            flightStamina = maxFlightStamina;
            ticksRechargingFlightStamina = 0;
        } else if (ticksRechargingFlightStamina > 0) {
            ticksRechargingFlightStamina++;
            flightStamina = Math.min(flightStamina + 0.02 * ticksRechargingFlightStamina, maxFlightStamina);
            host.displayClientMessage(Component.literal("Flight Stamina: " + flightStamina), true);
        }
    }

    @SubscribeEvent
    public static void onLivingBreathe(LivingBreatheEvent event) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(event.getEntity()), variant -> {
            if (!variant.shouldApplyAbilities())
                return;

            variant.tickBreathing(event);
        });
    }

    protected void tickBreathing(LivingBreatheEvent event) {
        if (!shouldApplyAbilities())
            return;

        boolean oxygenSymbiosis = false;
        if (hasFeature(ChangedVariantFeatures.OXYGEN_SYMBIOSIS.get())) {
            var grab = getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (grab != null && grab.grabbedEntity != null && grab.suited) {
                oxygenSymbiosis = !grab.grabbedEntity.canDrownInFluidType(ForgeMod.EMPTY_TYPE.get());
            }
        }

        if (getBreatheMode() == TransfurVariant.BreatheMode.NOT_REQUIRED) {
            event.setCanBreathe(true);
            event.setCanRefillAir(false);
        } else if (host.isEyeInFluidType(Fluids.WATER.getFluidType())) {
            if (getBreatheMode().canBreatheWater()) {
                event.setCanBreathe(true);
                event.setCanRefillAir(true);

                double intakeRate = getFeatureLevel(ChangedVariantFeatures.BREATHE_ACCEPT_WATER.get());
                if (intakeRate <= 0.0)
                    event.setRefillAirAmount(event.getRefillAirAmount());
                else
                    event.setRefillAirAmount((int)(event.getRefillAirAmount() * intakeRate));
            }
        } else {
            if (!getBreatheMode().canBreatheAir()) {
                event.setCanBreathe(oxygenSymbiosis);
                event.setCanRefillAir(oxygenSymbiosis);
            }
        }
    }

    protected void updateAbilitiesMap() {
        Map<AbstractAbility<?>, Integer> abilitiesToAdd = new Object2IntArrayMap<>();
        visitActiveNodeEffects(ChangedAbilityTreeCodecs.UNLOCK_ACTIVE_ABILITY_EFFECT.get(), activeAbilityNode -> {
            abilitiesToAdd.compute(activeAbilityNode.ability, (ability, amplifier) -> {
                if (amplifier == null)
                    return activeAbilityNode.level;
                else
                    return Math.max(amplifier, activeAbilityNode.level);
            });
        });

        abilityInstances.keySet().removeIf(ability -> {
            if (!abilitiesToAdd.containsKey(ability)) {
                abilityInstances.get(ability).onRemove();
                return true;
            }

            return false;
        });

        abilitiesToAdd.forEach((ability, amplifier) -> {
            if (abilityInstances.containsKey(ability)) {
                abilityInstances.get(ability).setLevel(amplifier);
            } else {
                var abilityInstance = ability.makeInstance(IAbstractChangedEntity.forPlayerWithVariant(host, this));
                abilityInstances.put(ability, abilityInstance);
                abilityInstance.onAdd();
            }
        });
    }

    protected void tickAbilities() {
        this.updateAbilitiesMap();

        for (var instance : abilityInstances.values()) {
            instance.getController().tickCoolDown();
        }

        if (!isTemporaryFromSuit() && shouldApplyAbilities()) {
            for (var instance : abilityInstances.values()) {
                instance.tickIdle();
            }

            if (selectedAbility != null) {
                var instance = abilityInstances.get(selectedAbility);
                if (instance != null) {
                    var controller = instance.getController();
                    this.abilityKey.handleStateUpdates((isDown, wasDown, unique) -> {
                        boolean oldState = controller.exchangeKeyState(isDown);
                        if (isDown || instance.getController().isCoolingDown())
                            this.resetTicksSinceLastAbilityActivity();
                        if (!host.isUsingItem() && !instance.getController().isCoolingDown())
                            instance.getUseType().check(isDown, oldState, unique, controller);
                    });
                }
            }

            if (menuAbility != null) {
                var instance = abilityInstances.get(menuAbility);
                if (instance != null && host.containerMenu != host.inventoryMenu)
                    instance.tick();
                else {
                    if (instance != null)
                        instance.stopUsing();
                    menuAbility = null;
                }
            }
        }
    }

    public void tickAge() {
        ageAsVariant++;
    }

    public void tick() {
        if (checkForTemporary())
            return;

        if (ageAsVariant == 0 && transfurProgression >= 1f)
            checkBreakItems(host);

        this.tickAge();

        if (previousAttributes.isEmpty()) {
            if (transfurProgression == 0.0f)
                captureBaseline(previousAttributes, this.host.getAttributes());
            else
                captureBaseline(previousAttributes, DEFAULT_PLAYER_ATTRIBUTES.get());
        }

        if (newAttributes.isEmpty()) {
            captureBaseline(newAttributes, this.entity.getAttributes());

            mapAttributes(host, previousAttributes, TransfurVariantInstance::noOp,
                    newAttributes, TransfurVariantInstance::correctScaling, getMorphProgression());
        }

        this.tickTransfurProgress();

        host.refreshDimensions();
        if (host.onGround())
            jumpCharges = parent.extraJumpCharges;

        this.tickFlying();

        if (shouldApplyAbilities())
            this.ticksSinceLastAbilityActivity++;

        Arrays.stream(EquipmentSlot.values()).filter(slot -> slot.getType() == EquipmentSlot.Type.ARMOR)
                .forEach(slot -> { // Force unequip invalid items
                    var itemStack = host.getItemBySlot(slot);
                    if (!canWear(host, itemStack, slot)) {
                        ItemStack copy = itemStack.copy();
                        itemStack.setCount(0);
                        if (!host.addItem(copy))
                            host.drop(copy, false);
                    }
                });

        if (getEntityShape().isLegless() && host.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) && shouldApplyAbilities())
            host.setPose(Pose.SWIMMING);

        // Sink in water
        if (hasFeature(ChangedVariantFeatures.PREVENT_SINKING.get())) {
            host.setNoGravity(host.isEyeInFluidType(ForgeMod.WATER_TYPE.get()));
        } else {
            host.setNoGravity(false);
        }

        // Effects
        if (visionType == VisionType.BLIND) {
            host.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 1, false, false, false));
        }

        if (visionType == VisionType.WAVE_VISION) {
            ticksInWaveVision++;
        } else {
            ticksInWaveVision = 0;
        }

        this.tickAbilities();

        sync(host);
    }

    public CompoundTag saveAbilities() {
        CompoundTag tagAbilities = new CompoundTag();
        ResourceLocation selectedKey = ChangedRegistry.ABILITY.get().getKey(this.selectedAbility);
        if (selectedKey != null)
            TagUtil.putResourceLocation(tagAbilities, "selectedAbility", selectedKey);
        abilityInstances.forEach((name, ability) -> {
            CompoundTag tagAbility = new CompoundTag();
            ability.saveData(tagAbility);
            if (!tagAbility.isEmpty())
                tagAbilities.put(Objects.requireNonNull(ChangedRegistry.ABILITY.getKey(ability.ability)).toString(), tagAbility);
        });
        return tagAbilities;
    }

    public void loadAbilities(CompoundTag tagAbilities) {
        this.updateAbilitiesMap();

        if (tagAbilities.contains("selectedAbility")) {
            var savedSelected = ChangedRegistry.ABILITY.getValue(TagUtil.getResourceLocation(tagAbilities, "selectedAbility"));
            if (abilityInstances.containsKey(savedSelected))
                this.selectedAbility = savedSelected;
        }
        tagAbilities.getAllKeys().stream().filter(key -> !"selectedAbility".equals(key)).forEach(key -> {
            var ability = ChangedRegistry.ABILITY.getValue(ResourceLocation.parse(key));
            if (ability == null)
                return;

            CompoundTag abilityTag = tagAbilities.getCompound(key);

            if (abilityInstances.containsKey(ability)) {
                abilityInstances.get(ability).readData(abilityTag);
            } else { // Ability no longer permitted. Load from tag and call onRemove()
                var abilityInstance = ability.makeInstance(IAbstractChangedEntity.forPlayerWithVariant(host, this));
                abilityInstance.readData(abilityTag);
                abilityInstance.onRemove();
            }
        });
    }

    public void unhookAll(Player player) {
        abilityInstances.forEach((name, ability) -> {
            ability.onRemove();
        });
        mapAttributes(player, previousAttributes, TransfurVariantInstance::noOp);
        player.setHealth(Math.min(player.getMaxHealth(), player.getHealth()));
        if (this.canCreativeFly()) {
            player.getAbilities().mayfly = player.isCreative() || player.isSpectator();
            if (!player.isCreative() && !player.isSpectator()) {
                player.getAbilities().flying = false;
            }
            player.onUpdateAbilities();
        }
        player.setNoGravity(false);
        player.refreshDimensions();
    }

    public LatexType getLatexType() {
        return entity.getLatexType();
    }

    public boolean is(TransfurVariant<?> variant) {
        return parent.is(variant);
    }

    public boolean is(Supplier<? extends TransfurVariant<?>> variant) {
        return parent.is(variant);
    }

    @Nullable
    public AbstractAbilityInstance getSelectedAbility() {
        return abilityInstances.get(this.selectedAbility);
    }

    public void setSelectedAbility(AbstractAbility<?> ability) {
        if (abilityInstances.containsKey(ability)) {
            this.resetTicksSinceLastAbilityActivity();
            var instance = abilityInstances.get(ability);

            if (instance.getUseType() != AbstractAbility.UseType.MENU) {
                if (this.selectedAbility != ability)
                    instance.onSelected();
                this.selectedAbility = ability;
            } else {
                instance.startUsing();
                this.menuAbility = ability;
            }
        }
    }

    public UseItemMode getItemUseMode() {
        var instance = getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (instance != null && instance.shouldAnimateArms())
            return UseItemMode.NONE;
        else
            return itemUseMode;
    }

    public float getSwimEfficiency() {
        if (!newAttributes.containsKey(ForgeMod.SWIM_SPEED.get()))
            return 1.0f;

        double baselineSwim = DEFAULT_PLAYER_ATTRIBUTES.get().getBaseValue(ForgeMod.SWIM_SPEED.get());
        double intendedSwim = newAttributes.get(ForgeMod.SWIM_SPEED.get());
        return (float)(baselineSwim / intendedSwim);
    }

    public float getSprintEfficiency() {
        if (!newAttributes.containsKey(Attributes.MOVEMENT_SPEED))
            return 1.0f;

        double baselineSprint = DEFAULT_PLAYER_ATTRIBUTES.get().getBaseValue(Attributes.MOVEMENT_SPEED);
        double intendedSprint = newAttributes.get(Attributes.MOVEMENT_SPEED) * 0.1;
        return (float)(baselineSprint / intendedSprint);
    }

    public float getFoodEfficiency() {
        if (host.isSwimming() || host.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) || host.isInWater()) {
            return getSwimEfficiency();
        } else if (host.onGround() && host.isSprinting()) {
            return getSprintEfficiency();
        }

        return 1.0f;
    }

    public int getTicksInWaveVision() {
        return ticksInWaveVision;
    }

    public EntityShape getEntityShape() {
        return entity.getEntityShape();
    }

    public void prepareForRender(float partialTicks) {}

    public Pair<Color3, Color3> getColors() {
        return ChangedEntities.getEntityColor(this.entity);
    }
}
