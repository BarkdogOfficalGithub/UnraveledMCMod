package me.unraveledmc.unraveledmcmod.command;

import java.util.Arrays;
import java.util.List;
import me.unraveledmc.unraveledmcmod.player.FPlayer;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Set yourself a prefix", usage = "/<command> <set <tag..> | off | clear <player> | clearall | list>")
public class Command_tag extends FreedomCommand
{

    public static final List<String> FORBIDDEN_WORDS = Arrays.asList(new String[]
    {
        "helper", "mod", "admin", "owner", "dev", "exec", "staff", "founder"
    });

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 1)
        {
            if ("list".equalsIgnoreCase(args[0]))
            {
                msg("Tags for all online players:");

                for (final Player player : server.getOnlinePlayers())
                {
                    final FPlayer playerdata = plugin.pl.getPlayer(player);
                    if (playerdata.getTag() != null)
                    {
                        msg(player.getName() + ": " + playerdata.getTag());
                    }
                }

                return true;
            }
            else if ("clearall".equalsIgnoreCase(args[0]))
            {
                if (!plugin.al.isStaffMember(sender))
                {
                    noPerms();
                    return true;
                }

                FUtil.staffAction(sender.getName(), "Removing all tags", false);

                int count = 0;
                for (final Player player : server.getOnlinePlayers())
                {
                    final FPlayer playerdata = plugin.pl.getPlayer(player);
                    if (playerdata.getTag() != null)
                    {
                        count++;
                        playerdata.setTag(null);
                    }
                }

                msg(count + " tag(s) removed.");

                return true;
            }
            else if ("off".equalsIgnoreCase(args[0]))
            {
                if (senderIsConsole)
                {
                    msg("\"/tag off\" can't be used from the console. Use \"/tag clear <player>\" or \"/tag clearall\" instead.");
                }
                else
                {
                    plugin.pl.getPlayer(playerSender).setTag(null);
                    msg("Your tag has been removed.");
                }

                return true;
            }
            else
            {
                return false;
            }
        }
        else if (args.length >= 2)
        {
            if ("clear".equalsIgnoreCase(args[0]))
            {
                if (!plugin.al.isStaffMember(sender))
                {
                    noPerms();
                    return true;
                }

                final Player player = getPlayer(args[1]);

                if (player == null)
                {
                    msg(FreedomCommand.PLAYER_NOT_FOUND);
                    return true;
                }

                plugin.pl.getPlayer(player).setTag(null);
                msg("Removed " + player.getName() + "'s tag.");

                return true;
            }
            else if ("set".equalsIgnoreCase(args[0]))
            {
                if (senderIsConsole)
                {
                    msg(FreedomCommand.NOT_FROM_CONSOLE);
                    return true;
                }
                final String inputTag = StringUtils.join(args, " ", 1, args.length);
                final String outputTag = FUtil.colorize(StringUtils.replaceEachRepeatedly(StringUtils.strip(inputTag),
                        new String[]
                        {
                            "" + ChatColor.COLOR_CHAR, "&k"
                        },
                        new String[]
                        {
                            "", ""
                        })) + ChatColor.RESET;

                if (!plugin.al.isStaffMember(sender))
                {
                    final String rawTag = ChatColor.stripColor(outputTag).toLowerCase();

                    if (rawTag.length() > 20)
                    {
                        msg("That tag is too long (Max is 20 characters).");
                        return true;
                    }

                    for (String word : FORBIDDEN_WORDS)
                    {
                        if (rawTag.contains(word))
                        {
                            msg("That tag contains a forbidden word.");
                            return true;
                        }
                    }
                }

                plugin.pl.getPlayer(playerSender).setTag(outputTag);
                msg("Tag set to '" + outputTag + "'.");

                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}
