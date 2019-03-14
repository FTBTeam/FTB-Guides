package com.feed_the_beast.mods.ftbguides;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBGuides.MOD_ID)
@Config(modid = FTBGuides.MOD_ID, category = "", name = "ftbguides/config")
@Config.LangKey(FTBGuides.MOD_ID)
public class FTBGuidesConfig
{
	@Config.LangKey("stat.generalButton")
	public static final General general = new General();

	public static class General
	{
		@Config.Comment("Change this to allow client to see when there is a new update for the modpack guide.")
		public String modpack_guide_version = "";

		@Config.Comment("Flash Guides button with '!' before the first time it's opened, and open modpack guide.")
		public boolean flash_guides = true;

		@Config.Comment("URI to load main guide page from.")
		public String base_uri = "https://guides.latmod.com";
	}

	public static void sync()
	{
		ConfigManager.sync(FTBGuides.MOD_ID, Config.Type.INSTANCE);
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(FTBGuides.MOD_ID))
		{
			sync();
		}
	}
}