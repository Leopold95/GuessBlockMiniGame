package me.leopold95.guessblock.core.guessblock;

import lombok.Getter;
import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.Config;
import me.leopold95.guessblock.core.Debug;
import me.leopold95.guessblock.models.Arena;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class Engine {
    private GuessBlock plugin;

    private final String ARENAS_CFG = "arenas";

    //список арен доступных для игры
    @Getter
    private ArrayList<Arena> arenas;
    @Getter
    //список рандомных блоков, доступных для игры
    private ArrayList<Material> randomBlockList;

    @Getter
    private List<String> arenaTypes;

    @Getter
    private ConfigParser configParser;
    @Getter
    private Game game;

    public Engine(GuessBlock plugin){
        this.plugin = plugin;

        configParser = new ConfigParser();
        game = new Game(this.plugin);
    }

    public void loadAllData(){
        arenaTypes = Config.getStringList("arena-types");
        arenas = loadAllArenas();
        randomBlockList = loadRandomBlocksList();

        plugin.getLogger().log(Level.FINE, Config.getMessage("loading.gc-called"));
        System.gc();
    }

    /**
     * Инициализирует список всех доступных арен из конфига
     * @return null или список арен
     */
    public ArrayList<Arena> loadAllArenas(){
        plugin.getLogger().log(Level.INFO, Config.getMessage("loading.arenas-begin"));

        ConfigurationSection arenasSection = Config.getArenasSection(ARENAS_CFG);

        if(arenasSection == null || arenasSection.getKeys(false).isEmpty()){
            plugin.getLogger().log(Level.WARNING, Config.getMessage("loading.arenas-bad"));
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return null;
        }

        ArrayList<Arena> arenas = new ArrayList<>();

        for(String key: arenasSection.getKeys(false)){
            try {
                Arena model = Arena.parseModel(ARENAS_CFG + "." + key, plugin);
                arenas.add(model);
                Debug.message(model.toString());
            }
            catch (Exception exp){
                plugin.getLogger().log(Level.WARNING, Config.getMessage("bad-arena-load").replace("%error%", exp.getMessage()));
            }
        }


        plugin.getLogger().log(Level.INFO, Config.getMessage("loading.arenas-end").replace("%count%", String.valueOf(arenas.size())));
        return arenas;
    }

    /**
     * Телепортирует игрока в центр арены
     */
    public void teleportToArenaCenter(int arenaId, Player player){
        Arena arena = arenas.get(arenaId);
        teleportToArenaCenter(arena, player);
    }

    /**
     * Телепортирует игрока в центр арены
     */
    public void teleportToArenaCenter(Arena arena, Player player){
        player.teleport(arena.getCenter());
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
