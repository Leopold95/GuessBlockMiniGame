package me.leopold95.guessblock.core;

import me.leopold95.guessblock.GuessBlock;
import org.bukkit.NamespacedKey;

public class Keys {
    private GuessBlock plugin;

    public NamespacedKey BLOCK_TO_GUESS;
    public NamespacedKey DUEL_ACCEPT_WAITING;
    public NamespacedKey INTO_GAME_NOW;

    public Keys(GuessBlock plugin){
        this.plugin = plugin;

        BLOCK_TO_GUESS = new NamespacedKey(this.plugin, "BLOCK_TO_GUESS");
        DUEL_ACCEPT_WAITING = new NamespacedKey(this.plugin, "DUEL_ACCEPT_WAITING");
        INTO_GAME_NOW = new NamespacedKey(this.plugin, "INTO_GAME_NOW");
    }
}
