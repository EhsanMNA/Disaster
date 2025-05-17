package me.ehsanmna.disaster.service;

import me.ehsanmna.disaster.DisasterPlugin;
import me.ehsanmna.disaster.model.arena.Arena;
import me.ehsanmna.disaster.model.arena.ArenaPlayer;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaWaitingService {

    private final Arena arena;
    private final ArenaService arenaService;

    private int timer;

    public ArenaWaitingService(ArenaService arenaService) {
        this.arena = arenaService.getArena();
        this.arenaService = arenaService;
        this.timer = arena.getArenaConfig().waitingTime();
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public boolean isWaitingFull(){
        return arena.getArenaHandler().getPlayers().size() == arena.getMaxPlayers();
    }

    private boolean canTimerStart(){
        return (arena.getArenaHandler().getPlayers().size() >= arena.getMaxPlayers() / 2) || arenaService.isDebug();
    }

    private boolean timerStarted() {
        return timer < arena.getArenaConfig().waitingTime();
    }

    /**
        * Adding player to waiting lobby!
     **/
    public void handleWaiting(){
        if (!timerStarted() && canTimerStart()) startTimer();
    }

    /**
     * Starting the countdown timer.
     **/
    public void startTimer(){
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!canTimerStart()){
                    arena.getArenaHandler().announce("arena-fill");
                    cancel();
                    return;
                }
                // start the arena
                if (timer == 0) {
                    start();
                    cancel();
                    return;
                }

                if (timer > 5 && isWaitingFull()) timer = 5;
                else if (timer > arena.getArenaConfig().waitingTime() / 2 && arena.getArenaHandler().getPlayers().size() >= arena.getMaxPlayers() / 1.5) timer = 30;

                if (timer % 10 == 0 || timer <= 5){
                    arena.getArenaHandler().announce("arena-countdown", timer+"");
                    for (ArenaPlayer arenaPlayer : arena.getArenaHandler().getPlayers())
                        arenaPlayer.getPlayer().playSound(arenaPlayer.getPlayer().getLocation() ,Sound.ENTITY_CHICKEN_EGG, 10 ,2);
                }

                timer--;
            }
        }.runTaskTimer(DisasterPlugin.getInstance(), 0, 20);
    }

    /**
     * Start the arena.
     * | Move players form waiting in to game service.
     **/
    public void start(){
        timer = arena.getArenaConfig().waitingTime();
        arenaService.startGame();
    }

}
