package com.feed_the_beast.ftbguides.gui;

import com.feed_the_beast.ftblib.lib.util.FinalIDObject;

/**
 * @author LatvianModder
 */
public class GuideType extends FinalIDObject implements Comparable<GuideType>
{
	public static final GuideType SERVER_INFO = new GuideType("server_info", "Server Info", "Server Info");
	public static final GuideType OTHER = new GuideType("other", "Other", "Other");

	public int index;
	public final String title;
	public final String titlePlural;

	public GuideType(String s, String t, String tp)
	{
		super(s);
		title = t;
		titlePlural = tp;
	}

	@Override
	public int compareTo(GuideType o)
	{
		return index - o.index;
	}
}