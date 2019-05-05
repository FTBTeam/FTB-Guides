package com.feed_the_beast.mods.ftbguides.gui;

import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiLoading;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.mods.ftbguides.FTBGuides;
import net.minecraft.client.Minecraft;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ThreadLoadPage extends Thread
{
	private final GuidePage page;
	public GuiLoading gui;
	public final List<String> text;

	public ThreadLoadPage(GuidePage p)
	{
		super("Guide Page Loader " + p.getPath());
		page = p;
		text = new ArrayList<>();
	}

	@Override
	public void start()
	{
		if (page.textLoadingState == GuidePage.STATE_LOADING)
		{
			return;
		}

		page.textLoadingState = GuidePage.STATE_LOADING;

		gui = new GuiLoading()
		{
			@Override
			public void finishLoading()
			{
				gui = null;
				page.onPageLoaded(text);
			}
		};

		gui.setTitle("Loading Page\n" + page.getPath());
		gui.openGui();
		setDaemon(true);

		if (FTBLibConfig.debugging.print_more_info)
		{
			FTBGuides.LOGGER.info("Started page loader for " + page.getPath());
		}

		super.start();
	}

	@Override
	public void run()
	{
		URI uri = page.resolveTextURI();

		if (uri != null)
		{
			try
			{
				text.addAll(DataReader.get(uri, Minecraft.getMinecraft().getProxy()).safeStringList());
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		gui.setFinished();
	}
}