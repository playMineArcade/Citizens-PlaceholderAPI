/*
 * The contents of this file is free and unencumbered software released into the
 * public domain. For more information, please refer to <http://unlicense.org/>
 */
package me.citizensplaceholderapi.data;

import me.citizensplaceholderapi.CitizensPlaceholderAPI;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigManager {

    private final Map<String, FileConfiguration> configs;
    private final JavaPlugin plugin;

    public ConfigManager(final JavaPlugin plugin) {
        this.configs = new ConcurrentHashMap<>();
        this.plugin = plugin;
    }

    private String correctConfigName(final String configName) {
        final StringBuilder sb = new StringBuilder(configName);
        if (!sb.toString().endsWith(".yml")) {
            return sb.append(".yml").toString();
        } else {
            return sb.toString();
        }
    }

    /**
     * Creates a config file and load it if it doesn't exist. otherwise just load it.
     *
     * @param configName (config .yml name) (example: data.yml)
     * @return FileConfiguration object of the config name
     */
    public FileConfiguration loadConfig(final String configName) {
        final File configFile = new File(this.plugin.getDataFolder(), correctConfigName(configName));
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            this.plugin.saveResource(correctConfigName(configName), false);
        }
        final FileConfiguration configYaml = new YamlConfiguration();
        try {
            configYaml.load(configFile);
        } catch (final FileNotFoundException e) {
            CitizensPlaceholderAPI.getInstance().getSLF4JLogger().error("File Not Found", e);
        } catch (final IOException e) {
            CitizensPlaceholderAPI.getInstance().getSLF4JLogger().error("Read/Write failed", e);
        } catch (final InvalidConfigurationException e) {
            CitizensPlaceholderAPI.getInstance().getSLF4JLogger().error("Corrupted config file", e);
        }
        this.configs.put(configName, configYaml);
        return configYaml;
    }

    /**
     * Creates a config file and load it if it doesn't exist. otherwise just load it.
     *
     * @param configName   (config .yml name) (example: data.yml)
     * @param copyDefaults should we copy default config sections and paths ?
     * @return FileConfiguration object of the config name
     */
    public FileConfiguration loadConfig(final String configName, final boolean copyDefaults) {
        final File configFile = new File(this.plugin.getDataFolder(), correctConfigName(configName));
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            this.plugin.saveResource(correctConfigName(configName), false);
        }
        final FileConfiguration configYaml = new YamlConfiguration();
        try {
            configYaml.load(configFile);
        } catch (final FileNotFoundException e) {
            CitizensPlaceholderAPI.getInstance().getSLF4JLogger().error("File Not Found", e);
        } catch (final IOException e) {
            CitizensPlaceholderAPI.getInstance().getSLF4JLogger().error("Read/Write failed", e);
        } catch (final InvalidConfigurationException e) {
            CitizensPlaceholderAPI.getInstance().getSLF4JLogger().error("Corrupted config file", e);
        }
        if (copyDefaults) {
            configYaml.getDefaults().options().copyDefaults(true);
        }
        this.configs.put(configName, configYaml);
        return configYaml;
    }

    /**
     * @param configName the config name
     * @return cached config from the map
     */
    public FileConfiguration getConfig(final String configName) {
        return this.configs.get(configName);
    }

    /**
     * @param configName the config name
     * @return reloads a config and returns it.
     */
    public FileConfiguration reloadConfig(final String configName) {
        final String correctedName = correctConfigName(configName);
        FileConfiguration configYaml = this.configs.get(correctedName);
        configYaml = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), correctedName));
        return configYaml;
    }

    /**
     * @param configName the config name
     * @return saves the loaded config to your plugin folder and returns it.
     */
    public FileConfiguration saveConfig(final String configName) {
        final String correctedName = correctConfigName(configName);
        final FileConfiguration configYaml = this.configs.get(correctedName);
        try {
            configYaml.save(new File(this.plugin.getDataFolder(), correctedName));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return configYaml;
    }

}
