package com.feed_the_beast.ftbguides.gui;

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
}