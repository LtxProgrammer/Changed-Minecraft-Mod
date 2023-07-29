package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.SharedConstants;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpecialLatex extends LatexEntity {
    private UUID assignedUUID = null;
    public String wantedState = "default";
    public List<String> possibleModels = new ArrayList<>();
    public PatreonBenefits.SpecialForm specialLatexForm = null;

    public SpecialLatex(EntityType<? extends LatexEntity> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }
    
    public PatreonBenefits.EntityData getCurrentData() {
        if (specialLatexForm == null)
            return null;
        return specialLatexForm.entityData().getOrDefault(wantedState, specialLatexForm.getDefaultEntity());
    }

    public void setSpecialLatexForm(UUID uuid) {
        this.assignedUUID = uuid;
        this.specialLatexForm = PatreonBenefits.getPlayerSpecialForm(uuid);
    }

    public EntityDimensions getDimensions(Pose pose) {
        if (specialLatexForm == null)
            return super.getDimensions(pose);

        EntityDimensions core = getCurrentData().dimensions();

        if (this.isVisuallySwimming())
            return EntityDimensions.scalable(core.width, core.width);
        return switch (pose) {
            case STANDING -> core;
            case SLEEPING -> SLEEPING_DIMENSIONS;
            case FALL_FLYING, SWIMMING, SPIN_ATTACK -> EntityDimensions.scalable(core.width, core.width);
            case CROUCHING -> EntityDimensions.scalable(core.width, core.height - 0.2f);
            case DYING -> EntityDimensions.fixed(0.2f, 0.2f);
            default -> core;
        };
    }

    @Override
    public Color3 getHairColor(int layer) {
        if (specialLatexForm == null)
            return Color3.WHITE;
        try {
            return getCurrentData().hairColors().get(layer);
        } catch (Exception ignored) {
            if (!getCurrentData().hairColors().isEmpty())
                return getCurrentData().hairColors().get(0);
            else
                return Color3.WHITE;
        }
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        if (specialLatexForm == null)
            return HairStyle.Collection.EMPTY;
        return getCurrentData().hairStyles();
    }

    public UUID getAssignedUUID() {
        return assignedUUID;
    }

    @Override
    public LatexType getLatexType() {
        if (specialLatexForm == null)
            return LatexType.NEUTRAL;
        return specialLatexForm.variant().getLatexType();
    }

    @Override
    public TransfurMode getTransfurMode() {
        if (specialLatexForm == null)
            return TransfurMode.REPLICATION;
        return specialLatexForm.variant().transfurMode();
    }

    @Override
    public Color3 getDripColor() {
        if (specialLatexForm == null)
            return null;
        return getCurrentData().dripColors().isEmpty() ? null :
                getCurrentData().dripColors().get(level.random.nextInt(getCurrentData().dripColors().size()));
    }

    @Override
    public LatexVariant<?> getTransfurVariant() {
        return specialLatexForm.variant();
    }

    public static void init() {}


    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Event {
        @SubscribeEvent
        public static void onVariantAssigned(ProcessTransfur.EntityVariantAssigned event) {
            if (SharedConstants.IS_RUNNING_IN_IDE || !FMLLoader.isProduction())
                return; // Ignore

            if (event.variant == null || event.variant.getEntityType() != ChangedEntities.SPECIAL_LATEX.get())
                return;

            if (event.livingEntity instanceof Player player)
                event.variant = PatreonBenefits.getPlayerSpecialVariant(player.getUUID()); // Ensure player was assigned correct special variant (or null if none)
        }
    }
}
