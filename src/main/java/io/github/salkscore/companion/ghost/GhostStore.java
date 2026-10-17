package io.github.salkscore.companion.ghost;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GhostStore {

    private static final Logger LOGGER = LoggerFactory.getLogger("parkour-companion");
    private static final int VERSION = 1;

    private final Path root = FabricLoader.getInstance().getConfigDir()
            .resolve("parkour-companion")
            .resolve("ghosts");

    public GhostRecording load(String server, String course) {
        Path file = file(server, course);
        if (!Files.exists(file)) return null;

        try (DataInputStream in = new DataInputStream(
                new GZIPInputStream(Files.newInputStream(file)))) {
            if (in.readInt() != VERSION) return null;
            long time = in.readLong();

            int count = in.readInt();
            List<Long> checkpoints = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                checkpoints.add(in.readLong());
            }

            int size = in.readInt();
            List<GhostFrame> frames = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                frames.add(new GhostFrame(in.readDouble(), in.readDouble(), in.readDouble(),
                        in.readFloat(), in.readFloat(), in.readFloat(), in.readBoolean()));
            }
            return new GhostRecording(time, checkpoints, frames);
        } catch (IOException e) {
            LOGGER.warn("Fantôme illisible : {}", file, e);
            return null;
        }
    }

    public void save(String server, String course, GhostRecording recording) {
        Path file = file(server, course);
        try {
            Files.createDirectories(file.getParent());
            try (DataOutputStream out = new DataOutputStream(
                    new GZIPOutputStream(Files.newOutputStream(file)))) {
                out.writeInt(VERSION);
                out.writeLong(recording.time());
                out.writeInt(recording.checkpoints().size());
                for (long time : recording.checkpoints()) {
                    out.writeLong(time);
                }
                out.writeInt(recording.frames().size());
                for (GhostFrame frame : recording.frames()) {
                    write(out, frame);
                }
            }
        } catch (IOException e) {
            LOGGER.warn("Impossible d'enregistrer le fantôme : {}", file, e);
        }
    }

    private static void write(DataOutputStream out, GhostFrame frame) throws IOException {
        out.writeDouble(frame.x());
        out.writeDouble(frame.y());
        out.writeDouble(frame.z());
        out.writeFloat(frame.yaw());
        out.writeFloat(frame.pitch());
        out.writeFloat(frame.head());
        out.writeBoolean(frame.crouching());
    }

    private Path file(String server, String course) {
        return root.resolve(clean(server)).resolve(clean(course) + ".ghost");
    }

    private static String clean(String raw) {
        return raw.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]", "_");
    }
}
