package com.feed_the_beast.ftbguides.handlers;

import com.feed_the_beast.ftbguides.FTBGuides;
import com.feed_the_beast.ftbguides.FTBGuidesCommon;
import com.feed_the_beast.ftbguides.FTBGuidesConfig;
import com.feed_the_beast.ftbguides.client.FTBGuidesClient;
import com.feed_the_beast.ftblib.events.RegisterSyncDataEvent;
import com.feed_the_beast.ftblib.events.ServerReloadEvent;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ISyncData;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBGuides.MOD_ID)
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
	public static void registerSyncData(RegisterSyncDataEvent event)
	{
		event.register(FTBGuides.MOD_ID, new ISyncData()
		{
			@Override
			public NBTTagCompound writeSyncData(EntityPlayerMP player, ForgePlayer forgePlayer)
			{
				NBTTagCompound nbt = new NBTTagCompound();
				NBTBase data = JsonUtils.toNBT(FTBGuidesCommon.getServerGuide());

				if (data != null)
				{
					nbt.setTag("Data", data);
				}

				return nbt;
			}

			@Override
			@SideOnly(Side.CLIENT)
			public void readSyncData(NBTTagCompound nbt)
			{
				FTBGuidesClient.loadServerGuide(JsonUtils.toJson(nbt.getTag("Data")));
			}
		});
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
			FTBGuidesCommon.reloadServerGuide();
		}
	}
}