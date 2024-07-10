package me.leopold95.guessblock.abstraction;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public abstract class RepeatingTask implements Runnable {
    private int taskId;

    public RepeatingTask(Plugin plugin, int startIn, int taskTickDelay) {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, startIn, taskTickDelay);
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public boolean isCanceled(){
        return Bukkit.getScheduler().isCurrentlyRunning(taskId);
    }
}