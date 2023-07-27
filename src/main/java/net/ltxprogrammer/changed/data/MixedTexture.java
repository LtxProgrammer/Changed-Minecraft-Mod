package net.ltxprogrammer.changed.data;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@OnlyIn(Dist.CLIENT)
public class MixedTexture extends AbstractTexture {
    public record OverlayBlock(ResourceLocation top, ResourceLocation side, ResourceLocation bottom) {
        private static final Map<String, Direction> NAME_TO_SIDE = ImmutableMap.<String, Direction>builder()
                .put("top", Direction.UP)
                .put("end", Direction.UP)
                .put("bottom", Direction.DOWN)
                .put("particle", Direction.UP)
                .put("side", Direction.NORTH)
                .put("texture", Direction.NORTH)
                .put("east", Direction.EAST)
                .put("west", Direction.WEST)
                .put("north", Direction.NORTH)
                .put("south", Direction.SOUTH)
                .put("cross", Direction.NORTH).build();

        public ResourceLocation guessSide(String name) {
            return switch (NAME_TO_SIDE.getOrDefault(name, Direction.NORTH)) {
                case UP -> top;
                case DOWN -> bottom;
                default -> side;
            };
        }
    }

    private static final Logger LOGGER = LogManager.getLogger(MixedTexture.class);

    private final ResourceLocation baseLocation;
    private final ResourceLocation overlayLocation;
    private final ResourceLocation name;

    public static final MixedTexture MISSING =
            new MixedTexture(MissingTextureAtlasSprite.getLocation(), MissingTextureAtlasSprite.getLocation(), MissingTextureAtlasSprite.getLocation());

    public MixedTexture(ResourceLocation base, ResourceLocation overlay, ResourceLocation name) {
        this.baseLocation = base;
        this.overlayLocation = overlay;
        this.name = name;
    }

    public ResourceLocation getBaseLocation() {
        return baseLocation;
    }

    public static NativeImage findMixedTexture(ResourceLocation name) {
        return findCachedTexture(name).orElse(null);
    }

    private record RGBA(float r, float g, float b, float a) {
        public static RGBA of(int integer) {
            return new RGBA(
                    (float)((integer & 0x00ff0000) >>> 16) / 255.0f,
                    (float)((integer & 0x0000ff00) >>> 8) / 255.0f,
                    (float)(integer & 0x000000ff) / 255.0f,
                    (float)((integer & 0xff000000) >>> 24) / 255.0f);
        }

        public int toInt() {
            return ((int)(clamp(r, 0f, 1f) * 255.0f) << 16) +
                    ((int)(clamp(g, 0f, 1f) * 255.0f) << 8) +
                    (int)(clamp(b, 0f, 1f) * 255.0f) +
                    ((int)(clamp(a, 0f, 1f) * 255.0f) << 24);
        }

        public RGBA add(RGBA rgba) {
            return new RGBA(r + rgba.r, g + rgba.g, b + rgba.b, a + rgba.a);
        }

        public RGBA mul(RGBA rgba) {
            return new RGBA(r * rgba.r, g * rgba.g, b * rgba.b, a * rgba.a);
        }

        public RGBA mul(float s) {
            return new RGBA(r * s, g * s, b * s, a * s);
        }

        public RGBA lerp(float s, RGBA rgba) {
            return new RGBA(Mth.lerp(s, r, rgba.r), Mth.lerp(s, g, rgba.g), Mth.lerp(s, b, rgba.b), Mth.lerp(s, a, rgba.a));
        }
    }

    private static float frac(float fraction) {
        return fraction - (float)Math.floor(fraction);
    }

    private static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public Pair<Integer, Integer> imageSize(NativeImage image, @Nullable AnimationMetadataSection metadataSection) {
        return metadataSection != null ?
                metadataSection.getFrameSize(image.getWidth(), image.getHeight()) :
                new Pair<>(image.getWidth(), image.getHeight());
    }

    public RGBA sampleNearest(NativeImage image, @Nullable AnimationMetadataSection metadataSection, float u, float v) {
        var size = imageSize(image, metadataSection);
        return RGBA.of(image.getPixelRGBA((int)(size.getFirst() * frac(u)),
                        (int)(size.getSecond() * frac(v))));
    }

    public RGBA sampleLinear(NativeImage image, @Nullable AnimationMetadataSection metadataSection, float u, float v) {
        var size = imageSize(image, metadataSection);
        float actualX = size.getFirst() * frac(u);
        float actualY = size.getFirst() * frac(v);
        int floorX = Mth.floor(actualX);
        int ceilX = Mth.ceil(actualX);
        int floorY = Mth.floor(actualY);
        int ceilY = Mth.ceil(actualY);

        float lerpX = actualX - floorX;
        float lerpY = actualY - floorY;

        int safeWrapFloorX = floorX < 0 ? floorX + size.getFirst() : floorX;
        int safeWrapFloorY = floorY < 0 ? floorY + size.getSecond() : floorY;
        int safeWrapCeilX = ceilX >= size.getFirst() ? ceilX - size.getFirst() : ceilX;
        int safeWrapCeilY = ceilY >= size.getSecond() ? ceilY - size.getSecond() : ceilY;

        var corner1 = RGBA.of(image.getPixelRGBA(safeWrapFloorX, safeWrapFloorY));
        var corner2 = RGBA.of(image.getPixelRGBA(safeWrapCeilX, safeWrapFloorY));
        var corner3 = RGBA.of(image.getPixelRGBA(safeWrapFloorX, safeWrapCeilY));
        var corner4 = RGBA.of(image.getPixelRGBA(safeWrapCeilX, safeWrapCeilY));

        var left = corner1.lerp(lerpY, corner3);
        var right = corner2.lerp(lerpY, corner4);

        return left.lerp(lerpX, right);
    }

    public static ResourceLocation getResourceLocation(ResourceLocation location) {
        return new ResourceLocation(location.getNamespace(), String.format("textures/%s%s", location.getPath(), ".png"));
    }

    private static final AtomicBoolean ATOMIC_LOCK = new AtomicBoolean(false);
    private static final Map<ResourceLocation, AnimationMetadataSection> IMAGE_META_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, NativeImage> IMAGE_SETUP_CACHE = new HashMap<>();
    private static final Map<ResourceLocation, NativeImage> IMAGE_FINAL_CACHE = new HashMap<>();

    public static void clearMemoryCache() {
        while (!ATOMIC_LOCK.compareAndSet(false, true)) {}

        IMAGE_META_CACHE.clear();
        IMAGE_SETUP_CACHE.forEach((location, nativeImage) -> nativeImage.close()); // Forgot to actually close the textures
        IMAGE_SETUP_CACHE.clear();
        IMAGE_FINAL_CACHE.forEach((location, nativeImage) -> nativeImage.close()); // Forgot to actually close the textures
        IMAGE_FINAL_CACHE.clear();

        ATOMIC_LOCK.set(false);
    }

    private static final int KERNEL_PASSES = 2;
    private static final float KERNEL_STEP = 1.0f / 16.0f;
    private static final float DELTA_SCALE = 0.175f;

    private static final float[][] EDGE_DETECTION = {
            { 1, 1, 1 },
            { 1, -8, 1 },
            { 1, 1, 1 }
    };

    private static final float[][] BLUR_QUICK = {
            { 1f / 16f, 2f / 16f, 1f / 16f },
            { 2f / 16f, 4f / 16f, 2f / 16f },
            { 1f / 16f, 2f / 16f, 1f / 16f }
    };

    private static final float[][] BLUR = {
            { 1f / 15f, 1f / 15f, 1f / 15f },
            { 1f / 15f, 7f / 15f, 1f / 15f },
            { 1f / 15f, 1f / 15f, 1f / 15f }
    };

    private static final float[][] KERNEL = BLUR;

    public static boolean cacheExists(ResourceLocation mixedName) {
        if (!Changed.config.client.cacheGeneratedTextures.get())
            return IMAGE_FINAL_CACHE.containsKey(mixedName);

        String path = "cache/" + mixedName.getNamespace() + "/" + mixedName.getPath();
        return Files.exists(Path.of(path));
    }

    public static Optional<NativeImage> findCachedTexture(ResourceLocation mixedName) {
        if (!Changed.config.client.cacheGeneratedTextures.get())
            return cacheExists(mixedName) ? Optional.of(IMAGE_FINAL_CACHE.get(mixedName)) : Optional.empty();

        String path = "cache/" + mixedName.getNamespace() + "/" + mixedName.getPath();
        if (!Files.exists(Path.of(path)))
            return Optional.empty();

        try {
            var fileStream = new FileInputStream(path);
            var nativeImage = NativeImage.read(fileStream);
            fileStream.close();
            return Optional.of(nativeImage);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private static void cacheMixedTexture(ResourceLocation mixedName, NativeImage image) {
        if (!Changed.config.client.cacheGeneratedTextures.get()) {
            IMAGE_FINAL_CACHE.put(mixedName, image);
            return;
        }

        String path = "cache/" + mixedName.getNamespace() + "/" + mixedName.getPath();

        try {
            Files.createDirectories(Path.of(path).getParent());
            image.writeToFile(path);
        } catch (Exception ex) {
            LOGGER.error("Failed to cache texture {}", mixedName, ex);
        }

        image.close();
    }

    @Override
    public void load(@NotNull ResourceManager resourceManager) {
        try {
            while (!ATOMIC_LOCK.compareAndSet(false, true)) {}

            AnimationMetadataSection baseMetadata = IMAGE_META_CACHE.computeIfAbsent(getResourceLocation(baseLocation), loc -> {
                try {
                    return resourceManager.getResource(loc).getMetadata(AnimationMetadataSection.SERIALIZER);
                } catch (RuntimeException | IOException e) {
                    return null;
                }
            });
            NativeImage baseImage = IMAGE_SETUP_CACHE.computeIfAbsent(getResourceLocation(baseLocation), loc ->
            {
                try {
                    return NativeImage.read(resourceManager.getResource(loc).getInputStream());
                } catch (IOException e) {
                    LOGGER.error(e);
                    return null;
                }
            });
            NativeImage overlayImage = IMAGE_SETUP_CACHE.computeIfAbsent(getResourceLocation(overlayLocation), loc ->
            {
                try {
                    return NativeImage.read(resourceManager.getResource(loc).getInputStream());
                } catch (IOException e) {
                    return null;
                }
            });

            ATOMIC_LOCK.set(false);

            if (baseImage == null) {
                LOGGER.warn("Unable to load base image {}", getResourceLocation(baseLocation));
                return;
            }

            Pair<Integer, Integer> frameSize = imageSize(baseImage, baseMetadata);
            var possibleCached = findCachedTexture(name);
            if (possibleCached.isPresent()) {
                if (frameSize.getFirst() == possibleCached.get().getWidth() && frameSize.getSecond() == possibleCached.get().getHeight())
                    return; // Image already cached
            }

            NativeImage newImage = new NativeImage(frameSize.getFirst(), frameSize.getSecond(), false);
            float averageLevel = 0.0f;
            int averageLevelCounter = 0;

            for (int pass = 0; pass < KERNEL_PASSES; ++pass) {
                NativeImage sampleImage = pass == 0 ? baseImage : newImage;
                AnimationMetadataSection sampleMeta = pass == 0 ? baseMetadata : null;

                for (int y = 0; y < newImage.getHeight(); y++)
                    for (int x = 0; x < newImage.getWidth(); x++) {
                        float u = (float) x / (float) newImage.getWidth();
                        float v = (float) y / (float) newImage.getHeight();

                        RGBA baseColor = sampleNearest(sampleImage, sampleMeta, u, v);

                        if (baseColor.a > 0.000001f) {
                            RGBA accumulator = RGBA.of(0);
                            for (int dy = -1; dy <= 1; dy++)
                                for (int dx = -1; dx <= 1; dx++) {
                                    float du = (float) dx * KERNEL_STEP;
                                    float dv = (float) dy * KERNEL_STEP;
                                    RGBA sampledColor = sampleNearest(sampleImage, sampleMeta, u + du, v + dv);
                                    if (sampledColor.a > 0.000001f) {
                                        accumulator = accumulator.add(sampledColor.mul(KERNEL[dx + 1][dy + 1]));
                                    }
                                }

                            float avg = (accumulator.r + accumulator.g + accumulator.b) / 3.0f;

                            RGBA newColor = new RGBA(
                                    avg,
                                    avg,
                                    avg, baseColor.a);
                            newImage.setPixelRGBA(x, y, newColor.toInt());
                            averageLevel += avg;
                            averageLevelCounter++;
                        } else {
                            newImage.setPixelRGBA(x, y, 0);
                        }
                    }
            }
            averageLevel /= (float)averageLevelCounter;
            for (int y = 0; y < newImage.getHeight(); y++) for (int x = 0; x < newImage.getWidth(); x++) {
                float u = (float)x / (float)newImage.getWidth();
                float v = (float)y / (float)newImage.getHeight();

                RGBA currentColor = sampleNearest(newImage, null, u, v);
                RGBA overlayColor = sampleLinear(overlayImage, null, u, v);

                if (currentColor.a > 0.000001f) {
                    float delta = (((currentColor.r + currentColor.g + currentColor.b) / 3.0f) - averageLevel) * DELTA_SCALE;

                    RGBA newColor = new RGBA(
                            overlayColor.r + delta,
                            overlayColor.g + delta,
                            overlayColor.b + delta, currentColor.a);
                    newImage.setPixelRGBA(x, y, newColor.toInt());
                }

                else {
                    newImage.setPixelRGBA(x, y, 0);
                }
            }

            cacheMixedTexture(getResourceLocation(name), newImage);
        } catch (Exception exception) {
            LOGGER.error("Failed to mix textures. {} + {}", baseLocation, overlayLocation, exception);
        }
    }
}
