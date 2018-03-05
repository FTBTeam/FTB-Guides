package com.feed_the_beast.ftbguides.gui;

import com.feed_the_beast.ftbguides.FTBGuides;
import com.feed_the_beast.ftbguides.client.FTBGuidesClient;
import com.feed_the_beast.ftbguides.gui.components.GuideComponent;
import com.feed_the_beast.ftbguides.gui.components.HRGuideComponent;
import com.feed_the_beast.ftbguides.gui.components.TextGuideComponent;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiLoading;
import com.feed_the_beast.ftblib.lib.io.HttpConnection;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.google.gson.JsonElement;

import java.io.File;

/**
 * @author LatvianModder
 */
class ThreadLoadPage extends Thread
{
	private final GuidePage page;
	private final GuiLoading gui;

	ThreadLoadPage(GuidePage p)
	{
		super("LoadPage" + p.getPath());
		page = p;
		setDaemon(true);
		gui = new GuiLoading()
		{
			@Override
			public void finishLoading()
			{
				FTBGuidesClient.openGuidesGui(page);
			}
		};

		gui.setTitle("Loading Page\n" + p.getPath());
		gui.openGui();
	}

	@Override
	public void run()
	{
		try
		{
			JsonElement json;

			if (page.textURL.startsWith("http:") || page.textURL.startsWith("https:"))
			{
				json = HttpConnection.getJson(page.textURL);
			}
			else
			{
				json = JsonUtils.fromJson(new File(page.textURL));
			}

			page.textURL = "";

			if (!page.pages.isEmpty())
			{
				for (GuidePage p : page.pages)
				{
					page.println(new TextGuideComponent(p.title.getUnformattedText()).setProperty("icon", p.icon.toString()).setProperty("click", p.getName()));
				}

				page.println(HRGuideComponent.INSTANCE);
			}

			for (JsonElement e : json.getAsJsonArray())
			{
				page.println(GuideComponent.create(e));
			}
		}
		catch (Exception ex)
		{
			page.clear();
			page.println("Error while loading the page! See log!");
			FTBGuides.LOGGER.error("Error while loading page " + page.getPath() + ":");
			ex.printStackTrace();
		}

		gui.setFinished();
	}
}