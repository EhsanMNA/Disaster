package me.ehsanmna.disaster.command;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.disaster.DisasterType;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.ehsanmna.disaster.command.DisasterDeveloperCommand.prefix;

public class DisasterAdminCommand implements CommandExecutor, TabCompleter {

    private final DisasterPlugin plugin;

    public DisasterAdminCommand(DisasterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player)){
            sender.sendMessage(TextUtils.toComponent(TextUtils.getMessage("console-access")));
            return true;
        }
        if (!player.hasPermission("disaster.command.admin")){
            TextUtils.sendActionbar(player, "player-permission");
            return true;
        }

        if (args.length == 0){
            TextUtils.sendMessage(player, "command-admin-arg");
            return true;
        }

        String subcommand = args[0].toLowerCase();
        switch (subcommand){
            case "help":
                TextUtils.sendMessage(player, "command-admin-help");
                break;

            case "kick":
                if (args.length < 2){
                    TextUtils.sendMessage(player, prefix+" <red>Enter the name of the Arena!");
                    return true;
                }
                String playerName = args[1];
                Player kickPlayer = Bukkit.getPlayer(playerName);
                if (kickPlayer == null || !kickPlayer.isOnline()){
                    TextUtils.sendMessage(player, prefix+" <red>Player is not online!");
                    return true;
                }

                if (!plugin.getPlayerManager().isPlayerInArena(player)){
                    TextUtils.sendMessage(player, prefix+" <red>This player is not in an arena!");
                    return true;
                }

                plugin.getPlayerManager().leavePlayer(kickPlayer);
                TextUtils.sendMessage(player, prefix+" <green>Kicked "+playerName+" from arena!");

                break;

            case "start":
                if (args.length < 2){
                    TextUtils.sendMessage(player, prefix+" <red>Enter the name of the Arena!");
                    return true;
                }
                String name = args[1];
                if (!plugin.getArenaManager().isArenaExist(name)){
                    TextUtils.sendMessage(player, prefix+" <red>This arena does not exist!");
                    return true;
                }

                Arena arena = plugin.getArenaManager().getArena(name);
                if (!arena.isEnable()){
                    TextUtils.sendMessage(player, prefix+" <red>This arena is not active!");
                    return true;
                }

                if (arena.getArenaHandler().isRunning()){
                    TextUtils.sendMessage(player, prefix+" <red>This arena is already running!");
                    return true;
                }

                arena.getArenaHandler().getArenaService().getWaitingService().startTimer();

                break;

            case "forcestart":
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
                if (!arena.isEnable()){
                    TextUtils.sendMessage(player, prefix+" <red>This arena is not active!");
                    return true;
                }

                if (arena.getArenaHandler().isRunning()){
                    TextUtils.sendMessage(player, prefix+" <red>This arena is already running!");
                    return true;
                }
                arena.getArenaHandler().getArenaService().getWaitingService().start();

                break;

            case "adddisaster":
                if (args.length < 3){
                    TextUtils.sendMessage(player, prefix+" <red>Enter the name of the Arena!");
                    return true;
                }

                name = args[1];
                if (!plugin.getArenaManager().isArenaExist(name)){
                    TextUtils.sendMessage(player, prefix+" <red>This arena does not exist!");
                    return true;
                }

                arena = plugin.getArenaManager().getArena(name);
                if (!arena.isEnable()){
                    TextUtils.sendMessage(player, prefix+" <red>This arena is not active!");
                    return true;
                }
                if (!arena.getArenaHandler().isRunning()){
                    TextUtils.sendMessage(player, prefix+" <red>This arena is not running!");
                    return true;
                }

                boolean sendMessage = args.length != 4 || Boolean.parseBoolean(args[3].toLowerCase());
                if (arena.getArenaHandler().getArenaService().getGameService().addDisaster(args[2], sendMessage))
                    TextUtils.sendMessage(player, prefix+" <green>Disaster has been added!");
                else TextUtils.sendMessage(player, prefix+" <red>No disaster with name "+args[2]+" exist!");
                break;

            default:
                TextUtils.sendMessage(player, "command-admin-arg");
                break;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) return List.of();
        if (!sender.hasPermission("disaster.command.admin")) return List.of();

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subcommands = List.of(
                    "help",
                    "kick",
                    "start",
                    "adddisaster"
            );

            for (String sub : subcommands)
                if (sub.startsWith(args[0].toLowerCase())) completions.add(sub);
        }
        else if (args.length == 2) {
            List<String> arenaNameCommands = List.of(
                    "start",
                    "adddisaster"
            );

            if (arenaNameCommands.contains(args[0].toLowerCase())) {
                for (Arena arena : plugin.getArenaManager().getArenas())
                    if (arena.getName().toLowerCase().startsWith(args[1].toLowerCase())) completions.add(arena.getName());
            }
        }
        else if (args.length == 3) {
            List<String> arenaNameCommands = List.of(
                    "adddisaster"
            );


            if (arenaNameCommands.contains(args[0].toLowerCase())) {
                for (DisasterType disasterType : DisasterType.values())
                    if (disasterType.name().toLowerCase().startsWith(args[2].toLowerCase())) completions.add(disasterType.name());
            }
        }

        return completions;
    }
}
