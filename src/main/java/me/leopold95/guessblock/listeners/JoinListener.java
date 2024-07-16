package me.leopold95.guessblock.listeners;

import me.leopold95.guessblock.GuessBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private GuessBlock plugin;
    public JoinListener(GuessBlock plugin){
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        plugin.engine.getGame().clearPersistence(player);
    }
}
