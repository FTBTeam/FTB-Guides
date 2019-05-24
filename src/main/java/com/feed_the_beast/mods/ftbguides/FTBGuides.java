package com.feed_the_beast.mods.ftbguides;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.OtherMods;
import com.feed_the_beast.mods.ftbguides.gui.GuiGuide;
import com.feed_the_beast.mods.ftbguides.gui.GuidePage;
import com.feed_the_beast.mods.ftbguides.gui.ThreadLoadPage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.util.Collections;

@Mod(
		modid = FTBGuides.MOD_ID,
		name = FTBGuides.MOD_NAME,
		version = FTBGuides.VERSION,
		clientSideOnly = true,
		dependencies = FTBLib.THIS_DEP + ";after:" + OtherMods.TINKERS_CONSTRUCT
)
public class FTBGuides
{
	public static final String MOD_ID = "ftbguides";
	public static final String MOD_NAME = "FTB Guides";
	public static final String VERSION = "0.0.0.ftbguides";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static KeyBinding KEY_GUIDE;

	static GuiGuide guidesGui = null;
	static ThreadLoadGuides reloadingThread = null;
	public static String pageToOpen = "";

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		FTBGuidesConfig.sync();
		ClientRegistry.registerKeyBinding(KEY_GUIDE = new KeyBinding("key.ftbguides.guide", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_NONE, FTBLib.KEY_CATEGORY));
	}

	@Mod.EventHandler
	public void onPostInit(FMLPostInitializationEvent event)
	{
		if (Minecraft.getMinecraft().getResourceManager() instanceof SimpleReloadableResourceManager)
		{
			((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new GuideReloadListener());
		}
	}

	public static void setShouldReload()
	{
		guidesGui = null;

		if (reloadingThread != null)
		{
			try
			{
				reloadingThread.interrupt();
			}
			catch (Throwable throwable)
			{
			}

			reloadingThread = null;
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

		if (!pageToOpen.isEmpty())
		{
			GuidePage page = guidesGui.page.getSubFromPath(pageToOpen);

			if (page != null)
			{
				if (page.textLoadingState == GuidePage.STATE_NOT_LOADING)
				{
					if (page.textURI == null)
					{
						page.onPageLoaded(Collections.emptyList());
						pageToOpen = "";
					}
					else
					{
						new ThreadLoadPage(page).start();
						return false;
					}
				}
				else
				{
					guidesGui = new GuiGuide(page);
					pageToOpen = "";
				}
			}
		}

		guidesGui.openGui();
		return true;
	}
}