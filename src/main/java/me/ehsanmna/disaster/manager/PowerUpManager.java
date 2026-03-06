package me.ehsanmna.disaster.manager;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.powerup.PowerUp;
import me.ehsanmna.disaster.model.powerup.PowerUpType;
import me.ehsanmna.disaster.model.powerup.impl.*;

import java.util.HashMap;
import java.util.Map;

public class PowerUpManager {

    private final DisasterPlugin plugin;
    private static final Map<PowerUpType, PowerUp> powerUps = new HashMap<>();

    public PowerUpManager(DisasterPlugin plugin) {
        this.plugin = plugin;
        powerUps.put(PowerUpType.CLEAR, new ClearPowerUp(plugin));
        powerUps.put(PowerUpType.JUMP, new JumpPowerUp(plugin));
        powerUps.put(PowerUpType.HEALTH, new HealthPowerUp(plugin));
        powerUps.put(PowerUpType.INVISIBLE, new InvisiblePowerUp(plugin));
        powerUps.put(PowerUpType.SPEED, new SpeedPowerUp(plugin));
    }

    public PowerUp getPowerUp(PowerUpType type){
        return powerUps.get(type);
    }

}
