package com.feed_the_beast.ftbguides.client;

import com.feed_the_beast.ftbguides.FTBGuidesFinals;
import com.feed_the_beast.ftblib.lib.gui.GuiLang;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBGuidesFinals.MOD_ID)
@Config(modid = FTBGuidesFinals.MOD_ID + "_client", category = "", name = "../local/client/ftbguides")
public class FTBGuidesClientConfig
{
	@Config.LangKey(GuiLang.LANG_GENERAL)
	public static final General general = new General();

	public static class General
	{
		@Config.Comment("Background alpha of the Guide GUI")
		@Config.RangeInt(min = 0, max = 255)
		public int background_alpha = 255;
	}

	public static void sync()
	{
		ConfigManager.sync(FTBGuidesFinals.MOD_ID + "_client", Config.Type.INSTANCE);
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(FTBGuidesFinals.MOD_ID + "_client"))
		{
			sync();
		}
	}
}