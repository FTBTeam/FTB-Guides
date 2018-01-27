package com.feed_the_beast.ftbguides.gui;

import com.feed_the_beast.ftbguides.gui.components.GuideComponent;
import com.feed_the_beast.ftbguides.gui.components.HRGuideComponent;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiLoading;
import com.feed_the_beast.ftblib.lib.io.HttpConnection;
import com.google.gson.JsonElement;

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
				Guides.openGui(page);
			}
		};

		gui.setTitle("Loading Page\n" + p.getPath());
		gui.openGui();
	}

	@Override
	public void run()
	{
		JsonElement json = HttpConnection.getJson(page.textURL);
		page.textURL = "";

		if (!page.pages.isEmpty())
		{
			page.println(HRGuideComponent.INSTANCE);
		}

		if (json.isJsonArray())
		{
			for (JsonElement e : json.getAsJsonArray())
			{
				page.println(GuideComponent.create(e));
			}
		}

		gui.setFinished();
	}
}