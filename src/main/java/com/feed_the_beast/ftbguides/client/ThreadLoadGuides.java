package com.feed_the_beast.ftbguides.client;

import com.feed_the_beast.ftbguides.FTBGuides;
import com.feed_the_beast.ftbguides.ServerInfoPage;
import com.feed_the_beast.ftbguides.events.ClientGuideEvent;
import com.feed_the_beast.ftbguides.gui.GuiGuide;
import com.feed_the_beast.ftbguides.gui.GuidePage;
import com.feed_the_beast.ftbguides.gui.GuideTitlePage;
import com.feed_the_beast.ftbguides.gui.GuideType;
import com.feed_the_beast.ftbguides.gui.SpecialGuideButton;
import com.feed_the_beast.ftbguides.gui.components.HRGuideComponent;
import com.feed_the_beast.ftbguides.gui.components.TextGuideComponent;
import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.client.FTBLibClient;
import com.feed_the_beast.ftblib.client.SidebarButton;
import com.feed_the_beast.ftblib.client.SidebarButtonGroup;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiLoading;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.io.HttpConnection;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
class ThreadLoadGuides extends Thread
{
	GuiLoading gui;
	GuidePage root;

	ThreadLoadGuides()
	{
		super("ReloadGuides");
		setDaemon(true);
		gui = new GuiLoading()
		{
			@Override
			public void finishLoading()
			{
				FTBGuidesClient.reloadingThread = null;
				FTBGuidesClient.guidesGui = new GuiGuide(root);
				FTBGuidesClient.openGuidesGui();
			}
		};
	}

	@Override
	public void run()
	{
		try
		{
			String s = run1();

			if (!s.isEmpty())
			{
				FTBGuides.LOGGER.error(s);
			}

			gui.setFinished();
		}
		catch (Exception ex)
		{
			FTBGuidesClient.reloadingThread = null;
			ex.printStackTrace();
		}
	}

	public String run1()
	{
		root = new GuidePage("root", null);
		root.title = new TextComponentTranslation(FTBGuides.MOD_ID + ".lang.home");
		gui.setTitle("Loading Guides\nAPI");
		JsonElement apijson = HttpConnection.getJson("http://guides.latmod.com/api/api.json");

		if (!apijson.isJsonObject())
		{
			return "Failed to load the API!";
		}

		JsonObject api = apijson.getAsJsonObject();
		root.readProperties(api.get("default_properties").getAsJsonObject());

		List<GuideTitlePage> guides = new ArrayList<>();
		JsonArray guidesArray = api.get("guides").getAsJsonArray();

		JsonElement modpackGuide = JsonUtils.fromJson(new File(CommonUtils.folderConfig, "ftbguides/modpack_guide/data.json"));

		if (modpackGuide.isJsonObject())
		{
			GuideTitlePage page = new GuideTitlePage("modpack_guide", root, GuideType.MODPACK);
			loadLocalPage(new File(CommonUtils.folderConfig, "ftbguides/modpack_guide"), page, modpackGuide.getAsJsonObject());
			page.textURL = new File(CommonUtils.folderConfig, "ftbguides/modpack_guide/index.json").getAbsolutePath();
			page.properties.put("browser_url", new JsonPrimitive(""));
			guides.add(page);
		}

		for (JsonElement e : guidesArray)
		{
			JsonObject o = e.getAsJsonObject();
			String id = o.get("id").getAsString();
			GuideType type = GuideType.NAME_MAP.get(o.get("type").getAsString());
			GuideTitlePage page = new GuideTitlePage(id, root, type);

			if (type == GuideType.MOD && Loader.isModLoaded(o.has("modid") ? o.get("modid").getAsString() : id))
			{
				page.properties.put("present", new JsonPrimitive(true));
			}

			if (o.has("authors"))
			{
				for (JsonElement e1 : o.get("authors").getAsJsonArray())
				{
					page.authors.add(e1.getAsString());
				}
			}

			loadPage(page, o);
			guides.add(page);
		}

		//gui.setTitle("Loading Guides\nModpack Guide");
		//gui.setTitle("Loading Guides\nMod Guides");

		gui.setTitle("Loading Guides\n" + I18n.format("sidebar_button"));

		GuideTitlePage sidebarButtons = new GuideTitlePage("sidebar_buttons", root, GuideType.OTHER);
		sidebarButtons.isPresent = true;
		sidebarButtons.authors.add("LatvianModder");
		sidebarButtons.icon = Icon.getIcon(FTBLib.MOD_ID + ":textures/gui/teams.png");
		sidebarButtons.title = new TextComponentTranslation("sidebar_button");

		for (SidebarButtonGroup group : FTBLibClient.SIDEBAR_BUTTON_GROUPS)
		{
			for (SidebarButton button : group.getButtons())
			{
				if (button.isVisible() && StringUtils.canTranslate(button.getTooltipLangKey()))
				{
					GuidePage page1 = sidebarButtons.getSub(button.id.toString());
					page1.icon = button.getIcon();
					page1.title = new TextComponentTranslation(button.getLangKey());
					page1.println(new TextComponentTranslation(button.getTooltipLangKey()));
				}
			}
		}

		gui.setTitle("Loading Guides\nMod Guides");

		Map<String, GuideTitlePage> eventMap = new HashMap<>();
		new ClientGuideEvent(root, eventMap).post();

		guides.addAll(eventMap.values());

		for (GuideTitlePage guide : guides)
		{
			root.addSub(guide);
		}

		gui.setTitle("Loading Guides\nFinishing");
		root.properties.put("browser_url", new JsonPrimitive("http://guides.latmod.com"));
		root.addSub(sidebarButtons);
		root.cleanup();
		root.updateCachedProperties(true);
		root.sort(false);

		if (FTBLibClient.isModLoadedOnServer(FTBGuides.MOD_ID + "_server_info"))
		{
			//check if server mod is loaded
			root.println(new TextGuideComponent(ServerInfoPage.INSTANCE.title.getUnformattedText()).setProperty("icon", ServerInfoPage.INSTANCE.icon.toString()).setProperty("click", "command:/ftb server_info"));
			root.println("");
		}

		for (GuideType type : GuideType.NAME_MAP)
		{
			boolean added = false;

			for (GuideTitlePage page : guides)
			{
				if (page.type == type)
				{
					if (!added)
					{
						added = true;
						root.println(new TextGuideComponent(type.titlePlural.translate()).setProperty("bold", "true").setProperty("underlined", "true"));
					}

					root.println(new TextGuideComponent(page.title.getUnformattedText()).setProperty("icon", page.icon.toString()).setProperty("click", page.getName()));
				}
			}
		}

		root.println(HRGuideComponent.INSTANCE);
		return "";
	}

	private void loadPage(GuidePage page, JsonObject json)
	{
		page.title = new TextComponentString(json.get("title").getAsString());

		if (!json.has("icon"))
		{
			json.addProperty("icon", "icon.png");
		}

		page.icon = page.getIcon(json.get("icon").getAsString());

		if (page.icon.isEmpty())
		{
			page.icon = Icon.getIcon(json.get("icon_url").getAsString());

			if (page.icon.isEmpty())
			{
				page.icon = GuidePage.DEFAULT_ICON;
			}
		}

		page.textURL = "http://guides.latmod.com" + page.getPath() + "/index.json";

		if (json.has("buttons"))
		{
			for (JsonElement e : json.get("buttons").getAsJsonArray())
			{
				page.specialButtons.add(new SpecialGuideButton(e.getAsJsonObject()));
			}
		}

		for (Map.Entry<String, JsonElement> entry : json.entrySet())
		{
			String key = entry.getKey();

			if (!GuidePage.STANDARD_KEYS.contains(key))
			{
				page.properties.put(key, entry.getValue());
			}
		}

		if (json.has("pages"))
		{
			for (JsonElement e : json.get("pages").getAsJsonArray())
			{
				JsonObject o = e.getAsJsonObject();
				loadPage(page.getSub(o.get("id").getAsString()), o);
			}
		}

		page.properties.put("browser_url", new JsonPrimitive("http://guides.latmod.com" + page.getPath()));
	}

	private void loadLocalPage(File folder, GuidePage page, JsonObject json)
	{
		page.title = new TextComponentString(json.get("title").getAsString());

		if (!json.has("icon"))
		{
			json.addProperty("icon", "icon.png");
		}

		page.icon = page.getIcon(json.get("icon").getAsString());

		if (page.icon.isEmpty())
		{
			page.icon = Icon.getIcon(json.get("icon_url").getAsString());

			if (page.icon.isEmpty())
			{
				page.icon = GuidePage.DEFAULT_ICON;
			}
		}

		if (json.has("buttons"))
		{
			for (JsonElement e : json.get("buttons").getAsJsonArray())
			{
				page.specialButtons.add(new SpecialGuideButton(e.getAsJsonObject()));
			}
		}

		for (Map.Entry<String, JsonElement> entry : json.entrySet())
		{
			String key = entry.getKey();

			if (!GuidePage.STANDARD_KEYS.contains(key))
			{
				page.properties.put(key, entry.getValue());
			}
		}

		if (json.has("pages"))
		{
			for (JsonElement e : json.get("pages").getAsJsonArray())
			{
				JsonObject o = e.getAsJsonObject();
				String id = o.get("id").getAsString();
				File folder1 = new File(folder, id);
				GuidePage page1 = page.getSub(id);
				loadLocalPage(folder1, page1, o);

				File shortFile = new File(folder, id + ".json");

				if (shortFile.exists())
				{
					page1.textURL = shortFile.getAbsolutePath();
				}
				else
				{
					page1.textURL = new File(folder1, "index.json").getAbsolutePath();
				}
			}
		}
	}
}