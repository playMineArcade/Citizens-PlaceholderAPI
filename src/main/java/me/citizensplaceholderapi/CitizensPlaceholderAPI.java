/*
 * The contents of this file is free and unencumbered software released into the
 * public domain. For more information, please refer to <http://unlicense.org/>
 */
package me.citizensplaceholderapi;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import me.citizensplaceholderapi.commands.CitizensPlaceholderAPICommand;
import me.citizensplaceholderapi.data.ConfigManager;
import me.citizensplaceholderapi.data.NPCDataStorage;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class CitizensPlaceholderAPI extends JavaPlugin implements Listener {

    private ConfigManager configManager;
    private FileConfiguration dataConfig;
    private NPCDataStorage npcDataStorage;
    private CitizensPlaceholderAPICommand command;
    private TaskChainFactory taskChainFactory;
    private static CitizensPlaceholderAPI instance;

    public <T> TaskChain<T> newChain() {
        return getTaskChainFactory().newChain();
    }

    public <T> TaskChain<T> newSharedChain(final String name) {
        return getTaskChainFactory().newSharedChain(name);
    }

    public boolean removeNPConEvent;

    @Override
    public void onEnable() {
        this.taskChainFactory = BukkitTaskChainFactory.create(this);
        this.configManager = new ConfigManager(this);
        this.dataConfig = this.configManager.loadConfig("data.yml");
        this.npcDataStorage = new NPCDataStorage(this);
        this.npcDataStorage.load();
        this.command = new CitizensPlaceholderAPICommand(this);
        this.removeNPConEvent = true;
        instance = this;
        getCommand("citizensplaceholderapi").setExecutor(this.command);
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("Enabled");
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    @EventHandler
    public void onNPCRemove(final NPCRemoveEvent e) {
        if (this.removeNPConEvent) {
            this.npcDataStorage.deleteNPC(e.getNPC().getUniqueId());
        }
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public FileConfiguration getDataConfig() {
        return this.dataConfig;
    }

    public NPCDataStorage getNPCDataStorage() {
        return this.npcDataStorage;
    }

    public CitizensPlaceholderAPICommand getCommand() {
        return this.command;
    }

    public void setCommand(final CitizensPlaceholderAPICommand command) {
        this.command = command;
    }

    public static CitizensPlaceholderAPI getInstance() {
        return instance;
    }

    public TaskChainFactory getTaskChainFactory() {
        return this.taskChainFactory;
    }

    public void setTaskChainFactory(final TaskChainFactory taskChainFactory) {
        this.taskChainFactory = taskChainFactory;
    }


}
