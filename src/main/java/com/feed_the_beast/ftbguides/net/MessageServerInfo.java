package com.feed_the_beast.ftbguides.net;

import com.feed_the_beast.ftbguides.FTBGuidesCommon;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * @author LatvianModder
 */
public class MessageServerInfo extends MessageToServer
{
	private String page;

	public MessageServerInfo()
	{
	}

	public MessageServerInfo(String p)
	{
		page = p;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBGuidesNetHandler.GENERAL;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeString(page);
	}

	@Override
	public void readData(DataIn data)
	{
		page = data.readString();
	}

	@Override
	public void onMessage(EntityPlayerMP player)
	{
		new MessageServerInfoResponse(page, FTBGuidesCommon.getLoadedPage(page)).sendTo(player);
	}
}