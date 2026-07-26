package net.ltxprogrammer.changed.entity.decoration;

import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedWallSigns;
import net.ltxprogrammer.changed.network.syncher.ChangedEntityDataSerializers;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WallSign extends HangingEntity {
    private static final EntityDataAccessor<WallSignVariant> DATA_WALL_SIGN_VARIANT_ID = SynchedEntityData.defineId(WallSign.class, ChangedEntityDataSerializers.WALL_SIGN_VARIANT.get());
    private static final ResourceLocation DEFAULT_VARIANT = ChangedWallSigns.DO_NOT_TOUCH.getId();
    public static final String VARIANT_TAG = "variant";

    public WallSign(EntityType<? extends WallSign> type, Level level) {
        super(type, level);
    }

    private boolean isItemRep = false;
    public WallSign markAsItemRep() {
        isItemRep = true;
        return this;
    }

    public boolean isItemRep() {
        return isItemRep;
    }

    private WallSign(Level level, BlockPos blockPos) {
        super(ChangedEntities.WALL_SIGN.get(), level, blockPos);
    }

    protected void recalculateBoundingBox() {
        if (this.direction != null) {
            double x = (double)this.pos.getX() + 0.5D;
            double y = (double)this.pos.getY() + 0.5D;
            double z = (double)this.pos.getZ() + 0.5D;
            x -= (double)this.direction.getStepX() * 0.46875D;
            z -= (double)this.direction.getStepZ() * 0.46875D;
            this.setPosRaw(x, y, z);
            double widthX = (double)this.getWidth();
            double height = (double)this.getHeight();
            double widthZ = (double)this.getWidth();
            if (this.direction.getAxis() == Direction.Axis.Z) {
                widthZ = 1.0D;
            } else {
                widthX = 1.0D;
            }

            widthX /= 32.0D;
            height /= 32.0D;
            widthZ /= 32.0D;
            this.setBoundingBox(new AABB(x - widthX, y - height, z - widthZ, x + widthX, y + height, z + widthZ));
        }
    }

    private static WallSignVariant getDefaultVariant() {
        return ChangedRegistry.WALL_SIGN_VARIANT.getValue(DEFAULT_VARIANT);
    }

    private static int variantArea(WallSignVariant variant) {
        return variant.getWidth() * variant.getHeight();
    }

    public static Optional<WallSign> create(Level level, BlockPos blockPos, Direction direction, WallSignVariant variants) {
        return create(level, blockPos, direction, List.of(variants));
    }

    public static Optional<WallSign> create(Level level, BlockPos blockPos, Direction direction, List<WallSignVariant> variants) {
        WallSign painting = new WallSign(level, blockPos);
        painting.setDirection(direction);
        List<WallSignVariant> list = new ArrayList<>(variants);
        list.removeIf((variant) -> {
            painting.setVariant(variant);
            return !painting.survives();
        });
        if (list.isEmpty()) {
            return Optional.empty();
        } else {
            int i = list.stream().mapToInt(WallSign::variantArea).max().orElse(0);
            list.removeIf((variant) -> {
                return variantArea(variant) < i;
            });
            Optional<WallSignVariant> optional = Util.getRandomSafe(list, painting.random);
            if (optional.isEmpty()) {
                return Optional.empty();
            } else {
                painting.setVariant(optional.get());
                painting.setDirection(direction);
                return Optional.of(painting);
            }
        }
    }

    protected void defineSynchedData() {
        this.entityData.define(DATA_WALL_SIGN_VARIANT_ID, getDefaultVariant());
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (DATA_WALL_SIGN_VARIANT_ID.equals(accessor)) {
            this.recalculateBoundingBox();
        }

    }

    public void setVariant(WallSignVariant variant) {
        this.entityData.set(DATA_WALL_SIGN_VARIANT_ID, variant);
    }

    public WallSignVariant getVariant() {
        return this.entityData.get(DATA_WALL_SIGN_VARIANT_ID);
    }

    @Override
    public int getWidth() {
        return getVariant().getWidth();
    }

    @Override
    public int getHeight() {
        return getVariant().getHeight();
    }

    public void addAdditionalSaveData(CompoundTag tag) {
        storeVariant(tag, this.getVariant());
        tag.putByte("facing", (byte)this.direction.get2DDataValue());
        super.addAdditionalSaveData(tag);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        WallSignVariant holder = loadVariant(tag).orElseGet(WallSign::getDefaultVariant);
        this.setVariant(holder);
        this.direction = Direction.from2DDataValue(tag.getByte("facing"));
        super.readAdditionalSaveData(tag);
        this.setDirection(this.direction);
    }

    public static void storeVariant(CompoundTag tage, WallSignVariant variantHolder) {
        tage.putString("variant", ChangedRegistry.WALL_SIGN_VARIANT.getKeySafe(variantHolder).orElse(DEFAULT_VARIANT).toString());
    }

    public static Item resolveItemForVariant(WallSignVariant variantHolder) {
        return variantHolder.getItem();
    }

    public static Optional<WallSignVariant> loadVariant(CompoundTag p_271010_) {
        return Optional.ofNullable(ResourceLocation.tryParse(p_271010_.getString("variant"))).map(ChangedRegistry.WALL_SIGN_VARIANT::getValue);
    }

    public void dropItem(@Nullable Entity sourceEntity) {
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
            if (sourceEntity instanceof Player) {
                Player player = (Player)sourceEntity;
                if (player.getAbilities().instabuild) {
                    return;
                }
            }

            this.spawnAtLocation(resolveItemForVariant(this.getVariant()));
        }
    }

    public void playPlacementSound() {
        this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
    }

    public void moveTo(double x, double y, double z, float p_31932_, float p_31933_) {
        this.setPos(x, y, z);
    }

    public void lerpTo(double x, double y, double z, float p_31920_, float p_31921_, int p_31922_, boolean p_31923_) {
        this.setPos(x, y, z);
    }

    public Vec3 trackingPosition() {
        return Vec3.atLowerCornerOf(this.pos);
    }

    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
    }

    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.setDirection(Direction.from3DDataValue(packet.getData()));
    }

    public ItemStack getPickResult() {
        return new ItemStack(resolveItemForVariant(this.getVariant()));
    }
}
