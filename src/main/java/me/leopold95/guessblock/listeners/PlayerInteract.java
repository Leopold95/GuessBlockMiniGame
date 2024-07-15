package me.leopold95.guessblock.listeners;

import customblockdata.CustomBlockData;
import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.guessblock.Arena;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class PlayerInteract implements Listener {
    private GuessBlock plugin;

    public PlayerInteract(GuessBlock plugin){
        this.plugin = plugin;
    }


    @EventHandler
    private void onPlayerClickTrapDoor(PlayerInteractEvent event){
        if(!event.getAction().isRightClick())
            return;

        if(event.getClickedBlock() == null)
            return;

        if(!(event.getClickedBlock().getBlockData() instanceof TrapDoor))
            return;

        if(event.getPlayer().getPersistentDataContainer().has(plugin.keys.SELECTING_BLOCK_TO_GUESS)){
            event.setCancelled(true);
            return;
        }

        PersistentDataContainer cdb = new CustomBlockData(event.getClickedBlock(), GuessBlock.getPlugin());

        //TODO somebody pls fix this shit
        if(cdb.has(plugin.keys.CAN_CLOSE_FIST_TRAPDOOR)){
            boolean canClose1 = cdb.get(plugin.keys.CAN_CLOSE_FIST_TRAPDOOR, PersistentDataType.BOOLEAN);

            //trapdoorClicked(canClose1, cdb, event, plugin.keys.CAN_CLOSE_FIST_TRAPDOOR);

            if(canClose1 ){
                String enemyName = event.getPlayer().getPersistentDataContainer().get(plugin.keys.CURRENT_ENEMY, PersistentDataType.STRING);

                Optional<Arena> optional = plugin.engine.getArenas()
                        .stream()
                        .filter(a -> a.getSecondPlayer().getName().equals(enemyName))
                        .findFirst();

                if(optional.isEmpty())
                    return;

                cdb.set(plugin.keys.CAN_CLOSE_FIST_TRAPDOOR, PersistentDataType.BOOLEAN, false);

                optional.get().removeFirstTrapdoor(event.getClickedBlock());
            }
            else {
                event.setCancelled(true);
            }
        } else if (cdb.has(plugin.keys.CAN_CLOSE_SECOND_TRAPDOOR)) {
            boolean canClose2 = cdb.get(plugin.keys.CAN_CLOSE_SECOND_TRAPDOOR, PersistentDataType.BOOLEAN);

            //trapdoorClicked(canClose2, cdb, event, plugin.keys.CAN_CLOSE_SECOND_TRAPDOOR);

            if(canClose2){
                String enemyName = event.getPlayer().getPersistentDataContainer().get(plugin.keys.CURRENT_ENEMY, PersistentDataType.STRING);

                Optional<Arena> optional = plugin.engine.getArenas()
                        .stream()
                        .filter(a -> a.getFirstPlayer().getName().equals(enemyName))
                        .findFirst();

                if(optional.isEmpty())
                    return;

                cdb.set(plugin.keys.CAN_CLOSE_SECOND_TRAPDOOR, PersistentDataType.BOOLEAN, false);

                optional.get().removeSecondTrapdoor(event.getClickedBlock());
            }
            else {
                event.setCancelled(true);
            }
        }
    }

//    private void trapdoorClicked(boolean canClose, PersistentDataContainer cont, PlayerInteractEvent event, NamespacedKey key){
//        if(canClose){
//            String enemyName = event.getPlayer().getPersistentDataContainer().get(plugin.keys.CURRENT_ENEMY, PersistentDataType.STRING);
//
//            if(key.equals(plugin.keys.CAN_CLOSE_FIST_TRAPDOOR)){
//
//
//
//
//            }
//            else {
//
//            }
//
//
//            Optional<Arena> optional = plugin.engine.getArenas()
//                    .stream()
//                    .filter(a -> a.getFirstPlayer().getName().equals(enemyName))
//                    .findFirst();
//
//            if(optional.isEmpty())
//                return;
//
//            cont.set(key, PersistentDataType.BOOLEAN, false);
//
//
//            if(key.equals(plugin.keys.CAN_CLOSE_FIST_TRAPDOOR))
//                optional.get().removeFirstTrapdoor(event.getClickedBlock());
//            else optional.get().removeSecondTrapdoor(event.getClickedBlock());
//
//        }
//        else {
//            event.setCancelled(true);
//        }
//    }
}
