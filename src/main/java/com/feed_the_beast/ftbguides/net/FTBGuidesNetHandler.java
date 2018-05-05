package com.feed_the_beast.ftbguides.net;

import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;

public class FTBGuidesNetHandler
{
	static final NetworkWrapper SERVER_INFO = NetworkWrapper.newWrapper("ftbguides_server");

	public static void init()
	{
		SERVER_INFO.register(new MessageServerInfo());
	}
}