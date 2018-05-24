package com.feed_the_beast.ftbguides;

import com.feed_the_beast.ftbguides.net.FTBGuidesNetHandler;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

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
			serverGuide = DataReader.get(new File(CommonUtils.folderLocal, "server_guide/data.json")).safeJson();
		}

		return serverGuide;
	}

	public static JsonElement getLoadedPage(String path)
	{
		JsonElement json = LOADED_PAGES.get(path);

		if (json == null)
		{
			File folder = new File(CommonUtils.folderLocal, "server_guide");
			File file = new File(folder, path + ".json");

			if (!file.exists())
			{
				file = new File(folder, path + ".json");
			}

			if (file.getAbsolutePath().startsWith(folder.getAbsolutePath()))
			{
				json = DataReader.get(file).safeJson();
			}

			LOADED_PAGES.put(path, json);
		}

		if (json == null || !json.isJsonArray())
		{
			json = JsonNull.INSTANCE;
		}

		return json;
	}
}