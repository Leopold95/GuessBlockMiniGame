package me.leopold95.guessblock.core.guessblock;

import customblockdata.CustomBlockData;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
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
    private ArrayList<Location> firstReplaceBlocks, secondReplaceBlocks;
    private Location firstSpawn, secondSpawn;
    private ArrayList<Block> firstTrapdoorsList, secondTrapdoorsList;
    private Location holoLocationFirst, holoLocationSecond;
    @Setter
    private Player firstPlayer, secondPlayer;

    private final static String REPLACE_CONFIG_PART = "replace-location";
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
        for(Location block: firstReplaceBlocks){
            Location trapDoorLocation = block.clone().add(0, 1, 0);

            firstTrapdoorsList.add(trapDoorLocation.getBlock());

            trapDoorLocation.getBlock().setType(Material.ACACIA_TRAPDOOR);

            TrapDoor openable = (TrapDoor) trapDoorLocation.getBlock().getBlockData();
            openable.setOpen(true);
            openable.setFacing(BlockFace.EAST);
            trapDoorLocation.getBlock().setBlockData(openable);

            PersistentDataContainer data = new CustomBlockData(trapDoorLocation.getBlock(), GuessBlock.getPlugin());
            data.set(GuessBlock.getPlugin().keys.CAN_CLOSE_FIST_TRAPDOOR, PersistentDataType.BOOLEAN, true);
        }

        for(Location block: secondReplaceBlocks){
            Location trapDoorLocation = block.clone().add(0, 1, 0);

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

    public void updateHolo(int timeLeft){
        Hologram holo1 = DHAPI.getHologram(name + "first");
        Hologram holo2 = DHAPI.getHologram(name + "second");

        if(holo1 != null){
            DHAPI.setHologramLines(holo1, parseHoloLines(timeLeft));
        }

        if(holo2 != null){
            DHAPI.setHologramLines(holo2, parseHoloLines(timeLeft));
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

        if(DHAPI.getHologram(name + "first") != null){
            DHAPI.removeHologram(name + "first");
        }

        if(DHAPI.getHologram(name + "second") != null){
            DHAPI.removeHologram(name + "second");
        }

        GuessBlock.getPlugin().getLogger().warning(holoLocationFirst.toString());
        GuessBlock.getPlugin().getLogger().warning(holoLocationSecond.toString());

        DHAPI.createHologram(name + "first", holoLocationFirst,  parseHoloLines(0));
        DHAPI.createHologram(name + "second", holoLocationSecond,  parseHoloLines(0));
    }

    public void checkGuessBlocks(){
        Material firstFindMaterial = findableBlockFirst.getBlock().getType();
        Material secondFindMaterial = findableBlockSecond.getBlock().getType();

        boolean isFirstContainsMaterial = firstReplaceBlocks.stream().map(Location::getBlock).map(Block::getType).anyMatch(m -> m.equals(firstFindMaterial));
        boolean isSecondContainsMaterial = secondReplaceBlocks.stream().map(Location::getBlock).map(Block::getType).anyMatch(m -> m.equals(secondFindMaterial));

        //GuessBlock.getPlugin().getLogger().warning(String.valueOf(isFirstContainsMaterial) + " - " + String.valueOf(isSecondContainsMaterial));

        if(!isFirstContainsMaterial){
            int index = Utils.getRandomNumber(0, firstReplaceBlocks.size());

            Material m1F =  firstReplaceBlocks.get(index).getBlock().getType();

            Location b = firstReplaceBlocks.get(index);
            b.getBlock().setType(firstFindMaterial);

            firstReplaceBlocks.get(index).getBlock().setType(firstFindMaterial);
            firstReplaceBlocks.set(index, b.getBlock().getLocation());

            GuessBlock.getPlugin().getLogger().warning(m1F.name() + " " + index + " " + b.getBlockX() + " " + b.getBlockY() + " " +b.getBlockZ());
        }

        if(!isSecondContainsMaterial){
            int index = Utils.getRandomNumber(0, secondReplaceBlocks.size());
            Material m2F =  secondReplaceBlocks.get(index).getBlock().getType();

            Location b = secondReplaceBlocks.get(index);
            b.getBlock().setType(secondFindMaterial);

            secondReplaceBlocks.get(index).getBlock().setType(secondFindMaterial);
            secondReplaceBlocks.set(index, b.getBlock().getLocation());
            GuessBlock.getPlugin().getLogger().warning(m2F.name() + " " + index + " " + b.getBlockX() + " " + b.getBlockY() +  " " +b.getBlockZ() );
        }
    }

    /**
     * Расставляет рандомные блоки под люки
     */
    public void updateRandomBlocks(){
        List<Material> randomMaterials = GuessBlock.getPlugin().engine.getRandomBlockList();

        Random random = new Random();


        if(firstReplaceBlocks.size() != secondReplaceBlocks.size()){
            String message = Config.getMessage("bad-arena-replace-blocks")
                    .replace("%name%", name);
            GuessBlock.getPlugin().getLogger().warning(message);
            GuessBlock.getPlugin().getServer().getPluginManager().disablePlugin(GuessBlock.getPlugin());
            return;
        }

        //Генерация списка с рандомными материалами
        List<Material> selectedMaterials = new ArrayList<>();
        for(Location l: firstReplaceBlocks){
            int randomMaterialId = random.nextInt(0, randomMaterials.size());
            Material randomMaterial = randomMaterials.get(randomMaterialId);

            if (selectedMaterials.contains(randomMaterial)) {
                int randomMaterialId2 = random.nextInt(0, randomMaterials.size());
                Material randomMaterial2 = randomMaterials.get(randomMaterialId2);
                selectedMaterials.add(randomMaterial2);
                return;
            }

            selectedMaterials.add(randomMaterial);
        }

        //Заполение списка блоков рандомными
        for(int i = 0; i <= firstReplaceBlocks.size() - 1; i++){
            Material mat = selectedMaterials.get(i);
            firstReplaceBlocks.get(i).getBlock().setType(mat);
            secondReplaceBlocks.get(i).getBlock().setType(mat);
        }

//        for(Location location: firstReplaceBlocks){
//            int randomMaterialId = random.nextInt(0, randomMaterials.size());
//            Material randomMaterial = randomMaterials.get(randomMaterialId);
//            location.getBlock().setType(randomMaterial);
//        }
//
//        for(Location location: secondReplaceBlocks){
//            int randomMaterialId = random.nextInt(0, randomMaterials.size());
//            Material randomMaterial = randomMaterials.get(randomMaterialId);
//            location.getBlock().setType(randomMaterial);
//        }
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
                getReplaceBlocksLocations(center, ".replace-list.first", replaceHeight, plugin, name, type),
                getReplaceBlocksLocations(center, ".replace-list.second", replaceHeight, plugin, name, type),
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
                new Location(
                        Bukkit.getWorld(Config.getString("arenas-world")),
                        Config.getArenasConfig().getDouble(configPart + ".holo.first.location.x"),
                        Config.getArenasConfig().getDouble(configPart + ".holo.first.location.y"),
                        Config.getArenasConfig().getDouble(configPart + ".holo.first.location.z")),
                new Location(
                        Bukkit.getWorld(Config.getString("arenas-world")),
                        Config.getArenasConfig().getDouble(configPart + ".holo.second.location.x"),
                        Config.getArenasConfig().getDouble(configPart + ".holo.second.location.y"),
                        Config.getArenasConfig().getDouble(configPart + ".holo.second.location.z")),
                null,
                null
        );
    }

    /**
     * Генерирует список позиций блоков, которые нужно заменить
     * @return null иил список позиуий блоков, которые нужно заменить
     */
    private static ArrayList<Location> getReplaceBlocksLocations(Location arenaCenter, String partInCfg, int replaceHeight, GuessBlock plugin, String arenaName, String arenaType){
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

    private List<String> parseHoloLines(int timeLeft){
        List<String> lines = Config.getMessageList("hologram.text");
        List<String> newLines = new ArrayList<>();

        for(String line: lines){
            newLines.add(line.replace("%time%", String.valueOf(timeLeft)));
        }

        return newLines;
    }
}
