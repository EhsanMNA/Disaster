package me.ehsanmna.disaster.command;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.machine.PowerUpMachine;
import me.ehsanmna.disaster.model.powerup.PowerUpType;
import me.ehsanmna.disaster.model.powerup.requirement.ItemRequirement;
import me.ehsanmna.disaster.model.powerup.requirement.XPRequirement;
import me.ehsanmna.disaster.model.region.Region;
import me.ehsanmna.disaster.util.TextUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
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
                TextUtils.sendMessage(player, "<green>/dd createmachine ");
                TextUtils.sendMessage(player, "<green>/dd list ");
                TextUtils.sendMessage(player, "<green>/dd setmachinepos1 <white><arena> <machine> ");
                TextUtils.sendMessage(player, "<green>/dd setmachinepos2 <white><arena> <machine> ");
                TextUtils.sendMessage(player, "<green>/dd dd setmachinebutton <white><arena> <machine> ");
                TextUtils.sendMessage(player, "<green>/dd additemreq <white><arena> <machine> <material> <amount> <gray>[customdata]");
                TextUtils.sendMessage(player, "<green>/dd dd addxpreq <white><arena> <machine> <amount> ");
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

            case "createmachine":
                if (args.length < 3) {
                    TextUtils.sendMessage(player, prefix + " <red>Usage: /dd createmachine <arena> <name>");
                    return true;
                }
                String arenaName = args[1];
                if (!plugin.getArenaManager().isArenaExist(arenaName)) {
                    TextUtils.sendMessage(player, prefix + " <red>Arena does not exist!");
                    return true;
                }
                arena = plugin.getArenaManager().getArena(arenaName);
                String machineName = args[2];
                if (plugin.getPowerUpMachineManager().hasMachineWithName(arena,machineName)){
                    TextUtils.sendMessage(player, prefix + " <red>This machine is already exist!");
                    return true;
                }
                plugin.getPowerUpMachineManager().createPowerUpMachine(arena, machineName);
                TextUtils.sendMessage(player, prefix + " <green>Machine created! Configure <yellow>region, type, button, and requirements<green>.");
                break;

            case "setmachinepos1":
                if (args.length < 3) {
                    TextUtils.sendMessage(player, prefix + " <red>Usage: /dd setmachinepos1 <arena> <machine>");
                    return true;
                }
                arenaName = args[1];
                machineName = args[2];
                if (!plugin.getArenaManager().isArenaExist(arenaName)) {
                    TextUtils.sendMessage(player, prefix + " <red>Arena does not exist!");
                    return true;
                }
                arena = plugin.getArenaManager().getArena(arenaName);

                PowerUpMachine machine = plugin.getPowerUpMachineManager().getMachine(arena, machineName);
                if (machine == null) {
                    TextUtils.sendMessage(player, prefix + " <red>Machine not found!");
                    return true;
                }
                if (machine.getRegion() == null)
                    machine.setRegion(new Region(player.getLocation(), player.getLocation()));
                else machine.getRegion().setPos1(player.getLocation());

                TextUtils.sendMessage(player, prefix + " <green>Machine Pos1 set!");
                break;

            case "setmachinepos2":
                if (args.length < 3) {
                    TextUtils.sendMessage(player, prefix + " <red>Usage: /dd setmachinepos2 <arena> <machine>");
                    return true;
                }
                arenaName = args[1];
                machineName = args[2];
                if (!plugin.getArenaManager().isArenaExist(arenaName)) {
                    TextUtils.sendMessage(player, prefix + " <red>Arena does not exist!");
                    return true;
                }

                arena = plugin.getArenaManager().getArena(arenaName);
                machine = plugin.getPowerUpMachineManager().getMachine(arena, machineName);
                if (machine == null) {
                    TextUtils.sendMessage(player, prefix + " <red>Machine not found!");
                    return true;
                }
                if (machine.getRegion() == null)
                    machine.setRegion(new Region(player.getLocation(), player.getLocation()));
                else machine.getRegion().setPos2(player.getLocation());

                TextUtils.sendMessage(player, prefix + " <green>Machine Pos2 set!");
                break;

            case "setmachinebutton":
                if (args.length < 3) {
                    TextUtils.sendMessage(player, prefix + " <red>Usage: /dd setmachinepos2 <arena> <machine>");
                    return true;
                }
                arenaName = args[1];
                machineName = args[2];
                if (!plugin.getArenaManager().isArenaExist(arenaName)) {
                    TextUtils.sendMessage(player, prefix + " <red>Arena does not exist!");
                    return true;
                }
                arena = plugin.getArenaManager().getArena(arenaName);
                machine = plugin.getPowerUpMachineManager().getMachine(arena, machineName);
                if (machine == null) {
                    TextUtils.sendMessage(player, prefix + " <red>Machine not found!");
                    return true;
                }

                Block targetBlock = player.getTargetBlockExact(5);
                if (targetBlock == null || !targetBlock.getType().name().contains("BUTTON")) {
                    TextUtils.sendMessage(player, prefix + " <red>Target a button block within 5 blocks!");
                    return true;
                }

                plugin.getPowerUpMachineManager().getPowerUpMachineService().setButtonLocation(machine, targetBlock);
                TextUtils.sendMessage(player, prefix + " <green>Machine button set and NBT applied!");
                break;

            case "additemreq":
                if (args.length < 5) {
                    TextUtils.sendMessage(player, prefix + " <red>Usage: /dd additemreq <arena> <machine> <material> <amount> <gray>[customdata]");
                    return true;
                }
                arenaName = args[1];
                machineName = args[2];

                if (!plugin.getArenaManager().isArenaExist(arenaName)) {
                    TextUtils.sendMessage(player, prefix + " <red>Arena does not exist!");
                    return true;
                }
                arena = plugin.getArenaManager().getArena(arenaName);
                machine = plugin.getPowerUpMachineManager().getMachine(arena, machineName);
                if (machine == null) {
                    TextUtils.sendMessage(player, prefix + " <red>Machine not found!");
                    return true;
                }
                Material material;
                try {
                    material = Material.valueOf(args[3].toUpperCase());
                } catch (IllegalArgumentException e) {
                    TextUtils.sendMessage(player, prefix + " <red>Invalid material!");
                    return true;
                }

                int amount = 1;
                int customData = 0;

                try {
                    amount = Integer.parseInt(args[4]);
                    customData = args.length>5 ? Integer.parseInt(args[5]) : 0;
                }
                catch (NumberFormatException ignored){
                    TextUtils.sendMessage(player, prefix + " <red>Invalid format of number!");
                }

                machine.getRequirements().add(new ItemRequirement(plugin, material, amount, customData));
                TextUtils.sendMessage(player, prefix + " <green>Item requirement added!");
                break;

            case "addxpreq":
                if (args.length < 4) {
                    TextUtils.sendMessage(player, prefix + " <red>Usage: /dd addxpreq <arena> <machine> <amount>");
                    return true;
                }
                arenaName = args[1];
                machineName = args[2];

                if (!plugin.getArenaManager().isArenaExist(arenaName)) {
                    TextUtils.sendMessage(player, prefix + " <red>Arena does not exist!");
                    return true;
                }
                arena = plugin.getArenaManager().getArena(arenaName);
                machine = plugin.getPowerUpMachineManager().getMachine(arena, machineName);
                if (machine == null) {
                    TextUtils.sendMessage(player, prefix + " <red>Machine not found!");
                    return true;
                }
                amount = Integer.parseInt(args[3]);
                machine.getRequirements().add(new XPRequirement(plugin, amount));
                TextUtils.sendMessage(player, prefix + " <green>XP requirement added!");
                break;

            case "setpoweruptype":
                if (args.length < 4) {
                    TextUtils.sendMessage(player, prefix + " <red>Usage: /dd setpoweruptype <arena> <machine> <type>");
                    return true;
                }
                arenaName = args[1];
                machineName = args[2];
                String powerUpTypeName = args[3];

                if (!plugin.getArenaManager().isArenaExist(arenaName)) {
                    TextUtils.sendMessage(player, prefix + " <red>Arena does not exist!");
                    return true;
                }
                arena = plugin.getArenaManager().getArena(arenaName);
                machine = plugin.getPowerUpMachineManager().getMachine(arena, machineName);
                if (machine == null) {
                    TextUtils.sendMessage(player, prefix + " <red>Machine not found!");
                    return true;
                }

                PowerUpType type = PowerUpType.NONE;
                try {
                    type = PowerUpType.valueOf(powerUpTypeName.toUpperCase());
                }
                catch (Exception ignored){
                    TextUtils.sendMessage(player, prefix + " <red>Type is not valid!");
                    return true;
                }

                machine.setPowerUp(plugin.getPowerUpManager().getPowerUp(type));
                TextUtils.sendMessage(player, prefix + " <green>Powerup type has been set!");
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
                    "debug",
                    "createmachine",
                    "setmachinepos1",
                    "setmachinepos2",
                    "setmachinebutton",
                    "additemreq",
                    "addxpreq",
                    "setpoweruptype"
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
                    "debug",
                    "createmachine",
                    "setmachinepos1",
                    "setmachinepos2",
                    "setmachinebutton",
                    "additemreq",
                    "addxpreq",
                    "setpoweruptype"
            );

            if (arenaNameCommands.contains(args[0].toLowerCase())) {
                for (Arena arena : plugin.getArenaManager().getArenas())
                    if (arena.getName().toLowerCase().startsWith(args[1].toLowerCase())) completions.add(arena.getName());
            }
        }
        else if (args.length == 3) {
            String arenaName = args[1];
            switch (args[0].toLowerCase()) {
                case "setworldname":
                case "setdisplayname":
                    completions.add("value");
                    break;
                case "setmachinepos1":
                case "setmachinepos2":
                case "setmachinebutton":
                case "setpoweruptype":
                case "additemreq":
                case "addxpreq":
                    for (String machine : plugin.getPowerUpMachineManager().getMachinesForArena(arenaName).keySet())
                        if (machine.toLowerCase().startsWith(args[2].toLowerCase())) completions.add(machine);
                    break;
            }
        }
        else if (args.length == 4) {
            switch (args[0].toLowerCase()) {
                case "setpoweruptype":
                    for (PowerUpType powerUpType : PowerUpType.values())
                        completions.add(powerUpType.name());
                    break;
                case "additemreq":
                    completions.add("Material Name");
                    completions.add("STONE");
                    completions.add("EMERALD");
                    break;
                case "addxpreq":
                    completions.add("Amount");
                    completions.add("10");
                    completions.add("250");
                    break;
            }
        }
        else if (args.length == 5) {
            if (args[0].toLowerCase().equals("additemreq")) {
                completions.add("Amount");
                completions.add("1");
                completions.add("2");
                completions.add("3");
                completions.add("5");
                completions.add("8");
                completions.add("10");
                completions.add("16");
                completions.add("32");
                completions.add("64");
            }
        }
        else if (args.length == 6) {
            if (args[0].toLowerCase().equals("additemreq")) {
                completions.add("Custom model data");
                completions.add("1");
                completions.add("2");
                completions.add("3");
            }
        }
        return completions;
    }
}
