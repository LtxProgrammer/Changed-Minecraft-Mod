package net.ltxprogrammer.changed.extension.terrablender;

import net.ltxprogrammer.changed.Changed;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

public class LatexBlender {
    public static void initialize() {
        Regions.register(new LatexRegion(Changed.modResource("overworld"), 2));

        // Register our surface rules
        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, Changed.MODID, LatexSurfaceRules.makeRules());
    }
}
