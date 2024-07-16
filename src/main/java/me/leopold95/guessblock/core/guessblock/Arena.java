package me.leopold95.guessblock.core.guessblock;

import customblockdata.CustomBlockData;
import lombok.*;
import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.Config;
import me.leopold95.guessblock.core.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.TrapDoor;
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
    private ArrayList<Block> firstReplaceBlocks, secondReplaceBlocks;
    private Location firstSpawn, secondSpawn;
    private ArrayList<Block> firstTrapdoorsList, secondTrapdoorsList;
    @Setter
    private Player firstPlayer, secondPlayer;

    private final static String REPLACE_CONFIG_PART = "replace-location";

//    /**
//     * Проверяет все текущие блоки в слотах рандома, и если нету блока для отгадки -
//     * заменяет рандомный блок на этот
//     */
//    public void setBlocksToGuess(){
//        if(!firstReplaceBlocks.contains(findableBlockFirst.getBlock())){
//            int random = new Random().nextInt(0, firstReplaceBlocks.size() - 1);
//            firstReplaceBlocks.set(random, findableBlockFirst.getBlock());
//        }
//
//        if(!secondTrapdoorsList.contains(findableBlockSecond.getBlock())){
//            int random = new Random().nextInt(0, firstReplaceBlocks.size() - 1);
//            secondTrapdoorsList.set(random, findableBlockSecond.getBlock());
//        }
//    }

    /**
     * Очищает списки люков
     */
    public void clearBannedTrapdoors(){
        firstTrapdoorsList.clear();
        secondTrapdoorsList.clear();
    }

    /**
     * Устанавливает материал первого блока для отгаки
     * @param material
     */
    public void setFirstGuessBlock(Material material){
        findableBlockFirst.getBlock().setType(material);
    }

    /**
     * Устанавливает материал второго блока для отгаки
     * @param material
     */
    public void setSecondGuessBlock(Material material){
        findableBlockSecond.getBlock().setType(material);
    }

    /**
     * Используется для информирования, что былы скрыт один из люков во время игры
     * @param trapdoor
     */
    public void removeFirstTrapdoor(Block trapdoor){
        firstTrapdoorsList.remove(trapdoor); //TODO убрать из коасса арены
        GuessBlock.getPlugin().engine.getGame().onTrapdoorClosed(this, firstTrapdoorsList, firstPlayer, secondPlayer);
    }

    /**
     * Используется для информирования, что былы скрыт один из люков во время игры
     * @param trapdoor
     */
    public void removeSecondTrapdoor(Block trapdoor){
        secondTrapdoorsList.remove(trapdoor); //TODO убрать из коасса арены
        GuessBlock.getPlugin().engine.getGame().onTrapdoorClosed(this, secondTrapdoorsList, secondPlayer, firstPlayer);
    }

    /**
     * Устанавливает люки над блоками для отгадки
     */
    public void setTrapdoors(){
        for(Block block: firstReplaceBlocks){
            Location trapDoorLocation = block.getLocation().clone().add(0, 1, 0);

            firstTrapdoorsList.add(trapDoorLocation.getBlock());

            trapDoorLocation.getBlock().setType(Material.ACACIA_TRAPDOOR);

            TrapDoor openable = (TrapDoor) trapDoorLocation.getBlock().getBlockData();
            openable.setOpen(true);
            openable.setFacing(BlockFace.EAST);
            trapDoorLocation.getBlock().setBlockData(openable);

            PersistentDataContainer data = new CustomBlockData(trapDoorLocation.getBlock(), GuessBlock.getPlugin());
            data.set(GuessBlock.getPlugin().keys.CAN_CLOSE_FIST_TRAPDOOR, PersistentDataType.BOOLEAN, true);
        }

        for(Block block: secondReplaceBlocks){
            Location trapDoorLocation = block.getLocation().clone().add(0, 1, 0);

            secondTrapdoorsList.add(trapDoorLocation.getBlock());

            trapDoorLocation.getBlock().setType(Material.ACACIA_TRAPDOOR);

            TrapDoor openable = (TrapDoor) trapDoorLocation.getBlock().getBlockData();
            openable.setOpen(true);
            openable.setFacing(BlockFace.WEST);
            trapDoorLocation.getBlock().setBlockData(openable);

            PersistentDataContainer data = new CustomBlockData(trapDoorLocation.getBlock(), GuessBlock.getPlugin());
            data.set(GuessBlock.getPlugin().keys.CAN_CLOSE_SECOND_TRAPDOOR, PersistentDataType.BOOLEAN, true);
        }
    }

    /**
     * Устанавливает блоки, которые необходимо отгадать
     * @param first первый тип блока
     * @param second второй тип блока
     */
    public void setBlocksToFind(Material first, Material second){
        updateRandomBlocks(GuessBlock.getPlugin().engine.getRandomBlockList());

        findableBlockFirst.getBlock().setType(first);
        findableBlockSecond.getBlock().setType(second);

        //проверка, чтобы точно был 1 блок который нужно найти
        if(!firstReplaceBlocks.contains(findableBlockFirst.getBlock())){;
            firstReplaceBlocks.set(Utils.getRandomNumber(0, firstReplaceBlocks.size()), findableBlockFirst.getBlock());
        }

        if(!secondReplaceBlocks.contains(findableBlockSecond.getBlock())){
            secondReplaceBlocks.set(Utils.getRandomNumber(0, secondReplaceBlocks.size()), findableBlockSecond.getBlock());
        }
    }

    /**
     * Расставляет рандомные блоки под люки
     * @param randomList список рандомных блоков
     */
    private void updateRandomBlocks(List<Material> randomList){
        Random random = new Random();

        for(Block block: firstReplaceBlocks){
            int randomMaterialId = random.nextInt(0, randomList.size());
            Material randomMaterial = randomList.get(randomMaterialId);
            block.setType(randomMaterial);
        }

        for(Block block: secondReplaceBlocks){
            int randomMaterialId = random.nextInt(0, randomList.size());
            Material randomMaterial = randomList.get(randomMaterialId);
            block.setType(randomMaterial);
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
    private static ArrayList<Block> loadReplaceBlocks(Location arenaCenter, String partInCfg, int replaceHeight, GuessBlock plugin, String arenaName, String arenaType){
        ArrayList<Block> list = new ArrayList<>();

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

                    list.add(blocksHeight.clone().add(x, 0, z).getBlock());
                }
                catch (Exception exp){
                    plugin.getLogger().warning("Cant parse replace location '"+ arenaName+"': " + exp.getMessage());
                }
            }
        }

        return list;
    }
}
