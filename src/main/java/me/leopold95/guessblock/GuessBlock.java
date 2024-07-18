package me.leopold95.guessblock;

import lombok.Getter;
import me.leopold95.guessblock.commands.GuessBlockCommand;
import me.leopold95.guessblock.commands.GuessBlockTesting;
import me.leopold95.guessblock.core.Config;
import me.leopold95.guessblock.core.Keys;
import me.leopold95.guessblock.core.guessblock.Engine;
import me.leopold95.guessblock.enums.Commands;
import me.leopold95.guessblock.listeners.JoinListener;
import me.leopold95.guessblock.listeners.LeaveListener;
import me.leopold95.guessblock.listeners.PlayerInteract;
import org.bukkit.plugin.java.JavaPlugin;

public final class GuessBlock extends JavaPlugin {

    @Getter
    private static GuessBlock plugin;

    public Engine engine;
    public Keys keys;

    @Override
    public void onEnable() {
        Config.register(this);

        plugin = this;

        keys = new Keys(this);
        engine = new Engine(this);

        getCommand(Commands.MG).setExecutor(new GuessBlockCommand(this));
        getCommand(Commands.MG).setTabCompleter(new GuessBlockCommand(this));

        //getCommand(Commands.MG_TESTING).setExecutor(new GuessBlockTesting(this));
        //getCommand(Commands.MG_TESTING).setTabCompleter(new GuessBlockTesting(this));

        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteract(this), this);

        engine.loadAllData();
    }

    @Override
    public void onDisable() {

    }
}
