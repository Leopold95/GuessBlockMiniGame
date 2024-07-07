package me.leopold95.guessblock.core.guessblock;

import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.Config;
import me.leopold95.guessblock.models.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;

public class Game {
    private GuessBlock plugin;
    public Game(GuessBlock plugin){
        this.plugin = plugin;
    }

    public void tryCreateGame(Player caller, Player target){
        boolean hasEmpty = plugin.engine.getArenas().stream().anyMatch(a -> !a.isBusy());
        if(!hasEmpty){

            return;
        }
    }

    public void selectBlocksToFind(Player caller, Player target){

    }


    /**
     * Начальная подготовка перед началом игры
     * @param callerName имя того, кто выщывает на дуэль
     * @param targetName имя того, кто принимает дуэль
     */
    public void tryAcceptGame(String callerName, String targetName){
        if(Bukkit.getPlayer(callerName) == null || Bukkit.getPlayer(targetName) == null){
            Objects.requireNonNull(Bukkit.getPlayer(callerName)).sendMessage(Config.getMessage("bad-duel-begin"));
            Objects.requireNonNull(Bukkit.getPlayer(targetName)).sendMessage(Config.getMessage("bad-duel-begin"));

            return;
        }

        Player caller = Bukkit.getPlayer(callerName);
        Player target = Bukkit.getPlayer(targetName);

        if(callerName.equals(targetName)){
            caller.sendMessage(Config.getMessage("commands.cant-accept-self"));
            return;
        }

        var optArena = getEmptyArena();

        if(optArena.isEmpty()){
            target.sendMessage(Config.getMessage("game.no-empty-arena"));
            caller.sendMessage(Config.getMessage("game.no-empty-arena"));
            return;
        }

        String callerMessage = Config.getMessage("commands.duel-accepted-caller").replace("%name%", targetName);
        target.sendMessage(callerMessage);

        String targetMessage = Config.getMessage("commands.duel-accepted-aim").replace("%name%", targetName);
        caller.sendMessage(targetMessage);

        teleportToEmptyArena(caller, target, optArena.get());
    }

    /**
     * Выбирает пустую арену из возможных, подготовливает ее к игре
     */
    public void tryPrepareEmptyArena(){
        boolean hasEmpty = plugin.engine.getArenas().stream().anyMatch(a -> !a.isBusy());
        if(!hasEmpty){
            return;

        }
    }

    /**
     * Вернет свободную арену или нет
     * @return
     */
    private Optional<Arena> getEmptyArena(){
        return plugin.engine.getArenas().stream().filter(a -> !a.isBusy()).findFirst();
    }

    private void teleportToEmptyArena(Player caller, Player target, int arenaId){
        Arena arena = plugin.engine.getArenas().get(arenaId);
        caller.teleport(arena.getFirstSpawn());
        target.teleport(arena.getSecondSpawn());
    }

    private void teleportToEmptyArena(Player caller, Player target, Arena arena){
        caller.teleport(arena.getFirstSpawn());
        target.teleport(arena.getSecondSpawn());
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
