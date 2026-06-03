package net.ltxprogrammer.changed.ability.tree;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Optional;

public record NodeDisplayInfo(Either<ResourceLocation, ItemStack> icon, FrameType frameType, int iconWidth, int iconHeight) {
    public enum FrameType implements StringRepresentable {
        BASIC("basic", 24, ChatFormatting.WHITE),
        STRONG("strong", 48, ChatFormatting.AQUA),
        ULTIMATE("ultimate", 72, ChatFormatting.LIGHT_PURPLE);

        public static Codec<FrameType> CODEC = Codec.STRING.comapFlatMap(FrameType::fromSerial, FrameType::getSerializedName);

        public final String serialName;
        public final int yPos;
        public final ChatFormatting titleColor;

        FrameType(String serialName, int yPos, ChatFormatting titleColor) {
            this.serialName = serialName;
            this.yPos = yPos;
            this.titleColor = titleColor;
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }

        public static DataResult<FrameType> fromSerial(String name) {
            return Arrays.stream(values()).filter(type -> type.serialName.equals(name))
                    .findFirst().map(DataResult::success).orElseGet(() -> DataResult.error(() -> name + " is not a valid FrameType"));
        }
    }

    private static final Codec<ItemStack> ITEMSTACK_CODEC = RecordCodecBuilder.create((builder) -> builder.group(
                    ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(ItemStack::getItem),
                    Codec.INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
                    CompoundTag.CODEC.optionalFieldOf("nbt").forGetter((itemStack) -> Optional.ofNullable(itemStack.getTag())))
            .apply(builder, (itemLike, count, optionalTag) -> {
                var itemStack = new ItemStack(itemLike, count);
                optionalTag.ifPresent(itemStack::setTag);
                return itemStack;
            }));

    public static final Codec<NodeDisplayInfo> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.either(ResourceLocation.CODEC, ITEMSTACK_CODEC).fieldOf("icon").orElse(Either.left(ResourceLocation.withDefaultNamespace("missingno"))).forGetter(NodeDisplayInfo::icon),
            FrameType.CODEC.fieldOf("frame").orElse(FrameType.BASIC).forGetter(NodeDisplayInfo::frameType),
            Codec.INT.fieldOf("iconWidth").orElse(16).forGetter(NodeDisplayInfo::iconWidth),
            Codec.INT.fieldOf("iconHeight").orElse(16).forGetter(NodeDisplayInfo::iconHeight)
    ).apply(builder, NodeDisplayInfo::new));

    public static final NodeDisplayInfo MISSING = new NodeDisplayInfo(Either.left(ResourceLocation.withDefaultNamespace("missingno")), FrameType.BASIC, 16, 16);
}
