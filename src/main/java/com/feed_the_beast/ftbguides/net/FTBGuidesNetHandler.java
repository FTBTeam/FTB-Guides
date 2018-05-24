package com.feed_the_beast.ftbguides.net;

import com.feed_the_beast.ftbguides.FTBGuides;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;

public class FTBGuidesNetHandler
{
	static final NetworkWrapper GENERAL = NetworkWrapper.newWrapper(FTBGuides.MOD_ID);

	public static void init()
	{
		GENERAL.register(new MessageServerInfo());
		GENERAL.register(new MessageServerInfoResponse());
	}
}