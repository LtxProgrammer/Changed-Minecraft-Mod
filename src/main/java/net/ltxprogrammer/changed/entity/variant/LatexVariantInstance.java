package net.ltxprogrammer.changed.entity.variant;

import com.google.common.collect.ImmutableMap;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.beast.LatexBee;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedCriteriaTriggers;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.item.AbdomenArmor;
import net.ltxprogrammer.changed.network.packet.SyncTransfurPacket;
import net.ltxprogrammer.changed.process.Pale;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = Changed.MODID)
public class LatexVariantInstance<T extends LatexEntity> {
    private final LatexVariant<T> parent;
    private final T entity;
    public final ImmutableMap<AbstractAbility<?>, AbstractAbilityInstance> abilityInstances;
    private final List<AbstractAbility<?>> activeAbilities = new ArrayList<>();

    private final AttributeModifier attributeModifierSwimSpeed;
    public TransfurMode transfurMode;
    protected int air = -100;
    protected int jumpCharges = 0;
    protected float lastSwimMul = 1F;
    private boolean dead;
    public int ticksBreathingUnderwater;
    public int ticksWhiteLatex;

    public LatexVariantInstance(LatexVariant<T> parent, Player host) {
        this.parent = parent;
        this.entity = parent.generateForm(host, host.level);

        this.transfurMode = parent.transfurMode;

        var builder = new ImmutableMap.Builder<AbstractAbility<?>, AbstractAbilityInstance>();
        parent.abilities.forEach(ability -> builder.put(ability.get(), ability.get().makeInstance(host, this)));
        abilityInstances = builder.build();

        attributeModifierSwimSpeed = new AttributeModifier(UUID.fromString("5c40eef3-ef3e-4d8d-9437-0da1925473d7"), "changed:trait_swim_speed", parent.swimSpeed, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    public static LatexVariantInstance<?> variantFor(@Nullable LatexVariant<?> variant, @NotNull Player host) {
        return variant != null ? new LatexVariantInstance<>(variant, host) : null;
    }

    public LatexVariant<T> getParent() {
        return parent;
    }

    public ResourceLocation getFormId() {
        return parent.getFormId();
    }

    public T getLatexEntity() {
        return entity;
    }

    public <A extends AbstractAbilityInstance> A getAbilityInstance(AbstractAbility<A> ability) {
        try {
            return (A) abilityInstances.get(ability);
        } catch (Exception unused) {
            return null;
        }
    }

    public void forEachActiveAbility(Consumer<AbstractAbilityInstance> consumer) {
        activeAbilities.forEach(name -> consumer.accept(abilityInstances.get(name)));
    }

    @SubscribeEvent
    public static void onLivingFallEvent(LivingFallEvent event) {
        LatexVariant<?> variant = LatexVariant.getEntityVariant(event.getEntityLiving());
        if (variant != null && variant.isReducedFall()) {
            event.setDamageMultiplier(0.5f * event.getDamageMultiplier());
        }
    }

    @SubscribeEvent
    public static void onSizeEvent(EntityEvent.Size event) {
        if (event.getEntity() instanceof Player player) {
            if (player.isAddedToWorld()) {
                ProcessTransfur.ifPlayerLatex(player, variant -> {
                    LatexEntity latexEntity = variant.getLatexEntity();

                    event.setNewSize(latexEntity.getDimensions(event.getPose()));
                    event.setNewEyeHeight(latexEntity.getEyeHeight(event.getPose()));
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Pale.tickPaleExposure(event.player);
            ProcessTransfur.ifPlayerLatex(event.player, instance -> {
                try {
                    instance.tick(event.player);
                    if (!event.player.isSpectator()) {
                        instance.getLatexEntity().visualTick(event.player.level);
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
            ProcessTransfur.ifPlayerLatex(player, instance -> {
                instance.setDead();
                instance.unhookAll(player);
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerSpawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntityLiving() instanceof Player player) {
            ProcessTransfur.ifPlayerLatex(player, instance -> {
                if (instance.isDead())
                    ProcessTransfur.setPlayerLatexVariant(player, null);
            });
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

        if (mul > 1f) {
            if (player.isOnGround()) {
                float friction = player.getLevel().getBlockState(player.blockPosition().below())
                        .getFriction(player.getLevel(), player.blockPosition(), player);
                double mdP = dP.length();
                mul = clamp(0.75, mul, lerp(mul, 0.8 * mul / Math.pow(mdP, 1.0/6.0), mdP * 3));
                mul /= clamp(0.6, 1, friction) * 0.65 + 0.61;
            }
        }

        player.setDeltaMovement(dP.multiply(mul, mul, mul));
    }

    private static void syncEntityPosRotWithPlayer(LatexEntity living, Player player) {
        living.tickCount = player.tickCount;

        living.setPos(player.getX(), player.getY(), player.getZ());
        living.setXRot(player.getXRot());
        living.setYRot(player.getYRot());

        living.xRotO = player.xRotO;
        living.yRotO = player.yRotO;
        living.xOld = player.xOld;
        living.yOld = player.yOld;
        living.zOld = player.zOld;
        living.yBodyRot = player.yBodyRot;
        living.yBodyRotO = player.yBodyRotO;
        living.yHeadRot = player.yHeadRot;
        living.yHeadRotO = player.yHeadRotO;
        living.xo = player.xo;
        living.yo = player.yo;
        living.zo = player.zo;
        living.xxa = player.xxa;
        living.yya = player.yya;
        living.zza = player.zza;
        living.walkDist = player.walkDist;
        living.walkDistO = player.walkDistO;
        living.moveDist = player.moveDist;

        living.getActiveEffectsMap().clear();
    }

    public static void syncEntityAndPlayer(LatexEntity living, Player player) {
        syncEntityPosRotWithPlayer(living, player); //re-sync with the player position in case the entity moved whilst ticking.

        living.setUnderlyingPlayer(player);

        //Others
        living.swingingArm = player.swingingArm;
        living.swinging = player.swinging;
        living.swingTime = player.swingTime;

        living.setDeltaMovement(player.getDeltaMovement());

        //Entity stuff
        living.horizontalCollision = player.horizontalCollision;
        living.verticalCollision = player.verticalCollision;
        living.setOnGround(player.isOnGround());
        living.setShiftKeyDown(player.isCrouching());
        if (living.isSprinting() != player.isSprinting())
            living.setSprinting(player.isSprinting());
        living.setSwimming(player.isSwimming());

        living.setHealth(living.getMaxHealth() * (player.getHealth() / player.getMaxHealth()));
        living.setAirSupply(player.getAirSupply());
        living.hurtTime = player.hurtTime;
        living.deathTime = player.deathTime;
        living.animationPosition = player.animationPosition;
        living.animationSpeed = player.animationSpeed;
        living.animationSpeedOld = player.animationSpeedOld;
        living.attackAnim = player.attackAnim;
        living.oAttackAnim = player.oAttackAnim;
        living.flyDist = player.flyDist;
        living.flyingSpeed = player.flyingSpeed;

        living.wasTouchingWater = player.wasTouchingWater;
        living.swimAmount = player.swimAmount;
        living.swimAmountO = player.swimAmountO;

        living.fallFlyTicks = player.fallFlyTicks;

        living.setSharedFlag(7, player.isFallFlying());

        living.vehicle = player.vehicle;

        living.useItemRemaining = player.useItemRemaining;

        Pose pose = living.getPose();
        living.setPose(player.getPose());

        if(pose != living.getPose()) {
            living.refreshDimensions();
        }

        if(player.getSleepingPos().isPresent())
        {
            living.setSleepingPos(player.getSleepingPos().get());
        }
        else
        {
            living.clearSleepingPos();
        }

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

    private static void specialEntityPlayerSync(LatexEntity living, Player player) {
        living.setLeftHanded(player.getMainArm() == HumanoidArm.LEFT);
        living.setAggressive(player.isUsingItem());
    }

    public static void syncInventory(LatexEntity living, Player player, boolean reset) {
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

    public void tick(Player player) {
        if (player == null) return;

        player.refreshDimensions();
        if (player.isOnGround())
            jumpCharges = parent.extraJumpCharges;

        if (parent.rideable() || !parent.hasLegs)
            player.stopRiding();

        if (!parent.hasLegs) {
            player.getArmorSlots().forEach(itemStack -> { // Force unequip invalid items
                if (itemStack.getItem() instanceof ArmorItem armorItem) {
                    if (armorItem instanceof AbdomenArmor)
                        return; // Allowed

                    switch (armorItem.getSlot()) {
                        case FEET:
                        case LEGS:
                            ItemStack copy = itemStack.copy();
                            itemStack.setCount(0);
                            player.addItem(copy);
                        default:
                            break;
                    }
                }
            });
        }

        if(!player.level.isClientSide) {
            final double distance = 8D;
            final double farRunSpeed = 0.5D;
            final double nearRunSpeed = 0.6666D;
            // Scare mobs
            for (Class<? extends PathfinderMob> entityClass : parent.scares) {
                if (entityClass.isAssignableFrom(AbstractVillager.class) && parent.ctor.get().is(ChangedTags.EntityTypes.ORGANIC_LATEX))
                    continue;

                List<? extends PathfinderMob> entitiesScared = player.level.getEntitiesOfClass(entityClass, player.getBoundingBox().inflate(distance, 6D, distance), Objects::nonNull);
                for(var v : entitiesScared) {
                    //if the creature has no path, or the target path is < distance, make the creature run.
                    if(v.getNavigation().getPath() == null || player.distanceToSqr(v.getNavigation().getTargetPos().getX(), v.getNavigation().getTargetPos().getY(), v.getNavigation().getTargetPos().getZ()) < distance * distance)
                    {
                        Vec3 vector3d = DefaultRandomPos.getPosAway(v, 16, 7, new Vec3(player.getX(), player.getY(), player.getZ()));

                        if(vector3d != null && player.distanceToSqr(vector3d) > player.distanceToSqr(v))
                        {
                            Path path = v.getNavigation().createPath(vector3d.x, vector3d.y, vector3d.z, 0);

                            if(path != null)
                            {
                                double speed = v.distanceToSqr(player) < 49D ? nearRunSpeed : farRunSpeed;
                                v.getNavigation().moveTo(path, speed);
                            }
                        }
                    }
                    else //the creature is still running away from us
                    {
                        double speed = v.distanceToSqr(player) < 49D ? nearRunSpeed : farRunSpeed;
                        v.getNavigation().setSpeedModifier(speed);
                    }

                    if (v.getTarget() == player)
                        v.setTarget(null);
                }
            }
        }

        // Breathing
        if(player.isAlive() && parent.breatheMode.canBreatheWater()) {
            if(air == -100)
            {
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
            else if (!parent.breatheMode.canBreatheAir()) //if the player is on land and the entity suffocates
            {
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

        else if (player.isAlive() && !parent.breatheMode.canBreatheWater() && parent.breatheMode == LatexVariant.BreatheMode.WEAK) {
            //if the player is in water, remove more air
            if (player.isEyeInFluid(FluidTags.WATER)) {
                int air = player.getAirSupply();
                if (air > -10)
                    player.setAirSupply(air-1);
                this.ticksBreathingUnderwater = 0;
            }
        }

        else if (player.isAlive() && !parent.breatheMode.canBreatheWater() && parent.breatheMode == LatexVariant.BreatheMode.STRONG) {
            //if the player is in water, add 1 air every other tick
            if (player.isEyeInFluid(FluidTags.WATER)) {
                int air = player.getAirSupply();
                if (air > -10 && player.tickCount % 2 == 0)
                    player.setAirSupply(air+1);
                this.ticksBreathingUnderwater = 0;
            }
        }

        // Speed
        if(parent.swimSpeed != 0F) {
            var attrib = player.getAttribute(ForgeMod.SWIM_SPEED.get());
            if (attrib != null && !attrib.hasModifier(attributeModifierSwimSpeed))
                attrib.addPermanentModifier(attributeModifierSwimSpeed);
        }

        if(parent.groundSpeed != 0F) {
            if(!player.isInWaterOrBubble() && player.isOnGround()) {
                if (parent.groundSpeed > 1f) {
                    if (!player.isCrouching())
                        multiplyMotion(player, parent.groundSpeed);
                }
                else {
                    multiplyMotion(player, parent.groundSpeed);
                }
            }
        }

        // Step size
        if (player.isCrouching() && parent.stepSize > 0.6f)
            player.maxUpStep = 0.6f;
        else
            player.maxUpStep = parent.stepSize;

        List<AbstractAbility<?>> toRemove = new ArrayList<>();
        forEachActiveAbility(instance -> {
            if (!instance.canKeepUsing()) {
                instance.stopUsing();
                toRemove.add(instance.ability);
                return;
            }

            instance.tick();
        });
        toRemove.forEach(activeAbilities::remove);

        sync(player);
    }

    public CompoundTag saveAbilities() {
        CompoundTag tagAbilities = new CompoundTag();
        abilityInstances.forEach((name, ability) -> {
            CompoundTag tagAbility = new CompoundTag();
            ability.saveData(tagAbility);
            if (!tagAbility.isEmpty())
                tagAbilities.put(name.toString(), tagAbility);
        });
        return tagAbilities;
    }

    public void loadAbilities(CompoundTag tagAbilities) {
        abilityInstances.forEach((name, instance) -> {
            if (!tagAbilities.contains(name.toString()))
                return;
            CompoundTag abilityTag = tagAbilities.getCompound(name.toString());
            instance.readData(abilityTag);
        });
    }

    public void activateAbility(Player player, AbstractAbility<?> ability) {
        if (abilityInstances.containsKey(ability)) {
            var instance = abilityInstances.get(ability);
            if (instance.canUse()) {
                activeAbilities.add(ability);
                instance.startUsing();
            }
        }
    }

    public void unhookAll(Player player) {
        forEachActiveAbility(AbstractAbilityInstance::stopUsing);
        activeAbilities.clear();
        abilityInstances.forEach((name, ability) -> {
            ability.onRemove();
        });
        if (player.getAttribute(ForgeMod.SWIM_SPEED.get()).hasModifier(attributeModifierSwimSpeed))
            player.getAttribute(ForgeMod.SWIM_SPEED.get()).removePermanentModifier(attributeModifierSwimSpeed.getId());
        player.setHealth(Math.min(player.getMaxHealth(), player.getHealth()));
        player.maxUpStep = 0.6F;
        player.refreshDimensions();
    }

    public LatexType getLatexType() {
        return parent.getLatexType();
    }

    public boolean is(LatexVariant<?> variant) {
        return parent.is(variant);
    }
}
