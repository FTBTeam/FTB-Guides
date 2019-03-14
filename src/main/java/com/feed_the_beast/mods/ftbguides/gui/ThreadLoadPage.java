package com.feed_the_beast.mods.ftbguides.gui;

import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiLoading;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.mods.ftbguides.FTBGuides;
import com.feed_the_beast.mods.ftbguides.gui.components.GuideComponent;
import com.feed_the_beast.mods.ftbguides.gui.components.HRGuideComponent;
import com.feed_the_beast.mods.ftbguides.gui.components.TextGuideComponent;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

/**
 * @author LatvianModder
 */
public class ThreadLoadPage extends Thread
{
	private final GuidePage page;
	public GuiLoading gui;
	public JsonElement json = JsonNull.INSTANCE;

	public ThreadLoadPage(GuidePage p)
	{
		super("Guide Page Loader " + p.getPath());
		page = p;
	}

	@Override
	public void start()
	{
		if (page.textLoadingState == GuidePage.STATE_LOADING || page == page.getRoot())
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

				if (!page.pages.isEmpty())
				{
					for (GuidePage p : page.pages)
					{
						page.println(new TextGuideComponent(p.title.getUnformattedText()).setProperty("icon", p.icon.toString()).setProperty("click", p.getID()));
					}

					page.println(HRGuideComponent.INSTANCE);
				}

				if (!page.specialButtons.isEmpty())
				{
					for (SpecialGuideButton button : page.specialButtons)
					{
						page.println(new TextGuideComponent(button.title.getUnformattedText()).setProperty("icon", button.icon.toString()).setProperty("click", button.click));
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

				page.textLoadingState = GuidePage.STATE_LOADED;
				FTBGuides.openGuidesGui(page.getPath());
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
		try
		{
			json = DataReader.get(page.textURI, Minecraft.getMinecraft().getProxy()).json();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		gui.setFinished();
	}
}