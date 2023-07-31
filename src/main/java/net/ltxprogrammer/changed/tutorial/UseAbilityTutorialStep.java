package net.ltxprogrammer.changed.tutorial;

import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.ltxprogrammer.changed.init.ChangedKeyMappings;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.world.inventory.AbilityRadialMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UseAbilityTutorialStep implements ChangedTutorialInstance {
    private static final int HINT_DELAY = 0;
    private static final Component TITLE = new TranslatableComponent("changed.tutorial.use_ability.title");
    private static final Component DESCRIPTION = new TranslatableComponent("changed.tutorial.use_ability.description", ChangedTutorial.key(ChangedKeyMappings.USE_ABILITY));
    private final Tutorial tutorial;
    private TutorialToast toast;
    private int timeWaiting;

    public UseAbilityTutorialStep(Tutorial tutorial) {
        this.tutorial = tutorial;
    }

    public void tick() {
        ++this.timeWaiting;
        var player = Minecraft.getInstance().player;
        if (!ProcessTransfur.isPlayerLatex(player)) {
            this.tutorial.setStep(TutorialSteps.NONE);
            return;
        }

        if (this.timeWaiting >= HINT_DELAY && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.MOUSE, TITLE, DESCRIPTION, false);
            this.tutorial.getMinecraft().getToasts().addToast(this.toast);
        }
    }

    @Override
    public void onUseAbility(AbstractAbilityInstance abilityInstance) {
        ((ChangedTutorial)this.tutorial).setStep(ChangedTutorialSteps.NONE);
    }

    public void clear() {
        if (this.toast != null) {
            this.toast.hide();
            this.toast = null;
        }
    }
}
