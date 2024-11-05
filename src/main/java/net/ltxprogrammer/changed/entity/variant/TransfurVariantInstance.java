package net.ltxprogrammer.changed.entity.variant;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.item.ExtendedItemProperties;
import net.ltxprogrammer.changed.network.packet.BasicPlayerInfoPacket;
import net.ltxprogrammer.changed.network.packet.SyncMoversPacket;
import net.ltxprogrammer.changed.network.packet.SyncTransfurPacket;
import net.ltxprogrammer.changed.process.Pale;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = Changed.MODID)
public class TransfurVariantInstance<T extends ChangedEntity> {
    private static final Cacheable<AttributeMap> DEFAULT_PLAYER_ATTRIBUTES = Cacheable.of(() -> new AttributeMap(Player.createAttributes().build()));

    private final TransfurVariant<T> parent;
    private final T entity;
    private final Player host;
    public final ImmutableMap<AbstractAbility<?>, AbstractAbilityInstance> abilityInstances;

    public AbstractAbility<?> selectedAbility = null;
    public AbstractAbility<?> menuAbility = null;
    public boolean abilityKeyState = false;
    public TransfurMode transfurMode;
    public int ageAsVariant = 0;
    protected int air = -100;
    protected int jumpCharges = 0;
    private boolean dead;
    public int ticksBreathingUnderwater;
    public int ticksFlying;
    protected int ticksSinceLastAbilityActivity = 0;

    private final Map<Attribute, Double> previousAttributes = new HashMap<>();
    private final Map<Attribute, Double> newAttributes = new HashMap<>();
    public float transfurProgressionO = 0.0f;
    public float transfurProgression = 0.0f;
    public TransfurContext transfurContext = TransfurContext.hazard(TransfurCause.ATTACK_REPLICATE_LEFT);
    public boolean willSurviveTransfur = true;
    private boolean isTemporaryFromSuit = false;

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
            map.putDouble(attribute.getRegistryName().toString(), base)
        ));
        tag.put("newAttributes", TagUtil.createMap(newAttributes, (attribute, base, map) ->
            map.putDouble(attribute.getRegistryName().toString(), base)
        ));
        tag.putFloat("transfurProgressionO", transfurProgressionO);
        tag.putFloat("transfurProgression", transfurProgression);
        tag.putBoolean("willSurviveTransfur", willSurviveTransfur);

        tag.put("transfurContext", transfurContext.toTag());
        tag.putBoolean("isTemporaryFromSuit", isTemporaryFromSuit);

        tag.put("abilities", this.saveAbilities());
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
                previousAttributes.put(ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(key)), map.getDouble(key))
        );
        TagUtil.readMap(tag.getCompound("newAttributes"), (key, map) ->
                newAttributes.put(ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(key)), map.getDouble(key))
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

        final float taggedProgress = tag.getFloat("transfurProgression");
        if (Mth.abs(transfurProgression - taggedProgress) > 0.5f) { // Prevent sync shudder
            transfurProgressionO = tag.getFloat("transfurProgressionO");
            transfurProgression = tag.getFloat("transfurProgression");
        }

        willSurviveTransfur = tag.getBoolean("willSurviveTransfur");
        isTemporaryFromSuit = tag.getBoolean("isTemporaryFromSuit");

        transfurContext = TransfurContext.fromTag(tag.getCompound("transfurContext"), host.level);

        this.loadAbilities(tag.getCompound("abilities"));
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
        this.entity = parent.generateForm(host, host.level);
        this.host = host;

        this.transfurMode = parent.transfurMode;

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

    public static TransfurVariantInstance<?> variantFor(@Nullable TransfurVariant<?> variant, @NotNull Player host) {
        return variant != null ? new TransfurVariantInstance<>(variant, host) : null;
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

        else if (!this.host.level.isClientSide && isTemporaryFromSuit) {
            if (grabber == null || grabber.getEntity().isDeadOrDying() || grabber.getEntity().isRemoved()) { // Remove variant if grabber doesn't exist
                ProcessTransfur.removePlayerTransfurVariant(this.host);
                return true;
            }

            var ability = grabber.getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (ability == null || ability.grabbedEntity != this.host) {
                ProcessTransfur.removePlayerTransfurVariant(this.host);
                return true;
            }
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
        if (!(event.getSource() instanceof EntityDamageSource entityDamageSource)) return;

        if (GrabEntityAbility.isEntityNoControl(entityDamageSource.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityRightClick(PlayerInteractEvent.EntityInteract event) {
        if (GrabEntityAbility.isEntityNoControl(event.getPlayer())) {
            event.setCanceled(true);
            return;
        }

        ProcessTransfur.ifPlayerTransfurred(event.getPlayer(), variant -> {
            if (!variant.getItemUseMode().canUseHand(event.getHand()))
                event.setCanceled(true);
        });
    }

    @SubscribeEvent
    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        if (GrabEntityAbility.isEntityNoControl(event.getPlayer())) {
            event.setCanceled(true);
            return;
        }

        ProcessTransfur.ifPlayerTransfurred(event.getPlayer(), variant -> {
            if (!variant.getItemUseMode().canUseHand(event.getHand()))
                event.setCanceled(true);
        });
    }

    @SubscribeEvent
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (GrabEntityAbility.isEntityNoControl(event.getPlayer())) {
            event.setCanceled(true);
            return;
        }

        ProcessTransfur.ifPlayerTransfurred(event.getPlayer(), variant -> {
            if (!variant.getItemUseMode().interactWithBlocks)
                event.setCanceled(true);
        });
    }

    @SubscribeEvent
    public static void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        if (GrabEntityAbility.isEntityNoControl(event.getPlayer())) {
            event.setCanceled(true);
            return;
        }

        ProcessTransfur.ifPlayerTransfurred(event.getPlayer(), variant -> {
            if (!variant.getItemUseMode().breakBlocks && !event.getPlayer().getAbilities().instabuild)
                event.setCanceled(true);
        });
    }

    @SubscribeEvent
    public static void onLivingFallEvent(LivingFallEvent event) {
        TransfurVariant<?> variant = TransfurVariant.getEntityVariant(event.getEntityLiving());
        if (variant != null && variant.isReducedFall()) {
            event.setDistance(0.4f * event.getDistance());
        }
    }

    @SubscribeEvent
    public static void onSizeEvent(EntityEvent.Size event) {
        if (event.getEntity() instanceof Player player) {
            if (player.isAddedToWorld()) {
                ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                    ChangedEntity changedEntity = variant.getChangedEntity();
                    final float morphProgress = variant.getMorphProgression();

                    if (morphProgress < 1f) {
                        final var playerDim = player.getDimensions(event.getPose());
                        final var latexDim = changedEntity.getDimensions(event.getPose());
                        float width = Mth.lerp(morphProgress, playerDim.width, latexDim.width);
                        float height = Mth.lerp(morphProgress, playerDim.height, latexDim.height);

                        event.setNewSize(new EntityDimensions(width, height, latexDim.fixed));
                        event.setNewEyeHeight(Mth.lerp(morphProgress, player.getEyeHeight(event.getPose()), changedEntity.getEyeHeight(event.getPose())));
                    } else {
                        event.setNewSize(changedEntity.getDimensions(event.getPose()));
                        event.setNewEyeHeight(changedEntity.getEyeHeight(event.getPose()));
                    }
                });

                if (player instanceof PlayerDataExtension extension && extension.getPlayerMover() != null) {
                    event.setNewSize(extension.getPlayerMover().getDimensions(event.getPose(), event.getNewSize()));
                    event.setNewEyeHeight(extension.getPlayerMover().getEyeHeight(event.getPose(), event.getNewEyeHeight()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Pale.tickPaleExposure(event.player);
            ProcessTransfur.ifPlayerTransfurred(event.player, instance -> {
                if (ChangedCompatibility.isPlayerUsedByOtherMod(event.player)) {
                    ProcessTransfur.removePlayerTransfurVariant(event.player);
                    return;
                }

                try {
                    instance.tick(event.player);
                    if (!event.player.isSpectator()) {
                        if (!instance.entity.level.isClientSide)
                            instance.entity.tickLeash();
                        instance.getChangedEntity().visualTick(event.player.level);
                    }
                } catch (Exception x) {
                    x.printStackTrace();
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof Player player) {
            ProcessTransfur.ifPlayerTransfurred(player, instance -> {
                instance.setDead();
                instance.unhookAll(player);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerSpawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntityLiving() instanceof Player player) {
            ProcessTransfur.ifPlayerTransfurred(player, instance -> {
                if (instance.isDead() && !player.level.getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM))
                    ProcessTransfur.removePlayerTransfurVariant(player);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SyncTransfurPacket.Builder builderTf = new SyncTransfurPacket.Builder();
            BasicPlayerInfoPacket.Builder builderBPI = new BasicPlayerInfoPacket.Builder();
            SyncMoversPacket.Builder builderMover = new SyncMoversPacket.Builder();
            player.getServer().getPlayerList().getPlayers().forEach(serverPlayer -> {
                builderTf.addPlayer(serverPlayer);
                builderBPI.addPlayer(serverPlayer);
                builderMover.addPlayer(serverPlayer);
            });

            final PacketDistributor.PacketTarget playerTarget = PacketDistributor.PLAYER.with(() -> player);
            Changed.PACKET_HANDLER.send(playerTarget, builderTf.build());
            Changed.PACKET_HANDLER.send(playerTarget, builderBPI.build());
            Changed.PACKET_HANDLER.send(playerTarget, builderMover.build());

            // Send client empty bpi packet, so it'll reply with its bpi
            Changed.PACKET_HANDLER.send(playerTarget, BasicPlayerInfoPacket.EMPTY);
        }
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

    protected void multiplyMotion(Player player, double mul) {
        var dP = player.getDeltaMovement();
        if (mul > 1f && dP.lengthSqr() > 0.0) {
            if (player.isOnGround()) {
                float friction = EntityUtil.getFrictionOnBlock(player);
                double mdP = dP.length();
                mul = clamp(0.75, mul, lerp(mul, 0.8 * mul / Math.pow(mdP, 1.0/6.0), mdP * 3));
                mul /= clamp(0.6, 1, friction) * 0.65 + 0.61;
                mul = Math.max(1.0, mul);
                if (Double.isNaN(mul)) {
                    Changed.LOGGER.error("Ran into NaN multiplier, falling back to zero");
                    mul = 0.0;
                }
            }
        } else if (mul < 1f && dP.lengthSqr() > 0.0) {
            if (player.isOnGround()) {
                float friction = EntityUtil.getFrictionOnBlock(player);
                mul = Math.min(1.0, Mth.map(friction, 1.0, 0.6, 0.95, mul));
                if (Double.isNaN(mul)) {
                    Changed.LOGGER.error("Ran into NaN multiplier, falling back to zero");
                    mul = 0.0;
                }
            }
        }

        player.setDeltaMovement(dP.multiply(mul, mul, mul));
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
            if(!ItemStack.isSameIgnoreDurability(living.getItemBySlot(value), shouldReset ? ItemStack.EMPTY : player.getItemBySlot(value))) {
                living.setItemSlot(value, shouldReset ? ItemStack.EMPTY : player.getItemBySlot(value).copy());
            }
        }

        /*if(player.isUsingItem())
        {
            if(player.getItemInUseMaxCount() == 1) {
                living.swingingArm = player.swingingArm;
                Hand hand = player.getActiveHand();
                living.setActiveHand(hand);
                living.setLivingFlag(1, true);
                living.setLivingFlag(2, hand == Hand.OFF_HAND);
            }
        }
        else {
            living.setLivingFlag(1, false);
            living.resetActiveHand();
        }*/
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
        if (itemStack.getItem() instanceof ExtendedItemProperties wearableItem && !wearableItem.allowedInSlot(itemStack, player, slot))
            return false;
        if (!entity.isItemAllowedInSlot(itemStack, slot))
            return false;

        int expectedLegCount;
        if (itemStack.getItem() instanceof ExtendedItemProperties wearableItem)
            expectedLegCount = wearableItem.getExpectedLegCount(itemStack);
        else
            expectedLegCount = 2;

        return switch (slot) {
            case LEGS, FEET -> expectedLegCount == parent.legCount;
            default -> true;
        };
    }

    protected static double correctScaling(Attribute attribute, double original) {
        if (attribute == Attributes.MOVEMENT_SPEED)
            return original * 0.1;
        if (attribute == ForgeMod.SWIM_SPEED.get())
            return original * Mth.map(original, 1.0, 5.0, 1.0, 0.75);
        return original;
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

        Arrays.stream(EquipmentSlot.values()).filter(slot -> slot.getType() == EquipmentSlot.Type.ARMOR)
                        .map(slot -> Pair.of(slot, player.getItemBySlot(slot))).forEach(pair -> {
                            var itemStack = pair.getSecond();

                            if (!itemStack.is(ChangedTags.Items.WILL_BREAK_ON_TF))
                                return;

                            int currentDamage = itemStack.getDamageValue();
                            int newDamage = (int) Math.ceil(Mth.lerp(morph, 0, itemStack.getMaxDamage()));

                            if (newDamage > currentDamage)
                                itemStack.setDamageValue(newDamage);
                            if (newDamage >= itemStack.getMaxDamage()) {
                                itemStack.shrink(1);

                                player.awardStat(Stats.ITEM_BROKEN.get(itemStack.getItem()));
                                player.broadcastBreakEvent(pair.getFirst());
                            }
                        });
    }

    public void tick(Player player) {
        if (player == null) return;

        if (checkForTemporary())
            return;

        if (ageAsVariant == 0 && transfurProgression >= 1f)
            checkBreakItems(player);

        ageAsVariant++;

        if (previousAttributes.isEmpty()) {
            if (transfurProgression == 0.0f)
                captureBaseline(previousAttributes, this.host.getAttributes());
            else
                captureBaseline(previousAttributes, DEFAULT_PLAYER_ATTRIBUTES.get());
        }

        if (newAttributes.isEmpty()) {
            captureBaseline(newAttributes, this.entity.getAttributes());

            mapAttributes(player, previousAttributes, TransfurVariantInstance::noOp,
                    newAttributes, TransfurVariantInstance::correctScaling, getMorphProgression());
        }

        transfurProgressionO = transfurProgression;
        if (transfurProgression < 1f) {
            transfurProgression += (1.0f / transfurContext.cause.getDuration()) * 0.05f;
            if (!player.level.getGameRules().getBoolean(ChangedGameRules.RULE_DO_TRANSFUR_ANIMATION)) {
                transfurProgressionO = 1f;
                transfurProgression = 1f;
            }

            if (player.level.getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_BRAIN)) {
                willSurviveTransfur = true;
            }

            checkBreakItems(player);
            mapAttributes(player, previousAttributes, TransfurVariantInstance::noOp,
                    newAttributes, TransfurVariantInstance::correctScaling, getMorphProgression());

            if (transfurProgression >= 1f && !willSurviveTransfur) {
                if (!player.level.isClientSide)
                    this.getParent().replaceEntity(player, transfurContext.source);
                return;
            }
        }

        if (transfurProgression >= 1f && !isTemporaryFromSuit()) {
            transfurContext = transfurContext.withSource(null);
            if (player instanceof ServerPlayer serverPlayer && willSurviveTransfur)
                ChangedCriteriaTriggers.TRANSFUR.trigger(serverPlayer, getParent());
        }

        player.refreshDimensions();
        if (player.isOnGround())
            jumpCharges = parent.extraJumpCharges;

        if (parent.rideable())
            player.stopRiding();

        if (parent.canGlide && shouldApplyAbilities()) {
            if (!player.isCreative() && !player.isSpectator()) {
                if (player.getFoodData().getFoodLevel() <= 6.0F && player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                    player.onUpdateAbilities();
                } else if (player.isEyeInFluid(FluidTags.WATER) && player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                    player.onUpdateAbilities();
                } else if (player.getVehicle() != null && player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = false;
                    player.getAbilities().flying = false;
                    player.onUpdateAbilities();
                } else if (player.getFoodData().getFoodLevel() > 6.0F && !player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = true;
                    player.onUpdateAbilities();
                }

                if (player.getAbilities().flying) {
                    float horizontalPenalty = player.isSprinting() ? 0.825f : 0.8f;
                    float verticalPenalty = player.getDeltaMovement().y > 0.0 ? 0.45f : 0.8f;
                    player.setDeltaMovement(player.getDeltaMovement().multiply(horizontalPenalty, verticalPenalty, horizontalPenalty)); // Speed penalty
                    player.causeFoodExhaustion(player.isSprinting() ? 0.05F : 0.025F); // Food penalty
                }
            }

            if (!player.isSpectator() && player.getAbilities().flying) {
                ticksFlying++;
                if (player instanceof ServerPlayer serverPlayer)
                    ChangedCriteriaTriggers.FLYING.trigger(serverPlayer, ticksFlying);
            } else
                ticksFlying = 0;

            if (!player.level.isClientSide) {
                this.entity.setChangedEntityFlag(ChangedEntity.FLAG_IS_FLYING, player.getAbilities().flying);
            }
        } else
            ticksFlying = 0;

        if (shouldApplyAbilities())
            this.ticksSinceLastAbilityActivity++;

        Arrays.stream(EquipmentSlot.values()).filter(slot -> slot.getType() == EquipmentSlot.Type.ARMOR)
                .forEach(slot -> { // Force unequip invalid items
                    var itemStack = player.getItemBySlot(slot);
                    if (!canWear(player, itemStack, slot)) {
                        ItemStack copy = itemStack.copy();
                        itemStack.setCount(0);
                        if (!player.addItem(copy))
                            player.drop(copy, false);
                    }
                });

        if(!player.level.isClientSide) {
            final double distance = 8D;
            final double farRunSpeed = 1.0D;
            final double nearRunSpeed = 1.2D;
            // Scare mobs
            for (Class<? extends PathfinderMob> entityClass : parent.scares) {
                if (entityClass.isAssignableFrom(AbstractVillager.class) && (!parent.ctor.get().is(ChangedTags.EntityTypes.LATEX) || player.isCreative() || player.isSpectator()))
                    continue;

                final double speedScale = entityClass.isAssignableFrom(AbstractVillager.class) ? 0.5D : 1.0D;

                List<? extends PathfinderMob> entitiesScared = player.level.getEntitiesOfClass(entityClass, player.getBoundingBox().inflate(distance, 6D, distance), entity -> entity.hasLineOfSight(player));

                for (var v : entitiesScared) {
                    //if the creature has no path, or the target path is < distance, make the creature run.
                    if (v.getNavigation().getPath() == null || player.distanceToSqr(v.getNavigation().getTargetPos().getX(), v.getNavigation().getTargetPos().getY(), v.getNavigation().getTargetPos().getZ()) < distance * distance) {
                        Vec3 vector3d = DefaultRandomPos.getPosAway(v, 16, 7, new Vec3(player.getX(), player.getY(), player.getZ()));

                        if (vector3d != null && player.distanceToSqr(vector3d) > player.distanceToSqr(v)) {
                            Path path = v.getNavigation().createPath(vector3d.x, vector3d.y, vector3d.z, 0);

                            if (path != null) {
                                double speed = v.distanceToSqr(player) < 49D ? nearRunSpeed : farRunSpeed;
                                v.getNavigation().moveTo(path, speed * speedScale);
                            }
                        }
                    }
                    else {
                        double speed = v.distanceToSqr(player) < 49D ? nearRunSpeed : farRunSpeed;
                        v.getNavigation().setSpeedModifier(speed * speedScale);
                    }

                    if (v.getTarget() == player)
                        v.setTarget(null);
                }
            }
        }

        // Breathing
        if (player.isAlive() && parent.breatheMode.canBreatheWater() && shouldApplyAbilities()) {
            if (air == -100) {
                air = player.getAirSupply();
            }

            //if the player is in water, add air
            if (player.isEyeInFluid(FluidTags.WATER)) {
                //Taken from determineNextAir in LivingEntity
                air = Math.min(air + 4, player.getMaxAirSupply());
                player.setAirSupply(air);
                this.ticksBreathingUnderwater++;
                if (player instanceof ServerPlayer serverPlayer)
                    ChangedCriteriaTriggers.AQUATIC_BREATHE.trigger(serverPlayer, this.ticksBreathingUnderwater);
            }

            //if the player is on land and the entity suffocates
            else if (!parent.breatheMode.canBreatheAir()) {
                //taken from decreaseAirSupply in Living Entity
                int i = EnchantmentHelper.getRespiration(player);
                air = i > 0 && player.getRandom().nextInt(i + 1) > 0 ? air : air - 1;

                if(air == -20)
                {
                    air = 0;

                    player.hurt(DamageSource.DROWN, 2F);
                }

                player.setAirSupply(air);
                this.ticksBreathingUnderwater = 0;
            }
            else {
                this.ticksBreathingUnderwater = 0;
            }
        }

        else if (player.isAlive() && !parent.breatheMode.canBreatheWater() && parent.breatheMode == TransfurVariant.BreatheMode.WEAK && shouldApplyAbilities()) {
            //if the player is in water, remove more air
            if (player.isEyeInFluid(FluidTags.WATER)) {
                int air = player.getAirSupply();
                if (air > -10)
                    player.setAirSupply(air-1);
                this.ticksBreathingUnderwater = 0;
            }
        }

        if (!parent.hasLegs && player.isEyeInFluid(FluidTags.WATER) && shouldApplyAbilities())
            player.setPose(Pose.SWIMMING);

        // Speed
        if (player.getAttributeValue(ForgeMod.SWIM_SPEED.get()) > 1.0) {
            player.setNoGravity(player.isEyeInFluid(FluidTags.WATER));
        }

        // Step size
        if (player.isCrouching() && parent.stepSize > 0.6f)
            player.maxUpStep = 0.6f;
        else
            player.maxUpStep = parent.stepSize;

        // Effects
        if (parent.visionType == VisionType.BLIND) {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 1, false, false, false));
        }

        for (var instance : abilityInstances.values()) {
            instance.getController().tickCoolDown();
        }

        if (!isTemporaryFromSuit() && shouldApplyAbilities()) {
            if (abilityInstances.containsKey(ChangedAbilities.GRAB_ENTITY_ABILITY.get())) {
                getAbilityInstance(ChangedAbilities.GRAB_ENTITY_ABILITY.get()).tickIdle();
            }

            if (selectedAbility != null) {
                var instance = abilityInstances.get(selectedAbility);
                if (instance != null) {
                    var controller = instance.getController();
                    boolean oldState = controller.exchangeKeyState(abilityKeyState);
                    if (abilityKeyState || instance.getController().isCoolingDown())
                        this.resetTicksSinceLastAbilityActivity();
                    if (player.containerMenu == player.inventoryMenu && !player.isUsingItem() && !instance.getController().isCoolingDown())
                        instance.getUseType().check(abilityKeyState, oldState, controller);
                }
            }

            if (menuAbility != null) {
                var instance = abilityInstances.get(menuAbility);
                if (instance != null && player.containerMenu != player.inventoryMenu)
                    instance.tick();
                else {
                    if (instance != null)
                        instance.stopUsing();
                    menuAbility = null;
                }
            }
        }

        sync(player);
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
                tagAbilities.put(Objects.requireNonNull(name.getRegistryName()).toString(), tagAbility);
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
            String abName = Objects.requireNonNull(name.getRegistryName()).toString();
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
        player.maxUpStep = 0.6F;
        player.setNoGravity(false);
        player.refreshDimensions();
    }

    public LatexType getLatexType() {
        return parent.getLatexType();
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
            return parent.itemUseMode;
    }

    public float getSwimEfficiency() {
        double baselineSwim = DEFAULT_PLAYER_ATTRIBUTES.get().getInstance(ForgeMod.SWIM_SPEED.get()).getBaseValue();
        double intendedSwim = entity.getAttributeBaseValue(ForgeMod.SWIM_SPEED.get());
        return (float)(baselineSwim / intendedSwim);
    }

    public float getSprintEfficiency() {
        double baselineSprint = DEFAULT_PLAYER_ATTRIBUTES.get().getInstance(Attributes.MOVEMENT_SPEED).getBaseValue();
        double intendedSprint = entity.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
        return (float)(baselineSprint / intendedSprint);
    }
}
