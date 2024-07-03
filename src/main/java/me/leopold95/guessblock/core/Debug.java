package me.leopold95.guessblock.core;

import org.bukkit.plugin.Plugin;

public class Debug {
    public static void message(String message){
        if(!Config.getBoolean("debug"))
            return;

        System.out.println(message);
    }

    public static void message(Object message){
        if(!Config.getBoolean("debug"))
            return;

        System.out.println(message);
    }
}
