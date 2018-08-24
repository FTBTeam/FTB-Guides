package com.feed_the_beast.ftbguides.client;

import com.feed_the_beast.ftbguides.FTBGuides;
import com.feed_the_beast.ftbguides.events.ClientGuideEvent;
import com.feed_the_beast.ftbguides.gui.GuiGuide;
import com.feed_the_beast.ftbguides.gui.GuidePage;
import com.feed_the_beast.ftbguides.gui.GuideTitlePage;
import com.feed_the_beast.ftbguides.gui.GuideType;
import com.feed_the_beast.ftbguides.gui.SpecialGuideButton;
import com.feed_the_beast.ftbguides.gui.components.HRGuideComponent;
import com.feed_the_beast.ftbguides.gui.components.TextGuideComponent;
import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.client.FTBLibClient;
import com.feed_the_beast.ftblib.client.SidebarButton;
import com.feed_the_beast.ftblib.client.SidebarButtonGroup;
import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiLoading;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.feed_the_beast.ftblib.lib.util.SidedUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;
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
	private boolean loaded = false;

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

				if (loaded)
				{
					FTBGuidesClient.guidesGui = new GuiGuide(root);
					FTBGuidesClient.openGuidesGui("");
				}
				else
				{
					gui.closeGui(false);
				}
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

			loaded = true;
		}
		catch (Exception ex)
		{
			FTBGuidesClient.reloadingThread = null;
			ex.printStackTrace();
		}

		gui.setFinished();
	}

	public String run1()
	{
		GuideTheme.THEMES.clear();

		try
		{
			for (IResource resource : ClientUtils.MC.getResourceManager().getAllResources(new ResourceLocation(FTBGuides.MOD_ID, "themes/index.json")))
			{
				JsonElement json = DataReader.get(resource).json();

				if (json.isJsonArray())
				{
					for (JsonElement element : json.getAsJsonArray())
					{
						String id = element.getAsString();

						JsonElement json1 = DataReader.get(ClientUtils.MC.getResourceManager().getResource(new ResourceLocation(FTBGuides.MOD_ID, "themes/" + id + ".json"))).json();

						if (json1.isJsonObject())
						{
							JsonObject o = json1.getAsJsonObject();
							GuideTheme theme = new GuideTheme(id);
							GuideTheme.THEMES.put(id, theme);

							if (o.has("title"))
							{
								theme.title = JsonUtils.deserializeTextComponent(o.get("title"));
							}
							else
							{
								theme.title = new TextComponentString(id);
							}

							theme.background = Icon.getIcon(o.get("background"));
							theme.text = Color4I.fromJson(o.get("text"));
							theme.textMouseOver = Color4I.fromJson(o.get("text_mouse_over"));
							theme.lines = Color4I.fromJson(o.get("lines"));
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			if (!(ex instanceof FileNotFoundException))
			{
				ex.printStackTrace();
			}
		}

		if (!GuideTheme.THEMES.isEmpty())
		{
			GuideTheme prevTheme = null;

			for (GuideTheme theme : GuideTheme.THEMES.values())
			{
				if (prevTheme != null)
				{
					prevTheme.next = theme;
				}

				prevTheme = theme;
			}

			if (prevTheme != null)
			{
				prevTheme.next = GuideTheme.THEMES.values().iterator().next();
			}
		}

		if (FTBLibConfig.debugging.print_more_info)
		{
			FTBGuides.LOGGER.info("Loaded Guide Themes: " + GuideTheme.THEMES.values());
		}

		root = new GuidePage("root", null);
		root.title = new TextComponentTranslation(FTBGuides.MOD_ID + ".lang.home");
		gui.setTitle("Loading Guides\nAPI");

		JsonElement apijson = JsonNull.INSTANCE;

		try
		{
			apijson = DataReader.get(new URL("https://guides.latmod.com/api/api.json"), DataReader.JSON, ClientUtils.MC.getProxy()).json();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		if (!apijson.isJsonObject())
		{
			return "Failed to load the API!";
		}

		JsonObject api = apijson.getAsJsonObject();

		try
		{
			root.textURI = new URI("https://raw.githubusercontent.com/LatvianModder/FTBGuidesWeb/master");
			root.textLoadingState = GuidePage.STATE_LOADED;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "Base path is incorrect!";
		}

		List<GuideTitlePage> guides = new ArrayList<>();
		JsonArray guidesArray = api.get("guides").getAsJsonArray();

		JsonElement modpackGuide = DataReader.get(new File(Loader.instance().getConfigDir(), "ftbguides/modpack_guide/data.json")).safeJson();

		if (modpackGuide.isJsonObject())
		{
			GuideTitlePage page = new GuideTitlePage("modpack_guide", root, GuideType.MODPACK);
			File folder = new File(Loader.instance().getConfigDir(), "ftbguides/modpack_guide");
			page.textURI = folder.toURI().resolve("index.json");
			loadLocalPage(folder, page, modpackGuide.getAsJsonObject());
			page.properties.put("browser_url", new JsonPrimitive(""));
			guides.add(page);
		}

		File[] modGuideFiles = new File(Loader.instance().getConfigDir(), "ftbguides/mod_guides").listFiles();

		if (modGuideFiles != null && modGuideFiles.length > 0)
		{
			for (File modGuideFile : modGuideFiles)
			{
				if (modGuideFile.isDirectory())
				{
					JsonElement modGuide = DataReader.get(new File(modGuideFile, "data.json")).safeJson();

					if (modGuide.isJsonObject())
					{
						JsonObject json = modGuide.getAsJsonObject();
						String modid = json.has("modid") ? json.get("modid").getAsString() : "";

						if (!FTBGuidesClientConfig.general.hide_mods_not_present || modid.isEmpty() || Loader.isModLoaded(modid))
						{
							GuideTitlePage page = new GuideTitlePage(modGuideFile.getName(), root, GuideType.MOD);
							page.textURI = modGuideFile.toURI().resolve("index.json");
							loadLocalPage(modGuideFile, page, json);
							page.properties.put("browser_url", new JsonPrimitive(""));
							guides.add(page);
						}
					}
				}
			}
		}

		for (JsonElement e : guidesArray)
		{
			JsonObject o = e.getAsJsonObject();
			String id = o.get("id").getAsString();
			GuideType type = GuideType.NAME_MAP.get(o.get("type").getAsString());

			if (type == GuideType.MOD && FTBGuidesClientConfig.general.hide_mods_not_present && o.has("modid") && !Loader.isModLoaded(o.get("modid").getAsString()))
			{
				continue;
			}
			else if (type == GuideType.OTHER && FTBGuidesClientConfig.general.hide_other)
			{
				continue;
			}

			GuideTitlePage page = new GuideTitlePage(id, root, type);

			if (o.has("authors"))
			{
				for (JsonElement e1 : o.get("authors").getAsJsonArray())
				{
					page.authors.add(e1.getAsString());
				}
			}

			try
			{
				loadPage(page, o);
				guides.add(page);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
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
				if (button.isVisible() && I18n.hasKey(button.getTooltipLangKey()))
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

		if (FTBGuidesClient.serverGuideClient != null && SidedUtils.isModLoadedOnServer(FTBGuides.MOD_ID))
		{
			for (Map.Entry<String, JsonElement> entry : FTBGuidesClient.serverGuideClient.entrySet())
			{
				if (entry.getValue().isJsonObject())
				{
					GuideTitlePage page = new GuideTitlePage(entry.getKey(), root, GuideType.SERVER_INFO);
					loadServerPage(page, entry.getValue().getAsJsonObject());
					guides.add(page);
				}
			}
		}

		for (GuideTitlePage guide : guides)
		{
			root.addSub(guide);
		}

		gui.setTitle("Loading Guides\nFinishing");
		root.properties.put("browser_url", new JsonPrimitive("https://guides.latmod.com"));
		root.addSub(sidebarButtons);
		root.cleanup();
		root.updateCachedProperties(true);
		root.sort(false);

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
						root.println(new TextGuideComponent(I18n.format(type.titlePlural)).setProperty("text_scale", "1.5").setProperty("bold", "true"));
					}

					root.println(new TextGuideComponent(page.title.getUnformattedText()).setProperty("icon", page.icon.toString()).setProperty("click", page.getName()));
				}
			}
		}

		root.println(HRGuideComponent.INSTANCE);
		return "";
	}

	private void loadPageBase(GuidePage page, JsonObject json)
	{
		page.title = json.has("title") ? new TextComponentString(json.get("title").getAsString()) : new TextComponentString(page.getName());

		if (FTBLibConfig.debugging.print_more_info && page.textURI != null)
		{
			FTBGuides.LOGGER.info("Base path of " + page.getPath() + ": " + page.textURI.resolve("."));
		}

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
	}

	private void loadPage(GuidePage page, JsonObject json) throws Exception
	{
		page.textURI = new URI(json.get("original_text_url").getAsString());

		loadPageBase(page, json);

		if (json.has("pages"))
		{
			for (JsonElement e : json.get("pages").getAsJsonArray())
			{
				JsonObject o = e.getAsJsonObject();
				loadPage(page.getSub(o.get("id").getAsString()), o);
			}
		}

		page.properties.put("browser_url", new JsonPrimitive("https://guides.latmod.com" + page.getPath()));
	}

	private void loadLocalPage(File folder, GuidePage page, JsonObject json)
	{
		loadPageBase(page, json);

		if (json.has("pages"))
		{
			for (JsonElement e : json.get("pages").getAsJsonArray())
			{
				JsonObject o = e.getAsJsonObject();
				String id = o.get("id").getAsString();
				File folder1 = new File(folder, id);
				GuidePage page1 = page.getSub(id);
				loadLocalPage(folder1, page1, o);

				try
				{
					File shortFile = new File(folder, id + ".json");

					if (shortFile.exists())
					{
						page1.textURI = shortFile.toURI();
					}
					else
					{
						page1.textURI = new File(folder1, "index.json").toURI();
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}

	private void loadServerPage(GuidePage page, JsonObject json)
	{
		loadPageBase(page, json);

		try
		{
			page.textURI = new URI("ftp", page.getPath(), null);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		if (json.has("pages"))
		{
			for (JsonElement e : json.get("pages").getAsJsonArray())
			{
				JsonObject o = e.getAsJsonObject();
				String id = o.get("id").getAsString();
				GuidePage page1 = page.getSub(id);
				loadServerPage(page1, o);
			}
		}
	}
}