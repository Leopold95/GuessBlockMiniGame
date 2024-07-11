package me.leopold95.guessblock.core.tasks;

import me.leopold95.guessblock.abstraction.RepeatingTask;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class SelectEnemyBlockTimer extends RepeatingTask {
    private long secondsPassed = 0;
    private long maxSeconds;
    private Plugin plugin;

    public SelectEnemyBlockTimer(Plugin plugin, int taskTickDelay, long maxSeconds, Player caller, Player target) {
        super(plugin, 0, taskTickDelay);

        this.plugin = plugin;
        this.maxSeconds = maxSeconds;

        run();
    }

    @Override
    public void run() {
        if(secondsPassed == maxSeconds){
            cancel();
            return;
        }

        plugin.getLogger().warning("updating");

        secondsPassed++;
    }
}
