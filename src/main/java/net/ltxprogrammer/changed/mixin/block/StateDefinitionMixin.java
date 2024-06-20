package net.ltxprogrammer.changed.mixin.block;

import net.ltxprogrammer.changed.block.NonLatexCoverableBlock;
import net.ltxprogrammer.changed.util.StateHolderHelper;
import net.minecraft.Util;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

@Mixin(StateDefinition.Builder.class)
public abstract class StateDefinitionMixin<O, S extends StateHolder<O, S>> {
    @Shadow @Final private O owner;
    @Shadow public abstract StateDefinition.Builder<O, S> add(Property<?>... p_61105_);

    @Unique private static final List<Class<?>> NON_COVERABLE_BY_CLASS = Util.make(new ArrayList<>(), list -> {
        list.add(NonLatexCoverableBlock.class);
        list.add(AirBlock.class);
        list.add(BaseFireBlock.class);
        list.add(RedStoneWireBlock.class);
        list.add(GlassBlock.class);
        list.add(TintedGlassBlock.class);
        list.add(StainedGlassPaneBlock.class);
        list.add(StainedGlassBlock.class);
        list.add(NoteBlock.class);
        list.add(StainedGlassPaneBlock.class);
        list.add(MagmaBlock.class);
        list.add(TorchBlock.class);
        list.add(EndPortalFrameBlock.class);
        list.add(NetherPortalBlock.class);
        list.add(EndPortalBlock.class);
        list.add(EndGatewayBlock.class);
        list.add(BeaconBlock.class);
        list.add(IceBlock.class);
        list.add(PowderSnowBlock.class);
        list.add(SnowLayerBlock.class);
        list.add(AbstractCandleBlock.class);
        list.add(BarrierBlock.class);
        list.add(GameMasterBlock.class);
        list.add(StructureVoidBlock.class);
        list.add(LiquidBlock.class);
        list.add(ConduitBlock.class);
        list.add(DragonEggBlock.class);
        list.add(CropBlock.class);
        list.add(TurtleEggBlock.class);
        list.add(SpawnerBlock.class);
        list.add(AbstractCauldronBlock.class);
        list.add(AnvilBlock.class);
        list.add(PistonBaseBlock.class);
        list.add(PistonHeadBlock.class);
        list.add(ShulkerBoxBlock.class);
        list.add(BannerBlock.class);
        list.add(WetSpongeBlock.class);
        list.add(RailBlock.class);
        list.add(PoweredRailBlock.class);
        list.add(DetectorRailBlock.class);
        list.add(DaylightDetectorBlock.class);
        list.add(RespawnAnchorBlock.class);
        list.add(RepeaterBlock.class);
        list.add(ComparatorBlock.class);
        list.add(StemBlock.class);
        list.add(FurnaceBlock.class);
        list.add(BlastFurnaceBlock.class);
        list.add(SmokerBlock.class);
        list.add(LightBlock.class);
    });

    @Inject(method = "create", at = @At("HEAD"))
    public void create(Function<O, S> defaultState, StateDefinition.Factory<O, S> stateFactory, CallbackInfoReturnable<StateDefinition<O, S>> callbackInfo) {
        if (defaultState == StateHolderHelper.FN_STATE_CREATION_BYPASS)
            return;

        if (owner instanceof Block ownerBlock) {
            if (NON_COVERABLE_BY_CLASS.stream().noneMatch(clazz -> clazz.isInstance(ownerBlock)))
                add(COVERED);
        }
    }
}
