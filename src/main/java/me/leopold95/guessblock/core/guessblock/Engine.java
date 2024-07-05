package me.leopold95.guessblock.core.guessblock;

import lombok.Getter;
import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.Config;
import me.leopold95.guessblock.models.ArenaModel;
import org.bukkit.Location;
import org.bukkit.Material;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;

public class Engine {
    private GuessBlock plugin;

    //список арен доступных для игры
    @Getter
    private ArrayList<ArenaModel> arenas;
    //список рандомных блоков, доступных для игры
    private ArrayList<Material> possibleBlocks;

    @Getter
    private ArenasManager arenasManager;
    private ConfigParser configParser;
    @Getter
    private Game game;

    public Engine(GuessBlock plugin){
        this.plugin = plugin;

        arenasManager = new ArenasManager(this.plugin);
        configParser = new ConfigParser();
        game = new Game(this.plugin);
    }

    public void loadAllData(){
        arenas = arenasManager.loadAll();

        possibleBlocks = loadRandomBlocksList();


        plugin.getLogger().log(Level.FINE, Config.getMessage("loading.gc-called"));
        System.gc();
    }

    /**
     * Расставляет рандомные блоки под люки
     * @param arenaId ид арены для обновления
     */
    public void placeRandom(int arenaId){
        ArenaModel model = arenas.get(arenaId);

        for(Location block: model.getFirstReplaceBlocks()){
            block.getBlock().setType(Material.ACACIA_LOG);
        }

        for(Location block: model.getSecondReplaceBlocks()){
            block.getBlock().setType(Material.DIRT);
        }
    }

    /**
     * Загружает список всех блоков, которые могут участвовать в игре
     * @return список блоков
     */
    private ArrayList<Material> loadRandomBlocksList(){
        ArrayList<Material> list = new ArrayList<>();
        for(String s: Config.getStringList("blocks-list")){
            list.add(Material.valueOf(s));
        }
        return list;
    }
}
