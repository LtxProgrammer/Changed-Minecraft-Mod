package net.ltxprogrammer.changed.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class RegistryElementPredicate<T> implements Predicate<T> {
    protected final IForgeRegistry<T> registry;

    protected RegistryElementPredicate(IForgeRegistry<T> registry) {
        this.registry = registry;
    }

    public abstract boolean testHolder(Holder<T> holder);

    public abstract String toString();

    public abstract void throwIfMissing();

    public Stream<T> getValues() {
        return registry.getValues().stream().filter(this);
    }

    protected static class NamespaceSpec<T> extends RegistryElementPredicate<T> {
        private final String namespace;

        public NamespaceSpec(IForgeRegistry<T> registry, String namespace) {
            super(registry);
            this.namespace = namespace;
        }

        @Override
        public boolean test(T t) {
            var regName = registry.getKey(t);
            if (regName == null) return false;
            return regName.getNamespace().equals(namespace);
        }

        @Override
        public boolean testHolder(Holder<T> holder) {
            return holder.is(key -> key.location().getNamespace().equals(namespace));
        }

        @Override
        public void throwIfMissing() {}

        @Override
        public String toString() {
            return "@" + namespace;
        }
    }

    protected static class FullNameSpec<T> extends RegistryElementPredicate<T> {
        private final ResourceLocation id;

        public FullNameSpec(IForgeRegistry<T> registry, ResourceLocation id) {
            super(registry);
            this.id = id;
        }

        @Override
        public boolean test(T t) {
            return id.equals(registry.getKey(t));
        }

        @Override
        public boolean testHolder(Holder<T> holder) {
            return holder.is(id);
        }

        @Override
        public void throwIfMissing() {
            registry.getHolder(id).orElseThrow(() -> new IllegalArgumentException("Full registry object name not present in registry"));
        }

        @Override
        public String toString() {
            return id.toString();
        }
    }

    protected static class TagSpec<T> extends RegistryElementPredicate<T> {
        private final TagKey<T> tag;

        public TagSpec(IForgeRegistry<T> registry, ResourceLocation tag) {
            super(registry);
            this.tag = TagKey.create(registry.getRegistryKey(), tag);
        }

        public TagSpec(IForgeRegistry<T> registry, TagKey<T> tag) {
            super(registry);
            this.tag = tag;
        }

        @Override
        public boolean test(T t) {
            var tags = registry.tags();
            if (tags == null) return false;
            return tags.getTag(tag).contains(t);
        }

        @Override
        public boolean testHolder(Holder<T> holder) {
            return holder.is(tag);
        }

        @Override
        public void throwIfMissing() {}

        @Override
        public String toString() {
            return "#" + tag.location().toString();
        }
    }

    protected static class AllSpec<T> extends RegistryElementPredicate<T> {
        protected AllSpec(IForgeRegistry<T> registry) {
            super(registry);
        }

        @Override
        public boolean test(T t) {
            return true;
        }

        @Override
        public boolean testHolder(Holder<T> holder) {
            return true;
        }

        @Override
        public void throwIfMissing() {}

        @Override
        public String toString() {
            return "";
        }
    }

    public static <T> RegistryElementPredicate<T> parseString(IForgeRegistry<T> registry, String string) {
        if (string.isEmpty() || string.equals("*"))
            return new AllSpec<>(registry);
        else if (string.startsWith("#"))
            return new TagSpec<>(registry, ResourceLocation.parse(string.substring(1)));
        else if (string.startsWith("@"))
            return new NamespaceSpec<>(registry, string.substring(1));
        else
            return new FullNameSpec<>(registry, ResourceLocation.parse(string));
    }

    public static <T> RegistryElementPredicate<T> forAll(IForgeRegistry<T> registry) {
        return new AllSpec<>(registry);
    }

    public static <T> RegistryElementPredicate<T> forTag(IForgeRegistry<T> registry, ResourceLocation name) {
        return new TagSpec<>(registry, name);
    }

    public static <T> RegistryElementPredicate<T> forTag(IForgeRegistry<T> registry, TagKey<T> tag) {
        return new TagSpec<>(registry, tag);
    }

    public static <T> RegistryElementPredicate<T> forNamespace(IForgeRegistry<T> registry, String string) {
        return new NamespaceSpec<>(registry, string);
    }

    public static <T> RegistryElementPredicate<T> forID(IForgeRegistry<T> registry, ResourceLocation name) {
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
        if (string.isEmpty())
            return true;
        else if (string.equals("*"))
            return true;
        else if (string.startsWith("#"))
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

    public static <T> Codec<RegistryElementPredicate<T>> codec(IForgeRegistry<T> registry) {
        return Codec.STRING.comapFlatMap(string -> {
            try {
                return DataResult.success(parseString(registry, string));
            } catch (Exception e) {
                return DataResult.error(e::getMessage);
            }
        }, RegistryElementPredicate::toString);
    }
}
