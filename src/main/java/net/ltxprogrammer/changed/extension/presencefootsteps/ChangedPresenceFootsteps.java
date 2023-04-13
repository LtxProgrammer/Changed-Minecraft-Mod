package net.ltxprogrammer.changed.extension.presencefootsteps;

import eu.ha3.presencefootsteps.config.Variator;
import eu.ha3.presencefootsteps.sound.PFIsolator;
import eu.ha3.presencefootsteps.sound.acoustics.AcousticsJsonParser;
import eu.ha3.presencefootsteps.sound.generator.Locomotion;
import eu.ha3.presencefootsteps.util.ResourceUtils;
import eu.ha3.presencefootsteps.world.Index;
import eu.ha3.presencefootsteps.world.Lookup;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Event;

import java.util.Objects;

public class ChangedPresenceFootsteps {
    public static final ResourceLocation BLOCK_MAP = Changed.modResource("presencefootsteps/blockmap.json");
    public static final ResourceLocation LOCOMOTION_MAP = Changed.modResource("presencefootsteps/locomotionmap.json");

    public static class LoadModdedFootstepsEvent extends Event {
        private final ResourceManager manager;
        private final PFIsolator isolator;

        public LoadModdedFootstepsEvent(ResourceManager manager, PFIsolator isolator) {
            this.manager = manager;
            this.isolator = isolator;
        }

        public void loadBlockMap(ResourceLocation location) {
            Lookup<BlockState> blockMap = this.isolator.getBlockMap();
            Objects.requireNonNull(blockMap);
            ResourceUtils.forEach(location, manager, blockMap::load);
        }

        public void loadGolemMap(ResourceLocation location) {
            Lookup<EntityType<?>> golemMap = this.isolator.getGolemMap();
            Objects.requireNonNull(golemMap);
            ResourceUtils.forEach(location, manager, golemMap::load);
        }

        public void loadLocomotionMap(ResourceLocation location) {
            Index<Entity, Locomotion> locomotionMap = this.isolator.getLocomotionMap();
            Objects.requireNonNull(locomotionMap);
            ResourceUtils.forEach(location, manager, locomotionMap::load);
        }

        public void loadPrimitiveMap(ResourceLocation location) {
            Lookup<SoundType> primitiveMap = this.isolator.getPrimitiveMap();
            Objects.requireNonNull(primitiveMap);
            ResourceUtils.forEach(location, manager, primitiveMap::load);
        }

        public void loadAcoustics(ResourceLocation location) {
            AcousticsJsonParser acousticsJsonParser = new AcousticsJsonParser(this.isolator.getAcoustics());
            ResourceUtils.forEach(location, manager, acousticsJsonParser::parse);
        }

        public void loadVariators(ResourceLocation location) {
            Variator variator = this.isolator.getVariator();
            Objects.requireNonNull(variator);
            ResourceUtils.forEach(location, manager, variator::load);
        }
    }
}
