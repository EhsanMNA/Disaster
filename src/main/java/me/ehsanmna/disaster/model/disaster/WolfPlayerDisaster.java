package me.ehsanmna.disaster.model.disaster;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Random;

public class WolfPlayerDisaster extends BaseDisaster{

    private ArenaPlayer wolfPlayer;
    private final Random random = new Random();

    public WolfPlayerDisaster(DisasterPlugin plugin, Arena arena) {
        super(plugin, "<gold><bold>WOLF PLAYER", "<white>If you see the wolf, RUN!", arena, DisasterType.WOLF_PLAYER);
    }

    @Override
    public void setup() {
        super.setup();
        List<ArenaPlayer> players = getArena().getArenaHandler().getPlayersPlaying();
        if (players.size() == 1) return;
        int i = random.nextInt(players.size());
        if (players.size() == 2) i = 0;
        wolfPlayer = players.get(i);

        Player player = wolfPlayer.getPlayer();
        player.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET));
        player.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        ItemStack itemStack = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        leatherArmorMeta.setColor(Color.RED);
        itemStack.setItemMeta(leatherArmorMeta);
        player.getEquipment().setLeggings(itemStack);
        player.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));

        player.setHealthScaled(true);
        player.setHealthScale(40);
        player.setHealth(40);

        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 3, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2, false, false));
    }

    @Override
    public void deActive() {
        super.deActive();

        if (getArena().getArenaHandler().getArenaService().isDebug())
            getPlugin().getLogger().info("Hot potato Disaster deactivated for arena: " + getArena().getName());
    }

    @Override
    public void act() {
        super.act();
        setup();
    }

    public ArenaPlayer getWolfPlayer() {
        return wolfPlayer;
    }

    public void setWolfPlayer(ArenaPlayer wolfPlayer) {
        this.wolfPlayer = wolfPlayer;
    }
}
