package net.ltxprogrammer.changed.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;

public class TextUtil {
    public record ScrollInfo(boolean canScrollUp, boolean canScrollDown) {}

    public static ScrollInfo drawScrollWrapped(Font font, PoseStack poseStack, String text, float posX, float posY, int color,
                                               int width, int height, int lineOffset) {
        int line = 0;
        int drawnHeight = 0;
        while (true) {
            String next = "";
            if (font.width(text) > width) {
                String tmpStr = "";
                boolean first = true;
                boolean charSplit = false;
                for (String substr : text.split(" ")) {
                    tmpStr += !first ? " " + substr : substr;
                    if (font.width(tmpStr) > width) {
                        if (first) {
                            tmpStr = "";
                            charSplit = true;
                        }

                        else {
                            tmpStr = tmpStr.substring(0, tmpStr.length() - (substr.length() + 1));
                        }

                        break;
                    }

                    first = false;
                }

                if (charSplit) {
                    first = true;
                    for (char c : text.toCharArray()) {
                        tmpStr += c;
                        if (font.width(tmpStr) > width) {
                            if (first) {
                                return new ScrollInfo(lineOffset > 0, false);
                            }

                            else {
                                tmpStr = tmpStr.substring(0, tmpStr.length() - 1);
                            }

                            break;
                        }

                        first = false;
                    }
                }

                int consumeLength = tmpStr.length() + (charSplit ? 0 : 1);
                if (consumeLength > text.length())
                    next = "";
                else
                    next = text.substring(consumeLength);
                text = tmpStr;
            }

            if (line >= lineOffset) {
                font.draw(poseStack, text, posX, posY, color);
                posY += font.lineHeight;
                drawnHeight += font.lineHeight;
            }

            if (drawnHeight > height - font.lineHeight || next.isEmpty()) {
                return new ScrollInfo(lineOffset > 0, !next.isEmpty());
            }

            text = next;
            line++;
        }
    }
}
