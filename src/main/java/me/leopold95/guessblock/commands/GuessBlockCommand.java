package me.leopold95.guessblock.commands;

import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.Config;
import me.leopold95.guessblock.enums.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GuessBlockCommand implements TabCompleter, CommandExecutor {
    private GuessBlock plugin;

    public GuessBlockCommand(GuessBlock plugin) {
        this.plugin = plugin;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1){
            return List.of(
                    Commands.MG_DUEL,
                    Commands.MG_GIVE_UP,
                    Commands.MG_ACCEPT,
                    Commands.MG_SET_CENTER
            );
        }

        if(args.length == 2){
            switch (args[0]){
                case Commands.MG_DUEL:
                case Commands.MG_ACCEPT:
                    ArrayList<String> names = new ArrayList<>();

                    for(Player p : Bukkit.getOnlinePlayers())
                        names.add(p.getName());

                    return names;
            }
        }

        return null;
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

            case Commands.MG_SET_CENTER -> {
                return onSetCenterCommand(args, player);
            }
        }

        return false;
    }

    private boolean onSetCenterCommand(@NotNull String[] args, Player player){
        if(args.length != 2){
            String message = Config.getMessage("commands.bad-set-center-args")
                            .replace("%base%", Commands.MG)
                            .replace("%first%", Commands.MG_SET_CENTER)
                            .replace("%second%", Config.getMessage("placeholders.arena-id"));
            player.sendMessage(message);
            return true;
        }

        String arenaIdStr = args[1];
        int arenaId = Integer.parseInt(arenaIdStr);

        double x = player.getLocation().getBlockX();
        double y = player.getLocation().getBlockY();
        double z = player.getLocation().getBlockZ();



        return true;
    }

    private boolean onDuelCommand(@NotNull String[] args, Player player){
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

        if(targetPlayer == null){
            player.sendMessage(Config.getMessage("commands.bad-duel-player"));
            return true;
        }

        if(player.getName().equals(targetName)){
            player.sendMessage(Config.getMessage("commands.cant-duel-self"));
            return true;
        }


        String message = Config.getMessage("called-for-minigame")
                .replace("%base%", Commands.MG)
                .replace("%first%", Commands.MG_DUEL)
                .replace("%second%", "<" + targetPlayer.getName() + ">");
        Component callDuel = Component.text(message)
                .clickEvent(ClickEvent.runCommand("/" + Commands.MG + " " + Commands.MG_ACCEPT + " " + player.getName()))
                .hoverEvent(HoverEvent.showText(Component.text(Config.getMessage("hover.accept-to-duel").replace("%name%", player.getName()))));


        targetPlayer.getPersistentDataContainer().set(plugin.keys.DUEL_ACCEPT_WAITING, PersistentDataType.STRING, player.getName());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            targetPlayer.getPersistentDataContainer().remove(plugin.keys.DUEL_ACCEPT_WAITING);
        }, Config.getInt("minigame-duel-accept-time"));

        targetPlayer.sendMessage(callDuel);
        player.sendMessage(Config.getMessage("commands.duel-accept-waiting").replace("%name%", targetName));

        return true;
    }

    private boolean onAcceptCommand(@NotNull String[] args, Player player){
        if (args.length != 2){
            String message = Config.getMessage("commands.bad-args")
                    .replace("%base%", Commands.MG)
                    .replace("%first%", Commands.MG_DUEL)
                    .replace("%second%", "<" + Config.getMessage("placeholders.player") + ">");

            player.sendMessage(message);
            return false;
        }

        player.getPersistentDataContainer().remove(plugin.keys.DUEL_ACCEPT_WAITING);
        plugin.engine.getGame().tryAcceptGame(player.getName(), args[1]);
        return true;
    }
}
