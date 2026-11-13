package net.ltxprogrammer.changed.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;

public record PaginatedWidget(int pageIndex, AbstractWidget widget) {
}
