package com.feed_the_beast.ftbguides.client;

import com.feed_the_beast.ftbguides.FTBGuides;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBGuides.MOD_ID)
@Config(modid = "ftbguides_client", category = "", name = "../local/client/ftbguides")
public class FTBGuidesClientConfig
{
	@Config.LangKey("stat.generalButton")
	public static final General general = new General();

	public static class General
	{
		@Config.Comment("Use unicode font for guides.")
		public boolean use_unicode_font = true;

		@Config.Comment("Hide mod guides that aren't loaded.")
		public boolean hide_mods_not_present = true;

		@Config.Comment("Hide all guides with type 'other'")
		public boolean hide_other = false;

		@Config.Comment("Background alpha of the Guide GUI.")
		@Config.RangeInt(min = 0, max = 255)
		public int background_alpha = 255;

		@Config.Comment({"Last guide version.", "Do not change, for internal use only."})
		public String last_guide_version = "";

		@Config.Comment("Current theme of guides.")
		public String theme = "paper";
	}

	public static void sync()
	{
		ConfigManager.sync("ftbguides_client", Config.Type.INSTANCE);
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals("ftbguides_client"))
		{
			sync();
		}
	}
}