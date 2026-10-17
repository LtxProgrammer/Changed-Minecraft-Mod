package net.ltxprogrammer.changed.computers.application;

import net.ltxprogrammer.changed.computers.LexicalPath;

public interface ISaveDialogListener {
    void acceptSavePath(LexicalPath.Absolute path);
}
