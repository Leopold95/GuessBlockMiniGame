package me.leopold95.guessblock.core.tasks;

import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.abstraction.RepeatingTask;
import me.leopold95.guessblock.core.Config;
import me.leopold95.guessblock.core.SoundPlayer;
import me.leopold95.guessblock.core.guessblock.Arena;
import me.leopold95.guessblock.enums.DuelResult;
import org.bukkit.entity.Player;

import java.time.LocalTime;

public class GameTickerTask extends RepeatingTask {
    private GuessBlock plugin;
    private Arena arena;
    private int timePassed;

    private Player caller;
    private Player target;

    private final long selectBlockTime = Config.getLong("time-to-select-enemy-block");
    private final int maxGameTime = Config.getInt("max-game-time");

    private boolean wasGameStarted = false;

    public GameTickerTask(GuessBlock plugin, Arena arena, Player caller, Player target) {
        super(plugin, 0, 20);
        this.plugin = plugin;
        this.arena = arena;

        this.caller = caller;
        this.target = target;
    }

    @Override
    public void run() {
        boolean bothSelected = plugin.engine.getGame().hasBothPlayersSelectedBlocks(caller, target);

        plugin.getLogger().warning(LocalTime.now().toString());

        if(timePassed == selectBlockTime && !bothSelected){
            plugin.engine.getGame().endGame(arena, caller, target, DuelResult.GB_WASNT_SELECTED);
            SoundPlayer.play(caller, "block-selecting-timer");
            SoundPlayer.play(target, "block-selecting-timer");
            plugin.getLogger().warning("1");
            cancel();
            return;
        }

        if (timePassed <= selectBlockTime && bothSelected && !wasGameStarted) {
            wasGameStarted = true;
            plugin.engine.getGame().startGame(arena, caller, target);
            SoundPlayer.play(caller, "block-selecting-timer");
            SoundPlayer.play(target, "block-selecting-timer");
            plugin.getLogger().warning("2");
            return;
        }

        if(timePassed == maxGameTime){
            plugin.engine.getGame().endGame(arena, caller, target, DuelResult.TIMER);
            plugin.getLogger().warning("3");
            cancel();
            return;
        }

//        if(!wasGameStarted) {
//            plugin.getLogger().warning("4");
//            wasGameStarted = true;
//            plugin.engine.getGame().startGame(arena, caller, target);
//            return;
//        }

        plugin.getLogger().warning("5");
        arena.updateHolo(maxGameTime - timePassed);

        timePassed++;
    }


}
