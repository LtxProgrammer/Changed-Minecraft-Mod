package net.ltxprogrammer.changed.entity.variant;

import com.google.common.collect.ImmutableMap;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.entity.robot.Exoskeleton;
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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = Changed.MODID)
public abstract class TransfurVariantInstance<T extends ChangedEntity> {
    private static final Cacheable<AttributeMap> DEFAULT_PLAYER_ATTRIBUTES = Cacheable.of(() -> new AttributeMap(Player.createAttributes().build()));

    protected final TransfurVariant<T> parent;
    protected final T entity;
    private final Player host;
    public final ImmutableMap<AbstractAbility<?>, AbstractAbilityInstance> abilityInstances;

    public AbstractAbility<?> selectedAbility = null;
    public AbstractAbility<?> menuAbility = null;
    public boolean abilityKeyState = false;
    public TransfurMode transfurMode;
    public TransfurVariant.BreatheMode breatheMode;
    public VisionType visionType;
    public MiningStrength miningStrength;
    public UseItemMode itemUseMode;
    public float jumpStrength;
    public float stepSize;
    public int ageAsVariant = 0;
    protected int air = -100;
    protected int jumpCharges = 0;
    private boolean dead;
    public int ticksBreathingUnderwater;
    public int ticksFlying;
    protected int ticksSinceLastAbilityActivity = 0;
    private int ticksInWaveVision = 0;

    private final Map<Attribute, Double> previousAttributes = new HashMap<>();
    private final Map<Attribute, Double> newAttributes = new HashMap<>();
    public float transfurProgressionO = 0.0f;
    public float transfurProgression = 0.0f;
    public TransfurContext transfurContext = TransfurContext.hazard(TransfurCause.ATTACK_REPLICATE_LEFT);
    public boolean willSurviveTransfur = true;
    protected boolean isTemporaryFromSuit = false;

    public void refreshAttributes() {
        newAttributes.clear();
    }

    private void captureBaseline(Map<Attribute, Double> baseValues, AttributeMap attributeMap) {
        baseValues.clear();
        baseValues.putAll(getBaseAttributeValues(attributeMap));
    }

    private Map<Attribute, Double> getBaseAttributeValues(AttributeMap attributeMap) {
        Map<Attribute, Double> map = new HashMap<>();
        ForgeRegistries.ATTRIBUTES.getValues().stream()
                .filter(attributeMap::hasAttribute)
                .forEach(attribute -> map.put(attribute, attributeMap.getBaseValue(attribute)));
        return map;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("ageAsVariant", ageAsVariant);
        tag.putInt("air", air);
        tag.putInt("jumpCharges", jumpCharges);
        tag.putBoolean("dead", dead);
        tag.putInt("ticksBreathingUnderwater", ticksBreathingUnderwater);
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

    public void load(CompoundTag tag) {
        ageAsVariant = tag.getInt("ageAsVariant");
        air = tag.getInt("air");
        jumpCharges = tag.getInt("jumpCharges");
        dead = tag.getBoolean("dead");
        ticksBreathingUnderwater = tag.getInt("ticksBreathingUnderwater");
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
        return getChangedEntity().getTransfurColor(this.transfurContext.cause);
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
        this.jumpStrength = parent.jumpStrength;

        var builder = new ImmutableMap.Builder<AbstractAbility<?>, AbstractAbilityInstance>();
        parent.abilities.forEach(abilityFunction -> {
            var ability = abilityFunction.apply(this.parent.getEntityType());
            if (ability != null)
                builder.put(ability, ability.makeInstance(IAbstractChangedEntity.forPlayer(host)));
        });
        abilityInstances = builder.build();
        if (abilityInstances.size() > 0)
            selectedAbility = abilityInstances.keySet().asList().get(0);
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

            isTemporaryFromSuit = true;
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
        TransfurVariant<?> variant = TransfurVariant.getEntityVariant(event.getEntity());
        if (variant != null && variant.isReducedFall()) {
            event.setDistance(0.4f * event.getDistance());
        }
        Exoskeleton.getEntityExoskeleton(event.getEntity())
                .ifPresent(pair -> {
                    event.setDistance(event.getDistance() * pair.getSecond().getFallDamageMultiplier(pair.getFirst()));
                });
    }

    public EntityDimensions getTransfurDimensions(Pose pose) {
        ChangedEntity changedEntity = getChangedEntity();
        final float morphProgress = getMorphProgression();

        if (morphProgress < 1f) {
            final var playerDim = host.getDimensions(pose);
            final var latexDim = changedEntity.getDimensions(pose);
            float width = Mth.lerp(morphProgress, playerDim.width, latexDim.width);
            float height = Mth.lerp(morphProgress, playerDim.height, latexDim.height);

            return new EntityDimensions(width, height, latexDim.fixed);
        } else {
            return changedEntity.getDimensions(pose);
        }
    }

    @SubscribeEvent
    public static void onSizeEvent(EntityEvent.Size event) {
        if (event.getEntity() instanceof Player player) {
            if (player.isAddedToWorld()) {
                ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                    ChangedEntity changedEntity = variant.getChangedEntity();
                    final float morphProgress = variant.getMorphProgression();
                    final EntityDimensions morphDimensions = variant.getTransfurDimensions(event.getPose());

                    if (morphProgress < 1f) {
                        event.setNewSize(morphDimensions);
                        event.setNewEyeHeight(Mth.lerp(morphProgress, player.getEyeHeight(event.getPose()), changedEntity.getEyeHeight(event.getPose())));
                    } else {
                        event.setNewSize(morphDimensions);
                        event.setNewEyeHeight(changedEntity.getEyeHeight(event.getPose()));
                    }
                });

                if (player instanceof PlayerDataExtension extension && extension.getPlayerMover() != null) {
                    event.setNewSize(extension.getPlayerMover().getDimensions(player, event.getPose(), event.getNewSize()));
                    event.setNewEyeHeight(extension.getPlayerMover().getEyeHeight(player, event.getPose(), event.getNewEyeHeight()));
                }
            }
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

            try {
                instance.tick();
                if (!event.player.isSpectator()) {
                    if (!instance.entity.level().isClientSide)
                        instance.entity.tickLeash();
                    instance.getChangedEntity().variantTick(event.player.level());
                }
            } catch (Exception x) {
                x.printStackTrace();
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
            transfurProgression += (1.0f / transfurContext.cause.getDuration()) * 0.05f;
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

    protected void tickFlying() {
        if (parent.canGlide && shouldApplyAbilities()) {
            if (!host.isCreative() && !host.isSpectator()) {
                if (host.getFoodData().getFoodLevel() <= 6.0F && host.getAbilities().mayfly) {
                    host.getAbilities().mayfly = false;
                    host.getAbilities().flying = false;
                    host.onUpdateAbilities();
                } else if (host.isEyeInFluid(FluidTags.WATER) && host.getAbilities().mayfly) {
                    host.getAbilities().mayfly = false;
                    host.getAbilities().flying = false;
                    host.onUpdateAbilities();
                } else if (host.getVehicle() != null && host.getAbilities().mayfly) {
                    host.getAbilities().mayfly = false;
                    host.getAbilities().flying = false;
                    host.onUpdateAbilities();
                } else if (host.getFoodData().getFoodLevel() > 6.0F && !host.getAbilities().mayfly) {
                    host.getAbilities().mayfly = true;
                    host.onUpdateAbilities();
                }

                if (host.getAbilities().flying) {
                    float horizontalPenalty = host.isSprinting() ? 0.825f : 0.8f;
                    float verticalPenalty = host.getDeltaMovement().y > 0.0 ? 0.45f : 0.8f;
                    host.setDeltaMovement(host.getDeltaMovement().multiply(horizontalPenalty, verticalPenalty, horizontalPenalty)); // Speed penalty
                    host.causeFoodExhaustion(host.isSprinting() ? 0.05F : 0.025F); // Food penalty
                }
            }

            if (!host.isSpectator() && host.getAbilities().flying)
                ticksFlying++;
            else
                ticksFlying = 0;
        } else
            ticksFlying = 0;
    }

    @SubscribeEvent
    public static void onLivingBreathe(LivingBreatheEvent event) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(event.getEntity()), variant -> {
            if (!variant.shouldApplyAbilities())
                return;

            // Let TransfurVariantInstance.tickBreathing() handle breathing
            event.setCanBreathe(true);
            event.setCanRefillAir(false);
        });
    }

    protected void tickBreathing() {
        if (!shouldApplyAbilities())
            return;

        var airSupplyMax = (int)host.getAttributeValue(ChangedAttributes.AIR_CAPACITY.get());
        var airSupplyScale = host.getAttributeValue(ChangedAttributes.AIR_CAPACITY.get()) / host.getMaxAirSupply();

        int airDelta;
        if (breatheMode == TransfurVariant.BreatheMode.NONE) {
            airDelta = 0;
            this.ticksBreathingUnderwater = 0;
        } else if (host.isEyeInFluidType(Fluids.WATER.getFluidType())) {
            if (breatheMode.canBreatheWater()) {
                airDelta = 4;
                this.ticksBreathingUnderwater++;
            }
            
            else if (host.hasEffect(MobEffects.WATER_BREATHING) || !host.canDrownInFluidType(ForgeMod.WATER_TYPE.get())) {
                airDelta = 0;
                this.ticksBreathingUnderwater = 0;
            }

            else {
                int i = EnchantmentHelper.getRespiration(host);
                airDelta = i > 0 && host.getRandom().nextInt(i + 1) > 0 ? 0 : -1;
                this.ticksBreathingUnderwater = 0;
            }
        } else {
            if (breatheMode.canBreatheAir()) {
                airDelta = 4;
            }

            else {
                int i = EnchantmentHelper.getRespiration(host);
                airDelta = i > 0 && host.getRandom().nextInt(i + 1) > 0 ? 0 : -1;
            }

            this.ticksBreathingUnderwater = 0;
        }

        if (air == -100) {
            air = (int)(host.getAirSupply() * airSupplyScale);
        }

        air += airDelta;
        air = Mth.clamp(air, -20, airSupplyMax);

        if (air <= 0) {
            LivingDrownEvent drownEvent = new LivingDrownEvent(host, air <= -20, 2.0F, 8);
            if (!MinecraftForge.EVENT_BUS.post(drownEvent) && drownEvent.isDrowning()) {
                air = 0;
                Vec3 vec3 = entity.getDeltaMovement();

                if (host.isEyeInFluidType(Fluids.WATER.getFluidType())) {
                    for (int i = 0; i < drownEvent.getBubbleCount(); ++i) {
                        double d2 = host.getRandom().nextDouble() - host.getRandom().nextDouble();
                        double d3 = host.getRandom().nextDouble() - host.getRandom().nextDouble();
                        double d4 = host.getRandom().nextDouble() - host.getRandom().nextDouble();
                        host.level().addParticle(ParticleTypes.BUBBLE, host.getX() + d2, host.getY() + d3, host.getZ() + d4, vec3.x, vec3.y, vec3.z);
                    }
                }

                if (drownEvent.getDamageAmount() > 0) host.hurt(entity.damageSources().drown(), drownEvent.getDamageAmount());
            }
        }

        if (!host.level().isClientSide) {
            host.setAirSupply(Mth.clamp(
                    (int) Math.round((float) air / airSupplyScale),
                    0,
                    host.getMaxAirSupply()));
        }
    }

    protected void tickAbilities() {
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
                    boolean oldState = controller.exchangeKeyState(abilityKeyState);
                    if (abilityKeyState || instance.getController().isCoolingDown())
                        this.resetTicksSinceLastAbilityActivity();
                    if (host.containerMenu == host.inventoryMenu && !host.isUsingItem() && !instance.getController().isCoolingDown())
                        instance.getUseType().check(abilityKeyState, oldState, controller);
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

    public void tick() {
        if (checkForTemporary())
            return;

        if (ageAsVariant == 0 && transfurProgression >= 1f)
            checkBreakItems(host);

        ageAsVariant++;

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

        this.tickBreathing();

        if (getEntityShape().isLegless() && host.isEyeInFluid(FluidTags.WATER) && shouldApplyAbilities())
            host.setPose(Pose.SWIMMING);

        // Sink in water
        if (host.getAttributeBaseValue(ForgeMod.SWIM_SPEED.get()) > 1.0) {
            host.setNoGravity(host.isEyeInFluid(FluidTags.WATER));
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
        if (tagAbilities.contains("selectedAbility")) {
            var savedSelected = ChangedRegistry.ABILITY.get().getValue(TagUtil.getResourceLocation(tagAbilities, "selectedAbility"));
            if (abilityInstances.containsKey(savedSelected))
                this.selectedAbility = savedSelected;
        }
        abilityInstances.forEach((name, instance) -> {
            String abName = Objects.requireNonNull(ChangedRegistry.ABILITY.getKey(name)).toString();
            if (!tagAbilities.contains(abName))
                return;
            CompoundTag abilityTag = tagAbilities.getCompound(abName);
            instance.readData(abilityTag);
        });
    }

    public void unhookAll(Player player) {
        abilityInstances.forEach((name, ability) -> {
            ability.onRemove();
        });
        mapAttributes(player, previousAttributes, TransfurVariantInstance::noOp);
        player.setHealth(Math.min(player.getMaxHealth(), player.getHealth()));
        if (parent.canGlide) {
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
        if (host.isSwimming() || host.isEyeInFluid(FluidTags.WATER) || host.isInWater()) {
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
}
