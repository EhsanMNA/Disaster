package me.ehsanmna.disaster.manager;

import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.machine.PowerUpMachine;
import me.ehsanmna.disaster.service.PowerUpMachineService;

import java.util.Map;

public class PowerUpMachineManager {

    private final PowerUpMachineService powerUpMachineService = new PowerUpMachineService();
    private final ArenaManager arenaManager;

    public PowerUpMachineManager(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
    }

    public void addMachine(PowerUpMachine machine) {
        machine.getArena().getMachines().put(machine.getName(), machine);
    }

    public PowerUpMachine getMachine(Arena arena, String machineName) {
        return arena.getMachines().get(machineName);
    }

    public boolean removeMachine(Arena arena, String machineName) {
        Map<String, PowerUpMachine> machines = arena.getMachines();
        if (machines != null) {
            machines.remove(machineName);
            return true;
        }
        return false;
    }

    public Map<String, PowerUpMachine> getMachinesForArena(String arenaName) {
        Arena arena = arenaManager.getArena(arenaName);
        return arena.getMachines();
    }

    public boolean hasMachineWithName(Arena arena, String name){
        return arena.getMachines().containsKey(name.toLowerCase());
    }

    public PowerUpMachine createPowerUpMachine(Arena arena, String machineName){
        PowerUpMachine machine = powerUpMachineService.createMachine(arena, machineName.toLowerCase());
        arena.getMachines().put(machineName,machine);
        return machine;
    }

    public PowerUpMachineService getPowerUpMachineService() {
        return powerUpMachineService;
    }

}