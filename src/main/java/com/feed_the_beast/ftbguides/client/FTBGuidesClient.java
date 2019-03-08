package com.feed_the_beast.ftbguides.client;

import com.feed_the_beast.ftbguides.FTBGuidesCommon;
import com.feed_the_beast.ftbguides.gui.GuiGuide;
import com.feed_the_beast.ftbguides.gui.GuidePage;
import com.feed_the_beast.ftbguides.gui.ThreadLoadPage;
import com.feed_the_beast.ftblib.FTBLib;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.Map;

public class FTBGuidesClient extends FTBGuidesCommon
{
	public static KeyBinding KEY_GUIDE;

	static GuiGuide guidesGui = null;
	static ThreadLoadGuides reloadingThread = null;
	public static String pageToOpen = "";
	public static JsonObject serverGuideClient = null;
	public static Map<String, ThreadLoadPage> serverGuideClientLoading;

	@Override
	public void preInit()
	{
		ClientRegistry.registerKeyBinding(KEY_GUIDE = new KeyBinding("key.ftbguides.guide", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NONE, FTBLib.KEY_CATEGORY));
	}

	@Override
	public void postInit()
	{
		if (Minecraft.getMinecraft().getResourceManager() instanceof SimpleReloadableResourceManager)
		{
			((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(resourceManager -> setShouldReload());
		}
	}

	public static void loadServerGuide(JsonElement json)
	{
		if (json.isJsonObject())
		{
			JsonObject o = json.getAsJsonObject();
			serverGuideClient = new JsonObject();

			if (o.has("pages") && o.get("pages").isJsonArray())
			{
				for (JsonElement element : o.get("pages").getAsJsonArray())
				{
					if (element.isJsonObject() && element.getAsJsonObject().has("id"))
					{
						serverGuideClient.add(element.getAsJsonObject().get("id").getAsString(), element);
					}
				}
			}

			serverGuideClientLoading = new HashMap<>();
		}
		else
		{
			serverGuideClient = null;
			serverGuideClientLoading = null;
		}
	}

	public static void loadServerGuidePage(String page, JsonElement json)
	{
		if (serverGuideClientLoading != null)
		{
			ThreadLoadPage thread = serverGuideClientLoading.get(page);

			if (thread != null)
			{
				thread.json = json;
				thread.gui.setFinished();
				serverGuideClientLoading.remove(page);
			}
		}
	}

	public static void setShouldReload()
	{
		guidesGui = null;

		if (serverGuideClientLoading != null)
		{
			for (ThreadLoadPage thread : serverGuideClientLoading.values())
			{
				thread.gui.setFinished();
			}
		}
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