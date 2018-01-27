package com.feed_the_beast.ftbguides.net;

import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * @author LatvianModder
 */
public class MessageRequestServerInfo extends MessageToServer<MessageRequestServerInfo>
{
	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBGuidesNetHandler.GENERAL;
	}

	@Override
	public boolean hasData()
	{
		return false;
	}

	@Override
	public void onMessage(MessageRequestServerInfo m, EntityPlayer player)
	{
		new MessageServerInfo((EntityPlayerMP) player).sendTo(player);
	}
}