package com.feed_the_beast.ftbguides.integration;

import com.feed_the_beast.ftbguides.FTBGuidesFinals;
import com.feed_the_beast.ftblib.events.RegisterOptionalServerModsEvent;
import com.feed_the_beast.ftblib.events.ServerReloadEvent;
import com.feed_the_beast.ftblib.lib.EventHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBLibIntegration
{
	public static final ResourceLocation DATA = FTBGuidesFinals.get("data");
	public static final ResourceLocation RELOAD_CONFIG = FTBGuidesFinals.get("config");
	public static final ResourceLocation RELOAD_QUESTS = FTBGuidesFinals.get("quests");

	@SubscribeEvent
	public static void registerReloadIds(ServerReloadEvent.RegisterIds event)
	{
		event.register(RELOAD_CONFIG);
		event.register(RELOAD_QUESTS);
	}

	@SubscribeEvent
	public static void onServerReload(ServerReloadEvent event)
	{
		if (event.reload(RELOAD_CONFIG))
		{
		}

		if (event.reload(RELOAD_QUESTS))
		{
		}
	}

	@SubscribeEvent
	public static void registerOptionalServerMod(RegisterOptionalServerModsEvent event)
	{
		event.register(FTBGuidesFinals.MOD_ID);
	}
}