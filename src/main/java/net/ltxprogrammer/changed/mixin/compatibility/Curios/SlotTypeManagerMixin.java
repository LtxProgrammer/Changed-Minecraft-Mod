package net.ltxprogrammer.changed.mixin.compatibility.Curios;

import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.extension.curios.CurioSlots;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;
import top.theillusivec4.curios.common.slottype.SlotType;
import top.theillusivec4.curios.common.slottype.SlotTypeManager;

import java.util.*;

@Mixin(value = SlotTypeManager.class, remap = false)
@RequiredMods("curios")
public abstract class SlotTypeManagerMixin {
    @Shadow private static Map<String, Set<String>> idsToMods;

    @Redirect(method = "buildSlotTypes", at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"))
    private static Collection<SlotType.Builder> addDatapackSlotTypes(Map<String, SlotType.Builder> instance) {
        Map<String, SlotType.Builder> copy = new HashMap<>();
        instance.forEach((id, builder) -> {
            var b = new SlotType.Builder(id);
            b.copyFrom(builder);
            copy.put(id, b);
        });
        CurioSlots.INSTANCE.getSlotDefinitions((id, msg) -> {
            SlotType.Builder builder = copy.get(id);

            if (builder == null) {
                builder = new SlotType.Builder(id);
                copy.put(id, builder);
                idsToMods.computeIfAbsent(id, (k) -> new HashSet<>()).add(msg.modId());
            }

            if (builder != null) {
                builder.size(msg.size()).visible(msg.visible()).hasCosmetic(msg.cosmetic());
                SlotTypeMessage.Builder preset = SlotTypePreset.findPreset(id)
                        .map(SlotTypePreset::getMessageBuilder).orElse(null);
                SlotTypeMessage presetMsg = preset != null ? preset.build() : null;

                if (msg.icon() == null && presetMsg != null) {
                    builder.icon(presetMsg.getIcon());
                } else {
                    builder.icon(msg.icon());
                }

                if (msg.priority() == null && presetMsg != null) {
                    builder.priority(presetMsg.getPriority());
                } else {
                    builder.priority(msg.priority());
                }
            }
        });

        return copy.values();
    }
}
