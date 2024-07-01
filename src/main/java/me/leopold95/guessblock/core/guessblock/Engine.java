package me.leopold95.guessblock.core.guessblock;

import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.models.ArenaModel;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;

public class Engine {
    private GuessBlock plugin;

    //список арен доступных для игры
    private ArrayList<ArenaModel> arenas;
    //список рандомных блоков, доступных для игры
    private ArrayList<String> possibleBlocks;

    private ArenasManager arenasManager;
    private ConfigParser configParser;

    public Engine(GuessBlock plugin){
        this.plugin = plugin;

        arenasManager = new ArenasManager(this.plugin);
        configParser = new ConfigParser();
    }




}
