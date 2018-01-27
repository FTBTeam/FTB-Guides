package com.feed_the_beast.ftbguides.net;

import com.feed_the_beast.ftbguides.FTBGuidesFinals;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;

public class FTBGuidesNetHandler
{
	static final NetworkWrapper GENERAL = NetworkWrapper.newWrapper(FTBGuidesFinals.MOD_ID);

	public static void init()
	{
		GENERAL.register(1, new MessageServerInfo());
		GENERAL.register(2, new MessageRequestServerInfo());
	}
}