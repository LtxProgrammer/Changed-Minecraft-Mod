package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.GooType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GooBee extends ChangedEntity {
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(GooBee.class, EntityDataSerializers.BYTE);
    public GooBee(EntityType<? extends GooBee> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public int getTicksRequiredToFreeze() { return 240; }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.BALD.get();
    }

    @Override
    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.MALE.getStyles();
    }

    @Override
    public Color3 getHairColor(int layer) {
        return Color3.getColor("#4d334d");
    }

    @Override
    public GooType getGooType() {
        return GooType.NEUTRAL;
    }

    @Override
    public TransfurMode getTransfurMode() { return TransfurMode.ABSORPTION; }

    @Override
    public Color3 getDripColor() {
        return Color3.getColor(this.random.nextInt(4) < 3 ? "#ffbf75" : "#ff9e58");
    }
}
