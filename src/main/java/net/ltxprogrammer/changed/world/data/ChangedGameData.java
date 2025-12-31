package net.ltxprogrammer.changed.world.data;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class ChangedGameData {
    private final ServerLevel attachedLevel;
    private int loggedFacilityTimes = 0;

    public final List<ActiveFacilityInstance> facilities = new ObjectArrayList<>(2);

    public ChangedGameData(ServerLevel attachedLevel) {
        this.attachedLevel = attachedLevel;
    }

    private static boolean isGzip(PushbackInputStream stream) throws IOException {
        byte[] abyte = new byte[2];
        boolean flag = false;
        int i = stream.read(abyte, 0, 2);
        if (i == 2) {
            int j = (abyte[1] & 255) << 8 | abyte[0] & 255;
            if (j == 35615) {
                flag = true;
            }
        }

        if (i != 0) {
            stream.unread(abyte, 0, i);
        }

        return flag;
    }

    public static CompoundTag loadTagFromDisk(File file) {
        CompoundTag compoundtag;
        try (
                FileInputStream fileinputstream = new FileInputStream(file);
                PushbackInputStream pushbackinputstream = new PushbackInputStream(fileinputstream, 2);
        ) {
            if (isGzip(pushbackinputstream)) {
                compoundtag = NbtIo.readCompressed(pushbackinputstream);
            } else {
                try (DataInputStream datainputstream = new DataInputStream(pushbackinputstream)) {
                    compoundtag = NbtIo.read(datainputstream);
                }
            }

            int i = NbtUtils.getDataVersion(compoundtag, 1343);
            Changed.dataFixer.updateCompoundTag(DataFixTypes.SAVED_DATA, compoundtag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return compoundtag;
    }

    public static void saveTagToDisk(File file, Tag tag) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("data", tag);
        file.getParentFile().mkdirs();

        try {
            NbtIo.writeCompressed(compoundTag, file);
        } catch (IOException ioexception) {
            Changed.LOGGER.error("Could not save data", ioexception);
        }
    }

    public void saveOrUnloadFeatures() {
        for (int i = 0; i < facilities.size(); ++i) {
            var facility = facilities.get(i);
            var header = facility.getHeader();

            if (!header.shouldBeLoaded(attachedLevel.getChunkSource())) {
                facility.saveToFile(attachedLevel.getDataStorage(), ChangedGameData::saveTagToDisk);
                facilities.remove(facility);
                i--;
            }

            else if (facility.isDirty()) {
                facility.saveToFile(attachedLevel.getDataStorage(), ChangedGameData::saveTagToDisk);
            }
        }
    }

    public void trackNewFacility(ActiveFacilityInstance facilityInstance) {
        facilities.add(facilityInstance);
        facilityInstance.saveToFile(attachedLevel.getDataStorage(), ChangedGameData::saveTagToDisk);
    }

    public void loadFeatures() {
        ActiveFacilityInstance.discoverInstances(attachedLevel.getDataStorage()).filter(file -> {
            var resourceName = file.getName().replace(".dat", "");
            return this.facilities.stream()
                    .map(ActiveFacilityInstance::getHeader)
                    .map(ActiveFacilityInstance.Header::getResourceName)
                    .noneMatch(resourceName::equals);
        }).map(file -> {
            ActiveFacilityInstance.Header header = new ActiveFacilityInstance.Header();
            if (!header.readInfoFromName(file.getName()))
                return null;
            if (!header.shouldBeLoaded(attachedLevel.getChunkSource()))
                return null;
            return Pair.of(file, header);
        }).filter(Objects::nonNull).map(pair -> {
            try {
                var facility = ActiveFacilityInstance.CODEC.parse(NbtOps.INSTANCE, loadTagFromDisk(pair.getFirst()).get("data")).getOrThrow(false, error -> {});
                facility.setHeader(pair.getSecond());
                return facility;
            } catch (Exception e) {
                if (loggedFacilityTimes < 20) {
                    Changed.LOGGER.error("Failed to load facility from disk ", e);
                    loggedFacilityTimes++;
                }
            }
            return null;
        }).filter(Objects::nonNull).forEach(facilities::add);
    }

    public void tick(BooleanSupplier hasTimeSupplier) {
        // These functions may be too expensive to run each tick
        this.saveOrUnloadFeatures();
        this.loadFeatures();

        this.facilities.forEach(facility -> facility.tick(attachedLevel));
    }
}
