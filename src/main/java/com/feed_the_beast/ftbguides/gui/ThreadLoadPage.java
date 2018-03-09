package com.feed_the_beast.ftbguides.gui;

import com.feed_the_beast.ftbguides.FTBGuides;
import com.feed_the_beast.ftbguides.client.FTBGuidesClient;
import com.feed_the_beast.ftbguides.gui.components.GuideComponent;
import com.feed_the_beast.ftbguides.gui.components.HRGuideComponent;
import com.feed_the_beast.ftbguides.gui.components.TextGuideComponent;
import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiLoading;
import com.feed_the_beast.ftblib.lib.io.HttpConnection;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.util.text.TextFormatting;

import java.io.File;

/**
 * @author LatvianModder
 */
public class ThreadLoadPage extends Thread
{
	private final String url;
	private final GuidePage page;
	private GuiLoading gui;
	private JsonElement json = JsonNull.INSTANCE;
	private boolean started = false;

	public ThreadLoadPage(GuidePage p, String u)
	{
		super("LoadPage" + p.getPath());
		page = p;
		url = u;
	}

	@Override
	public void start()
	{
		if (!started)
		{
			started = true;
			gui = new GuiLoading()
			{
				@Override
				public void finishLoading()
				{
					page.textLoader = null;
					gui = null;

					if (!page.pages.isEmpty())
					{
						for (GuidePage p : page.pages)
						{
							page.println(new TextGuideComponent(p.title.getUnformattedText()).setProperty("icon", p.icon.toString()).setProperty("click", p.getName()));
						}

						page.println(HRGuideComponent.INSTANCE);
					}

					if (json.isJsonArray())
					{
						for (JsonElement e : json.getAsJsonArray())
						{
							page.println(GuideComponent.create(page, e));
						}
					}
					else
					{
						FTBGuides.LOGGER.error("Failed to load page " + page.getPath() + "! Json is not an array: " + json);
						page.println(TextFormatting.RED + "Failed to load page! Try again later.");
					}

					FTBGuidesClient.openGuidesGui(page.getPath());
				}
			};

			gui.setTitle("Loading Page\n" + page.getPath());
			gui.openGui();
			setDaemon(true);
			super.start();
		}
	}

	@Override
	public void run()
	{
		if (url.startsWith("http:") || url.startsWith("https:"))
		{
			json = HttpConnection.getJson(url, ClientUtils.MC.getProxy(), true);
		}
		else
		{
			json = JsonUtils.fromJson(new File(url));
		}

		gui.setFinished();
	}
}