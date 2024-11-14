package net.ltxprogrammer.changed.entity.variant;

import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class UniversalAbilitiesHandle {

    @SubscribeEvent
    public static void AddUniversalAbilities(TransfurVariant.UniversalAbilitiesEvent event){
        event.addAbility(event.isOfTag(ChangedTags.EntityTypes.LATEX)
                .and(event.isNotOfTag(ChangedTags.EntityTypes.PARTIAL_LATEX)), ChangedAbilities.SWITCH_TRANSFUR_MODE);
        event.addAbility(event.isOfTag(ChangedTags.EntityTypes.LATEX)
                .and(event.isNotOfTag(ChangedTags.EntityTypes.ARMLESS))
                .and(event.isNotOfTag(ChangedTags.EntityTypes.PARTIAL_LATEX)), ChangedAbilities.GRAB_ENTITY_ABILITY);
    }
}
