package com.feed_the_beast.ftbguides;

import com.feed_the_beast.ftbguides.net.FTBGuidesNetHandler;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FTBGuidesCommon
{
	public static JsonElement serverGuide = null;
	private static final Map<String, JsonElement> LOADED_PAGES = new HashMap<>();

	public void preInit()
	{
		FTBGuidesConfig.sync();
		FTBGuidesNetHandler.init();
	}

	public void postInit()
	{
	}

	public void loadServerGuide(JsonElement json)
	{
	}

	public static void reloadServerGuide()
	{
		serverGuide = null;
		LOADED_PAGES.clear();
	}

	public static JsonElement getServerGuide()
	{
		if (serverGuide == null)
		{
			serverGuide = DataReader.get(new File(CommonUtils.folderLocal, "server_guide.json")).safeJson();
		}

		return serverGuide;
	}

	public static JsonObject getLoadedPage(String path)
	{
		JsonElement page = getServerGuide();

		return page.getAsJsonObject();
	}
}