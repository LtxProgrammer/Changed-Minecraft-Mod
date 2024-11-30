package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.entity.EyeStyle;
import net.ltxprogrammer.changed.entity.beast.CustomLatexEntity;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.world.inventory.StasisChamberMenu;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class StasisChamberModifyScreen extends Screen implements MenuAccess<StasisChamberMenu> {
    private final StasisChamberMenu menu;
    private final Screen lastScreen;
    private final @Nullable IAbstractChangedEntity chambered;
    private @Nullable Runnable toolTip = null;

    public StasisChamberModifyScreen(StasisChamberMenu menu, Screen parent, @Nullable IAbstractChangedEntity chambered) {
        super(new TranslatableComponent("changed.stasis.modify"));
        this.menu = menu;
        this.lastScreen = parent;
        this.chambered = chambered;
    }

    public @NotNull StasisChamberMenu getMenu() {
        return menu;
    }

    public void setToolTip(Runnable fn) {
        this.toolTip = fn;
    }

    @Override
    public void removed() {

    }

    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        super.init();
        if (chambered == null)
            return;
        if (!chambered.getChangedEntity().getType().is(ChangedTags.EntityTypes.LATEX))
            return;

        if (chambered.getChangedEntity() instanceof CustomLatexEntity customLatexEntity) {
            // Buttons to cycle through torso, arms, legs, etc
        } else ChangedTransfurVariants.Gendered.getOpposite(chambered.getSelfVariant()).ifPresent(otherVariant -> {
            var originalVariant = chambered.getSelfVariant();
            // Button to swap gender
        });

        var bpi = Changed.config.client.basicPlayerInfo;
        int i = 0;

        this.addRenderableWidget(new ColorSelector(this.font, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.config.bpi.hair_color"),
                bpi::getHairColor, bpi::setHairColor));
        i++;
        this.addRenderableWidget(new ColorSelector(this.font, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.config.bpi.sclera_color"),
                bpi::getScleraColor, bpi::setScleraColor));
        i++;
        var rightIris = this.addRenderableWidget(new ColorSelector(this.font, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.config.bpi.iris_color.right"),
                bpi::getRightIrisColor, bpi::setRightIrisColor));
        i++;
        var leftIris = this.addRenderableWidget(new ColorSelector(this.font, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.config.bpi.iris_color.left"),
                bpi::getLeftIrisColor, bpi::setLeftIrisColor));
        this.addRenderableWidget(new Button((this.width / 2 - 155 + i % 2 * 160) + 110, (this.height / 6 + 24 * (i >> 1)) + 24, 40, 20, new TranslatableComponent("changed.config.bpi.iris_color.sync"),
                button -> {
                    leftIris.setValue(rightIris.getValue());
                    //bpi.setLeftIrisColor(bpi.getRightIrisColor());
                }, (button, stack, x, y) -> {
                    setToolTip(() -> this.renderTooltip(stack, new TranslatableComponent("changed.config.bpi.iris_color.sync_tooltip"), x, y));
                }));
        i++;
        this.addRenderableWidget(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.config.bpi.eye_style", bpi.getEyeStyle().getName()), button -> {
            var style = bpi.getEyeStyle();
            int id = style.ordinal();
            if (id < EyeStyle.values().length - 1)
                id += 1;
            else
                id = 0;
            style = EyeStyle.values()[id];
            bpi.setEyeStyle(style);
            button.setMessage(new TranslatableComponent("changed.config.bpi.eye_style", style.getName()));
        }));
        i += 2;
        this.addRenderableWidget(new AbstractSliderButton(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.config.bpi.size"), bpi.getSizeValueForConfiguration()) {
            {
                this.updateMessage();
            }

            private double convertToScaledValue() {
                return (this.value * BasicPlayerInfo.getSizeTolerance() * 2) - BasicPlayerInfo.getSizeTolerance() + 1.0;
            }

            @Override
            protected void updateMessage() {
                this.setMessage(new TranslatableComponent("changed.config.bpi.size.value", Math.round(convertToScaledValue() * 100)));
            }

            @Override
            protected void applyValue() {
                bpi.setSize((float)convertToScaledValue());
            }
        });
        i += 2;
        this.addRenderableWidget(new Checkbox(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.config.bpi.override_dl_iris"), bpi.isOverrideIrisOnDarkLatex()) {
            @Override
            public void onPress() {
                super.onPress();
                bpi.setOverrideIrisOnDarkLatex(this.selected());
            }
        });
        i += 2;
        this.addRenderableWidget(new Checkbox(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.config.bpi.override_all_eye_styles"), bpi.isOverrideOthersToMatchStyle()) {
            @Override
            public void onPress() {
                super.onPress();
                bpi.setOverrideOthersToMatchStyle(this.selected());
            }
        });
        i += 2;

        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20, CommonComponents.GUI_DONE, (p_96700_) -> {
            this.minecraft.setScreen(this.lastScreen);
        }));
    }

    @Override
    public void render(PoseStack poseStack, int p_96563_, int p_96564_, float p_96565_) {
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 15, 16777215);
        super.render(poseStack, p_96563_, p_96564_, p_96565_);
        if (toolTip != null) {
            toolTip.run();
            toolTip = null;
        }
    }
}
