package net.ltxprogrammer.changed.entity;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.IExtensibleEnum;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class HairStyle extends ForgeRegistryEntry<HairStyle> {
    public enum Collection implements IExtensibleEnum {
        MALE("male"),
        FEMALE("female");

        Collection(String id) {
            this.id = id;
        }

        public static Collection create(String name, String id) {
            throw new IllegalStateException("enum not extended");
        }

        public static final List<HairStyle> EMPTY = List.of();
        public final String id;

        public static Collection byName(String string) {
            for (var c : values())
                if (c.id.equals(string))
                    return c;
            return null;
        }

        public static List<HairStyle> getAll() {
            return new ArrayList<>(ChangedRegistry.HAIR_STYLE.get().getValues());
        }

        public List<HairStyle> getStyles() {
            return ChangedRegistry.HAIR_STYLE.get().getValues().stream().filter(hairStyle -> hairStyle.collection == this).collect(Collectors.toList());
        }
    }

    public static final DeferredRegister<HairStyle> REGISTRY = ChangedRegistry.HAIR_STYLE.createDeferred(Changed.MODID);

    @Nullable
    public final Collection collection;
    public final List<ResourceLocation> textureLayers;

    public HairStyle(ResourceLocation texture) {
        this.collection = null;
        this.textureLayers = ImmutableList.of(texture);
    }

    public HairStyle(ResourceLocation... textures) {
        this.collection = null;
        this.textureLayers = ImmutableList.<ResourceLocation>builder().addAll(Arrays.asList(textures)).build();
    }

    public HairStyle(List<ResourceLocation> textureLayers) {
        this.collection = null;
        this.textureLayers = ImmutableList.<ResourceLocation>builder().addAll(textureLayers).build();
    }

    public HairStyle(Collection collection, ResourceLocation texture) {
        this.collection = collection;
        this.textureLayers = ImmutableList.of(texture);
    }

    public HairStyle(Collection collection, ResourceLocation... textures) {
        this.collection = collection;
        this.textureLayers = ImmutableList.<ResourceLocation>builder().addAll(Arrays.asList(textures)).build();
    }

    public HairStyle(Collection collection, List<ResourceLocation> textureLayers) {
        this.collection = collection;
        this.textureLayers = ImmutableList.<ResourceLocation>builder().addAll(textureLayers).build();
    }

    public static Supplier<HairStyle> with(ResourceLocation... textures) {
        return () -> new HairStyle(textures);
    }

    public static Supplier<HairStyle> with(Collection collection, ResourceLocation... textures) {
        return () -> new HairStyle(collection, textures);
    }

    public static final RegistryObject<HairStyle> BALD = REGISTRY.register("bald",
            with(Collection.MALE));
    public static final RegistryObject<HairStyle> LONG_MESSY = REGISTRY.register("long_messy",
            with(Collection.FEMALE, Changed.modResource("textures/hair/long_messy.png")));
    public static final RegistryObject<HairStyle> SHORT_MESSY = REGISTRY.register("short_messy",
            with(Collection.MALE, Changed.modResource("textures/hair/short_messy.png")));
    public static final RegistryObject<HairStyle> LONG_KEPT = REGISTRY.register("long_kept",
            with(Collection.FEMALE, Changed.modResource("textures/hair/long_kept.png")));
}
