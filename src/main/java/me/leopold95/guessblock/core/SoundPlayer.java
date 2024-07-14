package me.leopold95.guessblock.core;

import me.leopold95.guessblock.GuessBlock;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundPlayer {
    public static void play(Player player, String configName){
        String soundName = Config.getString("sounds." + configName + ".name");
        int volume = Config.getInt("sounds." + configName + ".volume");

        try {
            player.playSound(player, Sound.valueOf(soundName), 1, volume);
        }
        catch (Exception exp){
            String message = Config.getMessage("bad-sound")
                    .replace("%name%", soundName)
                    .replace("%cfg%", configName);

            GuessBlock.getPlugin().getLogger().warning(message);
        }
    }
}
