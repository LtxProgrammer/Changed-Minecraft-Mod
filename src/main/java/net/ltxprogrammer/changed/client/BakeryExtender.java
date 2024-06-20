package net.ltxprogrammer.changed.client;

import com.mojang.math.Transformation;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Triple;

import java.util.function.Predicate;

public interface BakeryExtender {
    void removeFromCacheIf(Predicate<Triple<ResourceLocation, Transformation, Boolean>> predicate);
}
