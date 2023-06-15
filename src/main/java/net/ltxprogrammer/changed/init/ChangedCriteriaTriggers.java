package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.advancements.critereon.*;
import net.minecraftforge.fml.common.Mod;

import static net.minecraft.advancements.CriteriaTriggers.register;

@Mod.EventBusSubscriber
public class ChangedCriteriaTriggers {
    public static final TransfurTrigger TRANSFUR = register(new TransfurTrigger());
    public static final AquaticBreatheTrigger AQUATIC_BREATHE = register(new AquaticBreatheTrigger());
    public static final WhiteLatexFuseTrigger WHITE_LATEX_FUSE = register(new WhiteLatexFuseTrigger());
    public static final BeehiveSleepTrigger BEEHIVE_SLEEP = register(new BeehiveSleepTrigger());
    public static final TameLatexTrigger TAME_LATEX = register(new TameLatexTrigger());
}
