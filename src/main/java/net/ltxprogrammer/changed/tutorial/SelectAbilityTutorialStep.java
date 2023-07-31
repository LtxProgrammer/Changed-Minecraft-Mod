package net.ltxprogrammer.changed.tutorial;

import net.ltxprogrammer.changed.init.ChangedKeyMappings;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.world.inventory.AbilityRadialMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SelectAbilityTutorialStep implements ChangedTutorialInstance {
    private static final int HINT_DELAY = 300;
    private static final Component TITLE = new TranslatableComponent("changed.tutorial.select_ability.title");
    private static final Component DESCRIPTION = new TranslatableComponent("changed.tutorial.select_ability.description", ChangedTutorial.key(ChangedKeyMappings.SELECT_ABILITY));
    private final Tutorial tutorial;
    private TutorialToast toast;
    private int timeWaiting;

    public SelectAbilityTutorialStep(Tutorial tutorial) {
        this.tutorial = tutorial;
    }

    public void tick() {
        var player = Minecraft.getInstance().player;
        if (!ProcessTransfur.isPlayerLatex(player))
            return; // Wait until player is TF'd

        ++this.timeWaiting;
        if (player.containerMenu instanceof AbilityRadialMenu) {
            ((ChangedTutorial)this.tutorial).setStep(ChangedTutorialSteps.USE_ABILITY);
            return;
        }

        if (this.timeWaiting >= HINT_DELAY && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.MOUSE, TITLE, DESCRIPTION, false);
            this.tutorial.getMinecraft().getToasts().addToast(this.toast);
        }
    }

    public void clear() {
        if (this.toast != null) {
            this.toast.hide();
            this.toast = null;
        }
    }
}
