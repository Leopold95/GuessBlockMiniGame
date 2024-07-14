package me.leopold95.guessblock.core.guessblock;

import customblockdata.CustomBlockData;
import lombok.*;
import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
    private Location findableBlockFirst, findableBlockSecond;
    private Location center;
    @Setter
    private boolean isBusy;
    private ArrayList<Location> firstReplaceBlocks, secondReplaceBlocks;
    private Location firstSpawn, secondSpawn;
    private ArrayList<Block> firstTrapdoorsList, secondTrapdoorsList;
    @Setter
    private Player firstPlayer, secondPlayer;

    private final static String REPLACE_CONFIG_PART = "replace-location";

    public void clearBannedTrapdoors(){
        firstTrapdoorsList.clear();
    }

    public void setFirstGuessBlock(Material material){
        findableBlockFirst.getBlock().setType(material);
    }

    public void setSecondGuessBlock(Material material){
        findableBlockSecond.getBlock().setType(material);
    }

    /**
     * Используется для информирования, что былы скрыт один из люков во время игры
     * @param trapdoor
     */
    public void removeFirstTrapdoor(Block trapdoor){
        firstTrapdoorsList.remove(trapdoor);

        if(firstTrapdoorsList.size() == 1){
            Material lastBlock = firstTrapdoorsList.get(0).getLocation().subtract(0, 1, 0).getBlock().getType();
            GuessBlock.getPlugin().getLogger().warning("last first block " + lastBlock.name() + " find " + findableBlockSecond.getBlock().getType());

            String guessStrMaterial = firstPlayer.getPersistentDataContainer().get(GuessBlock.getPlugin().keys.BLOCK_TO_GUESS, PersistentDataType.STRING);
            Material guessMaterial = Material.valueOf(guessStrMaterial);

            if(lastBlock.name().equals(guessMaterial.name())){
                firstPlayer.sendMessage(Config.getMessage("game.win"));
                GuessBlock.getPlugin().engine.getGame().endGame(this, firstPlayer, secondPlayer);
            }
            else {
                secondPlayer.sendMessage(Config.getMessage("game.loose"));
                GuessBlock.getPlugin().engine.getGame().endGame(this, firstPlayer, secondPlayer);
            }
        }
    }

    /**
     * Используется для информирования, что былы скрыт один из люков во время игры
     * @param trapdoor
     */
    public void removeSecondTrapdoor(Block trapdoor){
        secondTrapdoorsList.remove(trapdoor);

        if(secondTrapdoorsList.size() == 1){
            Material lastBlock = secondTrapdoorsList.get(0).getLocation().subtract(0, 1, 0).getBlock().getType();
            GuessBlock.getPlugin().getLogger().warning("last second block " + lastBlock.name() + " find " + findableBlockSecond.getBlock().getType());

            String guessStrMaterial = secondPlayer.getPersistentDataContainer().get(GuessBlock.getPlugin().keys.BLOCK_TO_GUESS, PersistentDataType.STRING);
            Material guessMaterial = Material.valueOf(guessStrMaterial);

            if(lastBlock.name().equals(guessMaterial.name())){
                secondPlayer.sendMessage(Config.getMessage("game.win"));
                GuessBlock.getPlugin().engine.getGame().endGame(this, firstPlayer, secondPlayer);
            }
            else {
                firstPlayer.sendMessage(Config.getMessage("game.loose"));
                GuessBlock.getPlugin().engine.getGame().endGame(this, firstPlayer, secondPlayer);
            }
        }
    }

    /**
     * Устанавливает люки над блоками для отгадки
     */
    public void setTrapDors(){
        for(Location block: firstReplaceBlocks){
            Location trapDoorLocation = block.clone().add(0, 1, 0);

            firstTrapdoorsList.add(trapDoorLocation.getBlock());

            trapDoorLocation.getBlock().setType(Material.ACACIA_TRAPDOOR);
            PersistentDataContainer data = new CustomBlockData(trapDoorLocation.getBlock(), GuessBlock.getPlugin());
            //data.set(GuessBlock.getPlugin().keys.CAN_CLOSE_TRAPDOOR, PersistentDataType.BOOLEAN, true);
            data.set(GuessBlock.getPlugin().keys.CAN_CLOSE_FIST_TRAPDOOR, PersistentDataType.BOOLEAN, true);
        }

        for(Location block: secondReplaceBlocks){
            Location trapDoorLocation = block.clone().add(0, 1, 0);

            secondTrapdoorsList.add(trapDoorLocation.getBlock());

            trapDoorLocation.getBlock().setType(Material.ACACIA_TRAPDOOR);
            PersistentDataContainer data = new CustomBlockData(trapDoorLocation.getBlock(), GuessBlock.getPlugin());
            //data.set(GuessBlock.getPlugin().keys.CAN_CLOSE_TRAPDOOR, PersistentDataType.BOOLEAN, true);
            data.set(GuessBlock.getPlugin().keys.CAN_CLOSE_SECOND_TRAPDOOR, PersistentDataType.BOOLEAN, true);
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
                        Config.getArenasConfig().getDouble(configPart + ".spawn.second.z")),
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                null
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

        return list;
    }
}
