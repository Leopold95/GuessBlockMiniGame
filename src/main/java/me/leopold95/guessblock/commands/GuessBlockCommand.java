package me.leopold95.guessblock.commands;

import me.leopold95.guessblock.GuessBlock;
import me.leopold95.guessblock.core.Config;
import me.leopold95.guessblock.core.SoundPlayer;
import me.leopold95.guessblock.enums.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                    Commands.MG_SET_BLOCK_TO_GUESS,
                    Commands.MG_HELP
            );
        }

        if(args.length == 2){
            switch (args[0]){
                case Commands.MG_DUEL:
                case Commands.MG_ACCEPT:
                    return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
                case Commands.MG_SET_BLOCK_TO_GUESS:
                    return plugin.engine.getRandomBlockList().stream().map(Material::toString).collect(Collectors.toList());

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
                if(!sender.hasPermission("guessblock.duel"))
                    return false;

                return onDuelCommand(args, player);
            }

            case Commands.MG_ACCEPT -> {
                if(!sender.hasPermission("guessblock.accept"))
                    return false;

                return onAcceptCommand(args, player);
            }

//            case Commands.MG_SET_CENTER -> {
//                return onSetCenterCommand(args, player);
//            }

            case Commands.MG_SET_BLOCK_TO_GUESS -> {
                if(!sender.hasPermission("guessblock.setblock"))
                    return false;

                return onSetGuessBlocks(args, player);
            }

            case Commands.MG_HELP -> {
                if(!sender.hasPermission("guessblock.help"))
                    return false;

                for(String line: Config.getMessageList("help")){
                    sender.sendMessage(line);
                }
            }
        }

        return false;
    }

    private boolean onSetGuessBlocks(@NotNull String[] args, Player player){
        if(!player.getPersistentDataContainer().has(plugin.keys.SELECTING_BLOCK_TO_GUESS)){
            player.sendMessage(Config.getMessage("commands.bad-selecting-block"));
            return true;
        }

        if(args.length != 2){
            String message = Config.getMessage("commands.bad-args")
                    .replace("%base%", Commands.MG)
                    .replace("%first%", Commands.MG_SET_BLOCK_TO_GUESS)
                    .replace("%second%>", Config.getMessage("placeholders.material"));
            player.sendMessage(message);
            return true;
        }

        try {
            Material material = Material.valueOf(args[1]);

            if(!material.isBlock()){
                player.sendMessage(Config.getMessage("commands.bad-guess-block-non-block"));
                return true;
            }

            String enemyName = player.getPersistentDataContainer().get(plugin.keys.CURRENT_ENEMY, PersistentDataType.STRING);
            Player enemy = Bukkit.getPlayer(enemyName);

            enemy.getPersistentDataContainer().set(plugin.keys.BLOCK_TO_GUESS, PersistentDataType.STRING, material.toString());

            player.sendMessage(Config.getMessage("commands.game-enemy-guess-block-selected").replace("%name%", material.toString()));
            enemy.sendActionBar(Config.getMessage("commands.game-you-has-guess-block"));

            player.getPersistentDataContainer().remove(plugin.keys.SELECTING_BLOCK_TO_GUESS);
        }
        catch (Exception exp){
            player.sendMessage(Config.getMessage("commands.bad-guess-block-type"));
        }

        return true;
    }

    private boolean onDuelCommand(@NotNull String @NotNull [] args, Player caller){
        if (args.length != 2){
            String message = Config.getMessage("commands.bad-args")
                    .replace("%base%", Commands.MG)
                    .replace("%first%", Commands.MG_DUEL)
                    .replace("%second%", "<" + Config.getMessage("placeholders.player") + ">");

            caller.sendMessage(message);
            return false;
        }

        var optArena = plugin.engine.getEmptyArena();
        if(optArena.isEmpty()){
            caller.sendMessage(Config.getMessage("game.no-empty-arena"));
            return true;
        }

        String targetName = args[1];
        Player targetPlayer = Bukkit.getPlayer(targetName);

        if(targetPlayer == null){
            caller.sendMessage(Config.getMessage("commands.bad-duel-player"));
            return true;
        }

        if(caller.getName().equals(targetName)){
            caller.sendMessage(Config.getMessage("commands.cant-duel-self"));
            return true;
        }

        String message = Config.getMessage("called-for-minigame")
                .replace("%base%", Commands.MG)
                .replace("%first%", Commands.MG_ACCEPT)
                .replace("%second%", "<" + targetPlayer.getName() + ">");
        Component callDuel = Component.text(message)
                .clickEvent(ClickEvent.runCommand("/" + Commands.MG + " " + Commands.MG_ACCEPT + " " + caller.getName()))
                .hoverEvent(HoverEvent.showText(Component.text(Config.getMessage("hover.accept-to-duel").replace("%name%", caller.getName()))));

        targetPlayer.getPersistentDataContainer().set(plugin.keys.DUEL_ACCEPT_WAITING_OF, PersistentDataType.STRING, caller.getName());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            targetPlayer.getPersistentDataContainer().remove(plugin.keys.DUEL_ACCEPT_WAITING_OF);
        }, Config.getInt("minigame-duel-accept-time"));

        targetPlayer.sendMessage(callDuel);
        caller.sendMessage(Config.getMessage("commands.duel-accept-waiting").replace("%name%", targetName));
        SoundPlayer.play(targetPlayer, "duel-throw-to-target");

        return true;
    }

    private boolean onAcceptCommand(@NotNull String[] args, Player target){
        if (args.length != 2){
            String message = Config.getMessage("commands.bad-args")
                    .replace("%base%", Commands.MG)
                    .replace("%first%", Commands.MG_ACCEPT)
                    .replace("%second%", "<" + Config.getMessage("placeholders.player") + ">");

            target.sendMessage(message);
            return false;
        }

        //игроку никто не кидал дуэль
        if(!target.getPersistentDataContainer().has(plugin.keys.DUEL_ACCEPT_WAITING_OF)){
            target.sendMessage(Config.getMessage("commands.accept-you-dont-has-any-duels"));
            return true;
        }

        String callerName = target.getPersistentDataContainer().get(plugin.keys.DUEL_ACCEPT_WAITING_OF, PersistentDataType.STRING);
        String targetName = target.getName();

        if(Bukkit.getPlayer(callerName) == null || Bukkit.getPlayer(targetName) == null){
            //TODO сдлеать сообщие когда одиз из игроков вышел
            Objects.requireNonNull(Bukkit.getPlayer(callerName)).sendMessage(Config.getMessage("bad-duel-begin"));
            Objects.requireNonNull(Bukkit.getPlayer(targetName)).sendMessage(Config.getMessage("bad-duel-begin"));
            return true;
        }

        Player caller = Bukkit.getPlayer(callerName);

        if(callerName.equals(targetName)){
            caller.sendMessage(Config.getMessage("commands.cant-accept-self"));
            return true;
        }

        target.getPersistentDataContainer().remove(plugin.keys.DUEL_ACCEPT_WAITING_OF);

        String callerMessage = Config.getMessage("commands.duel-accepted-caller").replace("%name%", targetName);
        target.sendMessage(callerMessage);

        String targetMessage = Config.getMessage("commands.duel-accepted-aim").replace("%name%", targetName);
        caller.sendMessage(targetMessage);


        var optArena = plugin.engine.getEmptyArena();
        if(optArena.isEmpty()){
            target.sendMessage(Config.getMessage("game.no-empty-arena"));
            return true;
        }

        SoundPlayer.play(caller, "duel-accept-to-caller");
        Bukkit.getScheduler().runTask(plugin, () -> plugin.engine.getGame().startGame(caller, target, optArena.get()));

        return true;
    }
}
