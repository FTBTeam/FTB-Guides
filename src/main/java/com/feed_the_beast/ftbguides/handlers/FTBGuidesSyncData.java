package com.feed_the_beast.ftbguides.handlers;

import com.feed_the_beast.ftbguides.client.CachedClientData;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ISyncData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

/**
 * @author LatvianModder
 */
public class FTBGuidesSyncData implements ISyncData
{
	@Override
	public NBTTagCompound writeSyncData(EntityPlayerMP player, ForgePlayer forgePlayer)
	{
		return new NBTTagCompound();
	}

	@Override
	public void readSyncData(NBTTagCompound nbt)
	{
		CachedClientData.clear();

		NBTTagCompound quests = nbt.getCompoundTag("Quests");

		for (String id : quests.getKeySet())
		{
			//FIXME: Implement me
		}
	}
}