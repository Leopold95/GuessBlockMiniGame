package me.leopold95.guessblock.core.tasks;

import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.abstraction.RepeatingTask;
import me.leopold95.guessblock.core.guessblock.Arena;
import org.bukkit.plugin.Plugin;

public class GameTickerTask extends RepeatingTask {
    private GuessBlock plugin;
    private Arena arena;
    private int maxTime;
    private int timePassed;

    public GameTickerTask(GuessBlock plugin, int maxTime, Arena arena) {
        super(plugin, 0, 20);
        this.plugin = plugin;
        this.maxTime = maxTime;
        this.arena = arena;
    }

    @Override
    public void run() {
        arena.updateHolo(maxTime - timePassed);
        timePassed++;
    }
}
