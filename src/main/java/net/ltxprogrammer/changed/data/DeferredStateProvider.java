package net.ltxprogrammer.changed.data;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Random;
import java.util.function.Supplier;

public class DeferredStateProvider extends BlockStateProvider {
    public static final Codec<DeferredStateProvider> CODEC = ResourceLocation.CODEC.fieldOf("state").xmap(
            (stateStr) -> new DeferredStateProvider(stateStr, () -> Registry.BLOCK.get(stateStr)),
            (p_68804_) -> p_68804_.id).codec();
    private static final BlockStateProviderType<DeferredStateProvider> DEFERRED_STATE_PROVIDER = register(Changed.modResource("deferred_state_provider"), DeferredStateProvider.CODEC);
    private final ResourceLocation id;
    private final Supplier<Block> state;

    public DeferredStateProvider(ResourceLocation id, Supplier<Block> p_68801_) {
        this.id = id;
        this.state = p_68801_;
    }

    protected BlockStateProviderType<?> type() {
        return DEFERRED_STATE_PROVIDER;
    }

    public BlockState getState(Random p_68806_, BlockPos p_68807_) {
        return this.state.get().defaultBlockState();
    }

    public static BlockStateProvider of(ResourceLocation blockLocation) {
        return new DeferredStateProvider(blockLocation, () -> Registry.BLOCK.get(blockLocation));
    }

    public static BlockStateProvider of(RegistryObject<Block> blockSupplier) {
        return new DeferredStateProvider(blockSupplier.getId(), blockSupplier);
    }

    public static <P extends BlockStateProvider> BlockStateProviderType<P> register(ResourceLocation p_68763_, Codec<P> p_68764_) {
        BlockStateProviderType<P> value = new BlockStateProviderType<>(p_68764_);
        value.setRegistryName(p_68763_);
        ForgeRegistries.BLOCK_STATE_PROVIDER_TYPES.register(value);
        return value;
    }
}
