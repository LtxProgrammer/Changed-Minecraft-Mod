package net.ltxprogrammer.changed.client.gui.computer;

import com.google.common.collect.ImmutableList;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.block.StasisChamber;
import net.ltxprogrammer.changed.block.entity.StasisChamberBlockEntity;
import net.ltxprogrammer.changed.client.gui.ComputerScreen;
import net.ltxprogrammer.changed.client.gui.PaginatedWidget;
import net.ltxprogrammer.changed.client.gui.StasisChamberScreen;
import net.ltxprogrammer.changed.computers.UITheme;
import net.ltxprogrammer.changed.computers.application.StasisChamberControllerApplication;
import net.ltxprogrammer.changed.computers.protocol.NetworkInterface;
import net.ltxprogrammer.changed.entity.ModifiableEntity;
import net.ltxprogrammer.changed.entity.ModificationVector;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.network.packet.ComputerAppClosePacket;
import net.ltxprogrammer.changed.util.SingleRunnable;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.ltxprogrammer.changed.world.inventory.StasisChamberMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ByteTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

public class StasisChamberControllerScreen implements ApplicationScreen {
    public static final ResourceLocation BACKGROUND_SELECTION = Changed.modResource("textures/gui/computer/app_bg/device_selection.png");
    public static final ResourceLocation BACKGROUND_INSPECTING = Changed.modResource("textures/gui/computer/app_bg/stasis_chamber_controller.png");

    public static ResourceLocation getDeviceIconLocation(ResourceLocation icon) {
        return ResourceLocation.fromNamespaceAndPath(
                icon.getNamespace(),
                "textures/gui/computer/devices/%s.png".formatted(icon.getPath())
        );
    }

    static Function<Button.Builder, Button> deviceListItemButton(Supplier<UITheme> themeSupplier, ResourceLocation deviceIcon) {
        return ApplicationScreen.listItemButtonStatic(themeSupplier,
                getDeviceIconLocation(deviceIcon), 0, 0, 2, 2, 16, 16, 16, 16, 16);
    }

    protected final StasisChamberControllerApplication application;
    protected final ComputerScreen screen;

    protected final SingleRunnable appCloser;

    protected @Nullable NetworkInterface.Address selectedChamber;
    private StasisChamberScreen.ButtonScope buttonScope = StasisChamberScreen.ButtonScope.DEFAULT;
    private StasisChamberScreen.ModifyType modifyType = StasisChamberScreen.ModifyType.INTERFACE;

    protected boolean initialized = false;
    protected final List<Button> deviceButtons = new ArrayList<>();
    protected EditBox chamberNameWidget;
    protected Button inspectButton;

    protected Button programsButton;
    protected Button modifyButton;
    protected Button openDoorButton;
    protected Button closeDoorButton;

    protected final List<AbstractWidget> programWidgets = new ArrayList<>();
    protected AbstractSliderButton waitDurationSlider;
    protected ModifiableEntity lastSeenEntityForModification = null;
    protected final List<PaginatedWidget> modificationWidgets = new ArrayList<>();
    protected int modificationsPageIndex = 0;
    protected int modificationsPageCount = 1;
    protected Button genderButton;

    protected int desktopLeft;
    protected int desktopTop;
    protected int desktopWidth;
    protected int desktopHeight;

    public StasisChamberControllerScreen(StasisChamberControllerApplication application, ComputerScreen screen) {
        this.application = application;
        this.screen = screen;

        this.appCloser = new SingleRunnable(() -> {
            Changed.PACKET_HANDLER.sendToServer(
                    ComputerAppClosePacket.closeApplication(application.getType()));
        });
    }

    @Override
    public ComputerScreen getScreen() {
        return screen;
    }
    
    protected void checkButtonStates() {
        if (!initialized)
            return;

        if (application.inspectingChamber == null) {
            buttonScope = StasisChamberScreen.ButtonScope.DEFAULT;
            programsButton.visible = false;
            modifyButton.visible = false;
            openDoorButton.visible = false;
            closeDoorButton.visible = false;
        } else {
            programsButton.visible = true;
            modifyButton.visible = true;
            openDoorButton.visible = buttonScope == StasisChamberScreen.ButtonScope.DEFAULT;
            closeDoorButton.visible = buttonScope == StasisChamberScreen.ButtonScope.DEFAULT;
        }

        boolean open = this.isChamberOpen();
        float fillLevel = this.getChamberFillLevel(Minecraft.getInstance().getPartialTick());
        var fillFluid = this.getChamberFillFluid();
        var chamberedEntity = this.getChamberedEntity();

        programsButton.active = true;
        modifyButton.active = true;
        openDoorButton.active = !open && fillLevel <= 0f;
        closeDoorButton.active = open;

        modifyType = this.getChamberedLatex().map(entity -> {
            if (entity.getChangedEntity() instanceof ModifiableEntity modifiable) {
                if (this.lastSeenEntityForModification != modifiable) {
                    modificationWidgets.forEach(entry -> this.removeWidget(entry.widget()));
                    modificationWidgets.clear();
                    this.createModificationWidgets(modifiable);
                }
                return StasisChamberScreen.ModifyType.INTERFACE;
            }
            else if (ChangedTransfurVariants.Gendered.getOpposite(entity.getSelfVariant()).isPresent())
                return StasisChamberScreen.ModifyType.GENDERED;
            return StasisChamberScreen.ModifyType.NONE;
        }).orElse(StasisChamberScreen.ModifyType.NONE);

        programWidgets.forEach(button -> button.visible = buttonScope == StasisChamberScreen.ButtonScope.PROGRAMS);
        genderButton.visible = buttonScope == StasisChamberScreen.ButtonScope.MODIFICATIONS && modifyType == StasisChamberScreen.ModifyType.GENDERED;
        modificationWidgets.forEach(entry -> {
            entry.widget().visible = (buttonScope == StasisChamberScreen.ButtonScope.MODIFICATIONS && modifyType == StasisChamberScreen.ModifyType.INTERFACE) &&
                    modificationsPageIndex == entry.pageIndex();
        });
    }

    protected void createModificationWidgets(ModifiableEntity entity) {
        this.lastSeenEntityForModification = entity;

        int leftMargin = this.desktopLeft + 7;
        int topMargin = this.desktopTop + 7 + 23;
        int buttonWidth = 98;
        int buttonHeight = 20;
        int buttonHeightSpacing = buttonHeight + 4;
        int buttonWidthSpacing = buttonWidth + 4;
        int buttonWidthLong = buttonWidth + buttonWidthSpacing;

        Map<String, ModificationVector.EnumVector> enumVectors = new HashMap<>();
        Map<String, ModificationVector.LinearVector> linearVectors = new HashMap<>();
        Map<String, ModificationVector.BooleanVector> booleanVectors = new HashMap<>();

        entity.getModificationVectors().forEach((key, vector) -> {
            if (vector instanceof ModificationVector.EnumVector enumVector)
                enumVectors.put(key, enumVector);
            if (vector instanceof ModificationVector.LinearVector linearVector)
                linearVectors.put(key, linearVector);
            if (vector instanceof ModificationVector.BooleanVector booleanVector)
                booleanVectors.put(key, booleanVector);
        });

        final AtomicInteger widgetIndex = new AtomicInteger(2);
        final AtomicInteger pageIndex = new AtomicInteger(0);

        Runnable createNewPage = () -> {
            widgetIndex.set(2);
            final int nextPage = pageIndex.get() + 1;
            final int prevPage = pageIndex.get();

            modificationWidgets.add(new PaginatedWidget(prevPage, this.addRenderableWidget(
                    Button.builder(Component.literal(">"), button -> modificationsPageIndex = nextPage)
                            .bounds(leftMargin + buttonWidthSpacing + buttonWidth - buttonHeight,
                                    topMargin + buttonHeightSpacing * 5 + (buttonHeight / 2),
                                    buttonHeight, buttonHeight).build())));
            modificationWidgets.add(new PaginatedWidget(nextPage, this.addRenderableWidget(
                    Button.builder(Component.literal("<"), button -> modificationsPageIndex = prevPage)
                            .bounds(leftMargin,
                                    topMargin + buttonHeightSpacing * 5 + (buttonHeight / 2),
                                    buttonHeight, buttonHeight).build())));

            pageIndex.incrementAndGet();
        };

        for (var entry : enumVectors.entrySet()) {
            if (widgetIndex.get() >= 10)
                createNewPage.run();

            final var key = entry.getKey();
            final var vector = entry.getValue();
            var tooltip = vector.getTooltipText();
            modificationWidgets.add(new PaginatedWidget(pageIndex.get(), this.addRenderableWidget(Button.builder(vector.getDisplayText(), button -> {
                        vector.cycleForward();
                        application.requestInputModification(key, vector.writeAsTag(), application.inspectingChamberAddress);
                        button.setMessage(vector.getDisplayText());
                    })
                    .bounds(
                            leftMargin + (widgetIndex.get() % 2 == 1 ? buttonWidthSpacing : 0),
                            topMargin + buttonHeightSpacing * (widgetIndex.get() / 2),
                            buttonWidth, buttonHeight)
                    .tooltip(tooltip == null ? null : Tooltip.create(tooltip)).build(ApplicationScreen.textButton(screen::getTheme)))));

            widgetIndex.addAndGet(1);
        }

        for (var entry : linearVectors.entrySet()) {
            widgetIndex.addAndGet(widgetIndex.get() % 2);
            if (widgetIndex.get() >= 10)
                createNewPage.run();

            final var key = entry.getKey();
            final var vector = entry.getValue();
            var tooltip = vector.getTooltipText();

            var slider = new AbstractSliderButton(
                    leftMargin,
                    topMargin + buttonHeightSpacing * (widgetIndex.get() / 2),
                    buttonWidthLong, buttonHeight, vector.getDisplayText(), vector.getValue()) {
                @Override
                protected void updateMessage() {
                    setMessage(vector.getDisplayText());
                }

                @Override
                protected void applyValue() {
                    vector.acceptValue(this.value);
                    application.requestInputModification(key, vector.writeAsTag(), application.inspectingChamberAddress);
                }

                @Override
                public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                    if (!(this.isHoveredOrFocused() && screen.getMinecraft().mouseHandler.isLeftPressed())) {
                        double last = this.value;
                        this.value = vector.getValue();
                        if (last != this.value)
                            updateMessage();
                    }
                    super.renderWidget(graphics, mouseX, mouseY, partialTicks);
                }
            };

            slider.setTooltip(tooltip == null ? null : Tooltip.create(tooltip));

            modificationWidgets.add(new PaginatedWidget(pageIndex.get(), this.addRenderableWidget(slider)));

            widgetIndex.addAndGet(2);
        }

        for (var entry : booleanVectors.entrySet()) {
            widgetIndex.addAndGet(widgetIndex.get() % 2);
            if (widgetIndex.get() >= 10)
                createNewPage.run();

            final var key = entry.getKey();
            final var vector = entry.getValue();
            var tooltip = vector.getTooltipText();

            var checkbox = new Checkbox(
                    leftMargin,
                    topMargin + buttonHeightSpacing * (widgetIndex.get() / 2),
                    buttonWidthLong, buttonHeight, vector.getDisplayText(), vector.getState()) {
                @Override
                public void onPress() {
                    super.onPress();
                    vector.acceptState(this.selected());
                    application.requestInputModification(key, vector.writeAsTag(), application.inspectingChamberAddress);
                }

                @Override
                public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                    this.selected = vector.getState();
                    super.renderWidget(graphics, mouseX, mouseY, partialTicks);
                }
            };

            checkbox.setTooltip(tooltip == null ? null : Tooltip.create(tooltip));

            modificationWidgets.add(new PaginatedWidget(pageIndex.get(), this.addRenderableWidget(checkbox)));

            widgetIndex.addAndGet(2);
        }
    }

    protected void updateChamberActionButtons(NetworkInterface.Address chamber) {
        if (!initialized)
            return;

        selectedChamber = chamber;

        this.deviceButtons.forEach(button -> {
            button.visible = application.inspectingChamber == null;
        });
        this.chamberNameWidget.visible = chamber != null && application.inspectingChamber == null;
        this.inspectButton.visible = chamber != null && application.inspectingChamber == null;

        if (chamber == null)
            return;

        var info = application.reachableDevices.get(chamber);
        if (info == null) {
            this.chamberNameWidget.visible = false;
            this.inspectButton.visible = false;
            return;
        }

        var doorName = info.deviceName().getString();
        if (!this.chamberNameWidget.isFocused()) {
            this.chamberNameWidget.setResponder(null);
            this.chamberNameWidget.setValue(doorName);
            this.chamberNameWidget.setResponder(text -> {
                if (text.equals(doorName)) {
                    // Revert name to translatable component
                } else {
                    // Set name to literal component
                }
            });
        }
    }

    @Override
    public void initialize(int desktopLeft, int desktopTop, int desktopWidth, int desktopHeight) {
        screen.clearApplicationWidgets();

        this.desktopLeft = desktopLeft;
        this.desktopTop = desktopTop;
        this.desktopWidth = desktopWidth;
        this.desktopHeight = desktopHeight;

        int x = desktopLeft + 4;
        int y = desktopTop + 4;
        AtomicInteger yOffset = new AtomicInteger(0);

        ComputerMenu menu = screen.getMenu();
        BlockPos computerPos = menu.computer.getBlockPos();

        screen.addApplicationWidget(ApplicationScreen.shadowlessString(x, y, desktopWidth - 8, 20,
                application.getType().getDisplayName(), screen.getFont())
                .alignCenter().setColor(0x404040));

        screen.addApplicationWidget(Button.builder(COMPONENT_EXIT, (self) -> {
            if (application.inspectingChamber != null) {
                application.inspectingChamber = null;
                application.inspectingChamberAddress = null;
                application.inspectDevice(application.inspectingChamberAddress);
            } else {
                this.appCloser.run();
            }
        }).bounds(x, y + yOffset.getAndAdd(23), 20, 20)
                .tooltip(Tooltip.create(COMPONENT_EXIT))
                .build(ApplicationScreen.iconButton(screen::getTheme, 200, 0)));

        deviceButtons.clear();
        application.reachableDevices.entrySet().stream().sorted(
                Comparator.comparingInt(left -> left.getValue().position().distManhattan(computerPos))
        ).forEach(entry -> {
            final var logicalDevice = entry.getKey();
            final var info = entry.getValue();
            deviceButtons.add(screen.addApplicationWidget(Button.builder(info.deviceName(), (self) -> {
                        this.updateChamberActionButtons(logicalDevice);
                    }).bounds(x, y + yOffset.getAndAdd(23), 136, 20)
                    .tooltip(Tooltip.create(info.deviceName()))
                    .build(deviceListItemButton(screen::getTheme, info.icon()))));
        });

        int operationsPaneX = x + 141;
        int operationsPaneY = y + 23;
        int operationsPaneWidth = desktopWidth - (operationsPaneX - x) - 8;
        int operationsPaneHalfWidth = (operationsPaneWidth / 2) - 2;

        this.chamberNameWidget = screen.addApplicationWidget(new EditBox(screen.getFont(), operationsPaneX, operationsPaneY, operationsPaneWidth, 20, Component.literal("Door Name")));
        this.chamberNameWidget.visible = false;

        this.inspectButton = screen.addApplicationWidget(Button.builder(Component.translatable("application.changed.stasis_chamber_controller.inspect"), (self) -> {
                    if (this.selectedChamber == null)
                        return;
                    var level = screen.getMenu().getPlayer().level();
                    var position = selectedChamber.blockPosOrID().left();
                    if (position.isEmpty())
                        return;
                    var blockEntity = level.getBlockEntity(position.get(), ChangedBlockEntities.STASIS_CHAMBER.get());
                    if (blockEntity.isEmpty())
                        return;
                    application.inspectingChamber = blockEntity.get();
                    application.inspectingChamberAddress = selectedChamber;
                    application.inspectDevice(application.inspectingChamberAddress);

                    this.updateChamberActionButtons(this.selectedChamber);
                }).bounds(operationsPaneX, operationsPaneY + 23, operationsPaneWidth, 20)
                .build(ApplicationScreen.textButton(screen::getTheme)));
        this.inspectButton.visible = false;

        int leftMargin = this.desktopLeft + 7;
        int topMargin = this.desktopTop + 7 + 23;
        int buttonWidth = 98;
        int buttonHeight = 20;
        int buttonHeightSpacing = buttonHeight + 4;
        int buttonWidthSpacing = buttonWidth + 4;
        int buttonWidthLong = buttonWidth + buttonWidthSpacing;

        programsButton = this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.programs"), button -> {
            buttonScope = buttonScope.toggleScope(StasisChamberScreen.ButtonScope.PROGRAMS);
            this.checkButtonStates();
        }).bounds(leftMargin, topMargin, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.programs.tooltip"))).build(ApplicationScreen.textButton(screen::getTheme)));

        modifyButton = this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.modify"), button -> {
            buttonScope = buttonScope.toggleScope(StasisChamberScreen.ButtonScope.MODIFICATIONS);
            this.checkButtonStates();
        }).bounds(leftMargin + buttonWidthSpacing, topMargin, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.modify.tooltip"))).build(ApplicationScreen.textButton(screen::getTheme)));

        openDoorButton = this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.open"), button -> {
            application.requestSimpleCommand(StasisChamberMenu.Command.OPEN_DOOR, application.inspectingChamberAddress);
        }).bounds(leftMargin, topMargin + buttonHeightSpacing, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.open.tooltip"))).build(ApplicationScreen.textButton(screen::getTheme)));

        closeDoorButton = this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.close"), button -> {
            application.requestSimpleCommand(StasisChamberMenu.Command.CLOSE_DOOR, application.inspectingChamberAddress);
        }).bounds(leftMargin + buttonWidthSpacing, topMargin + buttonHeightSpacing, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.close.tooltip"))).build(ApplicationScreen.textButton(screen::getTheme)));

        programWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.program.transfur"), button -> {
            application.requestInputProgram(StasisChamberBlockEntity.Program.TRANSFUR, application.inspectingChamberAddress);
        }).bounds(leftMargin, topMargin + buttonHeightSpacing, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.program.transfur.tooltip"))).build(ApplicationScreen.textButton(screen::getTheme))));

        programWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.program.modify"), button -> {
            application.requestInputProgram(StasisChamberBlockEntity.Program.MODIFY, application.inspectingChamberAddress);
        }).bounds(leftMargin + buttonWidthSpacing, topMargin + buttonHeightSpacing, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.program.modify.tooltip"))).build(ApplicationScreen.textButton(screen::getTheme))));

        programWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.program.capture_next"), button -> {
            application.requestInputProgram(StasisChamberBlockEntity.Program.CAPTURE_NEXT_ENTITY, application.inspectingChamberAddress);
        }).bounds(leftMargin, topMargin + buttonHeightSpacing * 2, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.program.capture_next.tooltip"))).build(ApplicationScreen.textButton(screen::getTheme))));

        programWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.program.toggle_stasis"), button -> {
            application.requestInputProgram(StasisChamberBlockEntity.Program.TOGGLE_STASIS, application.inspectingChamberAddress);
        }).bounds(leftMargin + buttonWidthSpacing, topMargin + buttonHeightSpacing * 2, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.program.toggle_stasis.tooltip"))).build(ApplicationScreen.textButton(screen::getTheme))));

        programWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.program.create_entity"), button -> {
            application.requestInputProgram(StasisChamberBlockEntity.Program.CREATE_ENTITY, application.inspectingChamberAddress);
        }).bounds(leftMargin, topMargin + buttonHeightSpacing * 3, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.program.create_entity.tooltip"))).build(ApplicationScreen.textButton(screen::getTheme))));

        programWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.program.discard_entity"), button -> {
            application.requestInputProgram(StasisChamberBlockEntity.Program.DISCARD_ENTITY, application.inspectingChamberAddress);
        }).bounds(leftMargin + buttonWidthSpacing, topMargin + buttonHeightSpacing * 3, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.program.discard_entity.tooltip"))).build(ApplicationScreen.textButton(screen::getTheme))));

        programWidgets.add(this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.program.abort"), button -> {
            application.requestInputProgram(StasisChamberBlockEntity.Program.ABORT, application.inspectingChamberAddress);
        }).bounds(leftMargin, topMargin + buttonHeightSpacing * 4, buttonWidth, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.program.abort.tooltip"))).build(ApplicationScreen.textButton(screen::getTheme))));

        waitDurationSlider = this.addRenderableWidget(new AbstractSliderButton(leftMargin, topMargin + buttonHeightSpacing * 5, buttonWidthLong, buttonHeight, Component.translatable("changed.stasis.program.wait_duration", this.getWaitDurationSeconds(this.getWaitDurationPercent())), this.getWaitDurationPercent()) {
            @Override
            protected void updateMessage() {
                setMessage(Component.translatable("changed.stasis.program.wait_duration", StasisChamberControllerScreen.this.getWaitDurationSeconds(this.value)));
            }

            @Override
            protected void applyValue() {
                application.requestSetWaitDuration((int)(this.value * StasisChamberMenu.MAX_WAIT_DURATION), application.inspectingChamberAddress);
            }

            @Override
            public void renderWidget(GuiGraphics graphics, int p_93677_, int p_93678_, float p_93679_) {
                if (!(this.isHoveredOrFocused() && screen.getMinecraft().mouseHandler.isLeftPressed())) {
                    double last = this.value;
                    this.value = StasisChamberControllerScreen.this.getWaitDurationPercent();
                    if (last != this.value)
                        updateMessage();
                }
                super.renderWidget(graphics, p_93677_, p_93678_, p_93679_);
            }
        });
        programWidgets.add(waitDurationSlider);

        genderButton = this.addRenderableWidget(Button.builder(Component.translatable("changed.stasis.modify.gender"), button -> {
            application.requestInputModification("gender", ByteTag.valueOf(true), application.inspectingChamberAddress);
        }).bounds(leftMargin, topMargin + buttonHeightSpacing, buttonWidthLong, buttonHeight).tooltip(Tooltip.create(Component.translatable("changed.stasis.modify.gender.tooltip"))).build());

        initialized = true;
        checkButtonStates();
    }

    @Override
    public void tick(int desktopLeft, int desktopTop, int desktopWidth, int desktopHeight) {
        if (application.inspectingChamber != null && application.inspectingChamber.isRemoved()) {
            application.inspectingChamber = null;
            application.inspectingChamberAddress = null;
            application.inspectDevice(application.inspectingChamberAddress);
        }

        if (application.devicesDirty) {
            application.devicesDirty = false;
            initialize(desktopLeft, desktopTop, desktopWidth, desktopHeight);
        }
        
        this.checkButtonStates();
        this.updateChamberActionButtons(this.selectedChamber);
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            if (application.inspectingChamber != null) {
                application.inspectingChamber = null;
                application.inspectingChamberAddress = null;
                application.inspectDevice(application.inspectingChamberAddress);
            } else {
                this.appCloser.run();
            }
            return true;
        }

        return ApplicationScreen.super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics graphics, int cursorX, int cursorY, float partialTicks) {
        if (application.inspectingChamber != null) {
            graphics.blit(BACKGROUND_INSPECTING, this.desktopLeft, this.desktopTop, 0, 0,
                    this.desktopWidth, this.desktopHeight, this.desktopWidth, this.desktopHeight);

            final int textLeftMargin = this.desktopLeft + 214;
            final int textRightMargin = this.desktopLeft + 316;
            final int textTopMargin = this.desktopTop + 6 + 23;
            final int commandsTopMargin = this.desktopTop + 24 + 23;
            final int commandsBottomMargin = this.desktopTop + 136 + 23;

            if (buttonScope == StasisChamberScreen.ButtonScope.PROGRAMS) {
                this.getChamberedEntity().ifPresent(entity -> {
                    graphics.drawCenteredString(screen.getFont(), entity.getDisplayName(), (textLeftMargin + textRightMargin) / 2, textTopMargin, 0xFFFFFF);
                });

                AtomicInteger yOffset = new AtomicInteger(0);
                this.getCurrentCommand().ifPresent(command -> {
                    graphics.drawString(screen.getFont(), command.getActiveDisplayText(), textLeftMargin, commandsTopMargin + yOffset.getAndAdd(12), 0x20FF20);
                });
                this.getScheduledCommands().forEach(command -> {
                    graphics.drawString(screen.getFont(), command.getDisplayText(), textLeftMargin, commandsTopMargin + yOffset.getAndAdd(12), 0xFFFFFF);
                });
            } else {
                this.getChamberedEntity().ifPresent(entity -> {
                    graphics.drawCenteredString(screen.getFont(), entity.getDisplayName(), (textLeftMargin + textRightMargin) / 2, textTopMargin, 0xFFFFFF);

                    int entityX = (textLeftMargin + textRightMargin) / 2;
                    int entityY = commandsBottomMargin - 5;
                    InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, entityX, entityY, 40,
                            (float)(this.desktopLeft) - cursorX + entityX,
                            (float)(this.desktopTop) - cursorY + (entityY - 100),
                            entity);
                });
            }
        } else {
            graphics.blit(BACKGROUND_SELECTION, this.desktopLeft, this.desktopTop, 0, 0,
                    this.desktopWidth, this.desktopHeight, this.desktopWidth, this.desktopHeight);
        }
    }

    public double getWaitDurationPercent() {
        if (application.inspectingChamber == null)
            return 0.0;
        return ((double)application.inspectingChamber.getWaitDuration()) / (double) StasisChamberMenu.MAX_WAIT_DURATION; // Ticks to percent
    }

    public String getWaitDurationSeconds(double percent) {
        return String.format("%.2f", Math.max(Math.round(percent * StasisChamberMenu.MAX_WAIT_DURATION), 0) * 0.05);
    }

    public float getFluidLevel(float partialTick) {
        if (application.inspectingChamber == null)
            return 0.0f;
        return application.inspectingChamber.getFluidLevel(partialTick);
    }

    public Optional<LivingEntity> getChamberedEntity() {
        if (application.inspectingChamber == null)
            return Optional.empty();
        return application.inspectingChamber.getChamberedEntity();
    }

    public Optional<IAbstractChangedEntity> getChamberedLatex() {
        if (application.inspectingChamber == null)
            return Optional.empty();
        return application.inspectingChamber.getChamberedLatex();
    }

    public Optional<StasisChamberBlockEntity.ScheduledCommand> getCurrentCommand() {
        if (application.inspectingChamber == null)
            return Optional.empty();
        return application.inspectingChamber.getCurrentCommand();
    }

    public ImmutableList<StasisChamberBlockEntity.ScheduledCommand> getScheduledCommands() {
        if (application.inspectingChamber == null)
            return ImmutableList.of();
        return application.inspectingChamber.getScheduledCommands();
    }

    public boolean isChamberOpen() {
        if (application.inspectingChamber == null)
            return false;
        return application.inspectingChamber.getBlockState().getValue(StasisChamber.OPEN);
    }

    public float getChamberFillLevel(float partialTick) {
        if (application.inspectingChamber == null)
            return 0f;
        return application.inspectingChamber.getFluidLevel(partialTick);
    }

    public Optional<Fluid> getChamberFillFluid() {
        if (application.inspectingChamber == null)
            return Optional.empty();
        return application.inspectingChamber.getFluidType();
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double p_97755_, double p_97756_) {
        if (buttonScope == StasisChamberScreen.ButtonScope.DEFAULT)
            return ApplicationScreen.super.mouseDragged(x, y, button, p_97755_, p_97756_);
        else {
            return screen.getFocused() != null && screen.isDragging() && button == 0 ? screen.getFocused().mouseDragged(x, y, button, p_97755_, p_97756_) : false;
        }
    }
}
