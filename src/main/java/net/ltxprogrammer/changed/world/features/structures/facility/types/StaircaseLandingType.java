package net.ltxprogrammer.changed.world.features.structures.facility.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.world.features.structures.facility.FacilitySinglePiece;
import net.ltxprogrammer.changed.world.features.structures.facility.FacilityStaircaseLanding;
import net.ltxprogrammer.changed.world.features.structures.facility.FacilityStaircaseSection;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class StaircaseLandingType extends PieceType<FacilityStaircaseLanding> {
    public static final Codec<FacilityStaircaseLanding> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("template").forGetter(piece -> piece.templateName),
            Codec.INT.fieldOf("expected_dependents").orElse(2).forGetter(piece -> piece.expectedDependents),
            Codec.optionalField("loot_table", ResourceLocation.CODEC).forGetter(piece -> piece.lootTable),
            ChangedRegistry.FACILITY_EVENTS.get().getCodec().listOf().fieldOf("events").orElseGet(List::of).forGetter(FacilitySinglePiece::getEvents)
    ).apply(instance, FacilityStaircaseLanding::new));

    @Override
    public Codec<FacilityStaircaseLanding> getCodec() {
        return CODEC;
    }

    @Override
    public boolean shouldConsumeSpan() {
        return false;
    }
}
