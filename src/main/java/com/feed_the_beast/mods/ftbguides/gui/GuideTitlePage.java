package com.feed_the_beast.mods.ftbguides.gui;

/**
 * @author LatvianModder
 */
public class GuideTitlePage extends GuidePage
{
	public final GuideType type;

	public GuideTitlePage(String id, GuidePage p, GuideType t)
	{
		super(id, p);
		type = t;
	}
}