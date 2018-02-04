package com.feed_the_beast.ftbguides.net;

import com.feed_the_beast.ftbguides.FTBGuidesFinals;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;

public class FTBGuidesNetHandler
{
	static final NetworkWrapper SERVER_INFO = NetworkWrapper.newWrapper(FTBGuidesFinals.MOD_ID + "_server_info");

	public static void init()
	{
		SERVER_INFO.register(1, new MessageServerInfo());
	}
}