package me.leopold95.guessblock.core.guessblock;

import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.Config;
import me.leopold95.guessblock.core.tasks.SelectEnemyBlockTimer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

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

        caller.sendTitle(Config.getMessage("game.game-select-guess-title"), "");
        target.sendTitle(Config.getMessage("game.game-select-guess-title"), "");

        //таймер ожидания выбора блоков для угадайки
        long seleBlockTime = Config.getLong("time-to-select-enemy-block");
        new SelectEnemyBlockTimer(plugin, 20, seleBlockTime, caller, target);

        //провека, что обы игрока выбрали блок для угадайки
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(!hasBothPlayersSelectedBlocks(caller, target)){
                caller.sendMessage(Config.getMessage("game.end-guess-block-didnt-select"));
                target.sendMessage(Config.getMessage("game.end-guess-block-didnt-select"));

                endGame(arena, caller, target);
            }

            String callerBlock = caller.getPersistentDataContainer().get(plugin.keys.BLOCK_TO_GUESS, PersistentDataType.STRING);
            String targetBlock = target.getPersistentDataContainer().get(plugin.keys.BLOCK_TO_GUESS, PersistentDataType.STRING);

            Material callerMaterial = Material.getMaterial(callerBlock);
            Material targetMaterial = Material.getMaterial(targetBlock);

            plugin.getLogger().warning(callerMaterial.toString() + ":" + targetMaterial.toString());

            

        }, seleBlockTime * 20 + 2);

    }

    public void endGame(Arena arena, Player caller, Player target){
        caller.getPersistentDataContainer().remove(plugin.keys.CURRENT_ENEMY);
        target.getPersistentDataContainer().remove(plugin.keys.CURRENT_ENEMY);

        caller.getPersistentDataContainer().remove(plugin.keys.SELECTING_BLOCK_TO_GUESS);
        target.getPersistentDataContainer().remove(plugin.keys.SELECTING_BLOCK_TO_GUESS);

        caller.getPersistentDataContainer().remove(plugin.keys.BLOCK_TO_GUESS);
        target.getPersistentDataContainer().remove(plugin.keys.BLOCK_TO_GUESS);
    }

    /**
     * Проверяет что оба игрока выбрали блок для отгадки
     * @param caller игрок 1
     * @param target игрок 2
     * @return true | false
     */
    private boolean hasBothPlayersSelectedBlocks(Player caller, Player target){
        boolean hasFirst = caller.getPersistentDataContainer().has(plugin.keys.SELECTING_BLOCK_TO_GUESS);
        boolean hasSecond = target.getPersistentDataContainer().has(plugin.keys.SELECTING_BLOCK_TO_GUESS);
        return (!hasFirst && !hasSecond);
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
