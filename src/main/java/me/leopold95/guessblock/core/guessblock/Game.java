package me.leopold95.guessblock.core.guessblock;

import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.Config;
import me.leopold95.guessblock.core.SoundPlayer;
import me.leopold95.guessblock.core.tasks.GlobalTimerTask;
import me.leopold95.guessblock.core.tasks.SelectEnemyBlockTimer;
import me.leopold95.guessblock.enums.DuelResult;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Objects;

public class Game {
    private GuessBlock plugin;

    private PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 99999, 1);

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

        SoundPlayer.play(caller, "game-selecting-blocks-start");
        SoundPlayer.play(target, "game-selecting-blocks-start");

        caller.addPotionEffect(blindness);
        target.addPotionEffect(blindness);

        //таймер ожидания выбора блоков для угадайки
        long seleBlockTime = Config.getLong("time-to-select-enemy-block");
        new SelectEnemyBlockTimer(plugin, 20, seleBlockTime, caller, target);

        int masGameDuration = Config.getInt("max-game-time");
        new GlobalTimerTask(plugin, arena, masGameDuration);

        //провека, что обы игрока выбрали блок для угадайки
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(!hasBothPlayersSelectedBlocks(caller, target)){
                endGame(arena, caller, target, DuelResult.GB_WASNT_SELECTED);
                return;
            }

            arena.setFirstPlayer(caller);
            arena.setSecondPlayer(target);

            caller.removePotionEffect(PotionEffectType.BLINDNESS);
            target.removePotionEffect(PotionEffectType.BLINDNESS);

            String callerBlock = caller.getPersistentDataContainer().get(plugin.keys.BLOCK_TO_GUESS, PersistentDataType.STRING);
            String targetBlock = target.getPersistentDataContainer().get(plugin.keys.BLOCK_TO_GUESS, PersistentDataType.STRING);

            arena.setFirstGuessBlock(Material.getMaterial(callerBlock));
            arena.setSecondGuessBlock(Material.getMaterial(targetBlock));

            arena.checkGuessBlocks();

            caller.sendMessage(Config.getMessage("game.start"));
            target.sendMessage(Config.getMessage("game.start"));

            SoundPlayer.play(caller, "game-started");
            SoundPlayer.play(target, "game-started");

        }, seleBlockTime * 20 + 2);

    }

    /**
     * Заканчивает игру
     * @param arena арена
     * @param caller игрок 1
     * @param target игрок 2
     */
    public void endGame(Arena arena, Player caller, Player target, DuelResult result){

        switch (result){
            case FIRST_WIN, GIVEUP_SECOND -> {
                caller.sendMessage(Config.getMessage("game.win"));
                Objects.requireNonNull(target).sendMessage(Config.getMessage("game.loose"));
            }
            case SECOND_WIN, GIVEUP_FIRST -> {
                caller.sendMessage(Config.getMessage("game.loose"));
                Objects.requireNonNull(target).sendMessage(Config.getMessage("game.win"));
            }
            case NON_WIN -> {
                caller.sendMessage(Config.getMessage("game.non-win"));
                Objects.requireNonNull(target).sendMessage(Config.getMessage("game.non-win"));
            }
            case TIMER -> {
                caller.sendMessage(Config.getMessage("game.timer"));
                Objects.requireNonNull(target).sendMessage(Config.getMessage("game.timer"));
            }
            case GB_WASNT_SELECTED -> {
                caller.sendMessage(Config.getMessage("game.end-guess-block-didnt-select"));
                Objects.requireNonNull(target).sendMessage(Config.getMessage("game.end-guess-block-didnt-select"));
            }
        }


        if (target != null) {
            target.sendMessage(Config.getMessage("game.end"));
            target.getPersistentDataContainer().remove(plugin.keys.CURRENT_ENEMY);
            target.getPersistentDataContainer().remove(plugin.keys.SELECTING_BLOCK_TO_GUESS);
            target.getPersistentDataContainer().remove(plugin.keys.BLOCK_TO_GUESS);

            teleportToSpawn(target);
            SoundPlayer.play(target, "game-ended");
        }

        caller.sendMessage(Config.getMessage("game.end"));
        caller.getPersistentDataContainer().remove(plugin.keys.CURRENT_ENEMY);
        caller.getPersistentDataContainer().remove(plugin.keys.SELECTING_BLOCK_TO_GUESS);
        caller.getPersistentDataContainer().remove(plugin.keys.BLOCK_TO_GUESS);

        teleportToSpawn(caller);
        SoundPlayer.play(caller, "game-ended");

        arena.setFirstPlayer(null);
        arena.setSecondPlayer(null);
    }

    /**
     * Телепорт игрока на спавн
     * @param player игрок
     */
    public void teleportToSpawn(Player player){
        Location spawnLocation = new Location(
                Bukkit.getWorld(Config.getString("spawn-location.world")),
                Config.getDouble("spawn-location.x"),
                Config.getDouble("spawn-location.y"),
                Config.getDouble("spawn-location.z"));

        player.teleport(spawnLocation);
    }

    /**
     * очистака всех PDC испольхуемых для игры
     * @param player игрок
     */
    public void clearPersistence(Player player){
        if(player.getPersistentDataContainer().has(plugin.keys.BLOCK_TO_GUESS))
            player.getPersistentDataContainer().remove(plugin.keys.BLOCK_TO_GUESS);

        if(player.getPersistentDataContainer().has(plugin.keys.DUEL_ACCEPT_WAITING_OF))
            player.getPersistentDataContainer().remove(plugin.keys.DUEL_ACCEPT_WAITING_OF);

        if(player.getPersistentDataContainer().has(plugin.keys.SELECTING_BLOCK_TO_GUESS))
            player.getPersistentDataContainer().remove(plugin.keys.SELECTING_BLOCK_TO_GUESS);

        if(player.getPersistentDataContainer().has(plugin.keys.CURRENT_ENEMY))
            player.getPersistentDataContainer().remove(plugin.keys.CURRENT_ENEMY);
    }

    /**
     * Когда искомый блок был закрыт люком
     * @param arena арена
     * @param trapdoors список открытых люков
     * @param whoRemoved игрок, который закрыд люк
     * @param enemy враг, закрывшего люк игрока
     */
    public void onTrapdoorClosed(Arena arena, ArrayList<Block> trapdoors, Player whoRemoved, Player enemy){
        if(trapdoors.size() != 1)
            return;

        Material lastBlock = trapdoors.get(0).getLocation().subtract(0, 1, 0).getBlock().getType();
        //GuessBlock.getPlugin().getLogger().warning("last second block " + lastBlock.name() + " find " + findableBlock.getBlock().getType());

        String guessStrMaterial = enemy.getPersistentDataContainer().get(GuessBlock.getPlugin().keys.BLOCK_TO_GUESS, PersistentDataType.STRING);
        Material blockToFind = Material.valueOf(guessStrMaterial);

        if(lastBlock.name().equals(blockToFind.name())){
//            whoRemoved.sendMessage(Config.getMessage("game.win"));
//            enemy.sendMessage(Config.getMessage("game.loose"));
            endGame(arena, whoRemoved, enemy, DuelResult.FIRST_WIN);
        }
        else {
//            whoRemoved.sendMessage(Config.getMessage("game.loose"));
//            enemy.sendMessage(Config.getMessage("game.win"));
            endGame(arena, whoRemoved, enemy, DuelResult.SECOND_WIN);
        }
    }

    /**
     * Проверяет что оба игрока выбрали блок для отгадки
     * @param caller игрок 1
     * @param target игрок 2
     * @return true | false
     */
    public boolean hasBothPlayersSelectedBlocks(Player caller, Player target){
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

        arena.getFirstTrapdoorsList().clear();
        arena.getSecondTrapdoorsList().clear();
        arena.clearBannedTrapdoors();

        //update fidable blocks
        //arena.updateRandomBlocks(plugin.engine.getRandomBlockList());

        //update target blocks
        arena.setBlocksToFind(Material.DIAMOND_BLOCK, Material.DIAMOND_BLOCK);

        //update hatches
        arena.setTrapdoors();

        //arena.setBlocksToGuess();
    }


}
