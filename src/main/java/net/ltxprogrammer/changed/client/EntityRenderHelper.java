package net.ltxprogrammer.changed.client;

import com.google.common.collect.Queues;
import net.minecraft.Util;
import net.minecraft.world.phys.Vec3;

import java.util.Deque;

public class EntityRenderHelper {
    public static final Deque<Vec3> ENTITY_RENDER_DISPATCHER_ENTITY_MINUS_CAMERA = Util.make(Queues.newArrayDeque(), (stack) -> {
        stack.add(Vec3.ZERO);
    });
}
