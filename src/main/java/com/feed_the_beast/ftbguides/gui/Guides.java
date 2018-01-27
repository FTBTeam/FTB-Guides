package com.feed_the_beast.ftbguides.gui;

import com.feed_the_beast.ftbguides.net.MessageServerInfo;
import com.feed_the_beast.ftblib.lib.icon.ItemIcon;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.google.gson.JsonElement;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * @author LatvianModder
 */
public class Guides
{
	static GuiGuide cachedGui = null;
	static ThreadReloadGuides reloadingThread = null;

	public static void setShouldReload()
	{
		cachedGui = null;
	}

	public static boolean openGui()
	{
		if (cachedGui == null)
		{
			if (reloadingThread == null)
			{
				reloadingThread = new ThreadReloadGuides();
				reloadingThread.start();
			}

			reloadingThread.gui.openGui();
			return false;
		}
		else
		{
			cachedGui.openGui();
			return true;
		}
	}

	public static void openGui(GuidePage page)
	{
		if (openGui() && cachedGui.page != page)
		{
			cachedGui = new GuiGuide(page);
			cachedGui.openGui();
		}
	}

	public static void readServerInfoPage(MessageServerInfo m)
	{
		GuideTitlePage.SERVER_INFO.clear();

		if (m.serverGuide.isJsonObject())
		{
			//FIXME: GuideTitlePage.SERVER_INFO.fromJson(m.serverGuide.getAsJsonObject());
		}

		for (JsonElement element : m.mainPage)
		{
			ITextComponent component = JsonUtils.deserializeTextComponent(element);
			GuideTitlePage.SERVER_INFO.println(component);
		}

		GuidePage commandPage = GuideTitlePage.SERVER_INFO.getSub("commands");

		for (MessageServerInfo.CommandInfo info : m.commands.subcommands)
		{
			addCommandTree(commandPage, info);
		}

		commandPage.sort(true);
		commandPage.title = new TextComponentTranslation("commands");
		commandPage.icon = ItemIcon.getItemIcon(new ItemStack(Blocks.COMMAND_BLOCK));

		/*
		if (cachedGui != null && cachedGui.getSelectedPage() == GuideTitlePage.SERVER_INFO)
		{
			cachedGui.refreshWidgets();
		}*/
	}

	private static void addCommandTree(GuidePage page, MessageServerInfo.CommandInfo info)
	{
		GuidePage subPage = page.getSub(info.name);
		subPage.title = new TextComponentString("/" + info.name);

		if (!info.info.isEmpty())
		{
			for (ITextComponent component : info.info)
			{
				subPage.println(component);
			}
		}

		if (!info.subcommands.isEmpty())
		{
			for (MessageServerInfo.CommandInfo info1 : info.subcommands)
			{
				addCommandTree(subPage, info1);
			}
		}
	}
}