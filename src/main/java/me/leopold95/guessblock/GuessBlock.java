package me.leopold95.guessblock;

import me.leopold95.guessblock.commands.GuessBlockCommand;
import me.leopold95.guessblock.commands.GuessBlockTesting;
import me.leopold95.guessblock.core.Config;
import me.leopold95.guessblock.core.Keys;
import me.leopold95.guessblock.core.guessblock.Engine;
import me.leopold95.guessblock.enums.Commands;
import me.leopold95.guessblock.listeners.JoinListener;
import me.leopold95.guessblock.listeners.LeaveListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class GuessBlock extends JavaPlugin {

    public Engine engine;
    public Keys keys;

    @Override
    public void onEnable() {
        Config.register(this);

        keys = new Keys(this);
        engine = new Engine(this);

        getCommand(Commands.MG).setExecutor(new GuessBlockCommand(this));
        getCommand(Commands.MG).setTabCompleter(new GuessBlockCommand(this));

        getCommand(Commands.MG_TESTING).setExecutor(new GuessBlockTesting(this));
        getCommand(Commands.MG_TESTING).setTabCompleter(new GuessBlockTesting(this));

        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new LeaveListener(), this);

        engine.loadAllData();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
