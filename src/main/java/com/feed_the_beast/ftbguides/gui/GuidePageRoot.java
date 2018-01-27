package com.feed_the_beast.ftbguides.gui;

import com.feed_the_beast.ftbguides.FTBGuidesFinals;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * @author LatvianModder
 */
public class GuidePageRoot extends GuidePage
{
	public static final GuidePageRoot INSTANCE = new GuidePageRoot();

	private GuidePageRoot()
	{
		super("root", null);
		title = new TextComponentTranslation("sidebar_button." + FTBGuidesFinals.MOD_ID + ".guides");
	}

	@Override
	public String getPath()
	{
		return "/";
	}
}