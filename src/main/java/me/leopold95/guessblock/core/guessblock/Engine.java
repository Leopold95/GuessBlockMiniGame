package me.leopold95.guessblock.core.guessblock;

import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.Config;
import me.leopold95.guessblock.models.ArenaModel;
import org.bukkit.Material;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.logging.Level;

public class Engine {
    private GuessBlock plugin;

    //список арен доступных для игры
    private ArrayList<ArenaModel> arenas;
    //список рандомных блоков, доступных для игры
    private ArrayList<Material> possibleBlocks;

    private ArenasManager arenasManager;
    private ConfigParser configParser;

    public Engine(GuessBlock plugin){
        this.plugin = plugin;

        arenasManager = new ArenasManager(this.plugin);
        configParser = new ConfigParser();
    }

    public void loadAllData(){
        arenas = arenasManager.loadAll();

        possibleBlocks = loadRandomBlocksList();


        plugin.getLogger().log(Level.FINE, Config.getMessage("loading.gc-called"));
        System.gc();
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
