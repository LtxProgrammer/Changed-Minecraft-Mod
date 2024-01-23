package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.EyeStyle;
import net.ltxprogrammer.changed.network.packet.BasicPlayerInfoPacket;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class BasicPlayerInfoScreen extends Screen {
    private final Screen lastScreen;
    private final @Nullable Player player;

    public BasicPlayerInfoScreen(Screen parent) {
        super(new TranslatableComponent("changed.config.bpi.screen"));
        this.lastScreen = parent;
        this.player = null;
    }

    public BasicPlayerInfoScreen(Screen parent, Player player) {
        super(new TranslatableComponent("changed.config.bpi.screen"));
        this.lastScreen = parent;
        this.player = player;
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
        var bpi = Changed.config.client.basicPlayerInfo;
        int i = 0;

        this.addRenderableWidget(new ColorSelector(this.font, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.config.bpi.hair_color"),
                bpi::getHairColor, bpi::setHairColor));
        i++;
        this.addRenderableWidget(new ColorSelector(this.font, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.config.bpi.sclera_color"),
                bpi::getScleraColor, bpi::setScleraColor));
        i++;
        this.addRenderableWidget(new ColorSelector(this.font, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.config.bpi.iris_color.right"),
                bpi::getRightIrisColor, bpi::setRightIrisColor));
        i++;
        this.addRenderableWidget(new ColorSelector(this.font, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, new TranslatableComponent("changed.config.bpi.iris_color.left"),
                bpi::getLeftIrisColor, bpi::setLeftIrisColor));
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
            if (this.player != null)
                Changed.PACKET_HANDLER.sendToServer(BasicPlayerInfoPacket.Builder.of(this.player));
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
