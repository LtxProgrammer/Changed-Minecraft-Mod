package net.ltxprogrammer.changed.tutorial;

import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.KeybindComponent;

public interface ChangedTutorial {
    void setStep(ChangedTutorialSteps step);

    void onOpenRadial();
    void onUseAbility(AbstractAbilityInstance ability);

    static Component key(KeyMapping keyMapping) {
        return (new KeybindComponent(keyMapping.getName())).withStyle(ChatFormatting.BOLD);
    }

    static void triggerOnOpenRadial() {
        ((ChangedTutorial)Minecraft.getInstance().getTutorial()).onOpenRadial();
    }

    static void triggerOnUseAbility(AbstractAbilityInstance ability) {
        ((ChangedTutorial)Minecraft.getInstance().getTutorial()).onUseAbility(ability);
    }
}
