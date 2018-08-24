package com.feed_the_beast.ftbguides;

import com.feed_the_beast.ftbguides.net.FTBGuidesNetHandler;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FTBGuidesCommon
{
	public static JsonElement serverGuide = null;
	private static final Map<String, File> LOADED_PAGE_PATHS = new HashMap<>();
	private static final Map<String, JsonElement> LOADED_PAGES = new HashMap<>();

	public void preInit()
	{
		FTBGuidesConfig.sync();
		FTBGuidesNetHandler.init();
	}

	public void postInit()
	{
	}

	public static boolean reloadServerGuide()
	{
		serverGuide = null;
		return true;
	}

	public static JsonElement getServerGuide(MinecraftServer server)
	{
		if (serverGuide == null)
		{
			File folder = new File(server.getDataDirectory(), "local/server_guide");
			serverGuide = DataReader.get(new File(folder, "data.json")).safeJson();
			LOADED_PAGE_PATHS.clear();
			LOADED_PAGES.clear();

			if (serverGuide.isJsonObject() && serverGuide.getAsJsonObject().has("pages"))
			{
				JsonElement pages0 = serverGuide.getAsJsonObject().get("pages");

				if (pages0.isJsonArray())
				{
					for (JsonElement element : pages0.getAsJsonArray())
					{
						if (element.isJsonObject() && element.getAsJsonObject().has("id"))
						{
							loadPagePath(folder, "/", element.getAsJsonObject());
						}
					}
				}
			}
		}

		return serverGuide;
	}

	private static void loadPagePath(File folder, String path, JsonObject json)
	{
		String id = json.get("id").getAsString();
		String pagePath = path + id + "/"; // /rules/

		File file = new File(folder, pagePath + "index.json");

		if (!file.exists())
		{
			file = new File(folder, pagePath.substring(0, pagePath.length() - 1) + ".json");
		}

		if (file.exists() && file.getAbsolutePath().startsWith(folder.getAbsolutePath()))
		{
			LOADED_PAGE_PATHS.put(pagePath, file);
		}
		else
		{
			FTBGuides.LOGGER.error("Couldn't load server guide page " + pagePath + "!");
		}

		if (json.has("pages"))
		{
			JsonElement pages0 = json.get("pages");

			if (pages0.isJsonArray())
			{
				for (JsonElement element : pages0.getAsJsonArray())
				{
					if (element.isJsonObject() && element.getAsJsonObject().has("id"))
					{
						loadPagePath(folder, pagePath, element.getAsJsonObject());
					}
				}
			}
		}
	}

	public static JsonElement getLoadedPage(MinecraftServer server, String path)
	{
		getServerGuide(server);
		JsonElement json = LOADED_PAGES.get(path);

		if (json == null)
		{
			json = JsonNull.INSTANCE;

			File file = LOADED_PAGE_PATHS.get(path);

			if (file != null)
			{
				json = DataReader.get(file).safeJson();

				if (!json.isJsonArray())
				{
					json = JsonNull.INSTANCE;
				}
			}

			LOADED_PAGES.put(path, json);
		}

		return json;
	}
}