package me.leopold95.guessblock.listeners;

import customblockdata.CustomBlockData;
import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.guessblock.Arena;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.A;

import java.util.Optional;

public class PlayerInteract implements Listener {
    private GuessBlock plugin;

    public PlayerInteract(GuessBlock plugin){
        this.plugin = plugin;
    }


    @EventHandler
    private void onPlayerClickTrapDoor(PlayerInteractEvent event){
        if(event.getClickedBlock() == null)
            return;

        if(!(event.getClickedBlock().getBlockData() instanceof TrapDoor))
            return;

        if(event.getPlayer().getPersistentDataContainer().has(plugin.keys.SELECTING_BLOCK_TO_GUESS)){
            event.setCancelled(true);
            return;
        }

        PersistentDataContainer cdb = new CustomBlockData(event.getClickedBlock(), GuessBlock.getPlugin());

        if(!cdb.has(plugin.keys.CAN_CLOSE_TRAPDOOR))
            return;

        boolean canClose = cdb.get(plugin.keys.CAN_CLOSE_TRAPDOOR, PersistentDataType.BOOLEAN);

        if(canClose){

            Optional<Arena> optional = plugin.engine.getArenas()
                .stream()
                .filter(a -> a.getFirstPlayer() == event.getPlayer() || a.getSecondPlayer() == event.getPlayer())
                .findFirst();

            if(optional.isEmpty())
                return;

            cdb.set(plugin.keys.CAN_CLOSE_TRAPDOOR, PersistentDataType.BOOLEAN, false);

            if(cdb.has(plugin.keys.FIST_TRAPDOOR)){
                optional.get().getFirstBlocksList().remove(event.getClickedBlock());
                plugin.getLogger().warning("f updated");
            } else if (cdb.has(plugin.keys.SECOND_TRAPDOOR)) {
                optional.get().getSecondBlocksList().remove(event.getClickedBlock());
                plugin.getLogger().warning("s updated");
            }

        }
        else {
            event.setCancelled(true);
        }
    }
}
