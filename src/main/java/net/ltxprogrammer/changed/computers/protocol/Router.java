package net.ltxprogrammer.changed.computers.protocol;

import net.minecraft.server.level.ServerLevel;

public interface Router extends NetworkInterface {
    // This router becomes responsible for sending `frame`
    void handoffUnicastFrame(ServerLevel level, Address origin, Address immediateSource, Address destination, Frame frame, int ttl);
    void handoffBroadcastFrame(ServerLevel level, Address origin, Address immediateSource, Frame frame, int ttl);
}
