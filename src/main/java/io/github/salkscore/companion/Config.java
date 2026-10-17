package io.github.salkscore.companion;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Config {

    private static final Logger LOGGER = LoggerFactory.getLogger("parkour-companion");
    private static final Path FILE =
            FabricLoader.getInstance().getConfigDir().resolve("parkour-companion.properties");

    public boolean ghost = true;
    public boolean hud = true;
    public boolean markers = true;

    public static Config load() {
        Config config = new Config();
        if (!Files.exists(FILE)) {
            config.save();
            return config;
        }

        Properties props = new Properties();
        try (Reader reader = Files.newBufferedReader(FILE)) {
            props.load(reader);
        } catch (IOException e) {
            LOGGER.warn("Config illisible, valeurs par défaut utilisées", e);
        }
        config.ghost = Boolean.parseBoolean(props.getProperty("ghost", "true"));
        config.hud = Boolean.parseBoolean(props.getProperty("hud", "true"));
        config.markers = Boolean.parseBoolean(props.getProperty("markers", "true"));
        return config;
    }

    public void save() {
        Properties props = new Properties();
        props.setProperty("ghost", String.valueOf(ghost));
        props.setProperty("hud", String.valueOf(hud));
        props.setProperty("markers", String.valueOf(markers));
        try (Writer writer = Files.newBufferedWriter(FILE)) {
            props.store(writer, "Parkour Companion");
        } catch (IOException e) {
            LOGGER.warn("Impossible d'écrire la config", e);
        }
    }
}
