package me.ehsanmna.disaster.service;

import de.tr7zw.nbtapi.NBTBlock;
import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.powerup.PowerUp;
import me.ehsanmna.disaster.model.powerup.PowerUpType;
import me.ehsanmna.disaster.model.powerup.impl.*;
import me.ehsanmna.disaster.model.machine.PowerUpMachine;
import me.ehsanmna.disaster.model.machine.PowerUpMachineImpl;
import org.bukkit.block.Block;

public class PowerUpMachineService {

    DisasterPlugin plugin = DisasterPlugin.getInstance();

    public PowerUpMachine createMachine(Arena arena, String machineName) {
        PowerUpType type = PowerUpType.NONE;
        PowerUp powerUp = createPowerUp(type);
        return new PowerUpMachineImpl(machineName, type.name() + " Machine", powerUp, plugin);
    }

    private PowerUp createPowerUp(PowerUpType type) {
        return switch (type) {
            case SPEED -> new SpeedPowerUp(plugin);
            case JUMP -> new JumpPowerUp(plugin);
            case CLEAR -> new ClearPowerUp(plugin);
            case HEALTH -> new HealthPowerUp(plugin);
            case INVISIBLE -> new InvisiblePowerUp(plugin);
            case NONE -> null;
            default -> null;
        };
    }

    public void setButtonLocation(PowerUpMachine machine, Block block){
        machine.setButtonLocation(block.getLocation());
        NBTBlock nbt = new NBTBlock(block);
        nbt.getData().setString("powerup_machine", machine.getName());
    }
}
