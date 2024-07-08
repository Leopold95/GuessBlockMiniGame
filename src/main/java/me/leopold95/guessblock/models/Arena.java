package me.leopold95.guessblock.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

@Getter
@AllArgsConstructor
@ToString
public class Arena {
    private String name;
    private String type;
    private int replaceBlocksHeight;
    private Location findableBlockFirst;
    private Location findableBlockSecond;
    private Location center;
    @Setter
    private boolean isBusy;

    private ArrayList<Location> firstReplaceBlocks;
    private ArrayList<Location> secondReplaceBlocks;

    private Location firstSpawn;
    private Location secondSpawn;

    private final static String REPLACE_CONFIG_PART = "replace-location";

    /**
     * Устанавливает люки над блоками для отгадки
     */
    public void setTrapDors(){
        for(Location block: firstReplaceBlocks){
            block.clone().add(0, 1, 0).getBlock().setType(Material.ACACIA_TRAPDOOR);
        }

        for(Location block: secondReplaceBlocks){
            block.clone().add(0, 1, 0).getBlock().setType(Material.ACACIA_TRAPDOOR);;
        }
    }

    /**
     * Устанавливает блоки, которые необходимо отгадать
     * @param first первый тип блока
     * @param second второй тип блока
     */
    public void setBlocksToFind(Material first, Material second){
        findableBlockFirst.getBlock().setType(first);
        findableBlockSecond.getBlock().setType(second);
    }

    /**
     * Расставляет рандомные блоки под люки
     * @param randomList список рандомных блоков
     */
    public void updateRandomBlocks(List<Material> randomList){
        Random random = new Random();

        for(Location block: firstReplaceBlocks){
            int randomMaterialId = random.nextInt(0, randomList.size());
            Material randomMaterial = randomList.get(randomMaterialId);
            block.getBlock().setType(randomMaterial);
        }

        for(Location block: secondReplaceBlocks){
            int randomMaterialId = random.nextInt(0, randomList.size());
            Material randomMaterial = randomList.get(randomMaterialId);
            block.getBlock().setType(randomMaterial);
        }
    }

    /**
     * Прогрузка доступных арен
     * @param configPart конфиг с аренами
     * @return null или список с 1 и более аренами
     */
     public static Arena parseModel(String configPart, GuessBlock plugin){
        int replaceHeight = Config.getArenasConfig().getInt(configPart + ".replace-blocks-height");

        String type = Config.getArenasConfig().getString(configPart + ".type");
        String name = Config.getArenasConfig().getString(configPart + ".name");

        if(!plugin.engine.getArenaTypes().contains(type)){
            plugin.getLogger().warning(Config.getMessage("loading.bad-type").replace("%name%", name));
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return null;
        }

        Location center = new Location(
                Bukkit.getWorld(Config.getString("arenas-world")),
                Config.getArenasConfig().getDouble(configPart + ".center.x"),
                Config.getArenasConfig().getDouble(configPart + ".center.y"),
                Config.getArenasConfig().getDouble(configPart + ".center.z"));

        return new Arena(
                name,
                type,
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
                loadReplaceBlocks(center, ".replace-list.first", replaceHeight, plugin, name, type),
                loadReplaceBlocks(center, ".replace-list.second", replaceHeight, plugin, name, type),
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
    private static ArrayList<Location> loadReplaceBlocks(Location arenaCenter, String partInCfg, int replaceHeight, GuessBlock plugin, String arenaName, String arenaType){
        ArrayList<Location> list = new ArrayList<>();

        Location blocksHeight = arenaCenter.clone().add(0, replaceHeight, 0);

        ConfigurationSection partSection = Config.getSection(REPLACE_CONFIG_PART);

        if(partSection == null || partSection.getKeys(false).isEmpty()){
            plugin.getLogger().log(Level.WARNING, Config.getMessage("loading.replace-blocks-bad"));
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return null;
        }


        for (String key: partSection.getKeys(false)){
            String type = Config.getString(REPLACE_CONFIG_PART + "." + key + ".type");

            if(!arenaType.equals(type)){
                continue;
            }

            List<String> posList = Config.getStringList(REPLACE_CONFIG_PART + "." + key + partInCfg);

            for (String pos: posList){
                String[] p = pos.split(":");

                try {
                    double x = Double.parseDouble(p[0]);
                    double z = Double.parseDouble(p[1]);

                    list.add(blocksHeight.clone().add(x, 0, z));
                }
                catch (Exception exp){
                    plugin.getLogger().warning("Cant parse replace location '"+ arenaName+"': " + exp.getMessage());
                }
            }
        }

        for(String key: partSection.getKeys(false)){
            int x = Config.getInt(partInCfg + "." + key + ".x");
            int z = Config.getInt(partInCfg + "." + key + ".z");

            list.add(blocksHeight.clone().add(x, 0, z));
        }

        return list;
    }
}
