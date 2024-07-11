package me.leopold95.guessblock.core;

import me.leopold95.guessblock.GuessBlock;
import org.bukkit.NamespacedKey;

public class Keys {
    private GuessBlock plugin;

    public NamespacedKey BLOCK_TO_GUESS;
    public NamespacedKey DUEL_ACCEPT_WAITING_OF;
    public NamespacedKey SELECTING_BLOCK_TO_GUESS;
    public NamespacedKey CURRENT_ENEMY;

    public NamespacedKey SELECTED_GUESS_BLOCK;

    public NamespacedKey CAN_CLOSE_TRAPDOOR;
    public NamespacedKey FIST_TRAPDOOR;
    public NamespacedKey SECOND_TRAPDOOR;

    public Keys(GuessBlock plugin){
        this.plugin = plugin;

        BLOCK_TO_GUESS = new NamespacedKey(this.plugin, "BLOCK_TO_GUESS");
        DUEL_ACCEPT_WAITING_OF = new NamespacedKey(this.plugin, "DUEL_ACCEPT_WAITING");
        SELECTING_BLOCK_TO_GUESS = new NamespacedKey(this.plugin, "SELECTING_BLOCK_TO_GUESS");
        CURRENT_ENEMY = new NamespacedKey(this.plugin, "CURRENT_ENEMY");

        SELECTED_GUESS_BLOCK = new NamespacedKey(this.plugin, "SELECTED_GUESS_BLOCK");

        CAN_CLOSE_TRAPDOOR = new NamespacedKey(this.plugin, "CAN_CLOSE_TRAPDOOR");
        FIST_TRAPDOOR = new NamespacedKey(this.plugin, "FIST_TRAPDOOR");
        SECOND_TRAPDOOR = new NamespacedKey(this.plugin, "SECOND_TRAPDOOR");
    }
}
