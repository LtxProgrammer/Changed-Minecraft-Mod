package net.ltxprogrammer.changed.mixin;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.ltxprogrammer.changed.world.features.structures.StructureTemplateExtender;
import net.ltxprogrammer.changed.world.features.structures.HangingBlockFixerProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin implements StructureTemplateExtender {
    @Shadow protected abstract ListTag newIntegerList(int... values);

    @Unique
    private final List<LatexCoverPalette> changed$latexCoverPalettes = Lists.newArrayList();

    @WrapMethod(method = "load")
    public void updateWithCDFU(HolderGetter<Block> registry, CompoundTag tag, Operation<Void> original) {
        if (Changed.dataFixer != null)
            Changed.dataFixer.updateCompoundTag(DataFixTypes.STRUCTURE, tag);
        
        original.call(registry, tag);
        this.changed$latexCoverPalettes.clear();
        
        ListTag listtag1 = tag.getList("latexCovers", 10);
        HolderGetter<LatexType> typeRegistry = ChangedRegistry.LATEX_TYPE.asLookup();
        if (tag.contains("latexCoverPalettes", 9)) {
            ListTag listtag2 = tag.getList("latexCoverPalettes", 9);

            for(int i = 0; i < listtag2.size(); ++i) {
                this.loadCoverPalette(typeRegistry, listtag2.getList(i), listtag1);
            }
        } else {
            this.loadCoverPalette(typeRegistry, tag.getList("latexCoverPalette", 10), listtag1);
        }
    }
    
    @Unique
    public void loadCoverPalette(HolderGetter<LatexType> registry, ListTag latexCoverPalette, ListTag latexCovers) {
        SimpleCoverPalette palette = new SimpleCoverPalette();

        for(int i = 0; i < latexCoverPalette.size(); ++i) {
            palette.addMapping(LatexType.readLatexCoverState(registry, latexCoverPalette.getCompound(i)), i);
        }

        List<StructureLatexCoverInfo> list2 = Lists.newArrayList();

        for(int j = 0; j < latexCovers.size(); ++j) {
            CompoundTag compoundtag = latexCovers.getCompound(j);
            ListTag listtag = compoundtag.getList("pos", 3);
            BlockPos coverPos = new BlockPos(listtag.getInt(0), listtag.getInt(1), listtag.getInt(2));
            LatexCoverState coverState = palette.stateFor(compoundtag.getInt("state"));

            list2.add(new StructureLatexCoverInfo(coverPos, coverState));
        }

        List<StructureLatexCoverInfo> list3 = buildLatexCoverInfoList(list2);
        this.changed$latexCoverPalettes.add(new LatexCoverPalette(list3));
    }
    
    @WrapMethod(method = "save")
    public CompoundTag saveWithExtra(CompoundTag tag, Operation<CompoundTag> original) {
        tag = original.call(tag);

        List<StructureTemplateExtender.SimpleCoverPalette> coverPalettes = Lists.newArrayList();
        StructureTemplateExtender.SimpleCoverPalette simplePalette = new StructureTemplateExtender.SimpleCoverPalette();
        coverPalettes.add(simplePalette);

        for(int i = 1; i < this.changed$latexCoverPalettes.size(); ++i) {
            coverPalettes.add(new StructureTemplateExtender.SimpleCoverPalette());
        }

        ListTag listtag1 = new ListTag();
        var list1 = this.changed$latexCoverPalettes.get(0).latexCovers();

        for(int j = 0; j < list1.size(); ++j) {
            var structuretemplate$structureblockinfo = list1.get(j);
            CompoundTag compoundtag = new CompoundTag();
            compoundtag.put("pos", this.newIntegerList(
                    structuretemplate$structureblockinfo.pos().getX(), 
                    structuretemplate$structureblockinfo.pos().getY(), 
                    structuretemplate$structureblockinfo.pos().getZ()));
            int k = simplePalette.idFor(structuretemplate$structureblockinfo.state());
            compoundtag.putInt("state", k);

            listtag1.add(compoundtag);

            for(int l = 1; l < this.changed$latexCoverPalettes.size(); ++l) {
                var structuretemplate$simplepalette1 = coverPalettes.get(l);
                structuretemplate$simplepalette1.addMapping((this.changed$latexCoverPalettes.get(l).latexCovers().get(j)).state(), k);
            }
        }

        tag.put("latexCovers", listtag1);
        if (coverPalettes.size() == 1) {
            ListTag paletteContents = new ListTag();

            for (var coverState : simplePalette) {
                paletteContents.add(LatexType.writeLatexCoverState(coverState));
            }

            tag.put("latexCoverPalette", paletteContents);
        } else {
            ListTag palletList = new ListTag();

            for (var palette : coverPalettes) {
                ListTag paletteContents = new ListTag();

                for (var coverState : palette) {
                    paletteContents.add(LatexType.writeLatexCoverState(coverState));
                }

                palletList.add(paletteContents);
            }

            tag.put("latexCoverPalettes", palletList);
        }
        
        return tag;
    }

    @Inject(method = "lambda$addEntitiesToWorld$5",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;moveTo(DDDFF)V", shift = At.Shift.AFTER))
    private static void fixPaintingPlacement(StructurePlaceSettings placementIn, Vec3 vec31, ServerLevelAccessor level, CompoundTag compoundtag, Entity entity, CallbackInfo ci) {
        if (placementIn.getProcessors().stream().noneMatch(HangingBlockFixerProcessor.INSTANCE::equals))
            return; // Require HangingBlockFixerProcessor.INSTANCE for compatibility

        // Code below from BluSpring (2/16/2024) at https://bugs.mojang.com/browse/MC/issues/MC-102223
        if (!(entity instanceof Painting painting)) {
            return;
        }

        var pos = new BlockPos.MutableBlockPos();
        pos.set(painting.getPos());
        var variant = painting.getVariant().value();

        var width = variant.getWidth() / 16;
        var height = variant.getHeight() / 16;
        var direction = painting.getDirection();

        // paintings with an even height seem to always be moved upwards...
        if (height % 2 == 0) {
            pos.move(0, -1, 0);
        }

        // paintings with an even width seem to be moved in the clockwise direction of their facing direction,
        // if they're west or south.
        if (width % 2 == 0 && (direction == Direction.WEST || direction == Direction.SOUTH)) {
            var moveTo = direction.getClockWise().getNormal();
            pos.move(moveTo);
        }

        painting.setPos(pos.getCenter());
    }

    @WrapMethod(method = "fillFromWorld")
    private void andReadLatexCovers(Level level, BlockPos origin, Vec3i size, boolean saveEntities, Block ignoreBlock, Operation<Void> original) {
        original.call(level, origin, size, saveEntities, ignoreBlock);

        if (size.getX() >= 1 && size.getY() >= 1 && size.getZ() >= 1) {
            BlockPos blockpos = origin.offset(size).offset(-1, -1, -1);
            List<StructureLatexCoverInfo> coverInfos = Lists.newArrayList();
            BlockPos minimum = new BlockPos(Math.min(origin.getX(), blockpos.getX()), Math.min(origin.getY(), blockpos.getY()), Math.min(origin.getZ(), blockpos.getZ()));
            BlockPos maximum = new BlockPos(Math.max(origin.getX(), blockpos.getX()), Math.max(origin.getY(), blockpos.getY()), Math.max(origin.getZ(), blockpos.getZ()));

            for(BlockPos worldPos : BlockPos.betweenClosed(minimum, maximum)) {
                BlockPos relativePos = worldPos.subtract(minimum);
                LatexCoverState coverState = LatexCoverState.getAt(level, worldPos);
                BlockState blockState = level.getBlockState(worldPos);
                if (ignoreBlock == null || !blockState.is(ignoreBlock)) { // No covers where structure voids are
                    coverInfos.add(new StructureLatexCoverInfo(relativePos, coverState));
                }
            }

            List<StructureLatexCoverInfo> sortedInfos = buildLatexCoverInfoList(coverInfos);
            this.changed$latexCoverPalettes.clear();
            this.changed$latexCoverPalettes.add(new LatexCoverPalette(sortedInfos));
        }
    }

    @Unique
    private static List<StructureLatexCoverInfo> buildLatexCoverInfoList(List<StructureLatexCoverInfo> root) {
        Comparator<StructureLatexCoverInfo> comparator = Comparator.<StructureLatexCoverInfo>comparingInt((p_74641_) -> {
            return p_74641_.pos().getY();
        }).thenComparingInt((p_74637_) -> {
            return p_74637_.pos().getX();
        }).thenComparingInt((p_74572_) -> {
            return p_74572_.pos().getZ();
        });
        root.sort(comparator);
        List<StructureLatexCoverInfo> list = Lists.newArrayList();
        list.addAll(root);
        return list;
    }

    @Unique
    private Map<BlockPos, StructureLatexCoverInfo> changed$cachedProcessedInfo = null;

    @WrapMethod(method = "placeInWorld")
    public boolean prepCoverPlaceInfo(ServerLevelAccessor level,
                                      BlockPos templatePos,
                                      BlockPos bottomCenterPos,
                                      StructurePlaceSettings settings,
                                      RandomSource random,
                                      int setBlockFlags,
                                      Operation<Boolean> original) {
        changed$cachedProcessedInfo = null;

        if (!this.changed$latexCoverPalettes.isEmpty()) {
            var coverStates = this.getRandomCoverPalette(settings, this.changed$latexCoverPalettes, templatePos).latexCovers();
            var processedInfo = StructureTemplateExtender.processCoverInfos(level, templatePos, bottomCenterPos, settings, coverStates, (StructureTemplate) (Object) this);

            if (!processedInfo.isEmpty()) {
                changed$cachedProcessedInfo = new HashMap<>(processedInfo.size());
                processedInfo.forEach(coverInfo -> changed$cachedProcessedInfo.put(coverInfo.pos(), coverInfo));
            }
        }

        var didWork = original.call(level, templatePos, bottomCenterPos, settings, random, setBlockFlags);

        changed$cachedProcessedInfo = null;

        return didWork;
    }

    @WrapOperation(method = "placeInWorld", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/ServerLevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
            ordinal = 1))
    public boolean andWriteLatexCovers(
            ServerLevelAccessor level, BlockPos blockPos, BlockState blockState, int setBlockFlags, Operation<Boolean> original,
            @Local(argsOnly = true) StructurePlaceSettings settings,
            @Local BoundingBox placeRegion) {
        var blockUpdated = original.call(level, blockPos, blockState, setBlockFlags);

        if (changed$cachedProcessedInfo == null || changed$cachedProcessedInfo.isEmpty())
            return blockUpdated;
        if (!changed$cachedProcessedInfo.containsKey(blockPos))
            return blockUpdated;

        var coverInfo = changed$cachedProcessedInfo.get(blockPos);
        BlockPos blockpos = coverInfo.pos();
        if (placeRegion == null || placeRegion.isInside(blockpos)) {
            LatexCoverState coverState = coverInfo.state().mirror(settings.getMirror()).rotate(settings.getRotation());
            if (LatexCoverState.setAt(level, blockpos, coverState, setBlockFlags))
                return true;
        }

        return blockUpdated;
    }
        
    @Unique
    public LatexCoverPalette getRandomCoverPalette(StructurePlaceSettings settings, List<LatexCoverPalette> p_74388_, @Nullable BlockPos blockPos) {
        int i = p_74388_.size();
        if (i == 0) {
            throw new IllegalStateException("No palettes");
        } else {
            return p_74388_.get(settings.getRandom(blockPos).nextInt(i));
        }
    }
}
