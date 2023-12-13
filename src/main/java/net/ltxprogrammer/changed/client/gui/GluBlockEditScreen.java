package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.entity.GluBlockEntity;
import net.ltxprogrammer.changed.network.packet.ServerboundSetGluBlockPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GluBlockEditScreen extends Screen {
    private static final int MAX_LEVELS = 7;
    private static final Component JOINT_TYPE_LABEL = new TranslatableComponent("glu_block.joint_type");
    private static final Component HAS_DOOR_LABEL = new TranslatableComponent("glu_block.has_door");
    private static final Component FINAL_STATE_LABEL = new TranslatableComponent("jigsaw_block.final_state");
    private final GluBlockEntity gluEntity;
    private int size = 3;
    private Button jointTypeButton;
    private GluBlockEntity.JointType jointType;
    private Checkbox hasDoor;
    private EditBox finalStateEdit;
    private Button doneButton;

    public GluBlockEditScreen(GluBlockEntity blockEntity) {
        super(NarratorChatListener.NO_TITLE);
        this.gluEntity = blockEntity;
        this.size = blockEntity.getSize();
        this.jointType = blockEntity.getJointType();
    }

    public void tick() {
        this.finalStateEdit.tick();
    }

    private void onDone() {
        this.sendToServer();
        this.minecraft.setScreen((Screen)null);
    }

    private void onCancel() {
        this.minecraft.setScreen((Screen)null);
    }

    private void sendToServer() {
        Changed.PACKET_HANDLER.sendToServer(new ServerboundSetGluBlockPacket(
                this.gluEntity.getBlockPos(),
                this.size,
                this.hasDoor.selected(),
                this.jointType,
                this.finalStateEdit.getValue()
        ));
    }

    public void onClose() {
        this.onCancel();
    }

    protected void init() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);

        this.jointTypeButton = new Button(this.width / 2 - 152, 55, 300, 20, jointType.getTranslatedName(), press -> {
            this.jointType = this.jointType.next();
            this.jointTypeButton.setMessage(jointType.getTranslatedName());
        });
        this.addWidget(this.jointTypeButton);

        this.hasDoor = new Checkbox(this.width / 2 - 152, 90, 300, 20, HAS_DOOR_LABEL, gluEntity.getHasDoor(), false);
        this.addWidget(this.hasDoor);

        this.finalStateEdit = new EditBox(this.font, this.width / 2 - 152, 125, 300, 20, FINAL_STATE_LABEL);
        this.finalStateEdit.setMaxLength(256);
        this.finalStateEdit.setValue(this.gluEntity.getFinalState());
        this.addWidget(this.finalStateEdit);

        this.addRenderableWidget(new AbstractSliderButton(this.width / 2 - 154, 180, 100, 20, TextComponent.EMPTY, 3.0D / 7.0D) {
            {
                this.updateMessage();
            }

            protected void updateMessage() {
                this.setMessage(new TranslatableComponent("glu_block.size", GluBlockEditScreen.this.size));
            }

            protected void applyValue() {
                GluBlockEditScreen.this.size = Mth.floor(Mth.clampedLerp(0.0D, 7.0D, this.value));
            }
        });
        this.doneButton = this.addRenderableWidget(new Button(this.width / 2 - 4 - 150, 210, 150, 20, CommonComponents.GUI_DONE, (p_98973_) -> {
            this.onDone();
        }));
        this.addRenderableWidget(new Button(this.width / 2 + 4, 210, 150, 20, CommonComponents.GUI_CANCEL, (p_98964_) -> {
            this.onCancel();
        }));
        this.setInitialFocus(this.finalStateEdit);
        this.updateValidity();
    }

    private void updateValidity() {
        boolean valid = true;
        this.doneButton.active = valid;
    }

    public void resize(Minecraft minecraft, int x, int y) {
        String s3 = this.finalStateEdit.getValue();
        this.init(minecraft, x, y);
        this.finalStateEdit.setValue(s3);
    }

    public void removed() {
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
    }

    public boolean keyPressed(int p_98951_, int p_98952_, int p_98953_) {
        if (super.keyPressed(p_98951_, p_98952_, p_98953_)) {
            return true;
        } else if (!this.doneButton.active || p_98951_ != 257 && p_98951_ != 335) {
            return false;
        } else {
            this.onDone();
            return true;
        }
    }

    public void render(PoseStack pose, int x, int y, float partialTicks) {
        this.renderBackground(pose);
        drawString(pose, this.font, JOINT_TYPE_LABEL, this.width / 2 - 153, 45, 10526880);
        this.jointTypeButton.render(pose, x, y, partialTicks);
        drawString(pose, this.font, HAS_DOOR_LABEL, this.width / 2 - 153, 80, 10526880);
        this.hasDoor.render(pose, x, y, partialTicks);
        drawString(pose, this.font, FINAL_STATE_LABEL, this.width / 2 - 153, 115, 10526880);
        this.finalStateEdit.render(pose, x, y, partialTicks);

        super.render(pose, x, y, partialTicks);
    }
}