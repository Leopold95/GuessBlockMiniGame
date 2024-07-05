package me.leopold95.guessblock.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.logging.Level;

@Getter
@AllArgsConstructor
@ToString
public class Arena {
    private String name;
    private int replaceBlocksHeight;
    private Location enemyBlockLocation_1;
    private Location enemyBlockLocation_2;
    private Location center;
    @Setter
    private boolean isBusy;

    private ArrayList<Location> firstReplaceBlocks;
    private ArrayList<Location> secondReplaceBlocks;

    private Location firstSpawn;
    private Location secondSpawn;

    /**
     * Прогрузка доступных арен
     * @param configPart конфиг с аренами
     * @return null или список с 1 и более аренами
     */
     public static Arena parseModel(String configPart, GuessBlock plugin){
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

        return new Arena(
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
                loadReplaceBlocks(center, nsORwe + ".first-part", replaceHeight, plugin),
                loadReplaceBlocks(center, nsORwe + ".second-part", replaceHeight, plugin),
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
    private static ArrayList<Location> loadReplaceBlocks(Location arenaCenter, String configPart, int replaceHeight, GuessBlock plugin){
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
}
