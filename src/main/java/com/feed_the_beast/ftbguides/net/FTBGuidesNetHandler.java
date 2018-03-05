package com.feed_the_beast.ftbguides.net;

import com.feed_the_beast.ftbguides.FTBGuides;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;

public class FTBGuidesNetHandler
{
	static final NetworkWrapper SERVER_INFO = NetworkWrapper.newWrapper(FTBGuides.MOD_ID + "_server");

	public static void init()
	{
		SERVER_INFO.register(1, new MessageServerInfo());
	}
}