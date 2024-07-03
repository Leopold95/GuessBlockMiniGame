package me.leopold95.guessblock.commands;

import me.leopold95.guessblock.core.Config;
import me.leopold95.guessblock.enums.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GuessBlockCommand implements TabCompleter, CommandExecutor {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of(
                Commands.MG_DUEL,
                Commands.MG_GIVE_UP,
                Commands.MG_ACCEPT
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            return true;
        }

        if(args.length == 0)
            return false;

        switch (args[0]){
            case Commands.MG_DUEL -> {
                return onDuelCommand(args, player);
            }

            case Commands.MG_ACCEPT -> {
                return onAcceptCommand(args, player);
            }
        }

        return false;
    }

    private boolean onDuelCommand(@NotNull String[] args, @NotNull Player player){
        if (args.length != 2){
            String message = Config.getMessage("commands.bad-args")
                    .replace("%base%", Commands.MG)
                    .replace("%first%", Commands.MG_DUEL)
                    .replace("%second%", "<" + Config.getMessage("placeholders.player") + ">");

            player.sendMessage(message);
            return false;
        }

        String targetName = args[1];
        Player targetPlayer = Bukkit.getPlayer(targetName);


        String message = Config.getMessage("called-for-minigame")
                .replace("%base%", Commands.MG)
                .replace("%first%", Commands.MG_ACCEPT);
        Component callDuel = Component.text(message)
                .clickEvent(ClickEvent.runCommand("/" + Commands.MG + " " + Commands.MG_ACCEPT));

        targetPlayer.sendMessage(callDuel);

        return true;
    }

    private boolean onAcceptCommand(@NotNull String[] args, Player player){

        return true;
    }
}
