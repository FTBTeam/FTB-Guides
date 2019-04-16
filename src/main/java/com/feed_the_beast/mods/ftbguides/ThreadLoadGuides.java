package com.feed_the_beast.mods.ftbguides;

import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiLoading;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.feed_the_beast.mods.ftbguides.events.ClientGuideEvent;
import com.feed_the_beast.mods.ftbguides.gui.GuiGuide;
import com.feed_the_beast.mods.ftbguides.gui.GuidePage;
import com.feed_the_beast.mods.ftbguides.gui.GuideTitlePage;
import com.feed_the_beast.mods.ftbguides.gui.GuideType;
import com.feed_the_beast.mods.ftbguides.gui.SpecialGuideButton;
import com.feed_the_beast.mods.ftbguides.gui.components.HRGuideComponent;
import com.feed_the_beast.mods.ftbguides.gui.components.ImageGuideComponent;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.Proxy;
import java.net.URI;
import java.util.Collections;
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
		super("Guide Loader");
		setDaemon(true);
		gui = new GuiLoading()
		{
			@Override
			public void finishLoading()
			{
				FTBGuides.reloadingThread = null;

				if (loaded)
				{
					FTBGuides.guidesGui = new GuiGuide(root);
					FTBGuides.openGuidesGui("");
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
			FTBGuides.reloadingThread = null;
			ex.printStackTrace();
		}

		gui.setFinished();
	}

	public String run1()
	{
		GuideTheme.THEMES.clear();
		Proxy proxy = Minecraft.getMinecraft().getProxy();

		try
		{
			for (IResource resource : Minecraft.getMinecraft().getResourceManager().getAllResources(new ResourceLocation(FTBGuides.MOD_ID, "themes/index.json")))
			{
				JsonElement json = DataReader.get(resource).json();

				if (json.isJsonArray())
				{
					for (JsonElement element : json.getAsJsonArray())
					{
						String id = element.getAsString();

						JsonElement json1 = DataReader.get(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(FTBGuides.MOD_ID, "themes/" + id + ".json"))).json();

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

		gui.setTitle("Loading Guides\n" + I18n.format("ftbguides.lang.type.modpack"));
		URI modpackGuide = new File(Loader.instance().getConfigDir(), "ftbguides/modpack_guide/").toURI();

		root = new GuidePage("root", null);
		root.title = new TextComponentTranslation(FTBGuides.MOD_ID + ".lang.home");
		root.textURI = modpackGuide.resolve("README.md");

		GuideTitlePage modpackGuidePage = new GuideTitlePage("modpack", root, GuideType.MODPACK);
		modpackGuidePage.title = new TextComponentTranslation(GuideType.MODPACK.title);
		loadChildPages(modpackGuidePage, modpackGuide, proxy, 0);
		modpackGuidePage.cleanup();

		if (!modpackGuidePage.isEmpty())
		{
			root.addSub(modpackGuidePage);
		}

		if (!FTBGuidesConfig.general.disable_non_modpack_guides)
		{
			for (String modid : Minecraft.getMinecraft().getResourceManager().getResourceDomains())
			{
				gui.setTitle("Loading Guides\n" + I18n.format("ftbguides.lang.type.mod") + "\n" + modid);
				GuideTitlePage page;

				ModContainer mod = Loader.instance().getIndexedModList().get(modid);

				if (mod != null)
				{
					page = new GuideTitlePage(modid, root, GuideType.MOD);
					page.title = new TextComponentString(mod.getName());
					String logo = mod.getMetadata().logoFile;
					Icon logoIcon = Icon.EMPTY;

					if (!logo.isEmpty())
					{
						String s = "assets/" + modid + "/";

						if (logo.startsWith(s))
						{
							page.icon = Icon.getIcon(modid + ":" + logo.substring(s.length()));
						}
					}

					if (!logoIcon.isEmpty())
					{
						page.icon = logoIcon;
					}

					if (!mod.getMetadata().url.isEmpty())
					{
						root.properties.put("browser_url", new JsonPrimitive(mod.getMetadata().url));
					}

					page.text.println(new ImageGuideComponent(page.icon));
					page.text.println("(Auto-generated)");

					if (!mod.getMetadata().description.isEmpty())
					{
						page.text.println(HRGuideComponent.INSTANCE);

						for (String s : mod.getMetadata().description.split("\n"))
						{
							page.text.println(s);
						}
					}
				}
				else
				{
					page = new GuideTitlePage(modid, root, GuideType.OTHER);
					page.title = new TextComponentString(modid);
				}

				try
				{
					loadChildPages(page, new URI("mcresource:/" + modid + ":guide/"), proxy, 0);
				}
				catch (Exception ex)
				{
				}

				root.addSub(page);
			}
		}

		gui.setTitle("Loading Guides\nCustom Mod Guides");
		new ClientGuideEvent(root).post();

		gui.setTitle("Loading Guides\nFinishing");
		root.cleanup();
		List<String> rootText = Collections.emptyList();

		try
		{
			rootText = DataReader.get(root.textURI, proxy).safeStringList();
		}
		catch (Exception ex)
		{
		}

		root.updateCachedProperties(true);
		root.textLoadingState = GuidePage.STATE_LOADING;

		/*
		if (FTBGuidesConfig.general.disable_non_modpack_guides && root.pages.size() == 1)
		{
			root.pages.addAll(root.pages.get(0).pages);
			root.pages.remove(0);
		}*/

		root.onPageLoaded(rootText);
		return "";
	}

	private void loadChildPages(GuidePage parent, URI parentURI, Proxy proxy, int depth)
	{
		if (depth > 20)
		{
			FTBGuides.LOGGER.warn("Depth is > 20, stopping at " + parent.getID());
			return;
		}

		JsonElement index = DataReader.get(parentURI.resolve("index.json"), proxy).safeJson();

		if (index.isJsonArray())
		{
			for (JsonElement element : index.getAsJsonArray())
			{
				if (element.isJsonObject())
				{
					JsonObject pageData = element.getAsJsonObject();

					if (pageData.has("id"))
					{
						GuidePage page = parent.getSub(pageData.get("id").getAsString());
						URI uri = parentURI.resolve(page.getID() + "/");

						FTBGuides.LOGGER.info("Loading " + page.getPath() + " from " + uri);

						if (pageData.has("title"))
						{
							String t = pageData.get("title").getAsString();
							page.title = t.startsWith("${") && t.endsWith("}") ? new TextComponentTranslation(t.substring(2, t.length() - 1)) : new TextComponentString(t);
						}
						else
						{
							page.title = new TextComponentString(page.getID());
						}

						page.textURI = uri.resolve("README.md");

						if (pageData.has("icon"))
						{
							page.icon = page.getIcon(pageData.get("icon"));

							if (page.icon.isEmpty())
							{
								page.icon = GuidePage.DEFAULT_ICON;
							}
						}

						if (pageData.has("buttons"))
						{
							for (JsonElement e : pageData.get("buttons").getAsJsonArray())
							{
								page.specialButtons.add(new SpecialGuideButton(e.getAsJsonObject()));
							}
						}

						if (pageData.has("properties"))
						{
							for (Map.Entry<String, JsonElement> entry : pageData.get("properties").getAsJsonObject().entrySet())
							{
								page.properties.put(entry.getKey(), entry.getValue());
							}
						}

						//TODO: Style

						try
						{
							loadChildPages(page, uri, proxy, depth + 1);
						}
						catch (StackOverflowError error)
						{
							FTBGuides.LOGGER.error("Failed to load " + page.getPath() + " child pages!");
						}
					}
				}
			}
		}
	}
}