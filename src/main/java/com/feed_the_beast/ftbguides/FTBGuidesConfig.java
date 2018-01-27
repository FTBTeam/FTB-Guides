package com.feed_the_beast.ftbguides;

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
@Config(modid = FTBGuidesFinals.MOD_ID, category = "")
public class FTBGuidesConfig
{
	public static final Quests quests = new Quests();

	public static final ServerInfo server_info = new ServerInfo();

	public static class Quests
	{
		@Config.LangKey(GuiLang.LANG_ENABLED)
		@Config.Comment("Enables quests")
		public boolean enabled = false;
	}

	public static class ServerInfo
	{
		@Config.Comment("Show current world difficulty in server info")
		public boolean difficulty = true;
		//public boolean admin_quick_access = true;
	}

	public static void sync()
	{
		ConfigManager.sync(FTBGuidesFinals.MOD_ID, Config.Type.INSTANCE);
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(FTBGuidesFinals.MOD_ID))
		{
			sync();
		}
	}
}