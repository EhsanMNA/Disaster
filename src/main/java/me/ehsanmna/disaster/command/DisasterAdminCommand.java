package me.ehsanmna.disaster.command;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

                break;

            case "start":

                break;

            default:
                TextUtils.sendMessage(player, "command-admin-arg");
                break;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
