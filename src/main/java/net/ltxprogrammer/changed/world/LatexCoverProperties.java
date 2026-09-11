package net.ltxprogrammer.changed.world;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class LatexCoverProperties {
    public Function<LatexCoverState, MapColor> mapColor = (coverState) -> MapColor.NONE;
    public boolean hasCollision = true;
    public SoundType soundType;
    public ToIntFunction<LatexCoverState> lightEmission;
    public float explosionResistance;
    public float destroyTime;
    public boolean requiresCorrectToolForDrops;
    public boolean isRandomlyTicking;
    public float friction;
    public float speedFactor;
    public float jumpFactor;
    public ResourceLocation drops;
    public boolean canOcclude;
    public boolean isAir;
    public boolean ignitedByLava;
    public boolean spawnParticlesOnBreak;
    public boolean replaceable;
    public Supplier<ResourceLocation> lootTableSupplier;

    private LatexCoverProperties() {
        this.soundType = SoundType.STONE;
        this.lightEmission = (p_60929_) -> 0;
        this.friction = 0.6F;
        this.speedFactor = 1.0F;
        this.jumpFactor = 1.0F;
        this.canOcclude = true;
        this.spawnParticlesOnBreak = true;
    }

    public static LatexCoverProperties of() {
        return new LatexCoverProperties();
    }
    
    public LatexCoverProperties mapColor(DyeColor dyeCOlor) {
        this.mapColor = (coverState) -> dyeCOlor.getMapColor();
        return this;
    }

    public LatexCoverProperties mapColor(MapColor mapColor) {
        this.mapColor = (coverState) -> mapColor;
        return this;
    }

    public LatexCoverProperties mapColor(Function<LatexCoverState, MapColor> function) {
        this.mapColor = function;
        return this;
    }

    public LatexCoverProperties noCollission() {
        this.hasCollision = false;
        this.canOcclude = false;
        return this;
    }

    public LatexCoverProperties noOcclusion() {
        this.canOcclude = false;
        return this;
    }

    public LatexCoverProperties friction(float friction) {
        this.friction = friction;
        return this;
    }

    public LatexCoverProperties speedFactor(float speedFactor) {
        this.speedFactor = speedFactor;
        return this;
    }

    public LatexCoverProperties jumpFactor(float jumpFactor) {
        this.jumpFactor = jumpFactor;
        return this;
    }

    public LatexCoverProperties sound(SoundType soundType) {
        this.soundType = soundType;
        return this;
    }

    public LatexCoverProperties lightLevel(ToIntFunction<LatexCoverState> lightEmission) {
        this.lightEmission = lightEmission;
        return this;
    }

    public LatexCoverProperties strength(float destroyTime, float explosionResistance) {
        return this.destroyTime(destroyTime).explosionResistance(explosionResistance);
    }

    public LatexCoverProperties instabreak() {
        return this.strength(0.0F);
    }

    public LatexCoverProperties strength(float generalStrength) {
        this.strength(generalStrength, generalStrength);
        return this;
    }

    public LatexCoverProperties randomTicks() {
        this.isRandomlyTicking = true;
        return this;
    }

    public LatexCoverProperties noLootTable() {
        this.drops = BuiltInLootTables.EMPTY;
        return this;
    }

    public LatexCoverProperties lootFrom(Supplier<? extends Block> blockIn) {
        this.lootTableSupplier = () -> ((Block)blockIn.get()).getLootTable();
        return this;
    }

    public LatexCoverProperties ignitedByLava() {
        this.ignitedByLava = true;
        return this;
    }

    public LatexCoverProperties air() {
        this.isAir = true;
        return this;
    }

    public LatexCoverProperties requiresCorrectToolForDrops() {
        this.requiresCorrectToolForDrops = true;
        return this;
    }

    public LatexCoverProperties destroyTime(float destroyTime) {
        this.destroyTime = destroyTime;
        return this;
    }

    public LatexCoverProperties explosionResistance(float explosionResistance) {
        this.explosionResistance = Math.max(0.0F, explosionResistance);
        return this;
    }

    public LatexCoverProperties noParticlesOnBreak() {
        this.spawnParticlesOnBreak = false;
        return this;
    }

    public LatexCoverProperties replaceable() {
        this.replaceable = true;
        return this;
    }
}
