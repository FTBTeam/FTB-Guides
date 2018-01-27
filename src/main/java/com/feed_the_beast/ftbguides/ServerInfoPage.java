package com.feed_the_beast.ftbguides;

import com.feed_the_beast.ftbguides.net.MessageServerInfo;
import com.feed_the_beast.ftblib.FTBLibLang;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.google.gson.JsonElement;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.server.command.CommandTreeBase;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ServerInfoPage
{
	private static JsonElement serverGuide = null;
	private static final Map<UUID, MessageServerInfo.CommandInfo> COMMAND_CACHE = new HashMap<>();

	public static void reloadCachedInfo()
	{
		serverGuide = null;
		COMMAND_CACHE.clear();
	}

	public static JsonElement getServerGuide()
	{
		if (serverGuide == null)
		{
			serverGuide = JsonUtils.fromJson(new File(CommonUtils.folderLocal, "ftbguides/server_guide.json"));
		}

		return serverGuide;
	}

	public static MessageServerInfo.CommandInfo getCommands(EntityPlayerMP player)
	{
		MessageServerInfo.CommandInfo info = COMMAND_CACHE.get(player.getUniqueID());

		if (info == null)
		{
			info = new MessageServerInfo.CommandInfo("", Collections.emptyList(), new ArrayList<>());

			for (ICommand command : ServerUtils.getAllCommands(player.mcServer, player))
			{
				try
				{
					addCommandUsage(player, info, command);
				}
				catch (Exception ex1)
				{
				}
			}

			COMMAND_CACHE.put(player.getUniqueID(), info);
		}

		return info;
	}

	public static List<ITextComponent> getMainPage(EntityPlayerMP player, long now)
	{
		List<ITextComponent> list = new ArrayList<>();

		if (FTBGuidesConfig.server_info.difficulty)
		{
			list.add(FTBLibLang.DIFFICULTY.textComponent(player, StringUtils.firstUppercase(player.world.getDifficulty().toString().toLowerCase())));
		}

		return list;
	}

	private static void addCommandUsage(ICommandSender sender, MessageServerInfo.CommandInfo parent, ICommand command)
	{
		Collection<ITextComponent> info = new ArrayList<>();

		if (!command.getAliases().isEmpty())
		{
			info.add(new TextComponentString("/" + command.getName()));

			for (String s : command.getAliases())
			{
				info.add(new TextComponentString("/" + s));
			}

			info.add(null);
		}

		for (String line : command.getUsage(sender).split("\n"))
		{
			if (line.indexOf('%') != -1 || line.indexOf('/') != -1)
			{
				info.add(FTBLibLang.COMMAND_USAGE.textComponent(sender, line));
			}
			else
			{
				info.add(FTBLibLang.COMMAND_USAGE.textComponent(sender, new TextComponentTranslation(line)));
			}
		}

		MessageServerInfo.CommandInfo cmd = new MessageServerInfo.CommandInfo(command.getName(), info, new ArrayList<>());

		CommandTreeBase treeCommand = null;

		if (command instanceof CommandTreeBase)
		{
			treeCommand = (CommandTreeBase) command;
		}
		/* FIXME: FTBUtilities Integration
		else if (command instanceof CmdOverride && ((CmdOverride) command).parent instanceof CommandTreeBase)
		{
			treeCommand = (CommandTreeBase) ((CmdOverride) command).parent;
		}*/

		if (treeCommand != null)
		{
			for (ICommand sub : treeCommand.getSubCommands())
			{
				addCommandUsage(sender, cmd, sub);
			}
		}

		parent.subcommands.add(new MessageServerInfo.CommandInfo(cmd.name, cmd.info.isEmpty() ? Collections.emptyList() : cmd.info, cmd.subcommands.isEmpty() ? Collections.emptyList() : cmd.subcommands));
	}
}