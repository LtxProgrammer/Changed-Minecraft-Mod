package net.ltxprogrammer.changed.entity.variant;

import com.google.common.collect.ImmutableMap;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.ability.IAbstractLatex;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.init.ChangedCriteriaTriggers;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.item.WearableItem;
import net.ltxprogrammer.changed.network.packet.BasicPlayerInfoPacket;
import net.ltxprogrammer.changed.network.packet.SyncMoverPacket;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = Changed.MODID)
public class LatexVariantInstance<T extends LatexEntity> {
    private final LatexVariant<T> parent;
    private final T entity;
    private final Player host;
    public final ImmutableMap<AbstractAbility<?>, AbstractAbilityInstance> abilityInstances;

    public AbstractAbility<?> selectedAbility = null;
    public AbstractAbility<?> menuAbility = null;
    public boolean abilityKeyState = false;
    private final AttributeModifier attributeModifierSwimSpeed;
    public TransfurMode transfurMode;
    public int ageAsVariant = 0;
    protected int air = -100;
    protected int jumpCharges = 0;
    protected float lastSwimMul = 1F;
    private boolean dead;
    public int ticksBreathingUnderwater;
    public int ticksWhiteLatex;

    public LatexVariantInstance(LatexVariant<T> parent, Player host) {
        this.parent = parent;
        this.entity = parent.generateForm(host, host.level);
        this.host = host;

        this.transfurMode = parent.transfurMode;

        var builder = new ImmutableMap.Builder<AbstractAbility<?>, AbstractAbilityInstance>();
        parent.abilities.forEach(abilityFunction -> {
            var ability = abilityFunction.apply(this.parent.getEntityType());
            if (ability != null)
                builder.put(ability, ability.makeInstance(IAbstractLatex.forPlayer(host)));
        });
        abilityInstances = builder.build();
        if (abilityInstances.size() > 0)
            selectedAbility = abilityInstances.keySet().asList().get(0);

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
    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        ProcessTransfur.ifPlayerLatex(event.getPlayer(), variant -> {
            if (!variant.getParent().itemUseMode.canUseHand(event.getHand()))
                event.setCanceled(true);
        });
    }

    @SubscribeEvent
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        ProcessTransfur.ifPlayerLatex(event.getPlayer(), variant -> {
            if (!variant.getParent().itemUseMode.interactWithBlocks)
                event.setCanceled(true);
        });
    }

    @SubscribeEvent
    public static void onBlockLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        ProcessTransfur.ifPlayerLatex(event.getPlayer(), variant -> {
            if (!variant.getParent().itemUseMode.breakBlocks)
                event.setCanceled(true);
        });
    }

    @SubscribeEvent
    public static void onLivingFallEvent(LivingFallEvent event) {
        LatexVariant<?> variant = LatexVariant.getEntityVariant(event.getEntityLiving());
        if (variant != null && variant.isReducedFall()) {
            event.setDistance(0.4f * event.getDistance());
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
            ProcessTransfur.ifPlayerLatex(event.player, instance -> {
                if (ChangedCompatibility.isPlayerUsedByOtherMod(event.player)) {
                    ProcessTransfur.setPlayerLatexVariant(event.player, null);
                    return;
                }

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
            SyncTransfurPacket.Builder builderTf = new SyncTransfurPacket.Builder();
            BasicPlayerInfoPacket.Builder builderBPI = new BasicPlayerInfoPacket.Builder();
            player.getServer().getPlayerList().getPlayers().forEach(builderTf::addPlayer);
            player.getServer().getPlayerList().getPlayers().forEach(builderBPI::addPlayer);

            Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), builderTf.build());
            Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), builderBPI.build());
            Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new SyncMoverPacket(player));

            // Send client empty bpi packet, so it'll reply with its bpi
            Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new BasicPlayerInfoPacket(Map.of()));
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
                float friction = player.getLevel().getBlockState(player.blockPosition().below())
                        .getFriction(player.getLevel(), player.blockPosition(), player);
                double mdP = dP.length();
                mul = clamp(0.75, mul, lerp(mul, 0.8 * mul / Math.pow(mdP, 1.0/6.0), mdP * 3));
                mul /= clamp(0.6, 1, friction) * 0.65 + 0.61;
                if (Double.isNaN(mul)) {
                    Changed.LOGGER.error("Ran into NaN multiplier, falling back to zero");
                    mul = 0.0;
                }
            }
        }

        player.setDeltaMovement(dP.multiply(mul, mul, mul));
    }

    public static void syncEntityPosRotWithEntity(LivingEntity set, LivingEntity get) {
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

    public static void syncEntityAndPlayer(LatexEntity living, Player player) {
        living.tickCount = player.tickCount;
        living.getActiveEffectsMap().clear();
        living.setUnderlyingPlayer(player);

        living.mirrorPlayer(player);

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

    public boolean canWear(Player player, ItemStack itemStack) {
        if (itemStack.getItem() instanceof WearableItem wearableItem)
            return wearableItem.allowedToKeepWearing(player);

        if (parent == LatexVariant.DARK_LATEX_PUP)
            return false;

        if (itemStack.getItem() instanceof ArmorItem armorItem) {
            if (parent.hasLegs)
                return true;
            else {
                switch (armorItem.getSlot()) {
                    case FEET:
                    case LEGS:
                        return false;
                    default:
                        break;
                }
            }
        }

        return true;
    }

    public void tick(Player player) {
        if (player == null) return;

        ageAsVariant++;

        player.refreshDimensions();
        if (player.isOnGround())
            jumpCharges = parent.extraJumpCharges;

        if (parent.rideable() || !parent.hasLegs)
            player.stopRiding();

        if (parent.canGlide) {
            if (!player.isCreative() && !player.isSpectator()) {
                if (player.getFoodData().getFoodLevel() <= 6.0F && player.getAbilities().mayfly) {
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

            if (!player.level.isClientSide) {
                this.entity.setLatexEntityFlag(LatexEntity.FLAG_IS_FLYING, player.getAbilities().flying);
            }
        }

        player.getArmorSlots().forEach(itemStack -> { // Force unequip invalid items
            if (!canWear(player, itemStack)) {
                ItemStack copy = itemStack.copy();
                itemStack.setCount(0);
                if (!player.addItem(copy))
                    player.drop(copy, false);
            }
        });

        if(!player.level.isClientSide) {
            final double distance = 8D;
            final double farRunSpeed = 0.5D;
            final double nearRunSpeed = 0.6666D;
            // Scare mobs
            for (Class<? extends PathfinderMob> entityClass : parent.scares) {
                if (entityClass.isAssignableFrom(AbstractVillager.class) && parent.ctor.get().is(ChangedTags.EntityTypes.ORGANIC_LATEX) || player.isCreative() || player.isSpectator())
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

        // Speed
        if(parent.swimSpeed != 0F && player.isInWaterOrBubble()) {
            if (parent.swimSpeed > 1f) {
                var attrib = player.getAttribute(ForgeMod.SWIM_SPEED.get());
                if (attrib != null && !attrib.hasModifier(attributeModifierSwimSpeed))
                    attrib.addPermanentModifier(attributeModifierSwimSpeed);
            }
            else {
                multiplyMotion(player, parent.swimSpeed);
            }
        }

        if(parent.groundSpeed != 0F && !player.isInWaterOrBubble() && player.isOnGround()) {
            if (parent.groundSpeed > 1f) {
                if (!player.isCrouching())
                    multiplyMotion(player, parent.groundSpeed);
            }
            else {
                multiplyMotion(player, parent.groundSpeed);
            }
        }

        // Step size
        if (player.isCrouching() && parent.stepSize > 0.6f)
            player.maxUpStep = 0.6f;
        else
            player.maxUpStep = parent.stepSize;

        for (var instance : abilityInstances.values()) {
            instance.getController().tickCoolDown();
        }

        if (selectedAbility != null) {
            var instance = abilityInstances.get(selectedAbility);
            if (instance != null) {
                var controller = instance.getController();
                boolean oldState = controller.exchangeKeyState(abilityKeyState);
                if (player.containerMenu == player.inventoryMenu && !player.isUsingItem() && !instance.getController().isCoolingDown())
                    selectedAbility.getUseType(IAbstractLatex.forPlayer(player)).check(abilityKeyState, oldState, controller);
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

        sync(player);
    }

    public CompoundTag saveAbilities() {
        CompoundTag tagAbilities = new CompoundTag();
        abilityInstances.forEach((name, ability) -> {
            CompoundTag tagAbility = new CompoundTag();
            ability.saveData(tagAbility);
            if (!tagAbility.isEmpty())
                tagAbilities.put(Objects.requireNonNull(name.getRegistryName()).toString(), tagAbility);
        });
        return tagAbilities;
    }

    public void loadAbilities(CompoundTag tagAbilities) {
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
        if (player.getAttribute(ForgeMod.SWIM_SPEED.get()).hasModifier(attributeModifierSwimSpeed))
            player.getAttribute(ForgeMod.SWIM_SPEED.get()).removePermanentModifier(attributeModifierSwimSpeed.getId());
        player.setHealth(Math.min(player.getMaxHealth(), player.getHealth()));
        if (parent.canGlide) {
            player.getAbilities().mayfly = player.isCreative() || player.isSpectator();
            if (!player.isCreative() && !player.isSpectator()) {
                player.getAbilities().flying = false;
            }
            player.onUpdateAbilities();
        }
        player.maxUpStep = 0.6F;
        player.refreshDimensions();
    }

    public LatexType getLatexType() {
        return parent.getLatexType();
    }

    public boolean is(LatexVariant<?> variant) {
        return parent.is(variant);
    }

    @Nullable
    public AbstractAbilityInstance getSelectedAbility() {
        return abilityInstances.get(this.selectedAbility);
    }

    public void setSelectedAbility(AbstractAbility<?> ability) {
        if (abilityInstances.containsKey(ability)) {
            var abstractLatex = IAbstractLatex.forPlayer(this.host);

            if (ability.getUseType(abstractLatex) != AbstractAbility.UseType.MENU)
                this.selectedAbility = ability;
            else {
                ability.startUsing(abstractLatex);
                this.menuAbility = ability;
            }
        }
    }
}
