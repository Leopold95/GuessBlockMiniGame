package me.leopold95.guessblock.core;

import me.leopold95.guessblock.GuessBlock;
import org.bukkit.NamespacedKey;

public class Keys {
    private GuessBlock plugin;

    public final NamespacedKey IN_GUESS_BLOCK = new NamespacedKey(plugin, "IN_GUESS_BLOCK");

    public Keys(GuessBlock plugin){
        this.plugin = plugin;
    }
}
