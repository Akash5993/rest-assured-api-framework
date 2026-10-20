package com.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Reads config.properties once and gives every class the same values.
 * PATTERN: Singleton - one shared instance, created on first use.
 */
public class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";

    // Created once by Java when the class is first used (eager Singleton)
    private static final ConfigReader INSTANCE = new ConfigReader();

    private final Properties properties = new Properties();

    // private: nobody outside can call "new ConfigReader()"
    private ConfigReader() {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (stream == null) {
                throw new IllegalStateException(CONFIG_FILE + " not found in src/test/resources");
            }
            properties.load(stream);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + CONFIG_FILE, e);
        }
    }

    public static ConfigReader getInstance() {
        return INSTANCE;
    }

    public String get(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing key '" + key + "' in " + CONFIG_FILE);
        }
        return value.trim();
    }
}
