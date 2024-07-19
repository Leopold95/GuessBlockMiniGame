package me.leopold95.guessblock.core.tasks;

import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.abstraction.LateTask;
import me.leopold95.guessblock.abstraction.RepeatingTask;
import me.leopold95.guessblock.core.guessblock.Arena;
import me.leopold95.guessblock.enums.DuelResult;
import org.bukkit.plugin.Plugin;

public class GlobalTimerTask extends LateTask {
    private Arena arena;
    private GuessBlock plugin;

    public GlobalTimerTask(GuessBlock plugin, Arena arena, int executeAfter) {
        super(plugin,executeAfter * 20);

        this.plugin = plugin;
        this.arena = arena;

        run();
    }

    @Override
    public void run() {
//        if(arena.getSecondPlayer() != null && arena.getSecondPlayer() != null) {
//            plugin.engine.getGame().endGame(arena, arena.getFirstPlayer(), arena.getSecondPlayer(), DuelResult.TIMER);
//        }
    }
}
