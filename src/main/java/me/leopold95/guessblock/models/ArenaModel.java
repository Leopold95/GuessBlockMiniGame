package me.leopold95.guessblock.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Location;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
@ToString
public class ArenaModel {
    private String name;
    private Location enemyBlockLocation;
    private Location center;
    @Setter
    private boolean isBusy;

    private ArrayList<Location> firstReplaceBlocks;
    private ArrayList<Location> secondReplaceBlocks;

    private Location firstSpawn;
    private Location secondSpawn;
}
