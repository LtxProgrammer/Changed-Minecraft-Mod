package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.client.RecipeCategories;
import net.ltxprogrammer.changed.init.ChangedRecipeTypes;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookTabButton.class)
public abstract class RecipeBookTabButtonMixin extends StateSwitchingButton {
    public RecipeBookTabButtonMixin(int p_94615_, int p_94616_, int p_94617_, int p_94618_, boolean p_94619_) {
        super(p_94615_, p_94616_, p_94617_, p_94618_, p_94619_);
    }

    @Inject(method = "updateVisibility", at = @At("HEAD"), cancellable = true)
    public void updateVisibility(ClientRecipeBook p_100450_, CallbackInfoReturnable<Boolean> ci) {
        if (RecipeCategories.INJECTED_CATEGORIES.contains(((RecipeBookTabButton)(StateSwitchingButton)this).getCategory())) {
            this.visible = true;
            ci.setReturnValue(true);
        }
    }
}
