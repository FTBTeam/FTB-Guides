package com.feed_the_beast.ftbguides.handlers;

import com.feed_the_beast.ftbguides.FTBGuides;
import com.feed_the_beast.ftbguides.FTBGuidesConfig;
import com.feed_the_beast.ftblib.events.RegisterOptionalServerModsEvent;
import com.feed_the_beast.ftblib.events.ServerReloadEvent;
import com.feed_the_beast.ftblib.lib.EventHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBGuidesEventHandler
{
	public static final ResourceLocation RELOAD_CONFIG = new ResourceLocation(FTBGuides.MOD_ID, "config");
	public static final ResourceLocation RELOAD_SERVER_INFO = new ResourceLocation(FTBGuides.MOD_ID, "server_info");

	@SubscribeEvent
	public static void registerReloadIds(ServerReloadEvent.RegisterIds event)
	{
		event.register(RELOAD_CONFIG);
		event.register(RELOAD_SERVER_INFO);
	}

	@SubscribeEvent
	public static void onServerReload(ServerReloadEvent event)
	{
		if (event.reload(RELOAD_CONFIG))
		{
			FTBGuidesConfig.sync();
		}
		
		/*
		if (event.reload(RELOAD_SERVER_INFO))
		{
			ServerInfoPage.CACHE.clear();
			ServerInfoPage.serverGuide = null;
		}
		*/
	}

	@SubscribeEvent
	public static void registerOptionalServerMod(RegisterOptionalServerModsEvent event)
	{
		event.register(FTBGuides.MOD_ID);
	}
}