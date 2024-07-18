package me.leopold95.guessblock.abstraction;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public abstract class LateTask implements Runnable {
    private int taskId;

    public LateTask(Plugin plugin, int executeTime) {
        taskId = Bukkit.getScheduler().runTaskLater(plugin, this, executeTime).getTaskId();
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public boolean isCanceled(){
        return Bukkit.getScheduler().isCurrentlyRunning(taskId);
    }
}