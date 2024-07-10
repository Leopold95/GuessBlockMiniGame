package me.leopold95.guessblock.core.guessblock;

import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.Config;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class Game {
    private GuessBlock plugin;
    public Game(GuessBlock plugin){
        this.plugin = plugin;
    }

    /**
     * Подготовка к начу игры в угадайку
     * @param caller призывник к игре
     * @param target ответчик на игру
     * @param arena арена игры
     */
    public void startGame(Player caller, Player target, Arena arena){
        updateCurrentArena(arena);

        caller.getPersistentDataContainer().set(plugin.keys.CURRENT_ENEMY, PersistentDataType.STRING, target.getName());
        target.getPersistentDataContainer().set(plugin.keys.CURRENT_ENEMY, PersistentDataType.STRING, caller.getName());

        caller.getPersistentDataContainer().set(plugin.keys.SELECTING_BLOCK_TO_GUESS, PersistentDataType.BOOLEAN, true);
        target.getPersistentDataContainer().set(plugin.keys.SELECTING_BLOCK_TO_GUESS, PersistentDataType.BOOLEAN, true);

        caller.teleport(arena.getFirstSpawn().toCenterLocation());
        target.teleport(arena.getSecondSpawn().toCenterLocation());

        caller.sendMessage(Config.getMessage("commands.game-select-guess-block-waiting"));
        target.sendMessage(Config.getMessage("commands.game-select-guess-block-waiting"));



        //TODO fix this
        //int selectBlockTime = Config.getInt("time-to-select-enemy-block");
        //new SelectEnemyBlockTimer(plugin, 0, 20);
    }


    /**
     * обновляет (подготовляивает) арену к новой игре
     * @param arena арена
     */
    private void updateCurrentArena(Arena arena){
        arena.setFirstPlayer(null);
        arena.setSecondPlayer(null);

        arena.getFirstBlocksList().clear();
        arena.getSecondBlocksList().clear();
        arena.clearBannedTrapdoors();

        //update fidable blocks
        arena.updateRandomBlocks(plugin.engine.getRandomBlockList());

        //update target blocks
        arena.setBlocksToFind(Material.DIAMOND_BLOCK, Material.DIAMOND_BLOCK);

        //update hatches
        arena.setTrapDors();
    }
}
