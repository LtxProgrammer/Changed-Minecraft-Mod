package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.Changed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class ChangedFunctionTags {
    public static final TargettedFunctionTag ON_TRANSFUR = createTargettedTag("on_transfur");

    public static class FunctionTag {
        public final ResourceLocation tagId;

        public FunctionTag(ResourceLocation tagId) {
            this.tagId = tagId;
        }

        public void execute(@Nullable MinecraftServer server) {
            if (server == null) return;
            var functions = server.getFunctions();

            functions.getTag(tagId).getValues().forEach(fn -> {
                functions.execute(fn, functions.getGameLoopSender());
            });
        }
    }

    public static class TargettedFunctionTag {
        public final ResourceLocation tagId;

        public TargettedFunctionTag(ResourceLocation tagId) {
            this.tagId = tagId;
        }

        public void execute(@Nullable MinecraftServer server, Entity source) {
            if (server == null) return;
            var functions = server.getFunctions();

            functions.getTag(tagId).getValues().forEach(fn -> {
                functions.execute(fn, functions.getGameLoopSender().withEntity(source));
            });
        }
    }

    private static TargettedFunctionTag createTargettedTag(String name) {
        return new TargettedFunctionTag(Changed.modResource(name));
    }
}
