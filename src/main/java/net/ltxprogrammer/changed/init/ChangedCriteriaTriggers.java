package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.advancements.critereon.*;
import net.minecraft.advancements.CriteriaTriggers;

public class ChangedCriteriaTriggers {
    // TODO grab mechanic
    public static final TransfurTrigger TRANSFUR = new TransfurTrigger();
    public static final AquaticBreatheTrigger AQUATIC_BREATHE = new AquaticBreatheTrigger();
    public static final WhiteLatexFuseTrigger WHITE_LATEX_FUSE = new WhiteLatexFuseTrigger();
    public static final BeehiveSleepTrigger BEEHIVE_SLEEP = new BeehiveSleepTrigger();
    public static final TameLatexTrigger TAME_LATEX = new TameLatexTrigger();
    public static final FlyingTrigger FLYING = new FlyingTrigger();

    public static void registerTriggers() {
        CriteriaTriggers.register(TRANSFUR);
        CriteriaTriggers.register(AQUATIC_BREATHE);
        CriteriaTriggers.register(WHITE_LATEX_FUSE);
        CriteriaTriggers.register(BEEHIVE_SLEEP);
        CriteriaTriggers.register(TAME_LATEX);
        CriteriaTriggers.register(FLYING);
    }
}
