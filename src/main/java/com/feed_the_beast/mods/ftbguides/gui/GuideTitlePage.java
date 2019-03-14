package com.feed_the_beast.mods.ftbguides.gui;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuideTitlePage extends GuidePage
{
	public final GuideType type;
	public final List<String> authors;
	public boolean isPresent;

	public GuideTitlePage(String id, GuidePage p, GuideType t)
	{
		super(id, p);
		type = t;
		authors = new ArrayList<>();
		isPresent = false;
	}
}