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
    private @Nullable Runnable toolTip = null;

    public StasisChamberModifyScreen(StasisChamberMenu menu, Screen parent) {
        super(new TranslatableComponent("changed.stasis.modify"));
        this.menu = menu;
        this.lastScreen = parent;
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
        int i = 0;

        this.addRenderableWidget(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.stasis.modify.torso", CustomLatexEntity.TorsoType.fromFlags(menu.configuredCustomLatex).name()), button -> {
            var next = CustomLatexEntity.TorsoType.fromFlags(menu.configuredCustomLatex).cycle();
            menu.configuredCustomLatex = next.setFlags(menu.configuredCustomLatex);
            button.setMessage(new TranslatableComponent("changed.stasis.modify.torso", next.name()));
        }));
        i++;
        this.addRenderableWidget(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.stasis.modify.hair", CustomLatexEntity.HairType.fromFlags(menu.configuredCustomLatex).name()), button -> {
            var next = CustomLatexEntity.HairType.fromFlags(menu.configuredCustomLatex).cycle();
            menu.configuredCustomLatex = next.setFlags(menu.configuredCustomLatex);
            button.setMessage(new TranslatableComponent("changed.stasis.modify.hair", next.name()));
        }));
        i++;
        this.addRenderableWidget(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.stasis.modify.ears", CustomLatexEntity.EarType.fromFlags(menu.configuredCustomLatex).name()), button -> {
            var next = CustomLatexEntity.EarType.fromFlags(menu.configuredCustomLatex).cycle();
            menu.configuredCustomLatex = next.setFlags(menu.configuredCustomLatex);
            button.setMessage(new TranslatableComponent("changed.stasis.modify.ears", next.name()));
        }));
        i++;
        this.addRenderableWidget(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.stasis.modify.tail", CustomLatexEntity.TailType.fromFlags(menu.configuredCustomLatex).name()), button -> {
            var next = CustomLatexEntity.TailType.fromFlags(menu.configuredCustomLatex).cycle();
            menu.configuredCustomLatex = next.setFlags(menu.configuredCustomLatex);
            button.setMessage(new TranslatableComponent("changed.stasis.modify.tail", next.name()));
        }));
        i++;
        this.addRenderableWidget(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.stasis.modify.legs", CustomLatexEntity.LegType.fromFlags(menu.configuredCustomLatex).name()), button -> {
            var next = CustomLatexEntity.LegType.fromFlags(menu.configuredCustomLatex).cycle();
            menu.configuredCustomLatex = next.setFlags(menu.configuredCustomLatex);
            button.setMessage(new TranslatableComponent("changed.stasis.modify.legs", next.name()));
        }));
        i++;
        this.addRenderableWidget(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.stasis.modify.arms", CustomLatexEntity.ArmType.fromFlags(menu.configuredCustomLatex).name()), button -> {
            var next = CustomLatexEntity.ArmType.fromFlags(menu.configuredCustomLatex).cycle();
            menu.configuredCustomLatex = next.setFlags(menu.configuredCustomLatex);
            button.setMessage(new TranslatableComponent("changed.stasis.modify.arms", next.name()));
        }));
        i++;
        this.addRenderableWidget(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.stasis.modify.scale", CustomLatexEntity.ScaleType.fromFlags(menu.configuredCustomLatex).name()), button -> {
            var next = CustomLatexEntity.ScaleType.fromFlags(menu.configuredCustomLatex).cycle();
            menu.configuredCustomLatex = next.setFlags(menu.configuredCustomLatex);
            button.setMessage(new TranslatableComponent("changed.stasis.modify.scale", next.name()));
        }));
        i++;
        i += 2;

        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20, CommonComponents.GUI_DONE, (p_96700_) -> {
            menu.inputCustomLatexConfig(menu.configuredCustomLatex);
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
