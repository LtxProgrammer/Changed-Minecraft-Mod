package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.entity.GluBlockEntity;
import net.ltxprogrammer.changed.network.packet.ServerboundSetGluBlockPacket;
import net.ltxprogrammer.changed.world.features.structures.facility.Zone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GluBlockEditScreen extends Screen {
    private static final int MAX_LEVELS = 7;
    private static final Component JOINT_TYPE_LABEL = Component.translatable("glu_block.joint_type");
    private static final Component ZONE_LABEL = Component.translatable("glu_block.zone");
    private static final Component HAS_DOOR_LABEL = Component.translatable("glu_block.has_door");
    private static final Component FINAL_STATE_LABEL = Component.translatable("jigsaw_block.final_state");
    private final GluBlockEntity gluEntity;
    private int size = 3;
    private Button jointTypeButton;
    private Button zoneButton;
    private Zone zone;
    private GluBlockEntity.JointType jointType;
    private Checkbox hasDoor;
    private EditBox finalStateEdit;
    private Button doneButton;

    public GluBlockEditScreen(GluBlockEntity blockEntity) {
        super(Component.empty());
        this.gluEntity = blockEntity;
        this.size = blockEntity.getSize();
        this.zone = blockEntity.getZone();
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
                this.zone,
                this.jointType,
                this.finalStateEdit.getValue()
        ));
    }

    public void onClose() {
        this.onCancel();
    }

    protected void init() {
        this.jointTypeButton = Button.builder(jointType.getTranslatedName(), press -> {
            this.jointType = this.jointType.next();
            this.jointTypeButton.setMessage(jointType.getTranslatedName());
        }).bounds(this.width / 2 - 152, 55, 148, 20).build();
        this.addWidget(this.jointTypeButton);

        this.zoneButton = Button.builder(zone.getTranslatedName(), press -> {
            this.zone = Zone.findNext(this.zone);
            this.zoneButton.setMessage(zone.getTranslatedName());
        }).bounds((this.width / 2 - 152) + 152, 55, 148, 20).build();
        this.addWidget(this.zoneButton);

        this.hasDoor = new Checkbox(this.width / 2 - 152, 90, 300, 20, HAS_DOOR_LABEL, gluEntity.getHasDoor(), false);
        this.addWidget(this.hasDoor);

        final var blockHolderLookup = minecraft.level.holderLookup(Registries.BLOCK);
        this.finalStateEdit = new EditBox(this.font, this.width / 2 - 152, 125, 300, 20, FINAL_STATE_LABEL);
        this.finalStateEdit.setMaxLength(256);
        this.finalStateEdit.setResponder(blockStateStr -> {
            try {
                BlockStateParser.parseForBlock(blockHolderLookup, blockStateStr, true);
                finalStateEdit.setTextColor(0xE0E0E0); // White
            } catch (CommandSyntaxException commandsyntaxexception) {
                finalStateEdit.setTextColor(0xE00000); // Red - indicate error
            }
        });
        this.finalStateEdit.setValue(this.gluEntity.getFinalState());
        this.addWidget(this.finalStateEdit);

        this.addRenderableWidget(new AbstractSliderButton(this.width / 2 - 154, 180, 100, 20, Component.empty(), 3.0D / 10.0D) {
            {
                this.updateMessage();
            }

            protected void updateMessage() {
                this.setMessage(Component.translatable("glu_block.size", GluBlockEditScreen.this.size));
            }

            protected void applyValue() {
                GluBlockEditScreen.this.size = Mth.floor(Mth.clampedLerp(0.0D, 10.0D, this.value));
            }
        });
        this.doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_98973_) -> {
            this.onDone();
        }).bounds(this.width / 2 - 4 - 150, 210, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (p_98964_) -> {
            this.onCancel();
        }).bounds(this.width / 2 + 4, 210, 150, 20).build());
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

    public void render(GuiGraphics graphics, int x, int y, float partialTicks) {
        this.renderBackground(graphics);
        graphics.drawString(this.font, JOINT_TYPE_LABEL, this.width / 2 - 153, 45, 10526880);
        this.jointTypeButton.render(graphics, x, y, partialTicks);
        graphics.drawString(this.font, ZONE_LABEL, (this.width / 2 - 153) + 152, 45, 10526880);
        this.zoneButton.render(graphics, x, y, partialTicks);
        graphics.drawString(this.font, HAS_DOOR_LABEL, this.width / 2 - 153, 80, 10526880);
        this.hasDoor.render(graphics, x, y, partialTicks);
        graphics.drawString(this.font, FINAL_STATE_LABEL, this.width / 2 - 153, 115, 10526880);
        this.finalStateEdit.render(graphics, x, y, partialTicks);

        super.render(graphics, x, y, partialTicks);
    }
}