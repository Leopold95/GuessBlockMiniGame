package me.leopold95.guessblock.core.tasks;

import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.abstraction.RepeatingTask;
import me.leopold95.guessblock.core.SoundPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class SelectEnemyBlockTimer extends RepeatingTask {
    private long secondsPassed = 0;
    private long maxSeconds;
    private Plugin plugin;

    private Player caller, target;

    public SelectEnemyBlockTimer(Plugin plugin, int taskTickDelay, long maxSeconds, Player caller, Player target) {
        super(plugin, 0, taskTickDelay);

        this.plugin = plugin;
        this.maxSeconds = maxSeconds;

        this.caller = caller;
        this.target = target;

        run();
    }

    @Override
    public void run() {
        if(secondsPassed == maxSeconds){
            cancel();
            return;
        }

        SoundPlayer.play(caller, "block-selecting-timer");
        SoundPlayer.play(target, "block-selecting-timer");

        secondsPassed++;
    }
}
