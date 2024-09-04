package net.ltxprogrammer.changed.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.function.Predicate;

public abstract class RegistryElementPredicate<T extends IForgeRegistryEntry<T>> implements Predicate<T> {
    protected final IForgeRegistry<T> registry;

    protected RegistryElementPredicate(IForgeRegistry<T> registry) {
        this.registry = registry;
    }

    protected static class NamespaceSpec<T extends IForgeRegistryEntry<T>> extends RegistryElementPredicate<T> {
        private final String namespace;

        public NamespaceSpec(IForgeRegistry<T> registry, String namespace) {
            super(registry);
            this.namespace = namespace;
        }

        @Override
        public boolean test(T t) {
            var regName = t.getRegistryName();
            if (regName == null) return false;
            return regName.getNamespace().equals(namespace);
        }
    }

    protected static class FullNameSpec<T extends IForgeRegistryEntry<T>> extends RegistryElementPredicate<T> {
        private final ResourceLocation id;

        public FullNameSpec(IForgeRegistry<T> registry, ResourceLocation id) {
            super(registry);
            this.id = id;
        }

        @Override
        public boolean test(T t) {
            return id.equals(t.getRegistryName());
        }
    }

    protected static class TagSpec<T extends IForgeRegistryEntry<T>> extends RegistryElementPredicate<T> {
        private final TagKey<T> tag;

        public TagSpec(IForgeRegistry<T> registry, ResourceLocation tag) {
            super(registry);
            this.tag = TagKey.create(registry.getRegistryKey(), tag);
        }

        @Override
        public boolean test(T t) {
            var tags = registry.tags();
            if (tags == null) return false;
            return tags.getTag(tag).contains(t);
        }
    }

    public static <T extends IForgeRegistryEntry<T>> RegistryElementPredicate<T> parseString(IForgeRegistry<T> registry, String string) {
        if (string.startsWith("#"))
            return new TagSpec<>(registry, new ResourceLocation(string.substring(1)));
        else if (string.startsWith("@"))
            return new NamespaceSpec<>(registry, string.substring(1));
        else
            return new FullNameSpec<>(registry, new ResourceLocation(string));
    }

    public static <T extends IForgeRegistryEntry<T>> RegistryElementPredicate<T> forTag(IForgeRegistry<T> registry, ResourceLocation name) {
        return new TagSpec<>(registry, name);
    }

    public static <T extends IForgeRegistryEntry<T>> RegistryElementPredicate<T> forNamespace(IForgeRegistry<T> registry, String string) {
        return new NamespaceSpec<>(registry, string);
    }

    public static <T extends IForgeRegistryEntry<T>> RegistryElementPredicate<T> forID(IForgeRegistry<T> registry, ResourceLocation name) {
        return new FullNameSpec<>(registry, name);
    }

    private static boolean validNamespaceChar(char p_135836_) {
        return p_135836_ == '_' || p_135836_ == '-' || p_135836_ >= 'a' && p_135836_ <= 'z' || p_135836_ >= '0' && p_135836_ <= '9' || p_135836_ == '.';
    }

    private static boolean isValidNamespace(String p_135844_) {
        for(int i = 0; i < p_135844_.length(); ++i) {
            if (!validNamespaceChar(p_135844_.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean isValidSyntax(String string) {
        if (string.startsWith("#"))
            return ResourceLocation.isValidResourceLocation(string.substring(1));
        else if (string.startsWith("@"))
            return isValidNamespace(string.substring(1));
        else
            return ResourceLocation.isValidResourceLocation(string);
    }

    public static boolean isValidSyntax(Object obj) {
        if (obj instanceof String s)
            return isValidSyntax(s);
        return false;
    }
}
