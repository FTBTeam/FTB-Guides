package com.feed_the_beast.ftbguides.handlers;

import com.feed_the_beast.ftbguides.FTBGuidesConfig;
import com.feed_the_beast.ftbguides.FTBGuidesFinals;
import com.feed_the_beast.ftbguides.util.FTBGuidesTeamData;
import com.feed_the_beast.ftblib.events.RegisterDataProvidersEvent;
import com.feed_the_beast.ftblib.events.RegisterOptionalServerModsEvent;
import com.feed_the_beast.ftblib.events.RegisterSyncDataEvent;
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
	public static final ResourceLocation DATA_ID = FTBGuidesFinals.get("data");
	public static final ResourceLocation RELOAD_CONFIG = FTBGuidesFinals.get("config");
	public static final ResourceLocation RELOAD_SERVER_INFO = FTBGuidesFinals.get("server_info");

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

		if (event.reload(RELOAD_SERVER_INFO))
		{
			//ServerInfoPage.CACHE.clear();
			//ServerInfoPage.serverGuide = null;
		}
	}

	@SubscribeEvent
	public static void registerOptionalServerMod(RegisterOptionalServerModsEvent event)
	{
		event.register(FTBGuidesFinals.MOD_ID);
	}

	@SubscribeEvent
	public static void registerTeamDataProvider(RegisterDataProvidersEvent.Team event)
	{
		event.register(DATA_ID, FTBGuidesTeamData::new);
	}

	@SubscribeEvent
	public static void registerSyncData(RegisterSyncDataEvent event)
	{
		event.register(FTBGuidesFinals.MOD_ID, new FTBGuidesSyncData());
	}

	/* TODO: Display quest book notification
	@SubscribeEvent
	public static void onPlayerLoggedIn(ForgePlayerLoggedInEvent event)
	{
		if (event.getPlayer().isFake())
		{
			return;
		}

		EntityPlayerMP player = event.getPlayer().getPlayer();

		if (event.isFirstLogin())
		{
		}
	}*/

	/*
	@SubscribeEvent
	public static void getTeamSettings(ForgeTeamConfigEvent event)
	{
		FTBGuidesTeamData.get(event.getTeam()).addConfig(event);
	}*/
}