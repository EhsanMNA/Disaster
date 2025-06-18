package me.ehsanmna.disaster.command;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.manager.ArenaManager;
import me.ehsanmna.disaster.manager.PlayerManager;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DisasterMainCommand implements CommandExecutor, TabCompleter {

    private final DisasterPlugin plugin;
    private final ArenaManager arenaManager;
    private final PlayerManager playerManager;

    public DisasterMainCommand(DisasterPlugin plugin) {
        this.plugin = plugin;
        this.arenaManager = plugin.getArenaManager();
        this.playerManager = plugin.getPlayerManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player)){
            sender.sendMessage(TextUtils.toComponent(TextUtils.getMessage("console-access")));
            return true;
        }
        if (!player.hasPermission("disaster.command.main")){
            TextUtils.sendActionbar(player, "player-permission");
            return true;
        }

        if (args.length == 0){
            TextUtils.sendMessage(player, "command-arg");
            return true;
        }

        String subcommand = args[0].toLowerCase();
        String arenaName;
        Arena arena;
        switch (subcommand){
            case "help":
                TextUtils.sendMessage(player, "command-help");
                break;

            case "join":
                if (args.length == 1){
                    TextUtils.sendMessage(player, "command-join-help");
                    return true;
                }

                arenaName = args[1];
                arena = arenaManager.getArena(arenaName);
                if (arena == null){
                    TextUtils.sendMessage(player, "arena-exist");
                    return true;
                }
                if (playerManager.isPlayerInArena(player)){
                    TextUtils.sendMessage(player, "player-in-arena");
                    return true;
                }
                if (!arena.isEnable()){
                    TextUtils.sendMessage(player, "arena-disable");
                    return true;
                }
                if (arena.getArenaHandler().isRunning()){
                    TextUtils.sendMessage(player, "arena-active");
                    return true;
                }
                if (arenaManager.isArenaFull(arena)){
                    TextUtils.sendMessage(player, "arena-full");
                    return true;
                }

                ArenaPlayer arenaPlayer = playerManager.joinPlayer(player,arena);
                if (arenaPlayer == null) TextUtils.sendMessage(player, "arena-join-fail", arenaName);
                else TextUtils.sendMessage(player, "arena-join", arenaName);

                break;

            case "spectate":
                if (args.length == 1){
                    TextUtils.sendMessage(player, "command-spectate-help");
                    return true;
                }

                arenaName = args[1];
                arena = arenaManager.getArena(arenaName);
                if (arena == null){
                    TextUtils.sendMessage(player, "arena-exist");
                    return true;
                }
                if (!arena.isEnable()){
                    TextUtils.sendMessage(player, "arena-disable");
                    return true;
                }
                if (!arena.getArenaHandler().isRunning()){
                    TextUtils.sendMessage(player, "arena-empty");
                    return true;
                }
                if (playerManager.isPlayerInArena(player)){
                    TextUtils.sendMessage(player, "player-in-arena");
                    return true;
                }

                arenaPlayer = playerManager.spectatePlayer(player,arena);
                TextUtils.sendMessage(player, "arena-spectate", arenaName);

                break;

            case "leave":
                if (!playerManager.isPlayerInArena(player)){
                    TextUtils.sendMessage(player, "player-no-arena");
                    return true;
                }

                playerManager.leavePlayer(player);
                TextUtils.sendMessage(player, "arena-leave");

                break;

            default:
                TextUtils.sendMessage(player, "command-arg");
                break;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) return List.of();
        if (!sender.hasPermission("disaster.command.main")) return List.of();
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> subcommands = List.of(
                    "help",
                    "join",
                    "leave",
                    "spectate");

            for (String sub : subcommands)
                if (sub.startsWith(args[0].toLowerCase())) completions.add(sub);
        }else if (args.length == 2) {
            List<String> arenaNameCommands = List.of("join", "spectate");

            if (arenaNameCommands.contains(args[0].toLowerCase())) {
                for (Arena arena : plugin.getArenaManager().getArenas())
                    if (arena.getName().toLowerCase().startsWith(args[1].toLowerCase()))
                        completions.add(arena.getName());
            }
        }

        return completions;
    }
}
