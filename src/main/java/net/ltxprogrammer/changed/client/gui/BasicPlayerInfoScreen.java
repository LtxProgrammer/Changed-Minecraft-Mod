package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.EyeStyle;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TextComponent;

public class BasicPlayerInfoScreen extends Screen {
    private final Screen lastScreen;

    public BasicPlayerInfoScreen(Screen parent) {
        super(new TextComponent("Basic Player Info"));
        this.lastScreen = parent;
    }

    @Override
    public void removed() {
        Changed.config.saveAdditionalData();
    }

    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    protected void init() {
        super.init();
        int i = 0;

        this.addRenderableWidget(new ColorSelector(this.font, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 36 * (i >> 1), 150, 32, new TextComponent("Hair Color"),
                Changed.config.client.basicPlayerInfo::getHairColor, Changed.config.client.basicPlayerInfo::setHairColor));
        i++;
        this.addRenderableWidget(new ColorSelector(this.font, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 36 * (i >> 1), 150, 32, new TextComponent("Iris Color"),
                Changed.config.client.basicPlayerInfo::getIrisColor, Changed.config.client.basicPlayerInfo::setIrisColor));
        i++;
        this.addRenderableWidget(new ColorSelector(this.font, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 36 * (i >> 1), 150, 32, new TextComponent("Sclera Color"),
                Changed.config.client.basicPlayerInfo::getScleraColor, Changed.config.client.basicPlayerInfo::setScleraColor));
        i++;
        this.addRenderableWidget(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 36 * (i >> 1) + 12, 150, 20, Changed.config.client.basicPlayerInfo.getEyeStyle().getName(), button -> {
            var style = Changed.config.client.basicPlayerInfo.getEyeStyle();
            int id = style.ordinal();
            if (id < EyeStyle.values().length - 1)
                id += 1;
            else
                id = 0;
            style = EyeStyle.values()[id];
            Changed.config.client.basicPlayerInfo.setEyeStyle(style);
            button.setMessage(style.getName());
        }));
        i++;

        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 6 + 36 * (i >> 1), 200, 20, CommonComponents.GUI_DONE, (p_96700_) -> {
            this.minecraft.setScreen(this.lastScreen);
        }));
    }

    @Override
    public void render(PoseStack poseStack, int p_96563_, int p_96564_, float p_96565_) {
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 15, 16777215);
        super.render(poseStack, p_96563_, p_96564_, p_96565_);
    }
}
