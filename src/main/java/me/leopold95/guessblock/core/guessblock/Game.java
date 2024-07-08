package me.leopold95.guessblock.core.guessblock;

import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.Config;
import me.leopold95.guessblock.models.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.Optional;

public class Game {
    private GuessBlock plugin;
    public Game(GuessBlock plugin){
        this.plugin = plugin;
    }

    public void startGame(Player caller, Player target, Arena arena){
        updateCurrentArena(arena);

        plugin.engine.teleportToEmptyArena(caller, target, arena);

        caller.getPersistentDataContainer().set(plugin.keys.CURRENT_ENEMY, PersistentDataType.STRING, target.getName());
        target.getPersistentDataContainer().set(plugin.keys.CURRENT_ENEMY, PersistentDataType.STRING, caller.getName());

        caller.getPersistentDataContainer().set(plugin.keys.SELECTING_BLOCK_TO_GUESS, PersistentDataType.BOOLEAN, true);
        target.getPersistentDataContainer().set(plugin.keys.SELECTING_BLOCK_TO_GUESS, PersistentDataType.BOOLEAN, true);

        caller.sendMessage(Config.getMessage("commands.game-select-guess-block-waiting"));
        target.sendMessage(Config.getMessage("commands.game-select-guess-block-waiting"));
    }


    private void updateCurrentArena(Arena arena){
        //update fidable blocks
        arena.updateRandomBlocks(plugin.engine.getRandomBlockList());

        //update target blocks
        arena.setBlocksToFind(Material.DIAMOND_BLOCK, Material.DIAMOND_BLOCK);

        //update hatches
        arena.setTrapDors();
    }
}
