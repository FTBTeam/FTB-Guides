package com.feed_the_beast.ftbguides.client;

import com.feed_the_beast.ftbguides.FTBGuides;
import com.feed_the_beast.ftbguides.FTBGuidesCommon;
import com.feed_the_beast.ftbguides.gui.GuiGuide;
import com.feed_the_beast.ftbguides.gui.GuidePage;
import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class FTBGuidesClient extends FTBGuidesCommon
{
	public static final KeyBinding KEY_GUIDE = new KeyBinding("key.ftbguides.guide", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_G, FTBLib.KEY_CATEGORY);

	static GuiGuide guidesGui = null;
	static ThreadLoadGuides reloadingThread = null;
	public static String pageToOpen = "";

	@Override
	public void preInit()
	{
		super.preInit();
		FTBGuidesClientConfig.sync();
		ClientRegistry.registerKeyBinding(KEY_GUIDE);
	}

	@Override
	public void postInit()
	{
		super.postInit();

		if (ClientUtils.MC.getResourceManager() instanceof SimpleReloadableResourceManager)
		{
			((SimpleReloadableResourceManager) ClientUtils.MC.getResourceManager()).registerReloadListener(resourceManager -> setShouldReload());
		}
	}

	public static void setShouldReload()
	{
		guidesGui = null;
	}

	public static boolean openGuidesGui(String path)
	{
		if (guidesGui == null)
		{
			if (reloadingThread == null)
			{
				reloadingThread = new ThreadLoadGuides();
				reloadingThread.start();
			}

			reloadingThread.gui.openGui();
			return false;
		}
		else
		{
			if (!path.isEmpty())
			{
				pageToOpen = path;
			}

			if (!pageToOpen.isEmpty())
			{
				GuidePage page = guidesGui.page.getSubFromPath(pageToOpen);

				if (page != null && page != guidesGui.page)
				{
					if (page.textLoader != null)
					{
						if (FTBLibConfig.debugging.print_more_info)
						{
							FTBGuides.LOGGER.info("Started page loader for " + page.getPath());
						}

						page.textLoader.start();
						return false;
					}
					else
					{
						guidesGui = new GuiGuide(page);
					}
				}

				pageToOpen = "";
			}

			guidesGui.openGui();
			return true;
		}
	}
}