package com.feed_the_beast.ftbguides.net;

import com.feed_the_beast.ftbguides.ServerInfoPage;
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
public class MessageServerInfo extends MessageToClient<MessageServerInfo>
{
	private String page;
	private JsonElement json;

	public MessageServerInfo()
	{
	}

	public MessageServerInfo(String p, JsonElement e)
	{
		page = p;
		json = e;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBGuidesNetHandler.SERVER_INFO;
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
		ServerInfoPage.INSTANCE.read(page, json);
	}
}