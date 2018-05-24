package com.feed_the_beast.ftbguides.net;

import com.feed_the_beast.ftbguides.client.FTBGuidesClient;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.google.gson.JsonElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class MessageServerInfoResponse extends MessageToClient
{
	private String page;
	private JsonElement json;

	public MessageServerInfoResponse()
	{
	}

	public MessageServerInfoResponse(String p, JsonElement e)
	{
		page = p;
		json = e;
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
		data.writeJson(json);
	}

	@Override
	public void readData(DataIn data)
	{
		page = data.readString();
		json = data.readJson();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		FTBGuidesClient.loadServerGuidePage(page, json);
	}
}