package net.ltxprogrammer.changed.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OnlineResource implements Resource {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private final String sourceName;
    private final ResourceLocation location;
    private final URI onlineLocation;

    public OnlineResource(String p_10929_, ResourceLocation p_10930_, URI onlineLocation) {
        this.sourceName = p_10929_;
        this.location = p_10930_;
        this.onlineLocation = onlineLocation;
    }

    public static Resource of(String s, ResourceLocation textureLocation, URI uri) {
        return new OnlineResource(s, textureLocation, uri);
    }

    @Override
    public ResourceLocation getLocation() {
        return location;
    }

    @Override
    public InputStream getInputStream() {
        HttpRequest request = HttpRequest.newBuilder(onlineLocation).GET().build();

        try {
            return CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream()).body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean hasMetadata() {
        return false;
    }

    @Nullable
    @Override
    public <T> T getMetadata(MetadataSectionSerializer<T> p_10725_) {
        return null;
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }

    @Override
    public void close() throws IOException {}
}
