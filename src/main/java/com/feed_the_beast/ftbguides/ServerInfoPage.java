package com.feed_the_beast.ftbguides;

import com.feed_the_beast.ftbguides.gui.GuidePage;
import com.feed_the_beast.ftbguides.gui.components.GuideComponent;
import com.feed_the_beast.ftblib.lib.gui.GuiIcons;
import com.google.gson.JsonElement;
import net.minecraft.util.text.TextComponentTranslation;

public class ServerInfoPage extends GuidePage
{
	public static final ServerInfoPage INSTANCE = new ServerInfoPage();

	private ServerInfoPage()
	{
		super("server_info", null);
		title = new TextComponentTranslation(FTBGuides.MOD_ID + ".lang.server_info");
		icon = GuiIcons.BOOK_RED;
	}

	public void read(String page, JsonElement json)
	{
		if (json.isJsonArray())
		{
			GuidePage p = getSubFromPath(page);

			if (p != null)
			{
				for (JsonElement e : json.getAsJsonArray())
				{
					p.println(GuideComponent.create(e));
				}
			}
		}
	}

	/*
	//COMMANDS
	for (String line : command.getUsage(player).split("\n"))
	{
		if (line.indexOf('%') != -1 || line.indexOf('/') != -1)
		{
			info.add(new TextComponentString(line));
		}
		else
		{
			info.add(new TextComponentTranslation(line));
		}
	}
	*/
}