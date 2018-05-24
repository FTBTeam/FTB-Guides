package com.feed_the_beast.ftbguides.client;

import com.feed_the_beast.ftbguides.FTBGuidesCommon;
import com.feed_the_beast.ftbguides.gui.GuiGuide;
import com.feed_the_beast.ftbguides.gui.GuidePage;
import com.feed_the_beast.ftbguides.gui.ThreadLoadPage;
import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
	public static JsonObject serverGuideClient = null;

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

	@Override
	public void loadServerGuide(JsonElement json)
	{
		serverGuideClient = json.getAsJsonObject();
	}

	public static void loadServerGuidePage(String page, JsonElement json)
	{
		if (json.isJsonArray())
		{
			/*
			GuidePage p = getSubFromPath(page);

			if (p != null)
			{
				for (JsonElement e : json.getAsJsonArray())
				{
					p.println(GuideComponent.create(p, e));
				}
			}
			*/
		}
	}

	public static void setShouldReload()
	{
		guidesGui = null;
	}

	public static boolean openGuidesGui(String path)
	{
		if (!path.isEmpty())
		{
			pageToOpen = path;
		}

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
			if (!pageToOpen.isEmpty())
			{
				GuidePage page = guidesGui.page.getSubFromPath(pageToOpen);

				if (page != null)
				{
					if (page.textURI != null && page.textLoadingState == GuidePage.STATE_NOT_LOADING)
					{
						new ThreadLoadPage(page).start();
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