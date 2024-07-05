package me.leopold95.guessblock.core.guessblock;

import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.Config;
import me.leopold95.guessblock.core.Debug;
import me.leopold95.guessblock.models.ArenaModel;
import me.leopold95.guessblock.models.LocationModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ArenasManager {
    private GuessBlock plugin;

    private final String ARENAS = "arenas";

    public ArenasManager(GuessBlock plugin){
        this.plugin = plugin;
    }

    /**
     * Инициализирует список всех доступных арен из конфига
     * @return null или список арен
     */
    public ArrayList<ArenaModel> loadAll(){
        plugin.getLogger().log(Level.INFO, Config.getMessage("loading.arenas-begin"));

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
     * Прогрузка доступных арен
     * @param configPart конфиг с аренами
     * @return null или список с 1 и более аренами
     */
    private ArenaModel parseModel(String configPart){
        String nsORwe = null;
        String orientation = Config.getArenasConfig().getString(configPart + ".orientation");
        int replaceHeight = Config.getArenasConfig().getInt(configPart + ".replace-blocks-height");

        if(orientation.equals("ns"))
            nsORwe = "ns-locations";
        else
            nsORwe = "we-locations";

        Location center = new Location(
                Bukkit.getWorld(Config.getString("arenas-world")),
                Config.getArenasConfig().getDouble(configPart + ".center.x"),
                Config.getArenasConfig().getDouble(configPart + ".center.y"),
                Config.getArenasConfig().getDouble(configPart + ".center.z"));

        return new ArenaModel(
            Config.getArenasConfig().getString(configPart + ".name"),
            replaceHeight,
            new Location(
                    Bukkit.getWorld(Config.getString("arenas-world")),
                    Config.getArenasConfig().getDouble(configPart + ".enemy-block-loc_1.x"),
                    Config.getArenasConfig().getDouble(configPart + ".enemy-block-loc_1.y"),
                    Config.getArenasConfig().getDouble(configPart + ".enemy-block-loc_1.z")),
            new Location(
                    Bukkit.getWorld(Config.getString("arenas-world")),
                    Config.getArenasConfig().getDouble(configPart + ".enemy-block-loc_2.x"),
                    Config.getArenasConfig().getDouble(configPart + ".enemy-block-loc_2.y"),
                    Config.getArenasConfig().getDouble(configPart + ".enemy-block-loc_2.z")),
            center,
            false,
            loadReplaceBlocks(center, nsORwe + ".first-part", replaceHeight),
            loadReplaceBlocks(center, nsORwe + ".second-part", replaceHeight),
            new Location(
                    Bukkit.getWorld(Config.getString("arenas-world")),
                    Config.getArenasConfig().getDouble(configPart + ".spawn.first.x"),
                    Config.getArenasConfig().getDouble(configPart + ".spawn.first.y"),
                    Config.getArenasConfig().getDouble(configPart + ".spawn.first.z")),
            new Location(
                    Bukkit.getWorld(Config.getString("arenas-world")),
                    Config.getArenasConfig().getDouble(configPart + ".spawn.second.x"),
                    Config.getArenasConfig().getDouble(configPart + ".spawn.second.y"),
                    Config.getArenasConfig().getDouble(configPart + ".spawn.second.z"))
            );
    }

    /**
     * Генерирует список позиций блоков, которые нужно заменить
     * @return null иил список позиуий блоков, которые нужно заменить
     */
    private ArrayList<Location> loadReplaceBlocks(Location arenaCenter, String configPart, int replaceHeight){
        ArrayList<Location> list = new ArrayList<>();

        Location blocksHeight = arenaCenter.clone().add(0, replaceHeight, 0);

        ConfigurationSection partSection = Config.getSection(configPart);

        if(partSection == null || partSection.getKeys(false).isEmpty()){
            plugin.getLogger().log(Level.WARNING, Config.getMessage("loading.replace-blocks-bad"));
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return null;
        }

        for(String key: partSection.getKeys(false)){
            int x = Config.getInt(configPart + "." + key + ".x");
            int z = Config.getInt(configPart + "." + key + ".z");

            list.add(blocksHeight.clone().add(x, 0, z));
        }

        return list;
    }

    public void tpToArena(Player player, int id){
        ArenaModel arena = plugin.engine.getArenas().get(id);
        player.teleport(arena.getCenter());
    }

}
