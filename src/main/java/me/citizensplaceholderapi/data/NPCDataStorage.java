/*
 * The contents of this file is free and unencumbered software released into the
 * public domain. For more information, please refer to <http://unlicense.org/>
 */
package me.citizensplaceholderapi.data;

import co.aikar.taskchain.TaskChain;
import com.google.common.collect.Lists;
import me.citizensplaceholderapi.CitizensPlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NPCDataStorage {

    private final CitizensPlaceholderAPI plugin;
    private final Map<UUID, NPCDataHandler> npcStorage;
    private final List<String> namePlaceholders;
    private final List<String> skinPlaceholders;
    private final ConfigurationSection mainSection;
    private int updateTime;
    private int i;

    public NPCDataStorage(final CitizensPlaceholderAPI plugin) {
        this.plugin = plugin;
        this.npcStorage = new ConcurrentHashMap<>();
        this.namePlaceholders = Lists.newLinkedList();
        this.skinPlaceholders = Lists.newLinkedList();
        this.mainSection = this.plugin.getDataConfig().getConfigurationSection("npcs");
        this.updateTime = 60 * 20;
        this.i = -1;
    }

    public void setUpdateTime(final int updateTime) {
        this.updateTime = updateTime;
    }

    public int getUpdateTime() {
        return this.updateTime;
    }

    public int getIncrement() {
        return this.i;
    }

    public void setIncrement(final int i) {
        this.i = i;
    }

    void addNamePlaceholder(final NPCDataHandler ndh) {
        if (ndh == null || ndh.getNamePlaceholder() == null) {
            return;
        }
        this.namePlaceholders.add(ndh.getNamePlaceholder());
    }

    void addSkinPlaceholder(final NPCDataHandler ndh) {
        if (ndh == null || ndh.getSkinPlaceholder() == null) {
            return;
        }
        this.skinPlaceholders.add(ndh.getSkinPlaceholder());
    }

    public void load() {
        this.i = -1;
        this.mainSection.getKeys(false).forEach(ids -> {
            this.i++;
            final NPCDataHandler ndh = new NPCDataHandler();
            ndh.setUniqueId(UUID.fromString(ids));
            final String path = ids + ".name-placeholder";
            final String path2 = ids + ".skin-placeholder";
            ndh.setNamePlaceholder(this.mainSection.isSet(path) ? this.mainSection.getString(path) : null);
            ndh.setSkinPlaceholder(this.mainSection.isSet(path2) ? this.mainSection.getString(ids + ".skin-placeholder") : null);
            ndh.setUpdateID(this.i);
            this.npcStorage.put(ndh.getUniqueId(), ndh);
            addNamePlaceholder(ndh);
            addSkinPlaceholder(ndh);
        });
        startPlaceholderUpdater();
    }

    public int getNextID() {
        if (this.npcStorage.isEmpty()) {
            return 0;
        }
        return this.npcStorage.size();
    }

    public List<String> getNamePlaceholders() {
        return this.namePlaceholders;
    }

    public List<String> getSkinPlaceholders() {
        return this.skinPlaceholders;
    }

    public void saveNPC(final UUID uniqueId, final String skinPlaceholder, final String namePlaceholder) {
        if (this.npcStorage.containsKey(uniqueId)) {
            final NPCDataHandler ndh = this.npcStorage.get(uniqueId);
            ndh.setUniqueId(uniqueId);
            if (namePlaceholder != null) {
                ndh.setNamePlaceholder(namePlaceholder);
                this.mainSection.set(uniqueId.toString() + ".name-placeholder", namePlaceholder);
                addNamePlaceholder(ndh);
            }
            if (skinPlaceholder != null) {
                ndh.setSkinPlaceholder(skinPlaceholder);
                this.mainSection.set(uniqueId.toString() + ".skin-placeholder", skinPlaceholder);
                addSkinPlaceholder(ndh);
            }
            ndh.setUpdateID(getNextID());
            this.npcStorage.put(uniqueId, ndh);
        } else {
            final NPCDataHandler ndh = new NPCDataHandler();
            ndh.setUniqueId(uniqueId);
            ndh.setNamePlaceholder(namePlaceholder);
            ndh.setSkinPlaceholder(skinPlaceholder);
            ndh.setUpdateID(getNextID());
            this.npcStorage.put(ndh.getUniqueId(), ndh);
            this.mainSection.set(uniqueId.toString() + ".name-placeholder", namePlaceholder);
            this.mainSection.set(uniqueId + ".skin-placeholder", skinPlaceholder);
            addNamePlaceholder(ndh);
            addSkinPlaceholder(ndh);
        }
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            update();
            this.plugin.getConfigManager().saveConfig("data.yml");
        });
    }

    public void deleteNPC(final UUID uniqueId) {
        final NPCDataHandler ndh = new NPCDataHandler();
        ndh.setUniqueId(uniqueId);
        ndh.setNamePlaceholder(null);
        ndh.setSkinPlaceholder(null);
        this.namePlaceholders.remove(ndh.getUpdateID());
        this.npcStorage.remove(ndh.getUniqueId());
        final String uuid = uniqueId.toString();
        this.mainSection.set(uuid + ".name-placeholder", null);
        this.mainSection.set(uuid + ".skin-placeholder", null);
        this.mainSection.set(uuid, null);
        this.plugin.getConfigManager().saveConfig("data.yml");
    }

    public void setNamePAPI(final Player player, final String string) {
        this.namePlaceholders.set(this.namePlaceholders.indexOf(string), PlaceholderAPI.setPlaceholders(player, string));
    }

    // 1
    // set (indexOf string , get from i)
    public void setSkinPAPI(final Player player, final String string) {
        this.skinPlaceholders.set(this.skinPlaceholders.indexOf(string), PlaceholderAPI.setPlaceholders(player, string));
    }

    public void startPlaceholderUpdater() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, this::update, this.updateTime, this.updateTime);
    }

    public Map<UUID, NPCDataHandler> getStorage() {
        return this.npcStorage;
    }

    public void update() {
        this.i = -1;
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final AccessibleString replaced = new AccessibleString("");
            final AccessibleString replaced2 = new AccessibleString("");
            for (final NPCDataHandler npcDataHandler : this.npcStorage.values()) {
                this.i++;
                final NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(npcDataHandler.getUniqueId());
                if (npc == null) {
                    return;
                }
                if (!npc.isSpawned()) {
                    return;
                }
                if (npc.getEntity() == null) {
                    return;
                }
                if (npcDataHandler.getNamePlaceholder() != null) {
                    final TaskChain<?> tc = this.plugin.getTaskChainFactory().newSharedChain("citizensUpdateName");
                    tc.async(() -> {
                        //setNamePAPI(player, namePlaceholders.get(i));
                        replaced.setString(PlaceholderAPI.setPlaceholders(player, npcDataHandler.getNamePlaceholder()));
                    }).sync(() -> {
                        npc.setName(replaced.getString());
                    }).execute();
                }
                if (npcDataHandler.getSkinPlaceholder() == null) {
                    return;
                }
                final TaskChain<?> tc = this.plugin.getTaskChainFactory().newSharedChain("citizensUpdateSkin");
                tc.async(() -> {
                    //setSkinPAPI(player, skinPlaceholders.get(i));
                    replaced2.setString(PlaceholderAPI.setPlaceholders(player, npcDataHandler.getSkinPlaceholder()));
                    npc.data().set(NPC.Metadata.PLAYER_SKIN_UUID, replaced2.getString());
                    npc.data().set(NPC.Metadata.PLAYER_SKIN_USE_LATEST, false);
                }).sync(() -> {
                    final SkinnableEntity skinnableEntity = (SkinnableEntity) npc.getEntity();
                    try {
                        final SkinTrait st = npc.getTrait(SkinTrait.class);
                        st.setSkinName(replaced2.getString(), true);
                    } catch (final Exception ex) {
                        CitizensPlaceholderAPI.getInstance().getSLF4JLogger().error("Error whilst trying to set name {} for {}", npc.getRawName(), replaced2.getString(), ex);
                    }
                    if (skinnableEntity != null) {
                        skinnableEntity.setSkinName(replaced2.getString());
                        if (skinnableEntity.getSkinTracker() != null) {
                            skinnableEntity.getSkinTracker().notifySkinChange(true);
                        }
                    }
                }).execute();
            }
        }
    }

}
