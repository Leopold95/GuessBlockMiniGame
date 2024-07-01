package me.leopold95.guessblock.core.guessblock;

import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.Config;
import me.leopold95.guessblock.models.ArenaModel;
import me.leopold95.guessblock.models.LocationModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ArenasManager {
    private GuessBlock plugin;

    private final String ARENAS = "arenas";

    private ArrayList<Material> randomBlocksList;

    public ArenasManager(GuessBlock plugin){
        this.plugin = plugin;

        randomBlocksList = loadRandomBlocksList();
    }


    public ArrayList<ArenaModel> loadAll(){
        plugin.getLogger().log(Level.FINE, Config.getMessage("loading.arenas-begin"));

        ConfigurationSection arenasSection = Config.getArenasSection(ARENAS);

        if(arenasSection == null || arenasSection.getKeys(false).isEmpty()){
            plugin.getLogger().log(Level.WARNING, Config.getMessage("loading.arenas-bad"));
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return null;
        }

        ArrayList<ArenaModel> arenas = new ArrayList<>();

        for(String key: arenasSection.getKeys(false)){
            try {
                ArenaModel model = parseModel(ARENAS + "." + key);
                arenas.add(model);
            }
            catch (Exception exp){
                plugin.getLogger().log(Level.CONFIG, Config.getMessage("bad-arena-load").replace("%error%", exp.getMessage()));
            }
        }


        plugin.getLogger().log(Level.FINE, Config.getMessage("loading.arenas-end").replace("%count%", String.valueOf(arenas.size())));
        return null;
    }

    private ArrayList<Material> loadRandomBlocksList(){
        ArrayList<Material> list = new ArrayList<>();
        for(String s: Config.getStringList("blocks-list")){
            list.add(Material.valueOf(s));
        }
        return list;
    }

    /**
     * Прогрузка доступных арен
     * @param configPart конфиг с аренами
     * @return null или список с 1 и более аренами
     */
    private ArenaModel parseModel(String configPart, String orientation){
        String nsORwe = null;

        if(orientation.equals("ns"))
            nsORwe = "ns-locations";
        else
            nsORwe = "we-locations";

        return new ArenaModel(
            Config.getString(configPart + ".name"),
            new LocationModel(
                    Config.getInt(configPart + ".center.x"),
                    Config.getInt(configPart + ".center.y"),
                    Config.getInt(configPart + ".center.z")),
            Config.getString(configPart + ".orientation"),
            false,
            loadReplaceBlocks(nsORwe + ".first-part"),
            loadReplaceBlocks(nsORwe + ".second-part"),
            new Location(
                    Bukkit.getWorld(Config.getString("arenas-world")),
                    Config.getInt(configPart + ".center.x"),
                    Config.getInt(configPart + ".center.y"),
                    Config.getInt(configPart + ".center.z")),
            new Location(
                    Bukkit.getWorld(Config.getString("arenas-world")),
                    Config.getInt(configPart + ".center.x"),
                    Config.getInt(configPart + ".center.y"),
                    Config.getInt(configPart + ".center.z"))
            );
    }

    /**
     * Генерирует список позиций блоков, которые нужно заменить
     * @return null иил список позиуий блоков, которые нужно заменить
     */
    private ArrayList<Location> loadReplaceBlocks(String configPart){
        ArrayList<Location> list = new ArrayList<>();

        ConfigurationSection partSection = Config.getSection(configPart);

        if(partSection == null || partSection.getKeys(false).isEmpty()){
            plugin.getLogger().log(Level.WARNING, Config.getMessage("loading.replace-blocks-bad"));
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return null;
        }

        for(String key: partSection.getKeys(false)){

        }

        return null;
    }

}
