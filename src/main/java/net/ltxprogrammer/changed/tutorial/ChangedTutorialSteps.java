package net.ltxprogrammer.changed.tutorial;

import net.minecraft.client.tutorial.CompletedTutorialStepInstance;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;
import net.minecraft.client.tutorial.TutorialSteps;

import java.util.function.Function;

public enum ChangedTutorialSteps {
    SELECT_ABILITY("select_ability", SelectAbilityTutorialStep::new),
    USE_ABILITY("use_ability", UseAbilityTutorialStep::new),
    NONE(TutorialSteps.NONE.getName(), CompletedTutorialStepInstance::new);

    private final String name;
    private final Function<Tutorial, ? extends TutorialStepInstance> constructor;

    <T extends TutorialStepInstance> ChangedTutorialSteps(String name, Function<Tutorial, T> constructor) {
        this.name = name;
        this.constructor = constructor;
    }

    public TutorialStepInstance create(Tutorial tutorial) {
        return this.constructor.apply(tutorial);
    }

    public String getName() {
        return this.name;
    }

    public static ChangedTutorialSteps getByName(String name) {
        for(var step : values()) {
            if (step.name.equals(name)) {
                return step;
            }
        }

        return NONE;
    }
}
