package me.leopold95.guessblock.core.tasks;

import me.leopold95.guessblock.abstraction.RepeatingTask;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class SelectEnemyBlockTimer extends RepeatingTask {
    private long secondsPassed = 0;
    private int maxTicks;
    private Plugin plugin;

    public SelectEnemyBlockTimer(Plugin plugin, int startIn, int taskTickDelay) {
        super(plugin, startIn, taskTickDelay);

        this.plugin = plugin;
        this.maxTicks = maxTicks;

        run();
    }

    @Override
    public void run() {
        if(secondsPassed == maxTicks){
            cancel();
            return;
        }

        plugin.getLogger().log(Level.ALL, "updating");

        secondsPassed++;
    }
}
