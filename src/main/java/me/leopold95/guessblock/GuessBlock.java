package me.leopold95.guessblock;

import me.leopold95.guessblock.commands.GuessBlockCommand;
import me.leopold95.guessblock.core.Config;
import me.leopold95.guessblock.core.Keys;
import me.leopold95.guessblock.core.guessblock.Engine;
import me.leopold95.guessblock.enums.Commands;
import org.bukkit.plugin.java.JavaPlugin;

public final class GuessBlock extends JavaPlugin {

    public Engine engine;
    public Keys keys;

    @Override
    public void onEnable() {
        Config.register(this);

        engine = new Engine(this);
        keys = new Keys(this);

        getCommand(Commands.MG).setExecutor(new GuessBlockCommand());
        getCommand(Commands.MG).setTabCompleter(new GuessBlockCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
