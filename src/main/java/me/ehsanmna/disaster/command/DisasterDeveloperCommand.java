package me.ehsanmna.disaster.command;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.region.Region;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DisasterDeveloperCommand implements CommandExecutor, TabCompleter {

    private final DisasterPlugin plugin;
    public final static String prefix = "<gradient:#0f7d46:#10b361><bold>DISASTERS <white>|<reset>";

    public DisasterDeveloperCommand(DisasterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player)){
            sender.sendMessage(TextUtils.toComponent(TextUtils.getMessage("console-access")));
            return true;
        }
        if (!player.hasPermission("disaster.command.developer")){
            TextUtils.sendActionbar(player, "player-permission");
            return true;
        }

        if (args.length == 0){
            TextUtils.sendMessage(player, prefix+" <white>/dd <create|delete>");
            return true;
        }

        String subcommand = args[0].toLowerCase();
        String name;
        Arena arena;
        switch (subcommand){
            case "help":
                TextUtils.sendMessage(player, prefix+" <yellow>List of commands: ");
                TextUtils.sendMessage(player, "<green>/dd create <white><name>");
                TextUtils.sendMessage(player, "<green>/dd delete <white><name>");
                TextUtils.sendMessage(player, "<green>/dd setDisplayname <white><arena> <name>");
                TextUtils.sendMessage(player, "<green>/dd setWorldName <white><arena> <name>");
                TextUtils.sendMessage(player, "<green>/dd setSpawn <white><name>");
                TextUtils.sendMessage(player, "<green>/dd setPos1 <white><name>");
                TextUtils.sendMessage(player, "<green>/dd setPos2 <white><name>");
                TextUtils.sendMessage(player, "<green>/dd list ");
                break;

            case "create":
                if (args.length < 2){
                    TextUtils.sendMessage(player, prefix+" <red>Enter the name of the Arena!");
                    return true;
                }

                name = args[1];
                if (plugin.getArenaManager().isArenaExist(name)){
                    TextUtils.sendMessage(player, prefix+" <red>This arena already exist!");
                    return true;
                }

                arena = new Arena(plugin, name);
                plugin.getArenaManager().addArena(arena);
                TextUtils.sendMessage(player, prefix+" <green>Arena has been created!");

                break;

            case "delete":
                if (args.length < 2){
                    TextUtils.sendMessage(player, prefix+" <red>Enter the name of the Arena!");
                    return true;
                }

                name = args[1];
                if (!plugin.getArenaManager().isArenaExist(name)){
                    TextUtils.sendMessage(player, prefix+" <red>This arena does not exist!");
                    return true;
                }

                arena = plugin.getArenaManager().removeArena(name);
                TextUtils.sendMessage(player, prefix+" <red>Arena {0} has been deleted!",arena.getName());
                break;

            case "list":
                if (plugin.getArenaManager().getArenas().isEmpty()){
                    TextUtils.sendMessage(player, prefix+" <red>There is no arena!");
                    return true;
                }

                TextUtils.sendMessage(player, prefix+" <yellow>Here is the list of all arenas:");
                for (Arena arena1 : plugin.getArenaManager().getArenas())
                    TextUtils.sendMessage(player, "<yellow> - <white>"+arena1.getName());
                break;

            case "setworldname":
                if (args.length < 3){
                    TextUtils.sendMessage(player, prefix+" <red>Usage: /dd setworldname <arena> <world name>!");
                    return true;
                }

                name = args[1];
                if (!plugin.getArenaManager().isArenaExist(name)){
                    TextUtils.sendMessage(player, prefix+" <red>This arena does not exist!");
                    return true;
                }

                arena = plugin.getArenaManager().getArena(name);
                arena.setWorldName(args[2]);
                TextUtils.sendMessage(player, prefix+" <green>Arena "+arena.getName()+" world name has been changed!");
                break;

            case "setdisplayname":
                if (args.length < 3){
                    TextUtils.sendMessage(player, prefix+" <red>Usage: /dd setdisplayname <arena> <name>!");
                    return true;
                }

                name = args[1];
                if (!plugin.getArenaManager().isArenaExist(name)){
                    TextUtils.sendMessage(player, prefix+" <red>This arena does not exist!");
                    return true;
                }

                arena = plugin.getArenaManager().getArena(name);
                arena.setDisplayName(args[2]);
                TextUtils.sendMessage(player, prefix+" <green>Arena "+arena.getName()+" displayname has been changed!");
                break;

            case "setspawn":
                if (args.length < 2){
                    TextUtils.sendMessage(player, prefix+" <red>Enter the name of the Arena!");
                    return true;
                }

                name = args[1];
                if (!plugin.getArenaManager().isArenaExist(name)){
                    TextUtils.sendMessage(player, prefix+" <red>This arena does not exist!");
                    return true;
                }

                arena = plugin.getArenaManager().getArena(name);
                arena.setSpawn(player.getLocation());
                TextUtils.sendMessage(player, prefix+" <green>Arena "+arena.getName()+" spawn has been set!");
                break;

            case "setlobby":
                plugin.getLobbyManager().setLobbySpawn(player.getLocation());
                TextUtils.sendMessage(player, prefix+" <green>Lobby spawn has been set!");
                break;

            case "setpos1":
                if (args.length < 2){
                    TextUtils.sendMessage(player, prefix+" <red>Enter the name of the Arena!");
                    return true;
                }

                name = args[1];
                if (!plugin.getArenaManager().isArenaExist(name)){
                    TextUtils.sendMessage(player, prefix+" <red>This arena does not exist!");
                    return true;
                }

                arena = plugin.getArenaManager().getArena(name);
                if (arena.hasArenaRegion()) arena.getArenaRegion().setPos1(player.getLocation());
                else arena.setArenaRegion(new Region(player.getLocation(), player.getLocation()));
                TextUtils.sendMessage(player, prefix+" <green>Position 1 has been changed!");
                break;

            case "setpos2":
                if (args.length < 2){
                    TextUtils.sendMessage(player, prefix+" <red>Enter the name of the Arena!");
                    return true;
                }

                name = args[1];
                if (!plugin.getArenaManager().isArenaExist(name)){
                    TextUtils.sendMessage(player, prefix+" <red>This arena does not exist!");
                    return true;
                }

                arena = plugin.getArenaManager().getArena(name);
                if (arena.hasArenaRegion()) arena.getArenaRegion().setPos2(player.getLocation());
                else arena.setArenaRegion(new Region(player.getLocation(), player.getLocation()));
                TextUtils.sendMessage(player, prefix+" <green>Position 2 has been changed!");
                break;

            case "debug":
                if (args.length < 2){
                    TextUtils.sendMessage(player, prefix+" <red>Enter the name of the Arena!");
                    return true;
                }

                name = args[1];
                if (!plugin.getArenaManager().isArenaExist(name)){
                    TextUtils.sendMessage(player, prefix+" <red>This arena does not exist!");
                    return true;
                }

                arena = plugin.getArenaManager().getArena(name);
                arena.getArenaHandler().getArenaService().setDebug(!arena.getArenaHandler().getArenaService().isDebug());
                TextUtils.sendMessage(player, prefix+" <green>Debug mode has been toggled!");
                break;

            default:
                TextUtils.sendMessage(player, "command-admin-arg");
                break;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player)) return List.of();
        if (!sender.hasPermission("disaster.command.developer")) return List.of();

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subcommands = List.of(
                    "help",
                    "create",
                    "delete",
                    "list",
                    "setworldname",
                    "setdisplayname",
                    "setlobby",
                    "setspawn",
                    "setpos1",
                    "setpos2",
                    "debug"
            );

            for (String sub : subcommands)
                if (sub.startsWith(args[0].toLowerCase())) completions.add(sub);
        }
        else if (args.length == 2) {
            List<String> arenaNameCommands = List.of(
                    "delete",
                    "setworldname",
                    "setdisplayname",
                    "setspawn",
                    "setpos1",
                    "setpos2",
                    "debug"
            );

            if (arenaNameCommands.contains(args[0].toLowerCase())) {
                for (Arena arena : plugin.getArenaManager().getArenas())
                    if (arena.getName().toLowerCase().startsWith(args[1].toLowerCase())) completions.add(arena.getName());
            }
        }
        else if (args.length == 3) {
            // For commands that take a third argument
            switch (args[0].toLowerCase()) {
                case "setworldname":
                case "setdisplayname":
                    completions.add("value");
                    break;
            }
        }

        return completions;
    }
}
