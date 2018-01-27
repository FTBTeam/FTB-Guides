package com.feed_the_beast.ftbguides;

import com.feed_the_beast.ftbguides.net.FTBGuidesNetHandler;

public class FTBGuidesCommon
{
	public void preInit()
	{
		FTBGuidesConfig.sync();
		FTBGuidesNetHandler.init();
	}

	public void postInit()
	{
	}
}