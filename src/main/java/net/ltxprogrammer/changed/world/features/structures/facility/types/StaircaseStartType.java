package net.ltxprogrammer.changed.world.features.structures.facility.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.world.features.structures.facility.FacilitySinglePiece;
import net.ltxprogrammer.changed.world.features.structures.facility.FacilityStaircaseStart;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class StaircaseStartType extends PieceType<FacilityStaircaseStart> {
    public static final Codec<FacilityStaircaseStart> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("template").forGetter(entrance -> entrance.templateName),
            Codec.optionalField("loot_table", ResourceLocation.CODEC).forGetter(entrance -> entrance.lootTable),
            ChangedRegistry.FACILITY_EVENTS.get().getCodec().listOf().fieldOf("events").orElseGet(List::of).forGetter(FacilitySinglePiece::getEvents)
    ).apply(instance, FacilityStaircaseStart::new));

    @Override
    public Codec<FacilityStaircaseStart> getCodec() {
        return CODEC;
    }
}
