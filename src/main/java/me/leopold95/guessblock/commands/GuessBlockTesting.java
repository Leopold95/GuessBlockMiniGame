package me.leopold95.guessblock.commands;

import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.enums.Commands;
import me.leopold95.guessblock.models.ArenaModel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GuessBlockTesting implements CommandExecutor, TabCompleter {
    private GuessBlock plugin;

    public GuessBlockTesting(GuessBlock plugin){
        this.plugin = plugin;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return List.of(
                Commands.MG_TESTING_LIST_ARENAS,
                Commands.MG_TESTING_LIST_P_R_B_1_A,
                Commands.MG_TESTING_TP_TO_ARENA,
                Commands.MG_TESTING_TO_SP_1,
                Commands.MG_TESTING_TO_SP_2
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 0)
            return false;

        if(args[0].equals(Commands.MG_TESTING_LIST_ARENAS)){
            for(ArenaModel arena: plugin.engine.getArenas())
                sender.sendMessage(arena.toString());

            sender.sendMessage("Total arenas: " + plugin.engine.getArenas().size());
        }

        if(args[0].equals(Commands.MG_TESTING_LIST_P_R_B_1_A)){
            plugin.engine.placeRandom(0);
        }

        if(args[0].equals(Commands.MG_TESTING_TP_TO_ARENA)){
            if(args.length != 2)
                return false;

            plugin.engine.getArenasManager().tpToArena((Player) sender, Integer.parseInt(args[1]));
        }

        if(args[0].equals(Commands.MG_TESTING_TO_SP_1)){
            int aId = Integer.parseInt(args[1]);

            ArenaModel arena = plugin.engine.getArenas().get(aId);

            ((Player)sender).teleport(arena.getFirstSpawn());
        }

        if(args[0].equals(Commands.MG_TESTING_TO_SP_2)){
            int aId = Integer.parseInt(args[1]);

            ArenaModel arena = plugin.engine.getArenas().get(aId);

            ((Player)sender).teleport(arena.getSecondSpawn());
        }

        return true;
    }
}
