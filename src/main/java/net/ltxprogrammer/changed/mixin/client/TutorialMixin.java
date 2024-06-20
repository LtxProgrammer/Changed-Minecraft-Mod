package net.ltxprogrammer.changed.mixin.client;

import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.client.ChangedOptions;
import net.ltxprogrammer.changed.tutorial.ChangedTutorial;
import net.ltxprogrammer.changed.tutorial.ChangedTutorialInstance;
import net.ltxprogrammer.changed.tutorial.ChangedTutorialSteps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.tutorial.CompletedTutorialStepInstance;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;
import net.minecraft.client.tutorial.TutorialSteps;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Tutorial.class)
public abstract class TutorialMixin implements ChangedTutorial {
    @Shadow @Nullable private TutorialStepInstance instance;
    @Shadow @Final private Minecraft minecraft;

    @Shadow public abstract void stop();

    @Unique
    private ChangedOptions getOptions() {
        return (ChangedOptions)this.minecraft.options;
    }

    @Inject(method = "start", at = @At("HEAD"), cancellable = true)
    public void start(CallbackInfo callback) {
        if (this.minecraft.options.tutorialStep == TutorialSteps.NONE && getOptions().getChangedTutorialStep() != null) {
            callback.cancel();
            if (this.instance != null) {
                this.stop();
            }

            this.instance = getOptions().getChangedTutorialStep().create((Tutorial)(Object)this);
        }
    }

    @Override
    public void setStep(ChangedTutorialSteps step) {
        getOptions().setChangedTutorialStep(step);
        if (this.instance instanceof CompletedTutorialStepInstance || this.instance instanceof ChangedTutorialInstance) {
            this.instance.clear();
            this.instance = step.create((Tutorial)(Object)this);
        }
    }

    @Inject(method = "setStep", at = @At("RETURN"))
    public void setStep(TutorialSteps step, CallbackInfo callback) {
        if (step != TutorialSteps.NONE)
            return;
        if (this.instance != null) {
            this.instance.clear();
            this.instance = getOptions().getChangedTutorialStep().create((Tutorial)(Object)this);
        }
    }

    @Override
    public void onOpenRadial() {
        if (this.instance instanceof ChangedTutorialInstance changedTutorialInstance)
            changedTutorialInstance.onOpenRadial();
    }

    @Override
    public void onUseAbility(AbstractAbilityInstance ability) {
        if (this.instance instanceof ChangedTutorialInstance changedTutorialInstance)
            changedTutorialInstance.onUseAbility(ability);
    }
}
