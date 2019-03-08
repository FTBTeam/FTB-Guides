package com.feed_the_beast.ftbguides;

import com.feed_the_beast.ftbguides.client.FTBGuidesClient;
import com.feed_the_beast.ftblib.events.FTBLibPreInitRegistryEvent;
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
	@SubscribeEvent
	public static void onFTBLibPreInitRegistry(FTBLibPreInitRegistryEvent event)
	{
		FTBLibPreInitRegistryEvent.Registry registry = event.getRegistry();
		registry.registerServerReloadHandler(new ResourceLocation(FTBGuides.MOD_ID, "server_info"), reloadEvent -> FTBGuidesCommon.reloadServerGuide());

		registry.registerSyncData(FTBGuides.MOD_ID, new ISyncData()
		{
			@Override
			public NBTTagCompound writeSyncData(EntityPlayerMP player, ForgePlayer forgePlayer)
			{
				NBTTagCompound nbt = new NBTTagCompound();
				NBTBase data = JsonUtils.toNBT(FTBGuidesCommon.getServerGuide(player.server));

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
}