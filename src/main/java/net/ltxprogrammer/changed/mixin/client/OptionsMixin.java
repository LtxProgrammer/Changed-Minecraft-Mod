package net.ltxprogrammer.changed.mixin.client;

import net.ltxprogrammer.changed.client.ChangedOptions;
import net.ltxprogrammer.changed.tutorial.ChangedTutorialSteps;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class OptionsMixin implements ChangedOptions {
    @Unique private ChangedTutorialSteps changedTutorialStep = ChangedTutorialSteps.SELECT_ABILITY;

    @Override
    public ChangedTutorialSteps getChangedTutorialStep() {
        return changedTutorialStep;
    }

    @Override
    public void setChangedTutorialStep(ChangedTutorialSteps step) {
        this.changedTutorialStep = step;
    }

    @Inject(method = "processOptions", at = @At("RETURN"))
    private void processOptions(Options.FieldAccess access, CallbackInfo callback) {
        changedTutorialStep = access.process("changed__tutorialStep", changedTutorialStep, ChangedTutorialSteps::getByName, ChangedTutorialSteps::getName);
    }
}
