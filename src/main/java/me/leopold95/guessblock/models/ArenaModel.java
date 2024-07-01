package me.leopold95.guessblock.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
public class ArenaModel {
    private String name;
    private LocationModel centerModel;
    private String orientation;
    @Setter
    private boolean isBusy;

    private ArrayList<Location> firstReplaceBlocks;
    private ArrayList<Location> secondReplaceBlocks;

    private Location firstSpawn;
    private Location secondSpawn;
}
