package com.feed_the_beast.ftbguides.gui;

import com.feed_the_beast.ftbguides.FTBGuidesFinals;
import com.feed_the_beast.ftbguides.events.ClientGuideEvent;
import com.feed_the_beast.ftbguides.gui.components.HRGuideComponent;
import com.feed_the_beast.ftbguides.gui.components.TextGuideComponent;
import com.feed_the_beast.ftblib.FTBLibFinals;
import com.feed_the_beast.ftblib.client.FTBLibModClient;
import com.feed_the_beast.ftblib.client.SidebarButton;
import com.feed_the_beast.ftblib.client.SidebarButtonGroup;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiLoading;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.io.HttpConnection;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.NameMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
class ThreadReloadGuides extends Thread
{
	GuiLoading gui;

	ThreadReloadGuides()
	{
		super("ReloadGuides");
		setDaemon(true);
		gui = new GuiLoading()
		{
			@Override
			public void finishLoading()
			{
				Guides.reloadingThread = null;
				Guides.cachedGui = new GuiGuide(GuidePageRoot.INSTANCE);
				Guides.openGui();
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
				FTBGuidesFinals.LOGGER.error(s);
			}

			gui.setFinished();
		}
		catch (Exception ex)
		{
			Guides.reloadingThread = null;
			ex.printStackTrace();
		}
	}

	public String run1()
	{
		GuidePageRoot.INSTANCE.clear();
		gui.setTitle("Loading Guides\nAPI");
		JsonElement apijson = HttpConnection.getJson("http://guides.latmod.com/api/api.json");

		if (!apijson.isJsonObject())
		{
			return "Failed to load the API!";
		}

		JsonObject api = apijson.getAsJsonObject();
		GuidePageRoot.INSTANCE.readProperties(api.get("default_properties").getAsJsonObject());

		LinkedHashSet<GuideType> types = new LinkedHashSet<>(4);
		types.add(GuideType.SERVER_INFO);
		int i;

		JsonArray typesArray = api.get("types").getAsJsonArray();
		i = 0;

		for (JsonElement e : typesArray)
		{
			i++;
			JsonObject o = e.getAsJsonObject();
			types.add(new GuideType(o.get("id").getAsString(), o.get("title").getAsString(), o.get("title_p").getAsString()));
		}

		types.remove(GuideType.OTHER);
		types.add(GuideType.OTHER);
		i = 0;

		GuideType[] typeArray = new GuideType[types.size()];

		for (GuideType type : types)
		{
			type.index = i;
			typeArray[i] = type;
			i++;
		}

		NameMap<GuideType> guideTypes = NameMap.create(GuideType.OTHER, typeArray);
		GuideType modType = guideTypes.get("mod");

		List<GuideTitlePage> guides = new ArrayList<>();
		JsonArray guidesArray = api.get("guides").getAsJsonArray();

		for (JsonElement e : guidesArray)
		{
			JsonObject o = e.getAsJsonObject();
			String id = o.get("id").getAsString();
			GuideType type = guideTypes.get(o.get("type").getAsString());
			GuideTitlePage page = new GuideTitlePage(id, type);

			if (type == modType && Loader.isModLoaded(o.has("modid") ? o.get("modid").getAsString() : id))
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

		GuideTitlePage sidebarButtons = new GuideTitlePage("sidebar_buttons", GuideType.OTHER);
		sidebarButtons.isPresent = true;
		sidebarButtons.authors.add("LatvianModder");
		sidebarButtons.icon = Icon.getIcon(FTBLibFinals.MOD_ID + ":textures/gui/teams.png");
		sidebarButtons.title = new TextComponentTranslation("sidebar_button");

		for (SidebarButtonGroup group : FTBLibModClient.SIDEBAR_BUTTON_GROUPS)
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
		new ClientGuideEvent(eventMap, modType).post();

		guides.addAll(eventMap.values());

		for (GuideTitlePage guide : guides)
		{
			GuidePageRoot.INSTANCE.addSub(guide);
		}

		gui.setTitle("Loading Guides\nFinishing");
		GuidePageRoot.INSTANCE.properties.put("browser_url", new JsonPrimitive("http://guides.latmod.com"));
		GuidePageRoot.INSTANCE.addSub(GuideTitlePage.SERVER_INFO);
		GuidePageRoot.INSTANCE.addSub(sidebarButtons);
		GuidePageRoot.INSTANCE.cleanup();
		GuidePageRoot.INSTANCE.updateCachedProperties(true);
		GuidePageRoot.INSTANCE.sort(false);

		for (GuideType type : guideTypes)
		{
			boolean added = false;

			for (GuideTitlePage page : guides)
			{
				if (page.type == type)
				{
					if (!added)
					{
						added = true;
						GuidePageRoot.INSTANCE.println(new TextGuideComponent(type.titlePlural).setProperty("bold", "true").setProperty("underlined", "true"));
					}

					GuidePageRoot.INSTANCE.println(new TextGuideComponent(page.title.getUnformattedText()).setProperty("icon", page.icon.toString()).setProperty("click", page.getName()));
				}
			}
		}

		GuidePageRoot.INSTANCE.println(HRGuideComponent.INSTANCE);
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
}