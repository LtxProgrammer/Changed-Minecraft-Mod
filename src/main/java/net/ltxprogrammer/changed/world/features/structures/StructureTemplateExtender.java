package net.ltxprogrammer.changed.world.features.structures;

import com.google.common.collect.Maps;
import net.ltxprogrammer.changed.entity.latex.LatexType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.util.Cacheable;
import net.ltxprogrammer.changed.world.LatexCoverState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public interface StructureTemplateExtender {
    record StructureLatexCoverInfo(BlockPos pos, LatexCoverState state) {
        public String toString() {
            return String.format(Locale.ROOT, "<StructureLatexCoverInfo | %s | %s>", this.pos, this.state);
        }
    }

    class SimpleCoverPalette implements Iterable<LatexCoverState> {
        private final IdMapper<LatexCoverState> ids = new IdMapper<>(16);
        private int lastId;

        public int idFor(LatexCoverState p_74670_) {
            int i = this.ids.getId(p_74670_);
            if (i == -1) {
                i = this.lastId++;
                this.ids.addMapping(p_74670_, i);
            }

            return i;
        }

        @Nullable
        public LatexCoverState stateFor(int id) {
            LatexCoverState blockstate = this.ids.byId(id);
            return blockstate == null ? ChangedLatexTypes.NONE.get().defaultCoverState() : blockstate;
        }

        public Iterator<LatexCoverState> iterator() {
            return this.ids.iterator();
        }

        public void addMapping(LatexCoverState state, int id) {
            this.ids.addMapping(state, id);
        }
    }

    final class LatexCoverPalette {
        private final List<StructureLatexCoverInfo> latexCovers;
        private final Map<LatexType, List<StructureLatexCoverInfo>> cache = Maps.newHashMap();

        public LatexCoverPalette(List<StructureLatexCoverInfo> latexCovers) {
            this.latexCovers = latexCovers;
        }

        public List<StructureLatexCoverInfo> latexCovers() {
            return this.latexCovers;
        }

        public List<StructureLatexCoverInfo> latexCovers(LatexType type) {
            return this.cache.computeIfAbsent(type, (computeType) -> {
                return this.latexCovers.stream().filter((info) -> {
                    return info.state.is(computeType);
                }).collect(Collectors.toList());
            });
        }
    }

    static List<StructureLatexCoverInfo> processCoverInfos(ServerLevelAccessor level, BlockPos structurePos, BlockPos bottomCenterPos, StructurePlaceSettings settings, List<StructureLatexCoverInfo> coverInfos, @Nullable StructureTemplate template) {
        List<StructureLatexCoverInfo> preProcessedCoverInfos = new ArrayList<>();
        List<StructureLatexCoverInfo> processedCoverInfos = new ArrayList<>();

        for(StructureLatexCoverInfo coverInfo : coverInfos) {
            BlockPos blockpos = StructureTemplate.calculateRelativePosition(settings, coverInfo.pos).offset(structurePos);
            StructureLatexCoverInfo processedCoverInfo = new StructureLatexCoverInfo(blockpos, coverInfo.state);

            for (var processor : settings.getProcessors()) {
                if (processor instanceof LatexAwareProcessor aware)
                    processedCoverInfo = aware.process(level, structurePos, bottomCenterPos, coverInfo, processedCoverInfo, settings, template);
            }

            if (processedCoverInfo != null) {
                processedCoverInfos.add(processedCoverInfo);
                preProcessedCoverInfos.add(coverInfo);
            }
        }

        for(StructureProcessor processor : settings.getProcessors()) {
            if (processor instanceof LatexAwareProcessor aware)
                processedCoverInfos = aware.finalizeProcessing(level, structurePos, bottomCenterPos, preProcessedCoverInfos, processedCoverInfos, settings);
        }

        return processedCoverInfos;
    }
}
