/*
 * The contents of this file is free and unencumbered software released into the
 * public domain. For more information, please refer to <http://unlicense.org/>
 */
package me.citizensplaceholderapi.commands;

import me.citizensplaceholderapi.CitizensPlaceholderAPI;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CitizensPlaceholderAPICommand implements CommandExecutor {

    private final CitizensPlaceholderAPI plugin;
    private final List<String> helpMessage;
    private final String noPermissionMessage;
    private final String setSkinPlaceholderMessage;
    private final String setNamePlaceholderMessage;
    private final String clearPlaceholdersMessage;
    private final String noNpcSelectedMessage;

    public CitizensPlaceholderAPICommand(final CitizensPlaceholderAPI plugin) {
        this.plugin = plugin;
        this.helpMessage = Arrays.asList(
                colorize("&7[&aCitizens&bPAPI&7]")
                , colorize("&7&m                               &7")
                , colorize("&eSelect a Citizens NPC to use one of these commands:")
                , colorize("&8- &6/cpapi setskinplaceholder <placeholder>")
                , colorize("&8- &6/cpapi setnameplaceholder <placeholder>")
                , colorize("&8- &6/cpapi clearplaceholders")
                , colorize("&7&m                               &7"));
        this.noPermissionMessage = colorize("&cYou don't have permission to use this command.");
        this.setSkinPlaceholderMessage = colorize("&8* &a%npc_selected% &7skin placeholder changed to &a%placeholder%");
        this.setNamePlaceholderMessage = colorize("&8* &a%npc_selected% &7name placeholder changed to &a%placeholder%");
        this.clearPlaceholdersMessage = colorize("&8* &a%npc_selected% &7had his placeholders cleared");
        this.noNpcSelectedMessage = colorize("&8* &c&oYou don't have any npc selected!");
    }

    private String colorize(final String textToTranslate) {
        return ChatColor.translateAlternateColorCodes('&', textToTranslate);
    }

    private String getArgs(final String[] args, final int num) {
        final StringBuilder sb = new StringBuilder();
        for (int i = num; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        return sb.toString().trim();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (cmd.getLabel().equalsIgnoreCase("citizensplaceholderapi")) {
            if (!sender.hasPermission("citizenspapi.use")) {
                sender.sendMessage(this.noPermissionMessage);
                return true;
            }
            switch (args.length) {

                case 0:
                    this.helpMessage.forEach(sender::sendMessage);
                    break;
                case 1:
                    switch (args[0].toLowerCase()) {

                        case "setskinplaceholder":
                            sender.sendMessage(this.helpMessage.get(3));
                            break;
                        case "setnameplaceholder":
                            sender.sendMessage(this.helpMessage.get(4));
                            break;
                        case "clearplaceholders":
                            final NPC selectedNPC = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
                            if (selectedNPC == null) {
                                sender.sendMessage(this.noNpcSelectedMessage);
                                return true;
                            }
                            this.plugin.getNPCDataStorage().deleteNPC(selectedNPC.getUniqueId());
                            sender.sendMessage(this.clearPlaceholdersMessage.replace("%npc_selected%", selectedNPC.getName()));
                            break;
                        case "debug":
                            sender.sendMessage(this.plugin.getNPCDataStorage().getNamePlaceholders().toString());
                            sender.sendMessage(this.plugin.getNPCDataStorage().getSkinPlaceholders().toString());
                        case "debug2":
                            this.plugin.getNPCDataStorage().getStorage().values().forEach(ndh -> {
                                sender.sendMessage("Name:" + ndh.getNamePlaceholder());
                                sender.sendMessage("Skin:" + ndh.getSkinPlaceholder());
                                sender.sendMessage("---------------------------------");
                            });
                        default:
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> this.plugin.getNPCDataStorage().update());
                            break;
                    }
                    break;

                default:
                    switch (args[0].toLowerCase()) {
                        case "setskinplaceholder":
                            final NPC selectedNPC = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
                            if (selectedNPC == null) {
                                sender.sendMessage(this.noNpcSelectedMessage);
                                return true;
                            }
                            final UUID u = selectedNPC.getUniqueId();
                            this.plugin.getNPCDataStorage().saveNPC(u, getArgs(args, 1), null);
                            sender.sendMessage(this.setSkinPlaceholderMessage.replace("%npc_selected%", selectedNPC.getName()).replace("%placeholder%", getArgs(args, 1)));
                            break;
                        case "setnameplaceholder":
                            final NPC selectedNPC1 = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
                            if (selectedNPC1 == null) {
                                sender.sendMessage(this.noNpcSelectedMessage);
                                return true;
                            }
                            final UUID u1 = selectedNPC1.getUniqueId();
                            final String name = selectedNPC1.getName();
                            this.plugin.getNPCDataStorage().saveNPC(u1, null, getArgs(args, 1));
                            sender.sendMessage(this.setNamePlaceholderMessage.replace("%npc_selected%", name).replace("%placeholder%", getArgs(args, 1)));
                            break;
                    }
            }
        }
        return true;
    }

}
